/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post.throughdefaultsubject;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.DeviceTemplateDAO;
import health.database.DAO.SubjectDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.DeviceTemplate;
import health.database.models.Subject;
import health.database.models.Users;
import health.input.jsonmodels.JsonDatastream;
import health.input.jsonmodels.JsonDatastreamUnits;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.JsonUtil;
import util.UnitValueTypes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import device.input.jsonmodels.JsonDeviceTemplate;

/**
 * 
 * @author Leon
 */
public class PostDatastreamThroughDefaultSubject extends HttpServlet {

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
			SubjectDAO subjDao = new SubjectDAO();
			Subject subject = (Subject) subjDao
					.findSystem_Default_Subject("leoncool"); // Retreive
			if (subject == null) {
				ReturnParser
						.outputErrorException(
								response,
								AllConstants.ErrorDictionary.SYSTEM_ERROR_NO_DEFAULT_HEALTH_SUBJECT,
								null, null);
				return;
				// try {
				// subject = subjDao.createDefaultHealthSubject(loginID);
				// HealthDataStreamDAO hdsDao = new HealthDataStreamDAO();
				//
				// hdsDao.createDefaultDatastreamsOnDefaultSubject(loginID,
				// subject.getId());
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// ReturnParser.outputErrorException(response,
				// AllConstants.ErrorDictionary.Internal_Fault, null,
				// null);
				// e.printStackTrace();
				// }
			}
			Users user = (Users) subjDao.getObjectByID(Users.class,
					subject.getLoginID()); // Retrieve Owner of Subject
			JsonUtil jutil = new JsonUtil();
			Gson gson = new Gson();
			JsonDatastream jDatasteram = null;
			boolean usingDeviceTemplate = false;

			try {
				String input = jutil.readJsonStrFromHttpRequest(request);
				System.out.println(input);
				jDatasteram = gson.fromJson(input, JsonDatastream.class);
				if (jDatasteram == null) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Input_Json_Format_Error,
									null, null);
					return;
				}
				if (jDatasteram.getTitle() == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.MISSING_DATA, null,
							null);
					return;
				}
				if (request
						.getParameter(AllConstants.api_entryPoints.request_devicetemplateID) != null) {
					usingDeviceTemplate = true;
					String devicetemplateid = request
							.getParameter(AllConstants.api_entryPoints.request_devicetemplateID);
					DeviceTemplateDAO dtDao = new DeviceTemplateDAO();
					DeviceTemplate devicetemplate = dtDao
							.getDeviceTemplate(devicetemplateid);
					if (devicetemplate == null) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.DeviceErrorDictionary.InvalidDeviceTemplateID,
										"", null);
						return;
					}
					JsonDeviceTemplate jdtemplate = dtDao
							.toJsonDeviceTemplate(devicetemplate);
					if (jdtemplate == null
							|| jdtemplate.getUnits_list().isEmpty()) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.DeviceErrorDictionary.DeviceTemplateParsingError,
										"", null);
						return;
					}
					jDatasteram.setUnits_list(jdtemplate.getUnits_list());
				}
				if (jDatasteram.getUnits_list().isEmpty()) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.MISSING_DATA,
							"units_list", null);
					return;
				}
			} catch (JsonSyntaxException ex) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				ex.printStackTrace();
				return;
			}
			Datastream datastream = new Datastream();
			datastream.setSubId(subject.getId());
			UUID streamUUID = UUID.randomUUID();
			datastream.setStreamId(streamUUID.toString());
			Date now = new Date();
			datastream.setCreatedTime(now);
			datastream.setOwner(subject.getLoginID());
			datastream.setTitle(jDatasteram.getTitle());
			ArrayList<DatastreamUnits> datastreamUnits = new ArrayList<DatastreamUnits>();
			for (JsonDatastreamUnits unit : jDatasteram.getUnits_list()) {
				if (unit.getValue_type() == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.MISSING_DATA,
							"unit_value_type", null);
					return;
				}
				if (!UnitValueTypes.existValueType(unit.getValue_type())) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_ValueType,
							AllConstants.ErrorDictionary.Invalid_ValueType,
							unit.getValue_type());
					return;
				}
				DatastreamUnits dsUnit = new DatastreamUnits();
				dsUnit.setStreamID(datastream);
				dsUnit.setCreatedTime(new Date());
				dsUnit.setUpdatedTime(new Date());
				dsUnit.setMaxValue(unit.getMax_value());
				dsUnit.setMinValue(unit.getMin_value());
				dsUnit.setCurrentValue(unit.getCurrent_value());
				dsUnit.setUnitLabel(unit.getUnit_label());
				dsUnit.setValueType(unit.getValue_type());
				dsUnit.setUnitSymbol(unit.getUnit_symbol());
				dsUnit.setUnitID(UUID.randomUUID().toString());
				datastreamUnits.add(dsUnit);
			}
			datastream.setDatastreamUnitsList(datastreamUnits);
			DatastreamDAO dstreamDao = new DatastreamDAO();
			datastream = dstreamDao.createDatastream(datastream,
					datastreamUnits);
			DBtoJsonUtil dbjUtil = new DBtoJsonUtil();
			try {
				JsonDatastream jobject = dbjUtil.convertDatastream(datastream,
						null);
				JsonElement je = gson.toJsonTree(jobject);
				JsonObject jo = new JsonObject();
				jo.addProperty(AllConstants.ProgramConts.result,
						AllConstants.ProgramConts.succeed);
				jo.add("datastream", je);
				JsonWriter jwriter = new JsonWriter(out);
				gson.toJson(jo, jwriter);
				System.out.println(gson.toJson(jobject));
			} catch (ParseException ex) {
				ex.printStackTrace();
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (NumberFormatException ex) {
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
