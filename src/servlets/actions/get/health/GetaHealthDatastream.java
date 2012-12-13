/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.health;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import servlets.util.ServerUtil;
import util.AllConstants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author Leon
 */
public class GetaHealthDatastream extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json"); 
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        try {
        	String loginID = "leoncool";
			if (request
					.getParameter(AllConstants.api_entryPoints.request_api_loginid) != null) {
				loginID = request
						.getParameter(AllConstants.api_entryPoints.request_api_loginid);
			}
			UserDAO userDao = new UserDAO();
			if (!userDao.existLogin(loginID)) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unauthorized_Access, null,
						null);
				return;
			}
			SubjectDAO subjDao = new SubjectDAO();
			Subject subject = (Subject) subjDao.findHealthSubject(loginID); // Retreive
			if (subject == null) {
//				ReturnParser.outputErrorException(response,
//						AllConstants.ErrorDictionary.SYSTEM_ERROR_NO_DEFAULT_HEALTH_SUBJECT, null,
//						null);
//				return;
				subject = subjDao.createDefaultSubject(loginID);
			}
            String streamID = ServerUtil.getHealthStreamID(ServletPath(request));
            
            DatastreamDAO dstreamDao = new DatastreamDAO();
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
            Datastream datastream = dstreamDao.getDatastream(streamID, true, false);
            if (datastream == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_StreamID, null, streamID);
                return;
            }
            Gson gson = new Gson();
            JsonElement je = gson.toJsonTree(dbtoJUtil.convertDatastream(datastream, null));
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.add("datastream", je);
            System.out.println(jo.toString());
            out.println(gson.toJson(jo));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
