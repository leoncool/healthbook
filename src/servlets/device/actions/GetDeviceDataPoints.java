/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.device.actions;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.hbase.models.HBaseDataImport;
import health.input.util.DBtoJsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import util.AllConstants;
import util.DateUtil;
import util.ServerUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 *
 * @author Leon
 */
public class GetDeviceDataPoints extends HttpServlet {

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

//        PrintWriter out = response.getWriter();
        OutputStream outStream = null;
        try {

            long start = 0;
            long end = 0;
            String blockid = null;
            DateUtil dateUtil=new DateUtil();
            try {
                if (request.getParameter(AllConstants.api_entryPoints.request_api_start) != null) {
                    start = Long.parseLong(request.getParameter(AllConstants.api_entryPoints.request_api_start));
                }
                if (request.getParameter(AllConstants.api_entryPoints.request_api_end) != null) {
                    end = Long.parseLong(request.getParameter(AllConstants.api_entryPoints.request_api_end));
                }
                if (request.getParameter(AllConstants.api_entryPoints.request_api_blockid) != null) {
                    blockid = request.getParameter(AllConstants.api_entryPoints.request_api_blockid);
                    if (blockid.length() < 5) {
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String deviceID = ServerUtil.getDeviceID(ServletPath(request));
            DatastreamDAO dstreamDao = new DatastreamDAO();
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
            Datastream datastream = dstreamDao.getDatastream(deviceID, true, false);
            if (blockid != null && dstreamDao.getDatastreamBlock(blockid) == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_Datablock_ID, null, blockid);
                return;
            }
            if (datastream == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_StreamID, null, deviceID);
                return;
            }
            HashMap<String, String> mapUnits = new HashMap<String, String>();
            HashMap<String, String> allUnits = new HashMap<String, String>();
            if (request.getParameter(AllConstants.api_entryPoints.request_api_unit_id) != null
                    && request.getParameter(AllConstants.api_entryPoints.request_api_unit_id).length() > 0) {
                String[] unitids = request.getParameter(AllConstants.api_entryPoints.request_api_unit_id).split(",");
                System.out.println("unitids:size:" + unitids.length);
                allUnits = dbtoJUtil.ToDatastreamUnitsMap(datastream);
                System.out.println("units size:" + datastream.getDatastreamUnitsList().size());
                for (String id : unitids) {
                    if (id.length() < 3) {
                        //error
                        return;
                    } else {
                        if (allUnits.get(id) == null) {
                            //error
                            System.out.println("cannot find id" + id + "");
                            return;
                        } else {
                            mapUnits.put(id, id);
                        }
                    }
                }
            }
            System.out.println("mapUnits.size():" + mapUnits.size() + ", " + mapUnits);
            Gson gson = new Gson();
            int debug = 1;
            if (debug == 1) {
                System.out.println("debuging.....going to hbase");
                HBaseDatapointDAO diDao = new HBaseDatapointDAO();
                HBaseDataImport hbaseexport=null;
                try {
                    hbaseexport = diDao.exportDatapoints(deviceID, start, end, blockid, mapUnits,null);
                } catch (ErrorCodeException ex) {
                    Logger.getLogger(GetDeviceDataPoints.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Throwable ex) {
                    Logger.getLogger(GetDeviceDataPoints.class.getName()).log(Level.SEVERE, null, ex);
                }
                hbaseexport.setDatastream_id(null);
                hbaseexport.setUnits_list(dbtoJUtil.convertDatastreamToJsonDevice(datastream, mapUnits).getUnits_list());
                outStream = null;
                boolean iftoZip = true;
                String encodings = request.getHeader("Accept-Encoding");
                if (encodings != null && encodings.indexOf("gzip") != -1 && iftoZip == true) {
                    // Go with GZIP
                    response.setHeader("Content-Encoding", "gzip");
                    outStream = new GZIPOutputStream(response.getOutputStream());
                } else {
                    outStream = response.getOutputStream();
                }
                response.setHeader("Vary", "Accept-Encoding");
                Date timerStart = new Date();
                JsonElement je = gson.toJsonTree(hbaseexport);
                JsonObject jo = new JsonObject();
                jo.add("datapoints_list", je);
                OutputStreamWriter osWriter = new OutputStreamWriter(outStream);
                JsonWriter jwriter = new JsonWriter(osWriter);
                gson.toJson(jo, jwriter);
                jwriter.close();
                Date timerEnd = new Date();
                System.out.println("Json Time takes:" + (timerEnd.getTime() - timerStart.getTime()) / (1000.00) + "seconds");
                osWriter.close();
                outStream.close();
            } else {
                String encodings = request.getHeader("Accept-Encoding");

                boolean iftoZip = true;
                if (encodings != null && encodings.indexOf("gzip") != -1 && iftoZip == true) {
                    // Go with GZIP
                    response.setHeader("Content-Encoding", "gzip");
                    outStream = new GZIPOutputStream(response.getOutputStream());
                } else {
                    outStream = response.getOutputStream();
                }
                response.setHeader("Vary", "Accept-Encoding");
                File inputFile = new File("E:/IC_Dropbox/Dropbox/java/healthbook/sample_data/download.txt");
//                File inputFile = new File("F:/Dropbox/Dropbox/java/healthbook/sample_data/download.txt");
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    outStream.write(inputLine.getBytes());
                    //  out.print(inputLine);
                }
                outStream.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
            return;
        } finally {
            System.out.println("running finally");
//            out.close();
            if (outStream != null) {
                outStream.close();
            }
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
