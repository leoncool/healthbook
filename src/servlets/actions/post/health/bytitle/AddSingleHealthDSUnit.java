/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post.health.bytitle;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.HealthDataStreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.Subject;
import health.database.models.Users;
import health.input.jsonmodels.JsonDatastream;
import health.input.jsonmodels.JsonDatastreamUnits;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;

import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.JsonUtil;
import util.UnitValueTypes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Leon
 */
public class AddSingleHealthDSUnit extends HttpServlet {

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

			SubjectDAO subjDao = new SubjectDAO();
			Subject subject = (Subject) subjDao.findHealthSubject(loginID); // Retreive
			if (subject == null) {
				try {
					subject = subjDao.createDefaultHealthSubject(loginID);
					HealthDataStreamDAO hdsDao = new HealthDataStreamDAO();
					hdsDao.createDefaultDatastreamsOnDefaultSubject(loginID,
							subject.getId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Internal_Fault, null,
							null);
					e.printStackTrace();
				}
			}
			String streamTitle = ServerUtil
					.getHealthStreamTitle(ServletPath(request));

			DatastreamDAO dstreamDao = new DatastreamDAO();
			DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
			Datastream datastream = null;
			try {
				datastream = dstreamDao.getHealthDatastreamByTitle(
						subject.getId(), streamTitle, true, false);
			} catch (NonUniqueResultException ex) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null,
						streamTitle);
				return;
			}			
			if (datastream == null) {				
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unknown_StreamTitle, null,
						streamTitle);
				return;
			}
			if(!datastream.getOwner().equalsIgnoreCase(loginID))
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unauthorized_Access, null,
						streamTitle);
				return;
			}

			JsonUtil jutil = new JsonUtil();
			Gson gson = new Gson();
			JsonDatastreamUnits junit = null;

			String input = jutil.readJsonStrFromHttpRequest(request);
			try {
				junit = gson.fromJson(input, JsonDatastreamUnits.class);

			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			try{
			DatastreamUnits unit=dbtoJUtil.convert_a_jdatastream_unit(junit);
			if (!UnitValueTypes.existValueType(unit.getValueType())) {
				ReturnParser
						.outputErrorException(
								response,
								AllConstants.ErrorDictionary.Invalid_ValueType,
								AllConstants.ErrorDictionary.Invalid_ValueType,
								unit.getValueType());
				return;
			}
			unit.setStreamID(datastream);
			dstreamDao.addSingleDS_Unit(unit);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault,
						null, null);
				return;
			}
			datastream = dstreamDao.getHealthDatastreamByTitle(
					subject.getId(), streamTitle, true, false);
			JsonDatastream jobject = dbtoJUtil.convertDatastream(datastream,
					null);
			JsonElement je = gson.toJsonTree(jobject);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("datastream", je);
			JsonWriter jwriter = new JsonWriter(out);
			gson.toJson(jo, jwriter);
			System.out.println(gson.toJson(jobject));
			
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
	}// </editor-fold>
}
