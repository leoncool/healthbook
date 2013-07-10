/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.health;

import health.database.DAO.DataPermissionDAO;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.DataPermission;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.database.models.Users;
import health.input.jsonmodels.JsonDatastream;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.AllConstants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Leon
 */
public class GetHealthDatastreamsList extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		try {
	
			Users accessUser = null;
			PermissionFilter filter = new PermissionFilter();
			String loginID = filter.checkAndGetLoginFromToken(request, response);

			UserDAO userDao = new UserDAO();
			if (loginID == null) {
				if (filter.getCheckResult().equalsIgnoreCase(
						filter.INVALID_LOGIN_TOKEN_ID)) {
					return;
				} else if (filter.getCheckResult().equalsIgnoreCase(
						AllConstants.ErrorDictionary.login_token_expired)) {
					return;
				} else {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_login_token_id,
							null, null);
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
			if(targetLoginID==null)
			{
				targetLoginID=loginID;
			}
			
			
			SubjectDAO subjDao = new SubjectDAO();

			Subject subject = (Subject) subjDao.findHealthSubject(targetLoginID); // Retreive
			if (subject == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.SYSTEM_ERROR_NO_DEFAULT_HEALTH_SUBJECT, null,
						null);
				return;
			}
			if (loginID!=null&&targetLoginID != null&&!loginID.equals(targetLoginID)) {
				DataPermissionDAO permissionDao = new DataPermissionDAO();
				List<DataPermission> permissionList = permissionDao
						.getDataPermission(
								targetLoginID,
								loginID,
								AllConstants.ProgramConts.data_permission_type_subject,
								Integer.toString(subject.getId()),
								AllConstants.ProgramConts.VALID);
				if (permissionList.size() > 0) {

				} else {
					DataPermission permission = new DataPermission();
					permission.setGivenLoginid(loginID);
					permission
							.setTargetDataType(AllConstants.ProgramConts.data_permission_type_subject);
					permission.setTargetDataId(Integer.toString(subject.getId()));
					permission.setOwner(targetLoginID);
					permission.setStatus("pending");
					permission.setPermission("");
					permissionDao.createDataPermission(permission);
					// ReturnParser.outputErrorException(response,
					// AllConstants.ErrorDictionary.PERMISSION_DENIED,
					// null, streamTitle);
					// return;
				}
			}
			DatastreamDAO dstreamDao = new DatastreamDAO();
			DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
			List<Datastream> dsList = dstreamDao.getDatastreamList(subject.getId(), true,
					false);
			System.out.println("dsList size:" + dsList.size());
			List<JsonDatastream> jsonDsList = new ArrayList<JsonDatastream>();
			for (Datastream ds : dsList) {
				jsonDsList.add(dbtoJUtil.convertDatastream(ds, null));
			}
			System.out.println(jsonDsList.size());
			Gson gson = new Gson();
			JsonElement je = gson.toJsonTree(jsonDsList);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.addProperty(AllConstants.api_entryPoints.request_api_targetid,
					targetLoginID);
			jo.addProperty(AllConstants.api_entryPoints.request_api_loginid,
					subject.getLoginID());
			jo.add("datastream_list", je);
			System.out.println(jo.toString());
			out.println(gson.toJson(jo));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}
}