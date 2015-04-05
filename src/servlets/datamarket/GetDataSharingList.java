package servlets.datamarket;

import health.database.DAO.UserDAO;
import health.database.DAO.datamarket.DataMarketDAO;
import health.database.datamarket.DataMarket;
import health.database.datamarket.DataSharing;
import health.database.models.Datastream;
import health.database.models.Users;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.AllConstants;
import util.MarketplaceContants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetDataSharingList")
public class GetDataSharingList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetDataSharingList() {
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

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			
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

			Gson gson = new Gson();
			DataMarketDAO dmDao=new DataMarketDAO();
			
			String searchName = null;
			searchName=request.getParameter(MarketplaceContants.RequestParameters.streamTitle);
			
			List<DataSharing> dsListRaw=dmDao.getDataSharingList(loginID, targetLoginID, searchName);
			List<DataSharing> dsList=new ArrayList<DataSharing>();
			for(DataSharing ds:dsListRaw)
			{
				Datastream stream=ds.getStreamID();
				stream.setDatastreamBlocksList(null);
				stream.setDatastreamUnitsList(null);
				ds.setStreamID(stream);
				dsList.add(ds);	
			}
			
		
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			JsonElement jelement=gson.toJsonTree(dsList);
			jo.add("data_sharing_list",jelement );
			System.out.println(gson.toJson(jo));
			out.println(gson.toJson(jo));

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();
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
		for(DataMarket dm:dmListRaw)
		{
			Datastream stream=dm.getStreamID();
			stream.setDatastreamBlocksList(null);
			stream.setDatastreamUnitsList(null);
			dm.setStreamID(stream);
			dmList.add(dm);	
		}
		
	
		JsonObject jo = new JsonObject();
		jo.addProperty(AllConstants.ProgramConts.result,
				AllConstants.ProgramConts.succeed);
		JsonElement jelement=gson.toJsonTree(dmList);
		jo.add("data_market_list",jelement );
		System.out.println(gson.toJson(jo));
//		out.println(gson.toJson(jo));

		// for (int i = 0; i < m.groupCount(); i++)
		// System.out.println("Group" + i + ": " + m.group(i));
	}

}
