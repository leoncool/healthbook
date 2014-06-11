package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisResult;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.analysis.service.JsonAnalysisResultWapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetResult")
public class GetResult extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetResult() {
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

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				AScontants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			AnalysisServiceDAO asDao = new AnalysisServiceDAO();
			String jobID = request
					.getParameter(AScontants.RequestParameters.Job_ID);
			AnalysisResult result = null;
			if (jobID != null && jobID.length() > 1) {
				result = asDao.getJobResultByID(jobID);
				if (result == null) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.analysis_invalid_job_id,
									null, jobID);
					return;
				}

				JsonObject jo = new JsonObject();
				jo.addProperty(AllConstants.ProgramConts.result,
						AllConstants.ProgramConts.succeed);
				String json_result_String = result.getJson_results();
				result.setJson_results(null);
				JsonElement jresult = gson.toJsonTree(result);
				jo.add("job", jresult);
				if (json_result_String != null
						&& json_result_String.length() > 1) {
					System.out.println("adding json result String....");
					JsonAnalysisResultWapper jarw = gson.fromJson(
							json_result_String, JsonAnalysisResultWapper.class);
					JsonElement janalysis_result = gson.toJsonTree(jarw);
					jo.add("service_results", janalysis_result);
				}
				System.out.println(gson.toJson(jo));
				out.println(gson.toJson(jo));
			} else {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.analysis_invalid_job_id,
						null, jobID);
			}

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
