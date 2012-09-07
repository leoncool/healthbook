/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.device.actions;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import health.database.DAO.DeviceSerialDAO;
import health.database.models.DeviceBinding;
import device.input.jsonmodels.JsonDeviceBinding;
import health.input.util.DBtoJsonUtil;
import java.io.IOException;
import java.io.PrintWriter;
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
public class GetADeviceBinding extends HttpServlet {

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
            String serialID = ServerUtil.getDeviceSerialID(ServletPath(request));
            DeviceBinding device = dsDap.getDeviceSerial(serialID);
            if (device == null) {
                ReturnParser.outputErrorException(response, AllConstants.DeviceErrorDictionary.Invalid_device_serial_id, null, serialID);
                return;
            }
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
            JsonDeviceBinding jdevice = dbtoJUtil.convertDeviceSerial(device);
            Gson gson = new Gson();
            JsonElement je = gson.toJsonTree(jdevice);
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.add("device_binding", je);
            System.out.println(jo.toString());
            out.println(gson.toJson(jo));
        } catch (Exception ex) {
            ex.printStackTrace();
            ReturnParser.outputErrorException(response, AllConstants.DeviceErrorDictionary.Internal_Fault, null, null);
            return;
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
