package servlets.datamarket;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.UserDAO;
import health.database.DAO.datamarket.DataMarketDAO;
import health.database.datamarket.DataMarket;
import health.database.datamarket.DataSharing;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.Users;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.NonUniqueResultException;

import server.conf.Constants;
import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.AllConstants;
import util.MarketplaceContants;
import util.ServerConfigUtil;
import cloudstorage.cacss.S3Engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zhumulangma.cloudstorage.server.entity.CloudFile;
import com.zhumulangma.cloudstorage.server.exception.ErrorCodeException;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetHealthFileThroughDM")
public class GetHealthFileThroughDM extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetHealthFileThroughDM() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		System.out.println("before checkAndGetLoginFromToken");
		Users accessUser = null;
		PermissionFilter filter = new PermissionFilter();
		String loginID = filter.checkAndGetLoginFromToken(request, response);

		UserDAO userDao = new UserDAO();
		if (loginID == null) {
			if (filter.getCheckResult().equalsIgnoreCase(
					filter.INVALID_LOGIN_TOKEN_ID)) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_login_token_id,
						null, null);
				return;
			} else if (filter.getCheckResult().equalsIgnoreCase(
					AllConstants.ErrorDictionary.login_token_expired)) {
				return;
			} else {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_login_token_id,
						null, null);
				return;
			}
		} else {
			accessUser = userDao.getLogin(loginID);
		}
		String targetLoginID = filter.getTargetUserID(request, response);
		if (targetLoginID != null) {
			Users targetUser = userDao.getLogin(targetLoginID);
			if (targetUser == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_Target_LoginID,
						null, null);
				return;
			}
		}
		if (targetLoginID == null) {
			targetLoginID = loginID;
		}
		OutputStream outStream = null;
		try{
		// PrintWriter out = response.getWriter();
		String streamID=request.getParameter(MarketplaceContants.RequestParameters.streamID);
		if(streamID==null)
		{
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.MISSING_DATA, null,
					MarketplaceContants.RequestParameters.streamID);
			return;
		}

		DatastreamDAO dstreamDao = new DatastreamDAO();
		DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
		Datastream datastream = null;
		try {
			datastream = dstreamDao.getDatastream(streamID, true, false);

		} catch (NonUniqueResultException ex) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null,
					MarketplaceContants.RequestParameters.streamID);
			return;
		}
		if (datastream == null) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Unknown_StreamID, null,
					MarketplaceContants.RequestParameters.streamID);
			return;
		}
		
		DataMarketDAO dmDao=new DataMarketDAO();
		System.out.println("checking permission:"+loginID+","+datastream.getOwner()+","+datastream.getStreamId());
		List<DataSharing> datasharingList=dmDao.getDataSharingListByStreamID(loginID,datastream.getOwner(),datastream.getStreamId());
		if(datasharingList==null||datasharingList.size()==0)
		{
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.PERMISSION_DENIED,
					null, "not allowed to access");
			return;
		}
		
			
			String timestampAndUnitIDAndFileName = request
					.getParameter(AllConstants.api_entryPoints.request_api_filekey);
			if (timestampAndUnitIDAndFileName == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						AllConstants.api_entryPoints.request_api_filekey);
				return;
			}
			if(!timestampAndUnitIDAndFileName.contains(streamID))
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.PERMISSION_DENIED,
						null, "not allowed to access");
				return;
			}
			String bucketName = ServerConfigUtil
					.getConfigValue(AllConstants.ServerConfigs.CloudStorageBucket);
//			String objectKey = loginID + "/" + datastream.getStreamId() + "/"
//					+ timestampAndUnitIDAndFileName;
			String objectKey=timestampAndUnitIDAndFileName;
			String fileName=timestampAndUnitIDAndFileName.substring(timestampAndUnitIDAndFileName.lastIndexOf("/")+1,timestampAndUnitIDAndFileName.length());
			System.out.println("objectKey:" + objectKey);
			// if (S3Engine.s3.existObject(bucketName, objectKey) == null) {
			// ReturnParser.outputErrorException(response,
			// AllConstants.ErrorDictionary.file_not_found, null,
			// streamTitle);
			// return;
			// }
			Hashtable<String, Object> returnValues = null;
			try {
				returnValues = (Hashtable<String, Object>) S3Engine.s3
						.GetObject(bucketName, "leoncool", objectKey, null,
								null);
			} catch (ErrorCodeException ex) {
				if (ex.getErrorCode() == Constants.S3ERROR.NOT_SUCH_KEY) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.file_not_found, null,
							timestampAndUnitIDAndFileName);
					return;
				}
			}
			if (returnValues == null || returnValues.get("owner") == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unauthorized_Access, null,
						timestampAndUnitIDAndFileName);
				return;
			}
			CloudFile file = (CloudFile) returnValues.get("data");
			if (file == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.file_not_found, null,
						timestampAndUnitIDAndFileName);
				return;
			}
			
			if (file.get(CloudFile.S3_CONTENT_TYPE) != null) {
				response.setHeader("Content-Type",
						(String) file.get(CloudFile.S3_CONTENT_TYPE));
				System.out.println("Head Request-----Contenttype:"
						+ file.get(CloudFile.S3_CONTENT_TYPE));
				System.out.println("Content-Type:"+(String) file.get(CloudFile.S3_CONTENT_TYPE));
			} else {
				response.setHeader("overwrite Content-Type", "application/octet-stream");
				System.out.println("Content-Type:"+"application/octet-stream");
					}
			response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
			
			response.setHeader("Content-Length",
					(String) file.get(CloudFile.SIZE));
			outStream = response.getOutputStream();
			S3Engine.s3.directAccessData((String) file.get(CloudFile.LINK),
					outStream, null);

		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;
		} finally {
			System.out.println("running finally");
			// out.close();
			if (outStream != null) {
				outStream.close();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}
	
	
	public static void main(String args[]) {
		Gson gson = new Gson();
		DataMarketDAO dmDao=new DataMarketDAO();
		String searchName = null;
		List<DataMarket> dmListRaw=dmDao.getDataMarketListing(searchName);
		List<DataMarket> dmList=new ArrayList<DataMarket>();
		DatastreamDAO dsDao=new DatastreamDAO();
		for(DataMarket dm:dmListRaw)
		{
			Datastream stream=dm.getDatastream();
			stream.setDatastreamBlocksList(null);
			String streamID=stream.getStreamId();
			List<DatastreamUnits> unitList=dsDao.getDatastreamUnits(streamID);
			dm.setDatastreamUnitsList(unitList);
			stream.setDatastreamBlocksList(null);
			stream.setDatastreamUnitsList(null);
			dm.setDatastream(stream);
			dmList.add(dm);	
		}
		
//		
		JsonObject jo = new JsonObject();
		jo.addProperty(AllConstants.ProgramConts.result,
				AllConstants.ProgramConts.succeed);
		JsonElement jelement=gson.toJsonTree(dmListRaw);
		jo.add("data_market_list",jelement );
		System.out.println(gson.toJson(jo));
//		System.out.println(dmListRaw.get(0).getStreamID().getDatastreamUnitsList().size());
//		out.println(gson.toJson(jo));

		// for (int i = 0; i < m.groupCount(); i++)
		// System.out.println("Group" + i + ": " + m.group(i));
	}

}
