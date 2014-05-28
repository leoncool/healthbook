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
	<h1>
		Analysis Model Logs:
		<%
		out.println(jobID);
	%>
	</h1>
	<%
		String outputLog = "";
		File folder = new File("F:/octave/");
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
			input1.setType("string");
			input1.setValue("input1.txt");
			// ASInput input2 = new ASInput();
			// input2.setName("input2");
			ASOutput output1 = new ASOutput();
			output1.setName("output1");
			output1.setType("sensordata");
			inputList.add(input1);
			// inputList.add(input2);
			outputList.add(output1);
			String mainFunctionString = awU.createMainFunction("main3",
					inputList, outputList);

			for (ASInput input : inputList) {
				if (input.getType().equals("string")) {
					OctaveString octaveInput = new OctaveString(
							(String) input.getValue());
					octave.put(input.getName(), octaveInput);
				}

			}

			try {
				octave.eval(mainFunctionString);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println(ex.getMessage());
			}
			for (ASOutput output : outputList) {
				if (output.getType() == AScontants.sensordataType) {
					OctaveCell result = (OctaveCell) octave.get(output
							.getName());
					List<JsonDataPoints> datapointsList = awU
							.unwrapOctaveSensorData(result);
					if (datapointsList == null) {

					} else {
						Gson gson = new Gson();
						out.println(gson.toJson(datapointsList));
					}
				} else if (output.getType() == AScontants.fileType) {
					OctaveString fileOutput= (OctaveString)octave.get(output.getName());	
					
				} else if (output.getType() == AScontants.StringType) {

				} else if (output.getType() == AScontants.integerType) {

				} else if (output.getType() == AScontants.doubleType) {

				}

			}

			octave.close();
			outputLog = stdout.toString();
			outputLog = outputLog.replace("\n", "<br>");

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
	%>

</body>
</html>