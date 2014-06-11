<%@page import="util.AScontants"%>
<%@page import="util.AllConstants"%>
<%@page import="server.exception.ErrorCodeException"%>
<%@page import="health.input.util.DBtoJsonUtil"%>
<%@page import="health.database.models.Datastream"%>
<%@page import="health.hbase.models.HBaseDataImport"%>
<%@page import="health.database.DAO.nosql.HBaseDatapointDAO"%>
<%@page import="health.database.DAO.SubjectDAO"%>
<%@page import="health.database.DAO.DatastreamDAO"%>
<%@page import="health.input.jsonmodels.JsonDataImport"%>
<%@page import="javax.activation.MimetypesFileTypeMap"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="health.input.jsonmodels.JsonDataPoints"%>
<%@page import="com.analysis.service.*"%>
<%@page import="java.io.StringWriter"%>
<%@page import="dk.ange.octave.OctaveEngineFactory"%>
<%@page import="dk.ange.octave.OctaveEngine"%>
<%@page import="dk.ange.octave.type.*"%>
<%@page import="java.io.File"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

</head>
<body>
	<%
		String jobID = request.getParameter("jobid");
		jobID = request.getParameter("jobid") != null ? request
				.getParameter("jobid") : "";
	%>

	<%
		String outputLog = "";
		String analysisDataMovementLog = "";
		String tmpfolderPath = "F:/octave/";
		File folder = new File(tmpfolderPath);
		String outputFolderURLPath = "http://localhost:8080/healthbook/as/getFile?path=";
		if (folder.exists()) {
			AnalysisWrapperUtil awU = new AnalysisWrapperUtil();
			StringWriter stdout = new StringWriter();
			OctaveEngineFactory octaveFactory = new OctaveEngineFactory();
			octaveFactory.setWorkingDir(folder);
			OctaveEngine octave = octaveFactory.getScriptEngine();
			octave.setWriter(stdout);
			octave.eval("addpath(\"signal_package\")");
			octave.eval("addpath(\"general_package\")");

			ArrayList<ASInput> inputList = new ArrayList<ASInput>();
			ArrayList<ASOutput> outputList = new ArrayList<ASOutput>();
			ASInput input1 = new ASInput();
			input1.setName("input1");
			input1.setType(AScontants.StringType);
			input1.setValue("input1.txt");
			// ASInput input2 = new ASInput();
			// input2.setName("input2");
			ASOutput output1 = new ASOutput();
			output1.setName("output1");
			output1.setType(AScontants.sensordataType);
			ASOutput output2 = new ASOutput();
			output2.setName("output2");
			output2.setType(AScontants.fileType);
			ASOutput output3 = new ASOutput();
			output3.setName("output3");
			output3.setType(AScontants.fileType);
			inputList.add(input1);
			// inputList.add(input2);
			outputList.add(output1);
			outputList.add(output2);
			outputList.add(output3);
			String mainFunctionString = awU.createMainFunction("main3",
					inputList, outputList);
	%>

	<h1>Analysis Input and Function:</h1>
	<p>
		<%
			out.println(mainFunctionString);
		%>
	</p>
	<%
		for (ASInput input : inputList) {
				if (input.getType().equals("string")) {
					OctaveString octaveInput = new OctaveString(
							(String) input.getValue());
					octave.put(input.getName(), octaveInput);
				}

			}

			boolean OctaveExecutionSuccessful = false;
			try {
				octave.eval(mainFunctionString);
				OctaveExecutionSuccessful = true;
			} catch (Exception ex) {
				//		ex.printStackTrace();
				System.out.println(ex.getMessage());
				outputLog = outputLog + ex.getMessage();
				OctaveExecutionSuccessful = false;
			}
			if (OctaveExecutionSuccessful) {
	%>
	<h1>
		Analysis Process: <span style="color: green;">Successful</span>
	</h1>
	<h1>
		Analysis Outputs:
		<%
		out.println(jobID);
	%>
	</h1>

	<%
		for (ASOutput output : outputList) {
					if (output.getType() == AScontants.sensordataType) {
						OctaveCell result = (OctaveCell) octave.get(output
								.getName());
						List<JsonDataPoints> datapointsList = awU
								.unwrapOctaveSensorData(result);
						if (datapointsList == null) {

						} else {
							Gson gson = new Gson();
							DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
							HBaseDataImport importData = new HBaseDataImport();
							DatastreamDAO dsDao = new DatastreamDAO();
							String datastreamID = "b4c45927-1c72-48b7-9bdd-1f6e920fdc87";
							//		out.println(gson.toJson(datapointsList));

							importData.setData_points(datapointsList);
							HBaseDatapointDAO importDao = new HBaseDatapointDAO();
							Datastream datastream = dsDao.getDatastream(
									datastreamID, true, false);
							if (datastream == null) {
								analysisDataMovementLog = analysisDataMovementLog
										+ "<p>Cannot find datastream with id:"
										+ datastreamID + "</p>";
							}
							importData.setDatastream_id(datastream
									.getStreamId());
							importData.setDatastream(dbtoJUtil
									.convertDatastream(datastream, null));
							try {
								int totalStoredByte = importDao
										.importDatapointsDatapoints(importData); //submit data
								analysisDataMovementLog = analysisDataMovementLog
										+ "<p>Data Stored Successfully for datastream ID: "
										+ datastreamID
										+ ", total bytes:"
										+ totalStoredByte + "</p>";
								System.out.println(totalStoredByte);
							} catch (ErrorCodeException ex) {
								if (ex.getErrorCode()
										.equals(AllConstants.ErrorDictionary.Input_data_contains_invalid_unit_id)) {
									analysisDataMovementLog = analysisDataMovementLog
											+ "<p>Contains Invalid UnitID"
											+ "</p>";
								}
							} catch (Exception ex) {
								ex.printStackTrace();
								analysisDataMovementLog = analysisDataMovementLog
										+ "<p>Internal Error" + "</p>";
							}
						}
					} else if (output.getType() == AScontants.fileType) {
						OctaveString fileOutput = (OctaveString) octave
								.get(output.getName());

						File outputFile = new File(tmpfolderPath
								+ fileOutput.getString());
						if (fileOutput.getString().length() < 1
								|| !outputFile.exists()) {
							System.out
									.println("ERROR Found No Output File Exist or empty string:"
											+ fileOutput.getString()
											+ ",Exist"
											+ outputFile.exists()
											+ ","
											+ outputFile.getAbsolutePath());
							continue;
						}
						MimetypesFileTypeMap imageMimeTypes = new MimetypesFileTypeMap();
						imageMimeTypes
								.addMimeTypes("image png tif jpg jpeg bmp");

						String mimetype = imageMimeTypes
								.getContentType(fileOutput.getString());
						String fileDownloadPath = outputFolderURLPath
								+ fileOutput.getString();
						//	String type = mimetype.split("/")[0];
	%>

	<br>
	<table style="width: 300px">
		<tr style="font-weight: bold;">
			<td>Name</td>
			<td>Type</td>
			<td>Filename</td>
			<td>File Link</td>
		</tr>
		<tr>
			<td>
				<%
					out.println("Name:" + output.getName());
				%>
			</td>
			<td>
				<%
					out.println(output.getType());
				%>
			</td>
			<td>
				<%
					out.println(fileOutput.getString());
				%>
			</td>
			<td><a target="_blank"
				href="
				<%out.println(fileDownloadPath);%>
				"> <%
 	out.println(fileOutput.getString());
 %>
			</a></td>
		</tr>
	</table>
	<%
		if (fileOutput.toString().length() > 1
								&& mimetype.startsWith("image")) {
	%>
	<div id="image-wrapper" style="width: 100%; text-align: center">
		<img style="max-width: 800px" src="<%out.println(fileDownloadPath);%>" />
	</div>
	<br>
	<%
		System.out.println("Image File:"
									+ outputFolderURLPath
									+ fileOutput.getString());

						} else if (fileOutput.toString().length() > 1
								&& outputFile.exists()) {
							System.out.println("Generic File:"
									+ outputFolderURLPath
									+ fileOutput.getString());
						} else {
							System.out.println("Somethign Wrong:" + "Type:"
									+ mimetype + "," + outputFolderURLPath
									+ fileOutput.getString());
						}
					} else if (output.getType() == AScontants.StringType) {

					} else if (output.getType() == AScontants.integerType) {

					} else if (output.getType() == AScontants.doubleType) {

					}

				}

			} else {
	%>
	<h1>
		Analysis Process: <span style="color: red;">Failed</span>
	</h1>
	<%
		}
			try {
				octave.close();
				outputLog = outputLog + stdout.toString();
				outputLog = outputLog.replace("\n", "<br>");
			} catch (Exception ex) {

			}

		}
	%>
	<h1>
		Analysis Model Logs:
		<%
		out.println(jobID);
	%>
	</h1>

	<%
		out.println(outputLog);
		System.out.println("outputLog:" + outputLog);
	%>
	<h1>
		Analysis Data Logs:
		<%
		out.println(jobID);
	%>
	</h1>

	<%
		out.println(analysisDataMovementLog);
		System.out.println("analysisDataMovementLog:"
				+ analysisDataMovementLog);
	%>



</body>
</html>