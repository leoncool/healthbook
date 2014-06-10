package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisResult;
import health.database.models.as.AnalysisService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AScontants;
import util.AllConstants;

import com.analysis.service.ASInput;
import com.analysis.service.ASOutput;
import com.analysis.service.AnalysisWrapperUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
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
		String serviceID_String = request
				.getParameter(AScontants.RequestParameters.Service_ID);
		int serviceID = 0;
		if (serviceID_String == null || serviceID_String.length() < 1) {
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

			try {
				if (request.getParameter("input" + Integer.toString(i + 1)
						+ "_source") != null) {
					String source = request.getParameter("input"
							+ Integer.toString(i + 1) + "_source");
					input.setSource(source);
				} else {
					ReturnParser
							.outputErrorException(response,
									AllConstants.ErrorDictionary.MISSING_DATA,
									null, "");
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_data_format, null,
						"");
				return;
			}
			inputList.add(input);
		}
		for (int i = 0; i < outputEntryList.size(); i++) {
			ASOutput output = new ASOutput();
			output.setName("output" + Integer.toString(i));
			output.setType(outputEntryList.get(i).getDataType());
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
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null, "");
				return;
			}
			ExecutionEngineThread executionThread = new ExecutionEngineThread();
			executionThread.inputList = inputList;
			executionThread.outputList = outputList;
			executionThread.jobID=jobID;
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
		String jobID;
		public boolean OctaveExecutionSuccessful = false;
		String outputLog = "";
		String analysisDataMovementLog = "";
		String tmpfolderPath = "F:/octave/";
		File folder = new File(tmpfolderPath);
		String outputFolderURLPath = "http://localhost:8080/healthbook/as/getFile?path=";
		ArrayList<ASInput> inputList = new ArrayList<ASInput>();

		ArrayList<ASOutput> outputList = new ArrayList<ASOutput>();

		public void run() {
			System.out.println("Hello from a thread!");

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
				octave.eval(mainFunctionString);
				OctaveExecutionSuccessful = true;

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println(ex.getMessage());
				outputLog = outputLog + ex.getMessage();
				OctaveExecutionSuccessful = false;
			}
			
			try {
				octave.close();
				outputLog = outputLog + stdout.toString();
				outputLog = outputLog.replace("\n", "<br>");
			} catch (Exception ex) {

			}
			
			AnalysisServiceDAO asDao=new AnalysisServiceDAO();
			AnalysisResult result=asDao.getJobResultByID(jobID);
			result.setJobEndTime(new Date());
			result.setModelLog(outputLog);
//			result.setJobLog((outputLog);
			
			
			if (OctaveExecutionSuccessful) {
				System.out.println("Execution Successful!");
				result.setJobStatus(AScontants.ModelJobStatus.finished_succesfully);
			}else{			
				System.out.println("Execution Failed!");
				result.setJobStatus(AScontants.ModelJobStatus.finished_with_error);
			}
			asDao.updateJobResult(result);
		}

	}

	public static void main(String args[]) {

	}
}
