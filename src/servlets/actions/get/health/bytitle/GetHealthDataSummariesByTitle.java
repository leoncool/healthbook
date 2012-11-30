/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.health.bytitle;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DataSummaryDAO;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.DataSummary;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.database.models.Users;
import health.input.jsonmodels.JsonDataSummary;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.DateUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * 
 * @author Leon
 */
public class GetHealthDataSummariesByTitle extends HttpServlet {

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
		System.out.println("before checkAndGetLoginFromToken");
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
			} else {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_login_token_id,
						null, null);
				return;
			}
		} else {
			accessUser = userDao.getLogin(loginID);
		}

		// PrintWriter out = response.getWriter();
		OutputStream outStream = null;
		try {

			long start = 0;
			long end = 0;
			Date date = null;
			Date startDate=null;
			Date endDate=null;
			try {
				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_start) != null
						&&request
						.getParameter(AllConstants.api_entryPoints.request_api_end) != null) {
					start = Long
							.parseLong(request
									.getParameter(AllConstants.api_entryPoints.request_api_start));
					end = Long
							.parseLong(request
									.getParameter(AllConstants.api_entryPoints.request_api_end));
				}
				else if (request
						.getParameter(AllConstants.api_entryPoints.request_api_startDate) != null
						&&request
						.getParameter(AllConstants.api_entryPoints.request_api_endDate) != null) {
					String startDateString = request
							.getParameter(AllConstants.api_entryPoints.request_api_startDate);
					String endDateString = request
							.getParameter(AllConstants.api_entryPoints.request_api_endDate);
					System.out.println("start Date" + startDateString);
					System.out.println("end Date" + startDateString);
					DateUtil dateUtil = new DateUtil();
					startDate = new Date();
					startDate = dateUtil.convert(startDateString,
							dateUtil.YearMonthDay_DateFormat);
					endDate = new Date();
					endDate = dateUtil.convert(endDateString,
							dateUtil.YearMonthDay_DateFormat);
				}
				else if (request
						.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay) != null) {
					String yearMonthDateString = request
							.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay);
					System.out.println("Date Request " + yearMonthDateString);
					DateUtil dateUtil = new DateUtil();
					date = new Date();
					date = dateUtil.convert(yearMonthDateString,
							dateUtil.YearMonthDay_DateFormat);
					System.out.println("DateRequest:" + date);

				}
				else{
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_date_format, null,
							null);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_date_format, null,
						null);
				return;
			}

			SubjectDAO subjDao = new SubjectDAO();
			Subject subject = (Subject) subjDao.findHealthSubject(loginID); // Retreive
			if (subject == null) {
				ReturnParser
						.outputErrorException(
								response,
								AllConstants.ErrorDictionary.SYSTEM_ERROR_NO_DEFAULT_HEALTH_SUBJECT,
								null, null);
				return;
			}
			String streamTitle = ServerUtil
					.getHealthStreamTitle(ServletPath(request));
			DatastreamDAO dstreamDao = new DatastreamDAO();
			DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
			Datastream datastream = null;
			try {
				datastream = dstreamDao.getDatastreamByTitle(subject.getId(),
						streamTitle, true, false);
			} catch (NonUniqueResultException ex) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null,
						streamTitle);
				return;
			}
			if (datastream == null) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unknown_StreamTitle, null,
						streamTitle);
				return;
			}
			String unitID = null;
			if (request
					.getParameter(AllConstants.api_entryPoints.request_api_unit_id) != null
					&& request.getParameter(
							AllConstants.api_entryPoints.request_api_unit_id)
							.length() > 0) {
				unitID = request
						.getParameter(AllConstants.api_entryPoints.request_api_unit_id);
			}
			Gson gson = new Gson();
			System.out.println("debuging.....going to get Data Summaries");
			System.out.println("datastreamID:" + datastream.getStreamId());
			List<DataSummary> summaryList = new ArrayList<DataSummary>();
			List<JsonDataSummary> jsummaryList = new ArrayList<JsonDataSummary>();
			DataSummaryDAO dsummaryDao = new DataSummaryDAO();
			if (date != null) {
				summaryList = dsummaryDao.getDataSummaries(
						datastream.getStreamId(), unitID, date);
			} else if(start!=0&&end!=0){
				summaryList = dsummaryDao.getDataSummariesByStartAndEndTime(
						datastream.getStreamId(), unitID, start, end);
			}
			else if(startDate!=null&&endDate!=null
					)
			{
				summaryList = dsummaryDao.getDataSummariesByStartAndEndTime(
						datastream.getStreamId(), unitID, startDate.getTime(), endDate.getTime());
			}
			for(DataSummary summary:summaryList)
			{
				jsummaryList.add(dbtoJUtil.convertDataSummary(summary));
			}
			outStream = null;
			boolean iftoZip = true;
			String encodings = request.getHeader("Accept-Encoding");
			if (encodings != null && encodings.indexOf("gzip") != -1
					&& iftoZip == true) {
				// Go with GZIP
				response.setHeader("Content-Encoding", "gzip");
				outStream = new GZIPOutputStream(response.getOutputStream());
			} else {
				outStream = response.getOutputStream();
			}
			response.setHeader("Vary", "Accept-Encoding");
			Date timerStart = new Date();
			JsonElement je = gson.toJsonTree(jsummaryList);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.addProperty("total_size", Integer.toString(jsummaryList.size()));
			jo.add("datasummary_list", je);
			
			OutputStreamWriter osWriter = new OutputStreamWriter(outStream);
			JsonWriter jwriter = new JsonWriter(osWriter);
			String callbackStr = null;
			if (request
					.getParameter(AllConstants.api_entryPoints.requset_api_callback) != null) {
				callbackStr = request
						.getParameter(AllConstants.api_entryPoints.requset_api_callback);
				osWriter.append(callbackStr + "(");
			}
			gson.toJson(jo, jwriter);
			if (callbackStr != null) {
				osWriter.append(");");
			}
			jwriter.close();
			Date timerEnd = new Date();
			System.out.println("Json Time takes:"
					+ (timerEnd.getTime() - timerStart.getTime()) / (1000.00)
					+ "seconds");
			osWriter.close();
			outStream.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;
		} finally {
			System.out.println("running finally");
			// out.close();
			if (outStream != null) {
				outStream.close();
			}
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
