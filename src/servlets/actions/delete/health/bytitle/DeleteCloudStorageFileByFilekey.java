/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.delete.health.bytitle;

import health.database.DAO.UserDAO;
import health.database.models.Users;
import health.input.jsonmodels.JsonCloudStorageFile;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.conf.Constants;
import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.AllConstants;
import util.ServerConfigUtil;
import cloudstorage.cacss.S3Engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zhumulangma.cloudstorage.server.entity.CloudFile;
import com.zhumulangma.cloudstorage.server.exception.ErrorCodeException;

/**
 * 
 * @author Leon
 */
public class DeleteCloudStorageFileByFilekey extends HttpServlet {

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
		PrintWriter out = response.getWriter();
		try {

			String bucketName = ServerConfigUtil
					.getConfigValue(AllConstants.ServerConfigs.CloudStorageBucket);
			
			String objectKey = request.getParameter(AllConstants.api_entryPoints.request_api_filekey);
			if(objectKey==null||objectKey.length()<1)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						AllConstants.api_entryPoints.request_api_filekey);
				return;
			}
			System.out.println("objectKey:" + objectKey);
		
			Hashtable<String, Object> returnValues = null;
			try {
				returnValues = (Hashtable<String, Object>) S3Engine.s3
						.DeleteObject(bucketName, "leoncool", targetLoginID+"/cs/"+objectKey, null);
				
			} catch (ErrorCodeException ex) {
				if (ex.getErrorCode() == Constants.S3ERROR.NOT_SUCH_KEY) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.file_not_found, null,
							objectKey);
					return;
				}
			}		
			Gson gson = new Gson();
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);			
			out.println(gson.toJson(jo));
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;
		} finally {
			System.out.println("running finally");
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
	}// </editor-fold>
}
