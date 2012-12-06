/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.users;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.LoginToken;
import health.database.models.Subject;
import health.database.models.Users;
import health.input.jsonmodels.JsonDatastream;
import health.input.jsonmodels.JsonSubject;
import health.input.jsonmodels.JsonUserToken;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Leon
 */
public class GetAUserToken extends HttpServlet {

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
			JsonUtil jutil = new JsonUtil();
			String input = jutil.readJsonStrFromHttpRequest(request);
			System.out.println("input:"+input);
			JsonUserToken jsonUserToken = null;
			Gson gson = new Gson();
			try{
			jsonUserToken = gson.fromJson(input, JsonUserToken.class);
			if (jsonUserToken == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			}catch(Exception ex)
			{
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			UserDAO userdao = new UserDAO();
			String username = null;
			String password = null;
			username = jsonUserToken.getLoginid();
			password = jsonUserToken.getPassword();
			if (username == null || username.length() < 1) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_Login_format,
						null, null);
				return;
			}
			if (password == null || password.length() < 1) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_password_format,
						null, null);
				return;
			}
			Users user=userdao.getLogin(username);
			if(user==null)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_LoginID,
						null, null);
				return;
			}
			if(!user.getPassword().equalsIgnoreCase(password))
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_wrong_password,
						null, null);
				return;
			}
			Date expireTime=null;
			if(jsonUserToken.getExpire_in_seconds()!=null&&jsonUserToken.getExpire_in_seconds().length()>1)
			{
				try{
				Date now=new Date();
				long expireLong=now.getTime()+1000L*Long.parseLong(jsonUserToken.getExpire_in_seconds());
				expireTime=new Date();
				expireTime.setTime(expireLong);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_date_format,
							null, null);
					return;
				}
			}
			String ipAddress = request.getRemoteAddr(); 
			LoginToken token=userdao.requestNewLoginToken(username, ipAddress, expireTime);
			jsonUserToken.setPassword(null);		
			jsonUserToken.setToken(token.getTokenID());
	            JsonElement je = gson.toJsonTree(jsonUserToken);
	            JsonObject jo = new JsonObject();
	            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
	        //    jo.addProperty(AllConstants.api_entryPoints.request_api_loginid, subject.getLoginID());
	            jo.add("usertoken", je);
	            System.out.println(jo.toString());
	            out.println(gson.toJson(jo));
			
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault,
					null, null);
			return;
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
	}// </editor-fold>
}
