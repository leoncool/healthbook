/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post.health.bytitle;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.UserDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamBlocks;
import health.database.models.Subject;
import health.database.models.Users;
import health.input.jsonmodels.JsonDatastreamBlock;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import servlets.util.HealthDatastreamFilter;
import servlets.util.HealthSubjectFilter;
import servlets.util.PermissionFilter;
import util.AllConstants;
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Leon
 */
public class CreateHealthDataBlock extends HttpServlet {

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
		PrintWriter out = response.getWriter();

		try {

			HealthSubjectFilter subFilter = new HealthSubjectFilter();
			Subject subject = subFilter.subjectFilter(loginID, request,
					response);
			if (subject == null) {
				ReturnParser
						.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, null);
				return;
			}

			HealthDatastreamFilter datastreamfilter = new HealthDatastreamFilter();
			Datastream datastream = datastreamfilter.datastreamFilter(loginID,
					subject, request, response);

			JsonUtil jutil = new JsonUtil();
			Gson gson = new Gson();
			JsonDatastreamBlock jblock = null;

			String input = jutil.readJsonStrFromHttpRequest(request);
			try {
				
			      jblock = gson.fromJson(input, JsonDatastreamBlock.class);

			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			if(jblock==null)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			if(jblock.getBlockname()==null)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA,
						"blockname", null);
				return;
			}
			try{
				DatastreamDAO dsDao = new DatastreamDAO();
				DatastreamBlocks block=new DatastreamBlocks();
            	block.setDisplayName(jblock.getBlockname());
            	block.setBlockDesc(jblock.getBlockdesc());
            	block.setStreamID(datastream);	
            	block.setTags(jblock.getTags());
				block = dsDao.CreateDatastreamBlock(block);
				DBtoJsonUtil dbjUtil = new DBtoJsonUtil();
				jblock = dbjUtil.convert_a_Datablock(block);
				JsonElement je = gson.toJsonTree(jblock);
				JsonObject jo = new JsonObject();
				jo.addProperty(AllConstants.ProgramConts.result,
						AllConstants.ProgramConts.succeed);
				jo.add("datastream_block", je);
				out.println(gson.toJson(jo));
			}catch(Exception ex)
			{
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault,
						null, null);
				return;
			}
					
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
