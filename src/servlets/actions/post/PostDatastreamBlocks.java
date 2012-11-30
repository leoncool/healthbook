/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamBlocks;
import health.database.models.Subject;
import health.input.jsonmodels.JsonDatastreamBlock;
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
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author Leon
 */
public class PostDatastreamBlocks extends HttpServlet {

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
            SubjectDAO subjDao = new SubjectDAO();
            int subID = ServerUtil.getSubjectID(ServletPath(request));
            String streamID = ServerUtil.getStreamID(ServletPath(request));
            Subject subject = null;
            try {
                subject = (Subject) subjDao.getObjectByID(Subject.class, subID);
                if (subject == null) {
                    ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_SubjectID, AllConstants.ErrorDictionary.Unknown_SubjectID, Integer.toString(subID));
                    return;
                }
            } catch (Exception ex) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                ex.printStackTrace();
            }
            DatastreamDAO dsDao = new DatastreamDAO();
            Datastream stream = null;
            try {
                stream = dsDao.getDatastream(streamID, false, true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (stream == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_StreamID, AllConstants.ErrorDictionary.Unknown_StreamID, streamID);
                return;
            }
            Gson gson = new Gson();
            JsonDatastreamBlock jblock = null;
            try {
                JsonUtil jutil = new JsonUtil();
                jblock = gson.fromJson(jutil.readJsonStrFromHttpRequest(request), JsonDatastreamBlock.class);
            } catch (JsonSyntaxException ex) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
                ex.printStackTrace();
                return;
            }
            if (jblock == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
                return;
            }
            try {
                DatastreamBlocks block = dsDao.CreateDatastreamBlock(stream, jblock.getBlockname(), jblock.getBlockdesc());
                DBtoJsonUtil dbjUtil = new DBtoJsonUtil();
                jblock = dbjUtil.convert_a_Datablock(block);
                JsonElement je = gson.toJsonTree(jblock);
                JsonObject jo = new JsonObject();
                jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
                jo.add("datastream_block", je);
                out.println(gson.toJson(jo));
            } catch (Exception ex) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                ex.printStackTrace();
            }

        } catch (IOException ex) {
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
