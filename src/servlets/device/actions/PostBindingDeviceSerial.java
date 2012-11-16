/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.device.actions;

import health.database.DAO.DeviceSerialDAO;
import health.database.DAO.UserDAO;
import health.database.models.DeviceBinding;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

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

import device.input.jsonmodels.JsonDeviceBinding;

/**
 *
 * @author Leon
 */
public class PostBindingDeviceSerial extends HttpServlet {

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
            throws ServletException, UnsupportedEncodingException, IOException {
        response.setContentType("application/json"); 
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonUtil jutil = new JsonUtil();
        Gson gson = new Gson();
        try {
            JsonDeviceBinding jDevice = gson.fromJson(jutil.readJsonStrFromHttpRequest(request), JsonDeviceBinding.class);
            if (jDevice.getActive_by() == null || jDevice.getSerial_id() == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, null, null);
                return;
            }
            UserDAO userDao = new UserDAO();
       
				if (!userDao.existLogin(jDevice.getActive_by())) {
				    ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unauthorized_Access, null, null);
				    return;
				}
	
            Date now = new Date();
            DeviceSerialDAO dsDao = new DeviceSerialDAO();
            DeviceBinding device = dsDao.getDeviceSerial(jDevice.getSerial_id());
            if (device == null) {
                ReturnParser.outputErrorException(response, AllConstants.DeviceErrorDictionary.Unknown_DeviceSerialID, null, null);
                return;
            }
            if (device.getActiveBy() != null || device.getOwner() != null) {
                ReturnParser.outputErrorException(response, AllConstants.DeviceErrorDictionary.DeviceBindingAlreadyActived, null, null);
                return;
            }
            device = dsDao.activeDevice(device, jDevice.getActive_by());
            if (device == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.unknownFault, null, null);
                return;
            }

            DBtoJsonUtil djUtil = new DBtoJsonUtil();
            try {
                JsonDeviceBinding jdevice = djUtil.convertDeviceSerial(device);
                JsonElement je = gson.toJsonTree(jdevice);
                JsonObject jo = new JsonObject();
                jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
                jo.add("device_serial_binding", je);
                JsonWriter jwriter = new JsonWriter(out);
                gson.toJson(jo, jwriter);
                out.println(gson.toJson(jdevice));
                System.out.println(gson.toJson(jdevice));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        } catch (JsonSyntaxException ex) {
            ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
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
