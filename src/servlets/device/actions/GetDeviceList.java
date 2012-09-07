/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.device.actions;

import servlets.actions.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import device.input.jsonmodels.JsonDevice;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.input.jsonmodels.JsonDatastream;
import health.input.util.DBtoJsonUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server.exception.ReturnParser;
import util.AllConstants;
import util.ServerUtil;
import static util.JsonUtil.ServletPath;

/**
 *
 * @author Leon
 */
public class GetDeviceList extends HttpServlet {

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
            String loginid = "leoncool";
            if (request.getParameter(AllConstants.api_entryPoints.request_api_loginid) != null) {
                loginid = request.getParameter(AllConstants.api_entryPoints.request_api_loginid);
            }
            UserDAO userDao = new UserDAO();
            if (!userDao.existLogin(loginid)) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unauthorized_Access, null, loginid);
                return;
            }
            Subject subject = subjDao.findDevicePurposeSubject(loginid);
            if (subject == null) {
                Date now = new Date();
                subject = new Subject();
                subject.setCreatedTime(now);
                subject.setLoginID(loginid);
                subject.setTitle("DEFAULT");
                subject.setDescription("DEFAULT");
                subject.setPrivateSet("private");
                subject.setUpdated(now);
                subject.setPurpose(AllConstants.ProgramConts.subject_medical_device_purpose);
                subject.setCreator(loginid);
                subject = subjDao.createSubject(subject);
            }
            DatastreamDAO dstreamDao = new DatastreamDAO();
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
            List<Datastream> dsList = dstreamDao.getDatastreamList(subject.getId(), true, false);
            System.out.println("dsList size:" + dsList.size());
            List<JsonDevice> jsonDsList = new ArrayList<JsonDevice>();
            for (Datastream ds : dsList) {
                jsonDsList.add(dbtoJUtil.convertDatastreamToJsonDevice(ds, null));
            }
            System.out.println(jsonDsList.size());
            Gson gson = new Gson();
            JsonElement je = gson.toJsonTree(jsonDsList);
            JsonObject jo = new JsonObject();
            jo.add("device_list", je);
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
