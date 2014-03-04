/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.humanmodels.get;

import health.database.DAO.FollowingDAO;
import health.database.DAO.UserDAO;
import health.database.models.Follower;
import health.database.models.merge.UserInfo;
import health.input.jsonmodels.JsonDatastreamBlock;
import health.input.jsonmodels.JsonUserInfo;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.physiology.calculator.HumanTemperature;
import com.physiology.input.jsonmodels.JsonTemperature;

/**
 * 
 * @author Leon
 */
public class GetTemperatureSimulation extends HttpServlet {

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
			HttpServletResponse response) throws ServletException,
			UnsupportedEncodingException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		JsonUtil jutil = new JsonUtil();
		Gson gson = new Gson();

		String input = jutil.readJsonStrFromHttpRequest(request);
		JsonTemperature jobject = null;
		HashMap<String, Object> inputs = new HashMap<String, Object>();
		double weight = 0;
		double WORK = 74;
		double RH = 0.3;
		double TAIR = 25;
		double VAIR = 0.1;
		int elapsed_seconds = 60;
		try {
			jobject = gson.fromJson(input, JsonTemperature.class);
			if (jobject.getWeight() > 0) {
				weight = jobject.getWeight();
			}
			if (jobject.getWork() > 0) {
				WORK = jobject.getWork();
			}
			if (jobject.getRh() > 0) {
				RH = jobject.getRh();
			}
			if (jobject.getTair() > 0) {
				TAIR = jobject.getTair();
			}
			if (jobject.getVair() > 0) {
				VAIR = jobject.getVair();
			}
			if (jobject.getElapsed_seconds() > 0) {
				elapsed_seconds = jobject.getElapsed_seconds();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Input_Json_Format_Error, null,
					null);
			return;
		}
		try {
			HumanTemperature ht = new HumanTemperature();
			System.out.println("start....");

			try {
				double[] T = HumanTemperature
						.convertStringToDoubleArray(jobject.getT_list());
				inputs.put("T", T);
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Input_Json_Format_Error,
						"temperature syntax error", "temperature syntax error");
				return;
			}
			inputs.put("weight", weight);
			inputs.put("RH", RH);
			inputs.put("WORK", WORK);
			inputs.put("TAIR", TAIR);
			inputs.put("VAIR", VAIR);
			inputs.put("elapsed_seconds", elapsed_seconds);
			HashMap<String, Object> results = HumanTemperature
					.CalculateTemperature(inputs);
			if (results == null) {
				ReturnParser
						.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, null);
				return;
			}
			double[] Tnew = (double[]) results.get("T");
			System.out.println("finish....");
			jobject.setT_average((Double) results.get("t_average"));
			jobject.setT_list(HumanTemperature.convertDoubleArrayToString(Tnew));
			JsonElement je = gson.toJsonTree(jobject);
			jobject.setElapsed_seconds(elapsed_seconds);
			jobject.setWork(WORK);
			jobject.setRh(RH);
			jobject.setWeight(weight);
			jobject.setTair(TAIR);
			jobject.setVair(VAIR);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("temperature", je);
			JsonWriter jwriter = new JsonWriter(response.getWriter());
			gson.toJson(jo, jwriter);
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
