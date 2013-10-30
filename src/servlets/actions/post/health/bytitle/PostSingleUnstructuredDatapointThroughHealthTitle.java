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
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataPointsPostResult;
import health.input.jsonmodels.JsonDataValues;
import health.input.util.DBtoJsonUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Leon
 */
public class PostSingleUnstructuredDatapointThroughHealthTitle extends
		HttpServlet {

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
	private String unstrcturedDataFolderString="/var/www/html/wikihealth/unstructured/";
	private File unstructuredDataFolder=new File(unstrcturedDataFolderString); 

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
			String unitid = ServerUtil
					.getHealthDataUnitIDfromURL(ServletPath(request));
			System.out.println("unitid::::" + unitid);

			String at = request
					.getParameter(AllConstants.api_entryPoints.request_api_at);
			if (at == null || at.length() < 1) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						AllConstants.api_entryPoints.request_api_at);
				return;
			}
			try{
				long at_long_test=Long.parseLong(at);
				Date atDateTest=new Date();
				atDateTest.setTime(at_long_test);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_date_format, null,
						AllConstants.api_entryPoints.request_api_at);
				return;
			}
//			String unstrcturedDataFolderString="/var/www/html/wikihealth/unstructured";
//			File unstructuredDataFolder=new File(unstrcturedDataFolderString);
//			if(!unstructuredDataFolder.exists())
//			{
//				ReturnParser.outputErrorException(response,
//						AllConstants.ErrorDictionary.Internal_Fault, null,
//						"unstrctured data folder not exist");
//				return;
//			}
	        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
//	        File filesDir = (File) getServletContext().getAttribute("FILES_DIR_FILE");
	        fileFactory.setRepository(unstructuredDataFolder);
	        ServletFileUpload uploader = new ServletFileUpload(fileFactory);
	        String fileURL=null;
			try {
	            List<FileItem> fileItemsList = uploader.parseRequest(request);
	            Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
	            while(fileItemsIterator.hasNext()){
	                FileItem fileItem = fileItemsIterator.next();
	                System.out.println("FieldName="+fileItem.getFieldName());
	                System.out.println("FileName="+fileItem.getName());
	                System.out.println("ContentType="+fileItem.getContentType());
	                System.out.println("Size in bytes="+fileItem.getSize());
	                String fileExtension = "";
	                int i = fileItem.getName().lastIndexOf('.');
	                if (i > 0) {
	                	fileExtension = fileItem.getName().substring(i+1);
	                }
	                UUID file_uuid=UUID.randomUUID();
	                String newFileName=file_uuid+"."+fileExtension;
	                File file = new File(unstrcturedDataFolderString+newFileName);
	                System.out.println("Absolute Path at server="+file.getAbsolutePath());
	                fileItem.write(file);
	                fileURL="/"+newFileName;
	            }
	      
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null,
						null);
				return;
	        }
			String timetag = null;
			timetag = request
					.getParameter(AllConstants.api_entryPoints.request_api_timetag);// not
																					// in
																					// use

			List<DatastreamUnits> unitList = datastream
					.getDatastreamUnitsList();
			System.out.println("unitList.size():" + unitList.size());
			HashMap<String, String> unitIDList = new HashMap<String, String>();
			for (DatastreamUnits unit : unitList) {
				if (unit.getShortUnitID() != null
						&& unit.getShortUnitID().length() > 1) {
					unitIDList
							.put(unit.getShortUnitID(), unit.getShortUnitID());
				} else {
					unitIDList.put(unit.getUnitID(), unit.getUnitID()); // retrieve
				} // all

				// System.out.println("unit.getUnitID():"+unit.getUnitID());
			}
			if (!unitIDList.containsKey(unitid)) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_Unit_ID, null,
						null);
				return;
			}
			JsonUtil jutil = new JsonUtil();
			Gson gson = new Gson();
			JsonDataImport jdataImport = new JsonDataImport();

			int jsonInputTotalByte = 0;
			List<JsonDataPoints> jsonDataPointsList = new ArrayList<JsonDataPoints>();
			List<JsonDataValues> value_list = new ArrayList<JsonDataValues>();
			JsonDataValues singleJsonDataValue = new JsonDataValues();
			singleJsonDataValue.setUnit_id(unitid);
			fileURL="http://wikihealth.bigdatapro.org/unstructured"+fileURL;
			singleJsonDataValue.setVal(fileURL);
			JsonDataPoints singleJsonDatapoint = new JsonDataPoints();
			singleJsonDatapoint.setAt(at);
			value_list.add(singleJsonDataValue);
			if (timetag != null) {
				singleJsonDatapoint.setTimetag(timetag);
			}
			singleJsonDatapoint.setValue_list(value_list);
			// if(timetag!=null){
			// singleJsonDatapoint.setTimetag(timetag); //maybe enable for
			// future
			// }
			jsonDataPointsList.add(singleJsonDatapoint);
			jdataImport.setData_points(jsonDataPointsList);

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
				if (datastream.getDatastreamUnitsList().size() == 0) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Unknown_StreamID,
							null, datastream.getStreamId());
					return;
				}
				if (datastream.getDatastreamUnitsList().size() > 1) {
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
				importData.setSingle_Unit_ID(unitList.get(0).getUnitID());
				importData.setData_points_single_list(jdataImport
						.getData_points_single_list());
				importData.setDatastream_id(datastream.getStreamId());
				importData.setBlock_id(jdataImport.getBlock_id());
			} else {
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
			jsonResult.setTotal_points(jdataImport.getData_points().size());
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
