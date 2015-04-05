package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;

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
import util.MarketplaceContants;
import util.AllConstants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetModelMetadata")
public class GetModelMetadata extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetModelMetadata() {
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
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			Gson gson = new Gson();
			AnalysisServiceDAO asDao = new AnalysisServiceDAO();
			String modelName = request
					.getParameter(MarketplaceContants.RequestParameters.ModelName);
			String modelID = request
					.getParameter(MarketplaceContants.RequestParameters.Model_ID);
			AnalysisModel model = null;
			if (modelName != null && modelName.length() > 1) {
				List<AnalysisModel> modelList = asDao
						.getModelListByModelName(modelName);
				if (modelList == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Internal_Fault, null,
							modelName);
					return;
				} else if (modelList.size() == 0) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.unknown_analysis_model_name,
									null, modelName);
					return;
				} else {
					model = modelList.get(0);
				}
			} else if (modelID != null && modelID.length() > 5) {
				model = asDao.getModelByID(modelID);
				if (model == null) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.unknown_analysis_model_id,
									null, modelID);
					return;
				}
			} else {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						modelName);
				return;
			}
			List<AnalysisModelEntry> inputsList=asDao.getModelEntriesByModelID(model.getId(),MarketplaceContants.as_input);
			List<AnalysisModelEntry> outputsList=asDao.getModelEntriesByModelID(model.getId(),MarketplaceContants.as_output);
			
			JsonElement jmodel = gson.toJsonTree(model);
			JsonElement jinputsList = gson.toJsonTree(inputsList);
			JsonElement joutputsList = gson.toJsonTree(outputsList);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("model", jmodel);
			jo.add("inputs", jinputsList);
			jo.add("outputs", joutputsList);
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
