<%@page import="java.io.StringWriter"%>
<%@page import="dk.ange.octave.OctaveEngineFactory"%>
<%@page import="dk.ange.octave.OctaveEngine"%>
<%@page import="java.io.File"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

</head>
<body>
	<h1>Output: 	<%=request.getParameter("jobid")!=null?request.getParameter("jobid"):""%></h1>

	<%
		File folder = new File("F:/octave/");
		if (folder.exists()) {
			StringWriter stdout = new StringWriter();
			System.out.println("going to run this1");
			OctaveEngineFactory octaveFactory=new OctaveEngineFactory();
			octaveFactory.setWorkingDir(folder);
			OctaveEngine octave = octaveFactory.getScriptEngine();
			octave.setWriter(stdout);
		
			octave.eval("main");
			octave.close();
			String outPut=stdout.toString();
			outPut=outPut.replace("\n", "<br>");
			out.println(outPut);
		}
	%>

</body>
</html>