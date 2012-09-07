/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import health.database.DAO.DataImportDAO;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.hbase.models.HBaseDataImport;
import health.input.util.DBtoJsonUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server.exception.ReturnParser;
import util.AllConstants;
import util.DateUtil;
import util.ServerUtil;
import static util.JsonUtil.ServletPath;

/**
 *
 * @author Leon
 */
public class GetDataPointsLocalDebug extends HttpServlet {

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

            long start = 0;
            long end = 0;
            String blockid = null;
            try {
                if (request.getParameter(AllConstants.api_entryPoints.request_api_start) != null) {
                    start = DateUtil.parseMillisecFormatToLong(request.getParameter(AllConstants.api_entryPoints.request_api_start));
                }
                if (request.getParameter(AllConstants.api_entryPoints.request_api_end) != null) {
                    end = DateUtil.parseMillisecFormatToLong(request.getParameter(AllConstants.api_entryPoints.request_api_end));
                }
                if (request.getParameter(AllConstants.api_entryPoints.request_api_blockid) != null) {
                    blockid = request.getParameter(AllConstants.api_entryPoints.request_api_blockid);
                    if (blockid.length() < 5) {
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
            if (blockid != null && dstreamDao.getDatastreamBlock(blockid) == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_Datablock_ID, null, blockid);
                return;
            }
            if (datastream == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_StreamID, null, streamID);
                return;
            }
            File inputFile = new File("E:/IC_Dropbox/Dropbox/java/healthbook/sample_data/download.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                out.print(inputLine);
            }

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
