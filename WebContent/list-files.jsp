<%@page import="java.util.HashMap"%>
<%@page import="com.analysis.service.AnalysisWrapperUtil"%>
<%@page import="util.AllConstants"%>
<%@page import="health.database.DAO.nosql.HBaseDatapointDAO"%>
<%@page import="health.hbase.models.HBaseDataImport"%>
<%@page import="health.database.models.Datastream"%>
<%@page import="health.input.util.DBtoJsonUtil"%>
<%@page import="health.database.DAO.DatastreamDAO"%>
<%@page import="health.database.models.Subject"%>
<%@page import="health.database.DAO.SubjectDAO"%>
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
	<%=request.getParameter("jobid")%>
	<%
		File folder = new File("F:/octave/");
		if (folder.exists()) {
			out.println("aaaa <br>");
			File[] fileList = folder.listFiles();
			for (File file : fileList) {
				out.println(file.getName() + "<br>");
			}
		}
		String targetLoginID = "testtest3";
		SubjectDAO subjDao = new SubjectDAO();
		Subject subject = (Subject) subjDao
				.findHealthSubject(targetLoginID); // Retreive
		DatastreamDAO dstreamDao = new DatastreamDAO();
		DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
		HBaseDatapointDAO diDao = new HBaseDatapointDAO();
		String streamTitle = "ecg";
		Datastream datastream = dstreamDao.getHealthDatastreamByTitle(
				subject.getId(), streamTitle, true, false);
		long start = 0;
		long end = 0;
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
		HashMap<String, Object> settings=new HashMap<String, Object>();
		int max=20000;
		settings.put(AllConstants.ProgramConts.exportSetting_MAX, max);

		HBaseDataImport hbaseexport = diDao.exportDatapoints(
				datastream.getStreamId(), start, end, null, null, null,
				settings);
		System.out.println("hbaseexport:"+hbaseexport.getData_points().size());
		out.println("hbaseexport:"+hbaseexport.getData_points().size());
		AnalysisWrapperUtil awU=new AnalysisWrapperUtil();
		File input1=new File("F:/octave/input1.txt");
		awU.saveDataPointsToFile(hbaseexport, input1, " ");
		
	%>
	<h1>Test</h1>
</body>
</html>