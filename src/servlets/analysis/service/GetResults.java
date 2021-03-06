package servlets.analysis.service;

import health.database.DAO.UserDAO;
import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.Users;
import health.database.models.as.AnalysisModel;
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
import org.apache.commons.beanutils.BeanUtilsBean;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import util.MarketplaceContants;
import util.AllConstants;

import com.analysis.service.JsonAnalysisResult;
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
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			Users accessUser = null;
			PermissionFilter filter = new PermissionFilter();
			String loginID = filter.checkAndGetLoginFromToken(request, response);

			UserDAO userDao = new UserDAO();
			if (loginID == null) {
				if (filter.getCheckResult().equalsIgnoreCase(
						filter.INVALID_LOGIN_TOKEN_ID)) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_login_token_id,
							null, null);
					return;
				} else if (filter.getCheckResult().equalsIgnoreCase(
						AllConstants.ErrorDictionary.login_token_expired)) {
					return;
				} else {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_login_token_id,
							null, null);
					return;
				}
			} else {
				accessUser = userDao.getLogin(loginID);
			}
			String targetLoginID = filter.getTargetUserID(request, response);
			if (targetLoginID != null) {
				Users targetUser = userDao.getLogin(targetLoginID);
				if (targetUser == null) {
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_Target_LoginID,
							null, null);
					return;
				}
			}
			if (targetLoginID == null) {
				targetLoginID = loginID;
			}
			Gson gson = new Gson();
			
			AnalysisServiceDAO asDao = new AnalysisServiceDAO();
			String jobID = request
					.getParameter(MarketplaceContants.RequestParameters.Job_ID);
			String serviceID = request.getParameter(MarketplaceContants.RequestParameters.Service_ID);
			int serviceid=0;
			try{
				if(serviceID!=null)
				{
					serviceid=Integer.parseInt(serviceID);
					System.out.println("serviceid:"+serviceid);
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error, "service_id", "");
				return;
			}
			List<AnalysisResult> resultList = null;
			List<JsonAnalysisResult> jresultList = new ArrayList<>();
			resultList = asDao.getJobResultsList(targetLoginID, serviceid);
//			if (resultList == null) {
//				ReturnParser.outputErrorException(response,
//						AllConstants.ErrorDictionary.Internal_Fault, null, "");
//				return;
//			}
			for (int i = 0; i < resultList.size(); i++) {
				AnalysisResult result = resultList.get(i);
				JsonAnalysisResult jresult = new JsonAnalysisResult();
				BeanUtils.copyProperties(jresult, result);
				jresult.setJson_results(null);
				AnalysisModel model=asDao.getModelByID(jresult.getModelId());
				if(model!=null){
				jresult.setModel_name(asDao.getModelByID(jresult.getModelId())
						.getName());
				jresultList.add(jresult);
				}
			}

			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			JsonElement jelement = gson.toJsonTree(jresultList);
			jo.add("jobs", jelement);
//			System.out.println(gson.toJson(jo));
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
