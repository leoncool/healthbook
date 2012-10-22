/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.device.actions;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.Subject;
import health.input.jsonmodels.JsonDatastreamUnits;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.JsonUtil;
import util.UnitValueTypes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import device.input.jsonmodels.JsonDevice;

/**
 *
 * @author Leon
 */
public class PostDevice extends HttpServlet {

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
            JsonDevice jdevice = gson.fromJson(jutil.readJsonStrFromHttpRequest(request), JsonDevice.class);
            if (jdevice == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
                return;
            }
            if (jdevice.getOwner() == null || jdevice.getDevice_name() == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, null, null);
                return;
            }
            System.out.println("debug0");
            UserDAO userDao = new UserDAO();
            if (!userDao.existLogin(jdevice.getOwner())) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unauthorized_Access, null, null);
                return;
            }
            System.out.println("debug1");
            SubjectDAO subdao = new SubjectDAO();
            Subject subject = subdao.findDevicePurposeSubject(jdevice.getOwner());

            if (subject == null) {
                Date now = new Date();
                subject = new Subject();
                subject.setCreatedTime(now);
                subject.setLoginID(jdevice.getOwner());
                subject.setTitle("DEFAULT");
                subject.setDescription("DEFAULT");
                subject.setPrivateSet("private");
                subject.setUpdated(now);
                subject.setPurpose(AllConstants.ProgramConts.subject_medical_device_purpose);
                subject.setCreator(jdevice.getOwner());
                subject = subdao.createSubject(subject);
            }
            Datastream datastream = new Datastream();
            datastream.setSubId(subject.getId());
            UUID streamUUID = UUID.randomUUID();
            datastream.setStreamId(streamUUID.toString());
            Date now = new Date();
            datastream.setCreatedTime(now);
            datastream.setOwner(subject.getLoginID());
            datastream.setTitle(jdevice.getDevice_name());
            ArrayList<DatastreamUnits> datastreamUnits = new ArrayList<DatastreamUnits>();
            for (JsonDatastreamUnits unit : jdevice.getUnits_list()) {
                if (unit.getValue_type() == null) {
                    ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, "unit_value_type", null);
                    return;
                }
                if (!UnitValueTypes.existValueType(unit.getValue_type())) {
                    ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_ValueType, AllConstants.ErrorDictionary.Invalid_ValueType, unit.getValue_type());
                    return;
                }
                DatastreamUnits dsUnit = new DatastreamUnits();
                dsUnit.setStreamID(datastream);
                dsUnit.setCreatedTime(new Date());
                dsUnit.setUpdatedTime(new Date());
                dsUnit.setMaxValue(unit.getMax_value());
                dsUnit.setMinValue(unit.getMin_value());
                dsUnit.setCurrentValue(unit.getCurrent_value());
                dsUnit.setUnitLabel(unit.getUnit_label());
                dsUnit.setValueType(unit.getValue_type());
                dsUnit.setUnitSymbol(unit.getUnit_symbol());
                dsUnit.setUnitID(UUID.randomUUID().toString());
                datastreamUnits.add(dsUnit);
            }
            datastream.setDatastreamUnitsList(datastreamUnits);
            DatastreamDAO dstreamDao = new DatastreamDAO();
            System.out.println("testtestset");
            datastream = dstreamDao.createDatastream(datastream, datastreamUnits);
            DBtoJsonUtil dbjUtil = new DBtoJsonUtil();
            try {
                JsonDevice outputJdevice = dbjUtil.convertDatastreamToJsonDevice(datastream, null);
                System.out.println("testtestset");
                out.println(gson.toJson(outputJdevice));
                System.out.println(gson.toJson(outputJdevice));
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
