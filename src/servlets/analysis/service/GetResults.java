package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisResult;

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

import org.apache.commons.beanutils.BeanUtils;

import server.exception.ReturnParser;
import util.AScontants;
import util.AllConstants;

import com.analysis.service.JsonAnalysisResult;
import com.analysis.service.JsonAnalysisResultWapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetResults")
public class GetResults extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetResults() {
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
			Gson gson = new Gson();
			String loginID = "testtest3";
			AnalysisServiceDAO asDao = new AnalysisServiceDAO();
			String jobID = request
					.getParameter(AScontants.RequestParameters.Job_ID);
			List<AnalysisResult> resultList = null;
			List<JsonAnalysisResult> jresultList = new ArrayList<>();
			resultList = asDao.getJobResultsList(null);
			if (resultList == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null, "");
				return;
			}
			for (int i = 0; i < resultList.size(); i++) {
				AnalysisResult result = resultList.get(i);
				JsonAnalysisResult jresult = new JsonAnalysisResult();
				BeanUtils.copyProperties(jresult, result);
				jresult.setJson_results(null);
				jresult.setModel_name(asDao.getModelByID(jresult.getModelId())
						.getName());
				jresultList.add(jresult);
			}

			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			JsonElement jelement = gson.toJsonTree(jresultList);
			jo.add("jobs", jelement);
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
