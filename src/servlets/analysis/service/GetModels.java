package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import server.exception.ReturnParser;
import util.AScontants;
import util.AllConstants;

/**
 * Servlet implementation class PostAnalysisService1
 */
@WebServlet("/GetModels")
public class GetModels extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetModels() {
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

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				util.AScontants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			AnalysisServiceDAO asDao = new AnalysisServiceDAO();
			Gson gson = new Gson();

			String searchName = request
					.getParameter(AScontants.RequestParameters.ModelName);
			if(searchName!=null&&searchName.length()<1)
			{
				searchName=null;
			}
			String statusType=request
					.getParameter(AScontants.RequestParameters.ModelStatus);
			if(statusType==null)
			{
				statusType="live";
			}
			List<AnalysisModel> modelList = asDao.getModelList(searchName,"live");
			JsonElement jmodel = gson.toJsonTree(modelList);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("model_list", jmodel);
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

	public static void main(String args[]) {
	}
}
