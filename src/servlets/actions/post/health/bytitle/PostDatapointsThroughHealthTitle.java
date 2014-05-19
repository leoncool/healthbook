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
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamBlocks;
import health.database.models.DatastreamUnits;
import health.database.models.Subject;
import health.database.models.Users;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataPointsPostResult;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Leon
 */
public class PostDatapointsThroughHealthTitle extends HttpServlet {

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
		PrintWriter out = response.getWriter();
		try {

			Users accessUser = null;
			PermissionFilter filter = new PermissionFilter();
			String loginID = filter
					.checkAndGetLoginFromToken(request, response);

			UserDAO userDao = new UserDAO();
			if (loginID == null) {
				if (filter.getCheckResult().equalsIgnoreCase(
						filter.INVALID_LOGIN_TOKEN_ID)) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_login_token_id,
									null, null);
					return;
				} else if (filter.getCheckResult().equalsIgnoreCase(
						AllConstants.ErrorDictionary.login_token_expired)) {
					return;
				} else {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_login_token_id,
									null, null);
					return;
				}
			} else {
				accessUser = userDao.getLogin(loginID);
			}
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
			if (!datastream.getOwner().equalsIgnoreCase(loginID)) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unauthorized_Access, null,
						streamTitle);
				return;
			}

			JsonUtil jutil = new JsonUtil();
			Gson gson = new Gson();
			JsonDataImport jdataImport = null;
			int jsonInputTotalByte = 0;
			try {
				String jsonInput = jutil.readJsonStrFromHttpRequest(request);
				jsonInputTotalByte = jsonInput.length();
				jdataImport = gson.fromJson(jsonInput, JsonDataImport.class);
			} catch (JsonSyntaxException ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			} catch (IOException ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}

			List<DatastreamUnits> unitList = datastream
					.getDatastreamUnitsList();
			HashMap<String, String> unitIDList = new HashMap<String, String>();
			for (DatastreamUnits unit : unitList) {
				if(unit.getShortUnitID() != null
						&& unit.getShortUnitID().length() > 1)
				{
					unitIDList.put(unit.getShortUnitID(), unit.getShortUnitID()); 
				}
				else{
				unitIDList.put(unit.getUnitID(), unit.getUnitID()); // retrieve
				}												// all
																	// existing
																	// units
			}

			if (jdataImport == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						null, null);
				return;
			}
			if (jdataImport.getBlock_id() != null) {
				DatastreamBlocks block = dstreamDao
						.getDatastreamBlock(jdataImport.getBlock_id());
				if (block == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_Datablock_ID,
							jdataImport.getBlock_id(),
							jdataImport.getBlock_id());
					return;
				} else {
					if (!block.getStreamID().getStreamId()
							.equalsIgnoreCase(datastream.getStreamId())) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.Invalid_Datablock_ID_for_such_stream,
										jdataImport.getBlock_id(),
										jdataImport.getBlock_id());
						return;
					}
				}
			}

			HBaseDatapointDAO importDao = null;
			try {
				importDao = new HBaseDatapointDAO();
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser
						.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, null);
				return;
			}

			HBaseDataImport importData = new HBaseDataImport();

			if (jdataImport.getData_points_single_list() != null
					&& jdataImport.getData_points_single_list().size() > 0) {
				System.out.println("--------------dealing with single data point list--------");
				if (unitIDList.size() == 0) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Unknown_StreamID,
							null, datastream.getStreamId());
					return;
				}
				if (unitIDList.size() > 1) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.MORE_THAN_ONE_DATASTREAM_UNIT,
									null, datastream.getStreamId());
					return;
				}
				if (jdataImport.getData_points_single_list().size() < 1) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.No_Input_Single_Datapoints,
									null, datastream.getStreamId());
					return;
				}
				String singleUnitID=datastream.getDatastreamUnitsList().get(0).getShortUnitID();
				if(singleUnitID==null||singleUnitID.length()<3){
					singleUnitID=datastream.getDatastreamUnitsList().get(0).getUnitID();
				}
				importData.setSingle_Unit_ID(singleUnitID);
				importData.setData_points_single_list(jdataImport
						.getData_points_single_list());
				importData.setDatastream_id(datastream.getStreamId());
				importData.setBlock_id(jdataImport.getBlock_id());
			} else {
				System.out.println("--------------dealing with normal data point list--------");
				if (jdataImport.getData_points() == null
						|| jdataImport.getData_points().size() < 1) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.No_Input_Datapoints,
							null, datastream.getStreamId());
					return;
				}
				importData.setDatastream(dbtoJUtil.convertDatastream(
						datastream, null));
				importData.setData_points(jdataImport.getData_points());
				importData.setDatastream_id(datastream.getStreamId());
				importData.setBlock_id(jdataImport.getBlock_id());
			}

			JsonDataPointsPostResult jsonResult = new JsonDataPointsPostResult();
			try {
				int totalStoredByte = importDao
						.importDatapointsDatapoints(importData); //submit data
				jsonResult.setTotal_stored_byte(totalStoredByte);

			} catch (ErrorCodeException ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response, ex.getErrorCode(),
						null, null);
				return;
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.INPUT_DATE_FORMAT_ERROR,
						null, null);
				return;
			}
			int totalInputSize=0;
			if(jdataImport.getData_points()!=null)
			{
				totalInputSize=jdataImport.getData_points().size();
			}else if (jdataImport.getData_points_single_list()!=null){
				totalInputSize=jdataImport.getData_points_single_list().size();
			}
			jsonResult.setTotal_points(totalInputSize);
			jsonResult.setTotal_input_byte(jsonInputTotalByte);
			JsonElement je = gson.toJsonTree(jsonResult);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("data_import_stat", je);
			JsonWriter jwriter = new JsonWriter(out);
			gson.toJson(jo, jwriter);
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
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
