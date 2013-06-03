/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.models.Subject;
import health.input.jsonmodels.JsonSubject;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.AllConstants;

import com.apidocument.objects.API_apis;
import com.apidocument.objects.API_main;
import com.apidocument.objects.API_operations;
import com.apidocument.objects.API_parameters;
import com.google.gson.Gson;

/**
 * 
 * @author Leon
 */
public class GetAPI_DocumentJson extends HttpServlet {

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
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		try {
			String requestURI=request.getRequestURI();
			System.out.println("contextPath:"+requestURI);
			
			Gson gson = new Gson();
			
			if(requestURI.matches("^"+AllConstants.api_entryPoints.api_url+ "api"+"[/]*$"))
			{
			API_main api_main = new API_main();
			api_main.setBasePath("http://localhost:8080/healthbook/v1/api");
			api_main.setApiVersion("1.0");
			API_apis health_datastream_api = new API_apis();
			health_datastream_api.setDescription("this is datastream API");
			health_datastream_api.setPath("/health/");
			API_apis user_api = new API_apis();
			user_api.setDescription("this is datastream API");
			user_api.setPath("/user");
			List<API_apis> api_raw_list = new ArrayList<API_apis>();
			api_raw_list.add(health_datastream_api);
//			api_raw_list.add(user_api);
			api_main.setApis(api_raw_list);
			out.println(gson.toJson(api_main));
			}
			else if(requestURI.matches(AllConstants.api_entryPoints.api_url+ "api"+"/health"+"[/]*$"))
			{
				API_main api_main = new API_main();
				api_main.setBasePath("http://146.169.35.28:55555/healthbook/v1/");
				api_main.setApiVersion("1.0");
				api_main.setResourcePath("/health/title");
				API_apis health_datastream_api = new API_apis();
				health_datastream_api.setDescription("Get Health Default Datastreams");
				health_datastream_api.setPath("/health/");
				API_operations title_datastreamAPI=new API_operations();
				title_datastreamAPI.setHttpMethod("GET");
				title_datastreamAPI.setNickname("GetHealthDefaultDatastreams");
			/*	API_parameters title_datastream
				health_datastream_api.setOperations(operations)*/
			
				List<API_apis> api_raw_list = new ArrayList<API_apis>();
				api_raw_list.add(health_datastream_api);
				api_main.setApis(api_raw_list);
				out.println(gson.toJson(api_main));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();
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
