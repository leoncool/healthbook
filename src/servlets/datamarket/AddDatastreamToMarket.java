package servlets.datamarket;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.UserDAO;
import health.database.DAO.datamarket.DataMarketDAO;
import health.database.datamarket.DataMarket;
import health.database.models.Datastream;
import health.database.models.Users;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.AllConstants;
import util.JsonUtil;
import util.MarketplaceContants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/AddDatastreamToMarket")
public class AddDatastreamToMarket extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddDatastreamToMarket() {
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
			if (targetLoginID == null) {
				targetLoginID = loginID;
			}
			DataMarket dm=null;
			Gson gson = new Gson();
			JsonUtil jutil=new JsonUtil();
			try{
			String jsonInput = jutil.readJsonStrFromHttpRequest(request);
			System.out.println(jsonInput);
			dm=gson.fromJson(jsonInput, DataMarket.class);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			if(dm==null)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA,
						null, "data market dm");
				return;
			}
			if(dm.getStreamid()==null)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA,
						null, "stream id");
				return;
			}
			String streamID=dm.getStreamid();
			DatastreamDAO dsDao=new DatastreamDAO();
			
			Datastream datastream=dsDao.getDatastream(streamID, false, false);
			if(datastream==null)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_datastream_id,
						null, MarketplaceContants.RequestParameters.streamID);
				return;
			}
			if(!datastream.getOwner().equalsIgnoreCase(loginID))
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.PERMISSION_DENIED,
						null, MarketplaceContants.RequestParameters.streamID);
				return;
			}
			
			DataMarketDAO dmDao=new DataMarketDAO();
			if(dmDao.existDataMarketItem(loginID,streamID))
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.data_market_item_exist,
						null, MarketplaceContants.RequestParameters.streamID);
				return;
			}
			
			
			dm.setLoginID(loginID);
			dm.setDatastream(datastream);
			dm.setCreatedTime(new Date());
		
			dm=dmDao.addToMarket(dm);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			//solve gson error with mulitple level reference
			dm.setDatastream(null);
			JsonElement jelement=gson.toJsonTree(dm);
			jo.add("data_market",jelement );
			System.out.println(gson.toJson(jo));
			out.println(gson.toJson(jo));

		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;
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
		Gson gson=new Gson();
		DataMarketDAO dmDao=new DataMarketDAO();
		DataMarket dm=dmDao.getDataMarketByID(1);
		dm.setDatastream(null);
		JsonObject jo = new JsonObject();
		jo.addProperty(AllConstants.ProgramConts.result,
				AllConstants.ProgramConts.succeed);
		JsonElement jelement=gson.toJsonTree(dm);
		jo.add("data_market",jelement );
		System.out.println(gson.toJson(jo));
	}

}
