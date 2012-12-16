/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.Subject;
import health.database.models.Users;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataPointsPostResult;
import health.input.jsonmodels.JsonDataValues;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

/**
 *
 * @author Leon
 */
public class PostDatapoints extends HttpServlet {

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
            Subject subject = subjDao.getSubjectByID(subID);
            if (subject == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_SubjectID, AllConstants.ErrorDictionary.Unknown_SubjectID, Integer.toString(subID));
                return;
            }
            DatastreamDAO dsDao = new DatastreamDAO();
            Datastream stream = dsDao.getDatastream(streamID, true, false);
            if (stream == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_StreamID, AllConstants.ErrorDictionary.Unknown_StreamID, streamID);
                return;
            }
            List<DatastreamUnits> unitList = stream.getDatastreamUnitsList();
            HashMap<String, String> unitIDList = new HashMap<String, String>();
            for (DatastreamUnits unit : unitList) {
                unitIDList.put(unit.getUnitID(), unit.getUnitID());   //retrieve all existing units
            }
            UserDAO userDao = new UserDAO();
            Users user = userDao.getLogin(subject.getLoginID());
            JsonUtil jutil = new JsonUtil();
            Gson gson = new Gson();
            JsonDataImport jdataImport = null;
            int jsonInputTotalByte = 0;
            try {
                String jsonInput = jutil.readJsonStrFromHttpRequest(request);
                jsonInputTotalByte = jsonInput.length();
                jdataImport = gson.fromJson(jsonInput, JsonDataImport.class);
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
                return;
            }
            if (jdataImport == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
                return;
            }
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
            HBaseDatapointDAO importDao = null;
            try {
                importDao = new HBaseDatapointDAO();
            } catch (Exception ex) {
                ex.printStackTrace();
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                return;
            }
            HBaseDataImport importData = new HBaseDataImport();
            importData.setData_points(jdataImport.getData_points());
            importData.setDatastream_id(streamID);
            importData.setBlock_id(jdataImport.getBlock_id());
            JsonDataPointsPostResult jsonResult = new JsonDataPointsPostResult();
            try {
                jsonResult.setTotal_stored_byte(importDao.importDatapointsDatapoints(importData));
            } catch (ErrorCodeException ex) {
                ex.printStackTrace();
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                return;
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_date_format, null, null);
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                return;
            }
            jsonResult.setTotal_points(jdataImport.getData_points().size());
            jsonResult.setTotal_input_byte(jsonInputTotalByte);
            JsonElement je = gson.toJsonTree(jsonResult);
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.add("data_import_stat", je);
            JsonWriter jwriter = new JsonWriter(out);
            gson.toJson(jo, jwriter);
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
