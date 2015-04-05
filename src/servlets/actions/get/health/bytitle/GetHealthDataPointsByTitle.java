/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.health.bytitle;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DataPermissionDAO;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.HealthDataStreamDAO;
import health.database.DAO.SleepDataDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.DAO.nosql.DataPointsSimulators;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.DataPermission;
import health.database.models.Datastream;
import health.database.models.SleepDataSummary;
import health.database.models.Subject;
import health.database.models.Users;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;
import health.input.jsonmodels.JsonDatastreamUnits;
import health.input.util.DBtoJsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

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
import util.DateUtil;
import util.ServerConfigUtil;
import util.UnitValueTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Leon
 */
public class GetHealthDataPointsByTitle extends HttpServlet {

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
		// PrintWriter out = response.getWriter();
		OutputStream outStream = null;
		try {

			long start = 0;
			long end = 0;
			String blockid = null;

			try {
				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_start) != null) {
					start = Long
							.parseLong(request
									.getParameter(AllConstants.api_entryPoints.request_api_start));
				}
				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_end) != null) {
					end = Long
							.parseLong(request
									.getParameter(AllConstants.api_entryPoints.request_api_end));
				}

				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay) != null) {
					String yearMonthDateString = request
							.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay);
					System.out.println("Date Request " + yearMonthDateString);
					DateUtil dateUtil = new DateUtil();
					Date date = dateUtil.convert(yearMonthDateString,
							dateUtil.YearMonthDay_DateFormat);
					System.out.println("DateRequest:" + date);
					Calendar calStart = Calendar.getInstance(DateUtil.UTC);
					Calendar calEnd = Calendar.getInstance(DateUtil.UTC);
					calStart.setTime(date);
					calEnd.setTime(date);
					calStart.set(Calendar.HOUR_OF_DAY, 0);
					calStart.set(Calendar.MINUTE, 0);
					start = calStart.getTimeInMillis();
					calEnd.set(Calendar.HOUR_OF_DAY, 23);
					calEnd.set(Calendar.MINUTE, 59);
					end = calEnd.getTimeInMillis();
					// System.out.println("Date Request start"+calStart.getTime());
					// System.out.println("Date Request end"+calEnd.getTime());
					// System.out.println("using Date Request:"+start+" "+end);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_date_format, null,
						null);
				return;
			}
			try {
				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_blockid) != null) {

					if (request.getParameter(
							AllConstants.api_entryPoints.request_api_blockid)
							.length() > 5) {
						blockid = request
								.getParameter(AllConstants.api_entryPoints.request_api_blockid);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_Datablock_ID,
						null, null);
				return;
			}

			SubjectDAO subjDao = new SubjectDAO();
			Subject subject = (Subject) subjDao
					.findHealthSubject(targetLoginID); // Retreive
			if (subject == null) {

				try {
					subject = subjDao.createDefaultHealthSubject(targetLoginID);
					HealthDataStreamDAO hdsDao = new HealthDataStreamDAO();

					hdsDao.createDefaultDatastreamsOnDefaultSubject(
							targetLoginID, subject.getId());
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
			if (!datastream.getOwner().equalsIgnoreCase(targetLoginID)) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unauthorized_Access, null,
						streamTitle);
				return;
			}
			if (loginID != null && targetLoginID != null
					&& !loginID.equals(targetLoginID)) {
				DataPermissionDAO permissionDao = new DataPermissionDAO();
				List<DataPermission> permissionList = permissionDao
						.getDataPermission(
								targetLoginID,
								loginID,
								AllConstants.ProgramConts.data_permission_type_datastream,
								datastream.getStreamId(),
								AllConstants.ProgramConts.VALID);
				if (permissionList.size() > 0) {

				} else {
					DataPermission permission = new DataPermission();
					permission.setGivenLoginid(loginID);
					permission
							.setTargetDataType(AllConstants.ProgramConts.data_permission_type_datastream);
					permission.setTargetDataId(datastream.getStreamId());
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
			if (blockid != null
					&& dstreamDao.getDatastreamBlock(blockid) == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_Datablock_ID,
						null, blockid);
				return;
			}
			HashMap<String, String> mapUnits = new HashMap<String, String>();
			HashMap<String, String> allUnits = new HashMap<String, String>();
			if (request
					.getParameter(AllConstants.api_entryPoints.request_api_unit_id) != null
					&& request.getParameter(
							AllConstants.api_entryPoints.request_api_unit_id)
							.length() > 0) {
				String[] unitids = request.getParameter(
						AllConstants.api_entryPoints.request_api_unit_id)
						.split(",");
				System.out.println("unitids:size:" + unitids.length);
				allUnits = dbtoJUtil.ToDatastreamUnitsMap(datastream);
				System.out.println("units size:"
						+ datastream.getDatastreamUnitsList().size());
				for (String id : unitids) {
					if (id.length() < 3) {
						// error
						ReturnParser.outputErrorException(response,
								AllConstants.ErrorDictionary.Invalid_Unit_ID,
								null, null);
						return;
					} else {
						if (allUnits.get(id) == null) {
							// error
							System.out.println("cannot find id" + id + "");
							return;
						} else {
							mapUnits.put(id, id);
						}
					}
				}
			} else {
				mapUnits = dbtoJUtil.ToDatastreamUnitsMap(datastream);
			}
			if (mapUnits.size() == 0) {
				ReturnParser
						.outputErrorException(
								response,
								AllConstants.ErrorDictionary.invalid_unitid_or_request_unitid_not_exist,
								null, null);
				return;
			}
			System.out.println("mapUnits.size():" + mapUnits.size() + ", "
					+ mapUnits);
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();;
			System.out.println("debuging.....going to hbase");
			HBaseDatapointDAO diDao = null;
			System.out.println("datastreamID:" + datastream.getStreamId());
			HBaseDataImport hbaseexport = null;
			try {
				if (streamTitle
						.equalsIgnoreCase(AllConstants.ProgramConts.defaultDS_Name_sleep)) {
					// sleep record
					if (request
							.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay) == null) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.Invalid_date_format,
										null, null);
						return;
					}
					DateUtil dateUtil = new DateUtil();
					String yearMonthDateString = request
							.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay);
					Date date = dateUtil.convert(yearMonthDateString,
							dateUtil.YearMonthDay_DateFormat);

					SleepDataDAO sleepdataDao = new SleepDataDAO();
					List<SleepDataSummary> sleepSummaryList = sleepdataDao
							.getSleepDataSummaries(datastream.getStreamId(),
									null, date);
					if (sleepSummaryList.size() < 1) {
						ReturnParser.outputErrorException(response,
								AllConstants.ErrorDictionary.NO_SLEEP_RECORD,
								null, datastream.getStreamId());
						return;
					}
					start = sleepSummaryList.get(0).getStartTime().getTime();
					end = sleepSummaryList.get(0).getEndtime().getTime();
					for (SleepDataSummary summary : sleepSummaryList) {
						if (start > summary.getStartTime().getTime()) {
							start = summary.getStartTime().getTime();
						}
						if (end < summary.getEndtime().getTime()) {
							end = summary.getEndtime().getTime();
						}
					}
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
					mapUnits = new HashMap<String, String>();
					mapUnits.put(datastream.getDatastreamUnitsList().get(0)
							.getUnitID(), datastream.getDatastreamUnitsList()
							.get(0).getUnitID());
					diDao = new HBaseDatapointDAO();
					hbaseexport = diDao.exportDatapointsForSingleUnit(
							datastream.getStreamId(), start, end, blockid,
							datastream.getDatastreamUnitsList().get(0)
									.getUnitID(), null, null);

				} else {

					if (request
							.getParameter(AllConstants.api_entryPoints.request_api_dataformat) != null) {
						DateUtil dateUtil = new DateUtil();
						diDao = new HBaseDatapointDAO();
						boolean useSingleUnitExport = false;

						if (request
								.getParameter(AllConstants.api_entryPoints.request_api_single_unit) != null
								&& request
										.getParameter(
												AllConstants.api_entryPoints.request_api_single_unit)
										.length() > 0) {

							useSingleUnitExport = true;
						}

						if (useSingleUnitExport) {
							String shortUnitID = datastream
									.getDatastreamUnitsList().get(0)
									.getShortUnitID();
							if (shortUnitID == null) {
								shortUnitID = datastream
										.getDatastreamUnitsList().get(0)
										.getUnitID();
							}
							System.out
									.println("--------using single unit export with request_api_dataformat-----:"
											+ shortUnitID);
							hbaseexport = diDao.exportDatapointsForSingleUnit(
									datastream.getStreamId(), start, end,
									blockid, shortUnitID, null, null);
						} else {
							System.out
									.println("--------normal data export with request_api_dataformat-----");
							System.out.println("------Debug-line426--Retriving Data:"+datastream.getStreamId()+",start:"+start+",end:"+end);
							hbaseexport = diDao.exportDatapoints(
									datastream.getStreamId(), start, end,
									blockid, mapUnits, dateUtil.millisecFormat,
									null);
						}

					} else {
						HashMap<String, Object> settings = new HashMap<String, Object>();
						if (request
								.getParameter(AllConstants.api_entryPoints.request_max) != null) {
							try {
								int max = Integer
										.parseInt(request
												.getParameter(AllConstants.api_entryPoints.request_max));
								settings.put(
										AllConstants.ProgramConts.exportSetting_MAX,
										max);
							} catch (Exception ex) {
								ReturnParser
										.outputErrorException(
												response,
												AllConstants.ErrorDictionary.Invalid_ValueType,
												null,
												AllConstants.api_entryPoints.request_max);
								return;
							}
						}
						if (streamTitle
								.equalsIgnoreCase(AllConstants.ProgramConts.defaultDS_Name_heart_rate)) {
							DataPointsSimulators simulator = new DataPointsSimulators();
							System.out.println("------Debug--Simulator Retriving Data:"+datastream.getStreamId()+",start:"+start+",end:"+end);
							hbaseexport = simulator.exportHeartRateDatapoints(
									datastream.getStreamId(), start, end,
									blockid, mapUnits, null, settings);
						} else {
							// normal data retrieval comes from here
							diDao = new HBaseDatapointDAO();
							boolean useSingleUnitExport = false;
							if (request
									.getParameter(AllConstants.api_entryPoints.request_api_single_unit) != null
									&& request
											.getParameter(
													AllConstants.api_entryPoints.request_api_single_unit)
											.length() > 0) {

								useSingleUnitExport = true;
							}

							if (useSingleUnitExport) {
								String shortUnitID = datastream
										.getDatastreamUnitsList().get(0)
										.getShortUnitID();
								if (shortUnitID == null) {
									shortUnitID = datastream
											.getDatastreamUnitsList().get(0)
											.getUnitID();
								}
								System.out
										.println("--------using single unit export-----:"
												+ shortUnitID);
								hbaseexport = diDao
										.exportDatapointsForSingleUnit(
												datastream.getStreamId(),
												start, end, blockid,
												shortUnitID, null, null);
							} else {
								System.out
										.println("--------normal data export-----");
								System.out.println("------Debug--Retriving Data:"+datastream.getStreamId()+",start:"+start+",end:"+end);
								
								hbaseexport = diDao.exportDatapoints(
										datastream.getStreamId(), start, end,
										blockid, mapUnits, null, settings);
							}
						}

					}
				}
			} catch (ErrorCodeException ex) {
				ex.printStackTrace();
				ReturnParser
						.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, null);
				return;
			} catch (Throwable ex) {
				ex.printStackTrace();
				ReturnParser
						.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, null);
				return;
			}
			if (hbaseexport != null) {
				hbaseexport.setUnits_list(dbtoJUtil.convertDatastream(
						datastream, mapUnits).getUnits_list());
			} else {
				hbaseexport = new HBaseDataImport();
				hbaseexport.setBlock_id(blockid);
				hbaseexport.setData_points(new ArrayList<JsonDataPoints>());
				hbaseexport.setStream_title(datastream.getTitle());
				// hbaseexport.setDatastream_id(datastream.getStreamId());
				hbaseexport.setUnits_list(dbtoJUtil.convertDatastream(
						datastream, mapUnits).getUnits_list());
				// hbaseexport.setDeviceid(streamID);
			}
			outStream = null;
			boolean iftoZip = true;
			GZIPOutputStream gzipStream = null;
			if (request.getParameter("nocompress") != null) {
				iftoZip = false;
			}
			String encodings = request.getHeader("Accept-Encoding");
			if (encodings != null && encodings.indexOf("gzip") >= 0
					&& iftoZip == true) {
				// Go with GZIP
				System.out
						.println("---------------responding with GZIP data--------------");
				response.setHeader("Content-Encoding", "gzip");
				gzipStream = new GZIPOutputStream(response.getOutputStream());
				// outStream = new
				// GZIPOutputStream(response.getOutputStream());
			} else {
				response.setContentType("application/json");
				outStream = response.getOutputStream();
			}
			// response.setHeader("Vary", "Accept-Encoding");
			Date timerStart = new Date();
			hbaseexport.setStream_title(datastream.getTitle());
			hbaseexport.setDatastream_id(datastream.getStreamId());
			hbaseexport.setDevice_id(null);

			int totalData_pointsSize = 0;

			if (hbaseexport.getData_points() != null) {
				// for file data
				totalData_pointsSize = hbaseexport.getData_points().size();
				HashMap<String, String> fileUnits = new HashMap<>();
				for (JsonDatastreamUnits unit : hbaseexport.getUnits_list()) {
					if (unit.getValue_type().equalsIgnoreCase(
							UnitValueTypes.FILE_TYPE)) {
						fileUnits.put(unit.getUnit_id(), unit.getUnit_id());
					}
				}
				if (fileUnits.size() > 0) {
					System.out.println("exist file type.........");
					String filebaseURL = ServerConfigUtil
							.getConfigValue(AllConstants.ServerConfigs.fileGetBaseURL);
					String objectKeyPrefix = filebaseURL + "title" + "/"
							+ datastream.getTitle() + "/"
							+ AllConstants.api_entryPoints.api_files;
					List<JsonDataPoints> jdatapointsList = hbaseexport
							.getData_points();
					for (int i = 0; i < jdatapointsList.size(); i++) {
						for (int j = 0; j < jdatapointsList.get(i)
								.getValue_list().size(); j++) {
							if (fileUnits.containsKey(jdatapointsList.get(i)
									.getValue_list().get(j).getUnit_id())) {
								System.out.println("exist unit:......"
										+ jdatapointsList.get(i)
												.getValue_list().get(j)
												.getUnit_id());
								List<JsonDataValues> jValueList = jdatapointsList
										.get(i).getValue_list();
								JsonDataValues jValue = jValueList.get(j);
								System.out.println(jdatapointsList.get(i).getAt());
								jValue.setLink(objectKeyPrefix
										+ "?"
										+ AllConstants.api_entryPoints.request_api_filekey
										+ "="
										+ jdatapointsList.get(i).getAt()
										+ "/"
										+ jdatapointsList.get(i)
												.getValue_list().get(j)
												.getUnit_id() + "/"
										+ jValue.getVal());
								jValue.setFilekey(jdatapointsList.get(i).getAt()
										+ "/"
										+ jdatapointsList.get(i)
												.getValue_list().get(j)
												.getUnit_id() + "/"
										+ jValue.getVal());
								jValueList.set(j, jValue);
								JsonDataPoints jp = jdatapointsList.get(i);
								jp.setValue_list(jValueList);
								jdatapointsList.set(i, jp);
							}
						}
					}
					hbaseexport.setData_points(jdatapointsList);
				}

			} else if (hbaseexport.getData_points_single_list() != null) {
				totalData_pointsSize = hbaseexport.getData_points_single_list()
						.size();
			}
			JsonElement je = gson.toJsonTree(hbaseexport);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.addProperty(AllConstants.api_entryPoints.request_api_targetid,
					targetLoginID);
			jo.addProperty(AllConstants.ProgramConts.total_points,
					totalData_pointsSize);
			jo.add("datapoints_list", je);
			OutputStreamWriter osWriter = null;
			if (gzipStream != null) {
				osWriter = new OutputStreamWriter(gzipStream);
			} else {
				if (outStream == null) {
					outStream = response.getOutputStream();
				}
				osWriter = new OutputStreamWriter(outStream);
			}
			JsonWriter jwriter = new JsonWriter(osWriter);
			String callbackStr = null;
			if (request
					.getParameter(AllConstants.api_entryPoints.requset_api_callback) != null) {
				callbackStr = request
						.getParameter(AllConstants.api_entryPoints.requset_api_callback);
				osWriter.append(callbackStr + "(");
			}
			gson.toJson(jo, jwriter);
			if (callbackStr != null) {
				osWriter.append(");");
			}
			jwriter.close();
			Date timerEnd = new Date();
			System.out.println("Json Time takes:"
					+ (timerEnd.getTime() - timerStart.getTime()) / (1000.00)
					+ "seconds");
			osWriter.close();
			if (gzipStream != null) {
				gzipStream.close();
			} else {
				outStream.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;
		} finally {
			System.out.println("running finally");
			// out.close();
			if (outStream != null) {
				outStream.close();
			}
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
