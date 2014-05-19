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
<%= request.getParameter("jobid") %>
<%
File folder=new File("F:/octave/");
if(folder.exists())
{
	out.println("aaaa <br>");
	File[] fileList=folder.listFiles();
	for(File file:fileList)
	{
		out.println(file.getName()+"<br>");
	}
}
%>
<h1>Test</h1>
</body>
</html>