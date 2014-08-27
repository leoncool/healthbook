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
import health.database.models.DatastreamUnits;
import health.database.models.Subject;
import health.database.models.Users;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataPointsPostResult;
import health.input.jsonmodels.JsonDataValues;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.JsonUtil;
import util.ServerConfigUtil;
import cloudstorage.cacss.S3Engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Leon
 */
public class PostFileThroughHealthTitle extends HttpServlet {
	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}

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

			List<DatastreamUnits> unitsList = datastream
					.getDatastreamUnitsList();
			DatastreamUnits targetUnit = null;
			String unitRequest = request
					.getParameter(AllConstants.api_entryPoints.request_api_unit_id);
			String at = null;
			String aString = request
					.getParameter(AllConstants.api_entryPoints.request_api_at);
			if (unitRequest == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						AllConstants.api_entryPoints.request_api_unit_id);
				return;
			}
			if (aString == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						AllConstants.api_entryPoints.request_api_at);
				return;
			}
			try {
				long checkDateLong = Long.parseLong(aString);
				Date checkDate = new Date();
				checkDate.setTime(checkDateLong);
				at = Long.toString(checkDate.getTime());
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_data_format, null,
						AllConstants.api_entryPoints.request_api_at);
				return;
			}
			for (DatastreamUnits unit : unitsList) {
				if (unit.getUnitID().equalsIgnoreCase(unitRequest)
						|| unit.getShortUnitID().equalsIgnoreCase(unitRequest)) {
					targetUnit = unit;
				}
			}
			if (targetUnit == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						AllConstants.api_entryPoints.request_api_unit_id);
				return;
			}
			String fileName = null;
			String previousFileName = null;
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

			HBaseDataImport hbaseexport = importDao
					.exportDatapointsForSingleUnit(datastream.getStreamId(),
							Long.parseLong(at), Long.parseLong(at), null,
							unitRequest, null);

			if (hbaseexport.getData_points_single_list().size() > 0) {
				if (!hbaseexport.getData_points_single_list().get(0).getAt()
						.equalsIgnoreCase(at)) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Internal_Fault, null,
							null);
					return;
				}
				previousFileName = hbaseexport.getData_points_single_list()
						.get(0).getVal();// fetch previousFileName for remove
											// old files
			}
			String bucketName = ServerConfigUtil
					.getConfigValue(AllConstants.ServerConfigs.CloudStorageBucket);
			String objectPrefix=loginID + "/" + datastream.getStreamId()
					+ "/" + at + "/" + unitRequest + "/" ;
			
			

			boolean fileUploaded = false;
			List<FileItem> items = new ServletFileUpload(
					new DiskFileItemFactory()).parseRequest(request);
			for (FileItem item : items) {
				if (item.isFormField()) {

				} else {
					// Process form file field (input type="file").
					// fileName = item.getFieldName();
					fileName = FilenameUtils.getName(item.getName());
					InputStream inputstream = item.getInputStream();
					Gson gson = new Gson();
					Hashtable<String, Object> paramters = new Hashtable<String, Object>();
					if (item.getContentType() != null) {
						paramters.put("Content-Type", item.getContentType());
					} else {
						paramters.put("Content-Type",
								"application/octet-stream");
					}

					try {
						String newObjectName = objectPrefix + fileName;
						Hashtable<String, Object> returnValues = (Hashtable<String, Object>) S3Engine.s3
								.PutObject("leoncool", bucketName,
										newObjectName, (long) item.getSize(),
										inputstream, 3, paramters, null);
					} catch (Exception ex) {
						ex.printStackTrace();
						ReturnParser.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, "file upload to cloud storage failed");
						inputstream.close();
						return;
					}
					fileUploaded = true;
					inputstream.close();
				}
			}
			if (fileUploaded == false) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						"missing file for uploading");
				return;
			}

			HBaseDataImport importData = new HBaseDataImport();
			List<JsonDataValues> jvalueList = new ArrayList<>();
			List<JsonDataPoints> jdatapointsList = new ArrayList<>();

			JsonDataImport jdataImport = new JsonDataImport();
			JsonDataPoints jdataPoint = new JsonDataPoints();
			JsonDataValues jvalue = new JsonDataValues();
			System.out.println("fileName:" + fileName);
			jvalue.setVal(fileName);
			jvalue.setUnit_id(unitRequest);
			jvalueList.add(jvalue);
			jdataPoint.setAt(at);
			jdataPoint.setValue_list(jvalueList);
			jdatapointsList.add(jdataPoint);
			jdataImport.setData_points(jdatapointsList);
			importData.setDatastream(dbtoJUtil.convertDatastream(datastream,
					null));
			importData.setData_points(jdataImport.getData_points());
			importData.setDatastream_id(datastream.getStreamId());

			JsonDataPointsPostResult jsonResult = new JsonDataPointsPostResult();
			try {
				int totalStoredByte = importDao
						.importDatapointsDatapoints(importData); // submit data
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
			if (previousFileName != null
					&& !previousFileName.equalsIgnoreCase(fileName)) {
				String oldObjectName = objectPrefix + previousFileName;
				S3Engine.s3.DeleteObject(bucketName, "leoncool", oldObjectName,
						null);
			}
			int totalInputSize = 1;
			Gson gson = new Gson();
			jsonResult.setTotal_points(totalInputSize);
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
