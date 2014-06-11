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
import java.util.Date;
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
import util.AScontants;
import util.AllConstants;

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

		Gson gson = new Gson();
		AnalysisServiceDAO asDao = new AnalysisServiceDAO();
		DatastreamDAO dsDao = new DatastreamDAO();
		String loginID = "testtest3";
		String serviceID_String = request
				.getParameter(AScontants.RequestParameters.Service_ID);
		int serviceID = 0;
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
		model = asDao.getModelByID(service.getModelId());

		List<AnalysisModelEntry> inputEntryList = asDao
				.getModelEntriesByModelID(model.getId(), AScontants.as_input);
		List<AnalysisModelEntry> outputEntryList = asDao
				.getModelEntriesByModelID(model.getId(), AScontants.as_output);
		ArrayList<ASInput> inputList = new ArrayList<ASInput>();

		ArrayList<ASOutput> outputList = new ArrayList<ASOutput>();
		for (int i = 0; i < inputEntryList.size(); i++) {
			ASInput input = new ASInput();
			input.setName("input" + Integer.toString(i + 1));
			input.setType(inputEntryList.get(i).getDataType());
			String source = request.getParameter("input"
					+ Integer.toString(i + 1) + "_source");
			try {
				if (source != null && source.length() > 1) {
					input.setSource(source);
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
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out
						.println("Return Error Message to User. GetLineNumber:"
								+ getLineNumber());

				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_data_format, null,
						"");
				return;
			}
			inputList.add(input);
		}
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
				String datastreamID = source;
				Datastream datastream = dsDao.getHealthDatastreamByTitle(
						datastreamID, loginID, true, false);
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
			}
			output.setType(type);
			outputList.add(output);
		}
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
			if (returnedResult == null) {
				System.out
						.println("Return Error Message to User. GetLineNumber:"
								+ getLineNumber());
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null, "");
				return;
			}
			ExecutionEngineThread executionThread = new ExecutionEngineThread();
			executionThread.inputList = inputList;
			executionThread.outputList = outputList;
			executionThread.jobID = jobID;
			executionThread.modelID = model.getId();
			executionThread.start();

			// List<AnalysisModelEntry> totalEntryList = new ArrayList<>();
			// totalEntryList.addAll(inputEntryList);
			// totalEntryList.addAll(outputEntryList);
			// asDao.updateModelEntries(totalEntryList);
			// model.setStatus(AScontants.status_live);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			System.out.println(gson.toJson(jo));
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
		String jobID=null;
		String modelID=null;
		public boolean OctaveExecutionSuccessful = false;
		public boolean WholeJobFinishedSuccessful = true;
		String outputLog = "";
		String analysisDataMovementLog = "";

		String outputFolderURLPath = "http://localhost:8080/healthbook/as/getFile?path=";
		ArrayList<ASInput> inputList = new ArrayList<ASInput>();

		ArrayList<ASOutput> outputList = new ArrayList<ASOutput>();

		public void run() {
			System.out.println("Hello from a thread!");
			String tmpfolderPath = "F:/model_repository/" + modelID + "/";
			String jobfolderPath = "F:/job_folder/" + jobID + "/";
			outputFolderURLPath = outputFolderURLPath + "/" + jobID + "/";
			File folder = new File(tmpfolderPath);
			File jobFolder = new File(jobfolderPath);
			if (!jobFolder.exists()) {
				jobFolder.mkdir();
			}

			AnalysisServiceDAO asDao = new AnalysisServiceDAO();
			AnalysisResult result = asDao.getJobResultByID(jobID);
			try {
				AnalysisWrapperUtil awU = new AnalysisWrapperUtil();
				StringWriter stdout = new StringWriter();
				OctaveEngineFactory octaveFactory = new OctaveEngineFactory();
				octaveFactory.setWorkingDir(folder);
				OctaveEngine octave = octaveFactory.getScriptEngine();
				try {
					octave.setWriter(stdout);
					octave.eval("addpath(\"signal_package\")");
					octave.eval("addpath(\"general_package\")");
					for (ASInput input : inputList) {
						if (input.getType().equals("string")) {
							OctaveString octaveInput = new OctaveString(
									(String) input.getSource());
							octave.put(input.getName(), octaveInput);
						}

					}
					String mainFunctionString = awU.createMainFunction("main",
							inputList, outputList);
					System.out.println("mainFunctionString:"
							+ mainFunctionString);
					octave.eval(mainFunctionString);
					OctaveExecutionSuccessful = true;

				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println(ex.getMessage());
					outputLog = outputLog + ex.getMessage();
					analysisDataMovementLog = analysisDataMovementLog
							+ ex.getMessage();
					OctaveExecutionSuccessful = false;
					WholeJobFinishedSuccessful = false;
				}

				// result.setJobLog((outputLog);
				if (OctaveExecutionSuccessful) {
					for (int i = 0; i < outputList.size(); i++) {
						ASOutput output = outputList.get(i);
						if (output.getType().equalsIgnoreCase(
								AScontants.sensordataType)
								&& !output.getDataAction().equalsIgnoreCase(
										AScontants.dataaction_ignore)) {
							OctaveCell octaveResult = (OctaveCell) octave
									.get(output.getName());
							List<JsonDataPoints> datapointsList = awU
									.unwrapOctaveSensorData(octaveResult);
							if (datapointsList == null) {
								System.out
										.println("some problem---:datapointsList == null");
							} else {
								DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
								HBaseDataImport importData = new HBaseDataImport();
								DatastreamDAO dsDao = new DatastreamDAO();
								String datastreamID = output.getSource();
								importData.setData_points(datapointsList);
								HBaseDatapointDAO importDao = new HBaseDatapointDAO();
								Datastream datastream = dsDao.getDatastream(
										datastreamID, true, false);
								importData.setDatastream_id(datastream
										.getStreamId());
								try {
									importData
											.setDatastream(dbtoJUtil
													.convertDatastream(
															datastream, null));
									int totalStoredByte = importDao
											.importDatapointsDatapoints(importData); // submit
																						// data
									analysisDataMovementLog = analysisDataMovementLog
											+ "<p>Data Stored Successfully for datastream ID: "
											+ datastreamID
											+ ", total bytes:"
											+ totalStoredByte + "</p>";
									output.setValue(datastream.getTitle());
									outputList.set(i, output);
									System.out.println(totalStoredByte);
								} catch (ErrorCodeException ex) {
									WholeJobFinishedSuccessful = false;
									if (ex.getErrorCode()
											.equals(AllConstants.ErrorDictionary.Input_data_contains_invalid_unit_id)) {
										analysisDataMovementLog = analysisDataMovementLog
												+ "<p>Contains Invalid UnitID"
												+ "</p>";
									} else {
										analysisDataMovementLog = analysisDataMovementLog
												+ "<p>Internal Error unknown type"
												+ "</p>";
									}
								} catch (Exception ex) {
									WholeJobFinishedSuccessful = false;
									ex.printStackTrace();
									analysisDataMovementLog = analysisDataMovementLog
											+ "<p>Internal Error" + "</p>";
								}
							}
						} else if (output.getType().equalsIgnoreCase(
								AScontants.fileType)) {
							OctaveString fileOutput = (OctaveString) octave
									.get(output.getName());

							File outputFile = new File(tmpfolderPath
									+ fileOutput.getString());
							File outputFileJob = new File(jobfolderPath
									+ fileOutput.getString());
							if (fileOutput.getString().length() < 1
									|| !outputFile.exists()) {
								analysisDataMovementLog = analysisDataMovementLog
										+ "<p>No Output File Exist or empty string:"
										+ "</p>" + fileOutput.getString();
								System.out
										.println("ERROR Found No Output File Exist or empty string:"
												+ fileOutput.getString()
												+ ",Exist"
												+ outputFile.exists()
												+ ","
												+ outputFile.getAbsolutePath());
								continue;
							}else{
								FileUtils.copyFile(outputFile, outputFileJob);
							}
							MimetypesFileTypeMap imageMimeTypes = new MimetypesFileTypeMap();
							imageMimeTypes
									.addMimeTypes("image png tif jpg jpeg bmp");

							String mimetype = imageMimeTypes
									.getContentType(fileOutput.getString());
							String fileDownloadPath = outputFolderURLPath
									+ fileOutput.getString();
							output.setValue(fileDownloadPath);
							outputList.set(i, output);
						} else {
							System.out.println("other type not supported yet");
						}
					}
				}

				try {
					octave.close();
					outputLog = outputLog + stdout.toString();
					outputLog = outputLog.replace("\n", "<br>");
				} catch (Exception ex) {
					ex.printStackTrace();
					WholeJobFinishedSuccessful = false;
				}

				System.out.println(outputLog);
				result.setModelLog(outputLog);

				if (OctaveExecutionSuccessful) {
					System.out.println("Model Execution Successful!");
					result.setModel_status(AScontants.ModelJobStatus.finished_succesfully);
				} else {
					System.out.println("Model Execution Failed!");
					result.setModel_status(AScontants.ModelJobStatus.finished_with_error);
				}
				if (WholeJobFinishedSuccessful) {
					System.out.println("Job Execution Successful!");
					result.setJobStatus(AScontants.ModelJobStatus.finished_succesfully);
				} else {
					System.out.println("Job Execution Failed!");
					result.setJobStatus(AScontants.ModelJobStatus.finished_with_error);
				}
				if (WholeJobFinishedSuccessful && OctaveExecutionSuccessful) {
					Gson gson = new GsonBuilder().disableHtmlEscaping()
							.create();
					JsonAnalysisResultWapper jarw = new JsonAnalysisResultWapper();
					jarw.setInputs(inputList);
					jarw.setOutputs(outputList);
					String json_String = gson.toJson(jarw);
					result.setJson_results(json_String);
					System.out.println(gson.toJson(json_String));
					// JsonElement jinputs=gson.toJsonTree(inputList);
					// JsonElement joutputs=gson.toJsonTree(outputList);
					// JsonObject jo=new JsonObject();
					// jo.add("outputs", joutputs);
					// jo.add("inputs", jinputs);
					// System.out.println(gson.toJson(jo));
					// result.setJson_results(gson.toJson(jo));
				}

				result.setJobEndTime(new Date());
				result.setJobLog(analysisDataMovementLog);
				asDao.updateJobResult(result);
			} catch (Exception ex) {
				ex.printStackTrace();
				result.setJobEndTime(new Date());
				result.setJobLog(analysisDataMovementLog);
				result.setJobStatus(AScontants.ModelJobStatus.finished_with_error);
				asDao.updateJobResult(result);
			}
		}
	}

	public static void main(String args[]) {

	}
}
