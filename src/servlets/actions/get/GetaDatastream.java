/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
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
public class GetaDatastream extends HttpServlet {

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
            int subID = ServerUtil.getSubjectID(ServletPath(request));
            SubjectDAO subjDao = new SubjectDAO();
            String streamID = ServerUtil.getStreamID(ServletPath(request));
            Subject subject = (Subject) subjDao.getObjectByID(Subject.class, subID); //Retreive Subject from DB
            if (subject == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_SubjectID, null, Integer.toString(subID));
                return;
            }
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
