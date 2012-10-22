/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.device.actions;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DataImportDAO;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.Users;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.JsonUtil;
import util.ServerUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author Leon
 */
public class PostDeviceDatapoints extends HttpServlet {

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
            String deviceID = ServerUtil.getDeviceID(ServletPath(request));
            System.out.println("deviceid:" + deviceID);
            if (deviceID == null || deviceID.length() < 3) {
                ReturnParser.outputErrorException(response, AllConstants.DeviceErrorDictionary.Unknown_DeviceID, AllConstants.DeviceErrorDictionary.Invalid_deviceid, deviceID);
                return;
            }
            DatastreamDAO dsDao = new DatastreamDAO();
            Datastream stream = dsDao.getDatastream(deviceID, true, false);
            if (stream == null) {
                ReturnParser.outputErrorException(response, AllConstants.DeviceErrorDictionary.Unknown_DeviceID, AllConstants.DeviceErrorDictionary.Unknown_DeviceID, deviceID);
                return;
            }
            List<DatastreamUnits> unitList = stream.getDatastreamUnitsList();
            HashMap<String, String> unitIDList = new HashMap<String, String>();
            for (DatastreamUnits unit : unitList) {
                unitIDList.put(unit.getUnitID(), unit.getUnitID());   //retrieve all existing units
            }
            Users user = (Users) subjDao.getObjectByID(Users.class, stream.getOwner());
            JsonUtil jutil = new JsonUtil();
            Gson gson = new Gson();
            JsonDataImport jdataImport = gson.fromJson(jutil.readJsonStrFromHttpRequest(request), JsonDataImport.class);
            if (jdataImport.getData_points().isEmpty()) {
                System.out.println("Null Datapoints");
                return;
            }
            for (int i = 0; i < jdataImport.getData_points().size(); i++) {
                List<JsonDataValues> jvalueList = jdataImport.getData_points().get(i).getValue_list();
                for (int j = 0; j < jvalueList.size(); j++) {
                    String unitID = jvalueList.get(j).getUnit_id();
                    if (unitIDList.get((String) unitID) == null) {
                        ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_Unit_ID, AllConstants.ErrorDictionary.Invalid_Unit_ID, unitID);
                        return;
                    }
                }
            }
            if (jdataImport.getBlock_id() != null) {
                if (dsDao.getDatastreamBlock(jdataImport.getBlock_id()) == null) {
                    ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_Datablock_ID, AllConstants.ErrorDictionary.Invalid_Datablock_ID, jdataImport.getBlock_id());
                    return;
                }
            }
            DataImportDAO importDao = new DataImportDAO();
            HBaseDataImport importData = new HBaseDataImport();
            importData.setData_points(jdataImport.getData_points());
            importData.setDatastream_id(deviceID);
            importData.setBlock_id(jdataImport.getBlock_id());
            importDao.importDatapoints(importData);
        } catch (JsonSyntaxException ex) {
            ex.printStackTrace();
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
