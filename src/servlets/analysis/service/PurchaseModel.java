package servlets.analysis.service;

import health.database.DAO.UserDAO;
import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.Users;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.AScontants;
import util.AllConstants;

/**
 * Servlet implementation class PostAnalysisService1
 */
@WebServlet("/PurchaseModel")
public class PurchaseModel extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PurchaseModel() {
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
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				util.AScontants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
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
			
			// user information
//			String loginID = "testtest3";
			AnalysisServiceDAO asDao = new AnalysisServiceDAO();

			Gson gson = new Gson();

			String modelID = request
					.getParameter(AScontants.RequestParameters.Model_ID);
			if (modelID == null || modelID.length() < 5) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						AScontants.RequestParameters.Model_ID);
				return;
			}
			AnalysisModel model = asDao.getModelByID(modelID);
			if (model == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.model_id_cannot_found,
						null, modelID);
				return;
			}
			AnalysisService service = new AnalysisService();
			service.setCreatedTime(new Date());
			service.setStartTime(new Date());
			service.setStatus(AScontants.status_live);
			service.setModelId(model.getId());
			service.setUserId(targetLoginID);
			service = asDao.createServiceBy(service);
			if (service == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null,
						modelID);
				return;
			}

			JsonElement jservice = gson.toJsonTree(service);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("service", jservice);
			System.out.println(gson.toJson(jo));
			out.println(gson.toJson(jo));
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, "");
			return;
		} finally {
			out.close();
		}

	}

	public static void main(String args[]) {
	}
}
