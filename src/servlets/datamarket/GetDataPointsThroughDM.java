package servlets.datamarket;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.UserDAO;
import health.database.DAO.datamarket.DataMarketDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.datamarket.DataMarket;
import health.database.datamarket.DataSharing;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.Users;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;
import health.input.jsonmodels.JsonDatastreamUnits;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.NonUniqueResultException;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.AllConstants;
import util.DateUtil;
import util.MarketplaceContants;
import util.ServerConfigUtil;
import util.UnitValueTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetDataPointsThroughDM")
public class GetDataPointsThroughDM extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetDataPointsThroughDM() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		System.out.println("GetDataPointsThroughDM....");
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
		System.out.println("----GetDataPointsThroughDM------loginID:"+loginID);
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


			String streamID=request.getParameter(MarketplaceContants.RequestParameters.streamID);
			if(streamID==null)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						MarketplaceContants.RequestParameters.streamID);
				return;
			}

			DatastreamDAO dstreamDao = new DatastreamDAO();
			DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
			Datastream datastream = null;
			try {
				datastream = dstreamDao.getDatastream(streamID, true, false);

			} catch (NonUniqueResultException ex) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null,
						MarketplaceContants.RequestParameters.streamID);
				return;
			}
			if (datastream == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unknown_StreamID, null,
						MarketplaceContants.RequestParameters.streamID);
				return;
			}
			
			DataMarketDAO dmDao=new DataMarketDAO();
			System.out.println("checking permission:"+loginID+","+datastream.getOwner()+","+datastream.getStreamId());
			List<DataSharing> datasharingList=dmDao.getDataSharingListByStreamID(loginID,datastream.getOwner(),datastream.getStreamId());
			if(datasharingList==null||datasharingList.size()==0)
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.PERMISSION_DENIED,
						null, "not allowed to access");
				return;
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
										+datastream.getOwner()+"/"
										+datastream.getStreamId()+"/"
										+ jdatapointsList.get(i).getAt()
										+ "/"
										+ jdatapointsList.get(i)
												.getValue_list().get(j)
												.getUnit_id() + "/"
										+ jValue.getVal());
								jValue.setFilekey(datastream.getOwner()+"/"
										+datastream.getStreamId()+"/"+jdatapointsList.get(i).getAt()
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}
	
	
	public static void main(String args[]) {
		Gson gson = new Gson();
		DataMarketDAO dmDao=new DataMarketDAO();
		String searchName = null;
		List<DataMarket> dmListRaw=dmDao.getDataMarketListing(searchName);
		List<DataMarket> dmList=new ArrayList<DataMarket>();
		DatastreamDAO dsDao=new DatastreamDAO();
		for(DataMarket dm:dmListRaw)
		{
			Datastream stream=dm.getDatastream();
			stream.setDatastreamBlocksList(null);
			String streamID=stream.getStreamId();
			List<DatastreamUnits> unitList=dsDao.getDatastreamUnits(streamID);
			dm.setDatastreamUnitsList(unitList);
			stream.setDatastreamBlocksList(null);
			stream.setDatastreamUnitsList(null);
			dm.setDatastream(stream);
			dmList.add(dm);	
		}
		
//		
		JsonObject jo = new JsonObject();
		jo.addProperty(AllConstants.ProgramConts.result,
				AllConstants.ProgramConts.succeed);
		JsonElement jelement=gson.toJsonTree(dmListRaw);
		jo.add("data_market_list",jelement );
		System.out.println(gson.toJson(jo));
//		System.out.println(dmListRaw.get(0).getStreamID().getDatastreamUnitsList().size());
//		out.println(gson.toJson(jo));

		// for (int i = 0; i < m.groupCount(); i++)
		// System.out.println("Group" + i + ": " + m.group(i));
	}

}
