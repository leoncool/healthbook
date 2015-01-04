package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AScontants;
import util.AllConstants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetUserServices")
public class GetUserServices extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetUserServices() {
		super();
		// TODO Auto-generated constructor stub
	}
	class AnalysisServiceReturn extends AnalysisService {
		protected AnalysisService service;
		protected AnalysisModel model;
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
				AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				AScontants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			Gson gson = new Gson();
			AnalysisServiceDAO asDao = new AnalysisServiceDAO();
			String target_user_id = request
					.getParameter(AllConstants.api_entryPoints.request_api_targetid);
			if(target_user_id==null)
			{
				target_user_id="testtest4";//for debugging 
			}
		List<AnalysisService> serviceList=asDao.getUserServices(target_user_id);
		List<AnalysisServiceReturn> serviceReturnList=new ArrayList<>();
		for(AnalysisService aService:serviceList)
		{
			AnalysisModel model=asDao.getModelByID(aService.getModelId());
			AnalysisServiceReturn asR=new AnalysisServiceReturn();
			asR.service=aService;
			asR.model=model;
			serviceReturnList.add(asR);
		}
			JsonElement service_list = gson.toJsonTree(serviceReturnList);

			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("service_list", service_list);
			System.out.println(gson.toJson(jo));
			out.println(gson.toJson(jo));

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();
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
		String s = "function [TimeStamp,UnitID,ValueList,TagList]=main(aaa)";
		Pattern p = Pattern.compile("^function \\[(.+?)\\]=main[(](.*?)[)]");
		Matcher m = p.matcher(s);
		if (m.matches()) {
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}

		String aaa = "ccccccc";
		String[] aaa_split = aaa.split(",");
		for (String a : aaa_split) {
			System.out.println(a);
		}

		// for (int i = 0; i < m.groupCount(); i++)
		// System.out.println("Group" + i + ": " + m.group(i));
	}

}
