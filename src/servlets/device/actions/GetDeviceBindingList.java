/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.device.actions;

import health.database.DAO.DeviceSerialDAO;
import health.database.models.DeviceBinding;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.AllConstants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import device.input.jsonmodels.JsonDeviceBinding;

/**
 *
 * @author Leon
 */
public class GetDeviceBindingList extends HttpServlet {

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

            String loginID = null;
            DeviceSerialDAO dsDap = new DeviceSerialDAO();
            List<DeviceBinding> dsList = new ArrayList<DeviceBinding>();
            List<JsonDeviceBinding> jdsList = new ArrayList<JsonDeviceBinding>();
            if (request.getParameter(AllConstants.api_entryPoints.request_api_loginid) != null) {
                loginID = request.getParameter(AllConstants.api_entryPoints.request_api_loginid);
            }
            dsList = dsDap.getDeviceSerialList(loginID);
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
            if (dsList != null && !dsList.isEmpty()) {
                for (DeviceBinding device : dsList) {
                    JsonDeviceBinding jdevice = dbtoJUtil.convertDeviceSerial(device);
                    jdsList.add(jdevice);
                }
            }

            Gson gson = new Gson();
            JsonElement je = gson.toJsonTree(jdsList);

            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.add("device_binding_list", je);
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
