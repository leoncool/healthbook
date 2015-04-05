package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisService;

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
@WebServlet("/GetService")
public class GetService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetService() {
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
			String serviceID_String = request
					.getParameter(MarketplaceContants.RequestParameters.Service_ID);
			int serviceID=0;
			if (serviceID_String == null || serviceID_String.length() < 1) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						MarketplaceContants.RequestParameters.Service_ID);
				return;
			}else{
				try{
					serviceID=Integer.parseInt(serviceID_String);
				}catch(Exception ex)
				{
					ex.printStackTrace();
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_data_format, null,
							MarketplaceContants.RequestParameters.Service_ID);
					return;
				}
			}
			AnalysisService service = null;
			AnalysisModel model = null;
			service = asDao.getServicebyID(serviceID);
			if (service == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.service_id_cannot_found,
						null, MarketplaceContants.RequestParameters.Service_ID);
				return;
			}
			model=asDao.getModelByID(service.getModelId());
			
			List<AnalysisModelEntry> inputsList = asDao
					.getModelEntriesByModelID(model.getId(),
							MarketplaceContants.as_input);
			List<AnalysisModelEntry> outputsList = asDao
					.getModelEntriesByModelID(model.getId(),
							MarketplaceContants.as_output);
			JsonElement jservice = gson.toJsonTree(service);
			JsonElement jmodel = gson.toJsonTree(model);
			JsonElement jinputsList = gson.toJsonTree(inputsList);
			JsonElement joutputsList = gson.toJsonTree(outputsList);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("service", jservice);
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
