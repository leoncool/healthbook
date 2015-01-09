package servlets.analysis.service;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.as.AnalysisServiceDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisResult;
import health.database.models.as.AnalysisService;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.util.DBtoJsonUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.ServerUtil;
import util.AScontants;
import util.AllConstants;
import util.AllConstants.ServerConfigs;
import util.ServerConfigUtil;

import com.analysis.service.ASInput;
import com.analysis.service.ASOutput;
import com.analysis.service.AnalysisWrapperUtil;
import com.analysis.service.JsonAnalysisResultWapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveCell;
import dk.ange.octave.type.OctaveString;

/**
 * Servlet implementation class PostAnalysisService1
 */
@WebServlet("/RunJob")
public class RunJob extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RunJob() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static int getLineNumber() {
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				util.AScontants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		System.out.println("AS FULL URL:" + ServerUtil.getFullURL(request));
		Gson gson = new Gson();
		AnalysisServiceDAO asDao = new AnalysisServiceDAO();
		DatastreamDAO dsDao = new DatastreamDAO();
		String loginID = "testtest4";
		// retrieve service id information
		String serviceID_String = request
				.getParameter(AScontants.RequestParameters.Service_ID);
		int serviceID = 0;
		int globalMaxDatapoints = -1;
		boolean runningLiveJob = false;
		if (request
				.getParameter(AScontants.RequestParameters.request_api_livejob) != null
				&& request.getParameter(
						AScontants.RequestParameters.request_api_livejob)
						.equalsIgnoreCase("true")) {
			runningLiveJob = true;
		}
		try {
			if (request
					.getParameter(AScontants.RequestParameters.request_api_maxGlobal) != null) {
				globalMaxDatapoints = Integer
						.parseInt(request
								.getParameter(AScontants.RequestParameters.request_api_maxGlobal));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Invalid_data_format, null,
					AScontants.RequestParameters.request_api_maxGlobal);
			return;
		}
		if (serviceID_String == null || serviceID_String.length() < 1) {
			System.out.println("Return Error Message to User. GetLineNumber:"
					+ getLineNumber());
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.MISSING_DATA, null,
					AScontants.RequestParameters.Service_ID);
			return;
		} else {
			try {
				serviceID = Integer.parseInt(serviceID_String);
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_data_format, null,
						AScontants.RequestParameters.Service_ID);
				return;
			}
		}
		AnalysisService service = null;
		AnalysisModel model = null;
		service = asDao.getServicebyID(serviceID);
		if (service == null) {
			System.out.println("Return Error Message to User. GetLineNumber:"
					+ getLineNumber());
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.service_id_cannot_found, null,
					AScontants.RequestParameters.Service_ID);
			return;
		}
		// get model information
		model = asDao.getModelByID(service.getModelId());

		List<AnalysisModelEntry> inputEntryList = asDao
				.getModelEntriesByModelID(model.getId(), AScontants.as_input);
		List<AnalysisModelEntry> outputEntryList = asDao
				.getModelEntriesByModelID(model.getId(), AScontants.as_output);
		ArrayList<ASInput> inputList = new ArrayList<ASInput>();
		ArrayList<ASOutput> outputList = new ArrayList<ASOutput>();
		// check input and output settings
		for (int i = 0; i < inputEntryList.size(); i++) {
			ASInput input = new ASInput();
			input.setName("input" + Integer.toString(i + 1));
			input.setType(inputEntryList.get(i).getDataType());
			String[] dataTypes = { AScontants.sensordataType,
					AScontants.fileType, AScontants.StringType,
					AScontants.integerType, AScontants.doubleType };
			int typeEntry = Arrays.asList(dataTypes).indexOf(input.getType());

			String source = request.getParameter("input"
					+ Integer.toString(i + 1) + "_source");
			switch (typeEntry) {
			case 0:// sensor data type
				System.out.println("-----------case 0:-------------");
				try {

					if (source != null && source.length() > 1) {
						input.setSource(source);
						Datastream datastream = dsDao.getHealthDatastreamByTitle(source, loginID, true, false);
						if(datastream==null)
						{
							ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_datastream_title,
									null, source);
					return;
						}
						input.setValue(datastream.getStreamId());
						long start = 0;
						long end = 0;
						int max = 1000;
						try {
							if (request.getParameter("input"
									+ Integer.toString(i + 1) + "_start") != null) {
								start = Long.parseLong(request.getParameter("input"
										+ Integer.toString(i + 1) + "_start"));
							}
							if (request.getParameter("input"
									+ Integer.toString(i + 1) + "_end") != null) {
								end = Long.parseLong(request.getParameter("input"
										+ Integer.toString(i + 1) + "_end"));
							}
							if (request.getParameter("input"
									+ Integer.toString(i + 1) + "_max") != null) {
								max = Integer.parseInt(request.getParameter("input"
										+ Integer.toString(i + 1) + "_max"));
							}
							if (globalMaxDatapoints > 0) {
								System.out.println("--globalMaxDatapoints---");
								input.setMaxDataPoints(globalMaxDatapoints);
							} else {
								input.setMaxDataPoints(max);
							}
							input.setStart(start);
							input.setEnd(end);
						} catch (Exception ex) {
							ex.printStackTrace();
							ReturnParser
									.outputErrorException(
											response,
											AllConstants.ErrorDictionary.Invalid_data_format,
											null, "");
							return;
						}
					} else {
						System.out
								.println("Return Error Message to User. GetLineNumber:"
										+ getLineNumber());
						ReturnParser.outputErrorException(response,
								AllConstants.ErrorDictionary.MISSING_DATA,
								null, "");
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out
							.println("Return Error Message to User. GetLineNumber:"
									+ getLineNumber());

					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_data_format,
							null, "");
					return;
				}
				break;

			case 1: // if data type is file
				System.out.println("-----------case 1:--file input-----------");

				if (source == null && source.length() < 2) {
					// if source not found
					System.out
							.println("Return Error Message to User. GetLineNumber:"
									+ getLineNumber());

					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_data_format,
							null, "data stream title");
					return;
				}
				Datastream datastream = dsDao.getHealthDatastreamByTitle(
						source, loginID, true, false);
				if (datastream == null) {
					System.out
							.println("Return Error Message to User. GetLineNumber:"
									+ getLineNumber());

					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Unknown_StreamTitle,
							null, "data stream title");
					return;
				}
				String filekey = request.getParameter("input"
						+ Integer.toString(i + 1) + "_filekey");

				if (filekey == null && filekey.length() < 2) {
					// if source not found
					System.out
							.println("Return Error Message to User. GetLineNumber:"
									+ getLineNumber());

					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_data_format,
							null, "filekey");
					return;
				}

				input.setSource(source);
				input.setFilekey(filekey);
				break;
			case 2:// String Input Type
				System.out
						.println("-----------case 2:- String Input Type------------");
				try {
					if (source == null || source.length() < 1) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.Invalid_data_format,
										null, "source");
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				input.setSource(source);
				break;
			case 3:// Integer Input Type
				System.out
						.println("-----------case 3:---Integer Input Type----------");
				try {
					if (source == null || source.length() < 1) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.Invalid_data_format,
										null, "source");
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				input.setSource(source);
				break;
			case 4:// Double Input Type
				System.out
						.println("-----------case 4:----Double Input Type---------");
				try {
					if (source == null || source.length() < 1) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.Invalid_data_format,
										null, "source");
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				input.setSource(source);
				break;
			default:
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_data_format, null,
						"missing data type");
				return;
			}

			inputList.add(input);
		}
		
		
		
		
		
		// check and pre-load output list
		for (int i = 0; i < outputEntryList.size(); i++) {
			ASOutput output = new ASOutput();
			String dataAction = outputEntryList.get(i).getDataAction();
			output.setDataAction(dataAction);
			output.setName("output" + Integer.toString(i + 1));
			String type = outputEntryList.get(i).getDataType();
			String source = request.getParameter("output"
					+ Integer.toString(i + 1) + "_source");
			if (!dataAction.equalsIgnoreCase(AScontants.dataaction_ignore)
					&& type.equals(AScontants.sensordataType)) {
				if (source != null) {
					
				} else {
					System.out
							.println("Return Error Message to User. GetLineNumber:"
									+ getLineNumber());
					ReturnParser
							.outputErrorException(response,
									AllConstants.ErrorDictionary.MISSING_DATA,
									null, "");
					return;
				}
				String datastreamTitle = source;
				Datastream datastream = dsDao.getHealthDatastreamByTitle(
						datastreamTitle, loginID, true, false);
				if (datastream == null) {
					System.out
							.println("Return Error Message to User. GetLineNumber:"
									+ getLineNumber());
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_datastream_title,
									null, "");
					return;
				}
				output.setSource(source);
				output.setValue(datastream.getStreamId());
			} else if (!dataAction
					.equalsIgnoreCase(AScontants.dataaction_ignore)
					&& type.equals(AScontants.fileType)) {
				if (source != null) {
					output.setSource(source);
				} else {
					System.out
							.println("Return Error Message to User. GetLineNumber:"
									+ getLineNumber());
					ReturnParser
							.outputErrorException(response,
									AllConstants.ErrorDictionary.MISSING_DATA,
									null, "");
					return;
				}
			}else{
//				System.out.println("-----Unkown Type Name:"+type);
//				ReturnParser
//				.outputErrorException(response,
//						AllConstants.ErrorDictionary.Invalid_data_format,
//						null, "unknown data type");
//		return;
			}
			output.setType(type);
			outputList.add(output);
		}
		// start initial stage of creating TED folders and copy data to tmp
		// folders
		try {
			AnalysisResult result = new AnalysisResult();
			UUID uuid = UUID.randomUUID();
			String jobID = uuid.toString();
			result.setJobId(jobID);
			result.setJobStartTime(new Date());
			result.setJobStatus(AScontants.ModelJobStatus.running);
			result.setModelId(service.getModelId());
			result.setUserId(service.getUserId());
			result.setService_id(serviceID);
			AnalysisResult returnedResult = asDao.createJobResultBy(result);
			AnalysisResult asresult = null;
			if (returnedResult == null) {
				System.out
						.println("Return Error Message to User. GetLineNumber:"
								+ getLineNumber());
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null, "");
				return;
			}
			if (runningLiveJob == false) {
				ExecutionEngineThread executionThread = new ExecutionEngineThread();
				executionThread.inputList = inputList;
				executionThread.outputList = outputList;
				executionThread.jobID = jobID;
				executionThread.modelID = model.getId();
				executionThread.start();
			} else {
				String outputFolderURLPath = "http://api.wiki-health.org:55555/healthbook/as/getFile?path=";
				AnalysisWrapperUtil awU = new AnalysisWrapperUtil();
				asresult = awU.octaveRun(service.getModelId(), jobID,
						outputFolderURLPath, inputList, outputList);
			}
			// List<AnalysisModelEntry> totalEntryList = new ArrayList<>();
			// totalEntryList.addAll(inputEntryList);
			// totalEntryList.addAll(outputEntryList);
			// asDao.updateModelEntries(totalEntryList);
			// model.setStatus(AScontants.status_live);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			if (runningLiveJob) {
				jo.add("asresult", gson.toJsonTree(asresult));
				jo.addProperty("livejob", "true");
			} else {
				jo.addProperty("livejob", "false");
			}
			// System.out.println(gson.toJson(jo));
			out.println(gson.toJson(jo));

		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, "");
			return;
		} finally {
			out.close();
		}

	}

	public class ExecutionEngineThread extends Thread {
		String jobID = null;
		String modelID = null;
		public boolean OctaveExecutionSuccessful = false;
		public boolean WholeJobFinishedSuccessful = true;
		String outputLog = "";
		String analysisDataMovementLog = "";
		String outputFolderURLPath = "http://api.wiki-health.org:55555/healthbook/as/getFile?path=";
		// String outputFolderURLPath =
		// "http://localhost:8080/healthbook/as/getFile?path=";
		ArrayList<ASInput> inputList = new ArrayList<ASInput>();

		ArrayList<ASOutput> outputList = new ArrayList<ASOutput>();

		public void run() {
			System.out.println("Hello from a thread!");
			AnalysisWrapperUtil awU = new AnalysisWrapperUtil();
			awU.octaveRun(modelID, jobID, outputFolderURLPath, inputList,
					outputList);
		}
	}

	public static void main(String args[]) {

	}
}
