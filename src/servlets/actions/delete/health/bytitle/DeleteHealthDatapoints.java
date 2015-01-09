/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.delete.health.bytitle;

import health.database.DAO.UserDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.database.models.Users;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.HealthDatastreamFilter;
import servlets.util.HealthSubjectFilter;
import servlets.util.PermissionFilter;
import util.AllConstants;
import util.AllConstants.api_entryPoints;
import util.DateUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 
 * @author Leon
 */
public class DeleteHealthDatapoints extends HttpServlet {

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
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		System.out.println("before checkAndGetLoginFromToken");
		PrintWriter out = response.getWriter();
		try {
			Users accessUser = null;
			PermissionFilter filter = new PermissionFilter();
			String loginID = filter
					.checkAndGetLoginFromToken(request, response);

			UserDAO userDao = new UserDAO();
			if (loginID == null) {
				if (filter.getCheckResult().equalsIgnoreCase(
						filter.INVALID_LOGIN_TOKEN_ID)) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_login_token_id,
									null, null);
					return;
				} else if (filter.getCheckResult().equalsIgnoreCase(
						AllConstants.ErrorDictionary.login_token_expired)) {
					return;
				} else {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_login_token_id,
									null, null);
					return;
				}
			} else {
				accessUser = userDao.getLogin(loginID);
			}
			if(loginID.equalsIgnoreCase("dongdong"))
			{
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.PERMISSION_DENIED, null,
						null);
				return;
			}
			HealthSubjectFilter subFilter = new HealthSubjectFilter();
			Subject subject = subFilter.subjectFilter(loginID, request,
					response);
			if (subject == null) {
				ReturnParser
						.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, null);
				return;
			}

			HealthDatastreamFilter datastreamfilter = new HealthDatastreamFilter();
			Datastream datastream = datastreamfilter.datastreamFilter(loginID,
					subject, request, response);

			String atString = request
					.getParameter(api_entryPoints.request_api_at);
			long totalDeleted = 0;
			if (atString != null) {
				boolean isNumeric = atString.matches("[0-9]+");
				long at = 0;
				if (isNumeric) {
					try {
						at = Long.parseLong(atString);
					} catch (Exception ex) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.Invalid_date_format,
										null, null);
						return;
					}
				} else {
					try {
						DateUtil dateUtil = new DateUtil();
						Date at_date = dateUtil.convert_SetLenient(atString,
								dateUtil.utcFormat);
						at = at_date.getTime();
						System.out.println("fromUTC:longAt:" + at);
					} catch (Exception ex) {
						ex.printStackTrace();
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.INPUT_DATE_FORMAT_ERROR,
										null, null);
						return;
					}
				}
				try {

					class DeleteThread extends Thread {
						Datastream datastream;
						long at;
						String loginID;
					    public DeleteThread(Datastream _datastream, long _at,String _loginID) {
					    	datastream=_datastream;
					    	at=_at;
					    	loginID=_loginID;
					    }
					 
					    public void run() {
					    	System.out.println("Delete Runnable running");
					        HBaseDatapointDAO dpDap = new HBaseDatapointDAO();
							try {
								dpDap.delete_A_Datapoint(
										datastream.getStreamId(), at, null,loginID);
							} catch (ErrorCodeException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    }
					}
					DeleteThread delete=new DeleteThread(datastream, at,loginID);
					delete.start();
					totalDeleted=1;
	
				} catch (Exception ex) {
					ex.printStackTrace();
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Internal_Fault, null,
							null);
					return;
				}
			} else if (request.getParameter(api_entryPoints.request_api_start) != null
					&& request.getParameter(api_entryPoints.request_api_end) != null) {
				long start = 0;
				long end = 0;
				String startStr = request
						.getParameter(api_entryPoints.request_api_start);
				String endStr = request
						.getParameter(api_entryPoints.request_api_end);
				try {
					if (startStr != null) {
						start = Long.parseLong(startStr);
					}
					if (endStr != null) {
						end = Long.parseLong(endStr);
					}
					HBaseDatapointDAO dpDap = new HBaseDatapointDAO();
					totalDeleted = dpDap.delete_range_Datapoint(
							datastream.getStreamId(), start, end); // has not
																	// finished
																	// yet

				} catch (Exception ex) {
					ex.printStackTrace();
					ReturnParser.outputErrorException(response,
							AllConstants.ErrorDictionary.Invalid_date_format,
							null, null);
					return;
				}
			} else {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null, null);
				return;
			}
			Gson gson = new Gson();
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.addProperty(AllConstants.ProgramConts.total_deleted_records,
					Long.toString(totalDeleted));
			out.println(gson.toJson(jo));

		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;

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
