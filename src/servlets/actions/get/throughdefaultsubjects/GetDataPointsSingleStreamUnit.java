/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.throughdefaultsubjects;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.singleunitstream.JsonSingleDataPoints;
import health.input.util.DBtoJsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.DateUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 *
 * @author Leon
 */
public class GetDataPointsSingleStreamUnit extends HttpServlet {

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

			try {
				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_start) != null) {
					start = Long
							.parseLong(request
									.getParameter(AllConstants.api_entryPoints.request_api_start));
				}
				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_end) != null) {
					end = Long
							.parseLong(request
									.getParameter(AllConstants.api_entryPoints.request_api_end));
				}

				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay) != null) {
					String yearMonthDateString=request
							.getParameter(AllConstants.api_entryPoints.request_api_YearMonthDay);
					System.out.println("Date Request "+yearMonthDateString);
					DateUtil dateUtil=new DateUtil();
					Date date = dateUtil.convert(yearMonthDateString,dateUtil.YearMonthDay_DateFormat);
									System.out.println("DateRequest:"+date);
									Calendar calStart = Calendar.getInstance(DateUtil.UTC);
									Calendar calEnd = Calendar.getInstance(DateUtil.UTC);
									calStart.setTime(date);
									calEnd.setTime(date);
									calStart.set(Calendar.HOUR_OF_DAY, 0);
									calStart.set(Calendar.MINUTE, 0);
									start = calStart.getTimeInMillis();
									calEnd.set(Calendar.HOUR_OF_DAY, 23);
									calEnd.set(Calendar.MINUTE, 59);
									end = calEnd.getTimeInMillis();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unauthorized_Access, null,
						null);
				return;
			}
			try {
				if (request
						.getParameter(AllConstants.api_entryPoints.request_api_blockid) != null) {
					if (blockid.length() > 5) {
						blockid = request
								.getParameter(AllConstants.api_entryPoints.request_api_blockid);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_Datablock_ID,
						null, null);
				return;
			}
			String loginID = "leoncool";
			if (request
					.getParameter(AllConstants.api_entryPoints.request_api_loginid) != null) {
				loginID = request
						.getParameter(AllConstants.api_entryPoints.request_api_loginid);
			}
			UserDAO userDao = new UserDAO();
			if (!userDao.existLogin(loginID)) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Unauthorized_Access, null,
						null);
				return;
			}
			SubjectDAO subjDao = new SubjectDAO();
			Subject subject = (Subject) subjDao.findSystem_Default_Subject(loginID); // Retreive
			if (subject == null) {
				ReturnParser
						.outputErrorException(
								response,
								AllConstants.ErrorDictionary.SYSTEM_ERROR_NO_DEFAULT_SUBJECT,
								null, null);
				return;
			}
			String streamID = ServerUtil
					.getDefault_Subject_StreamID(ServletPath(request));

        	
        	
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

            if(datastream.getDatastreamUnitsList().size()==0)
            {
            	 ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_StreamID, null, streamID);
                 return;
            }
            if(datastream.getDatastreamUnitsList().size()>1)
            {
            	 ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MORE_THAN_ONE_DATASTREAM_UNIT, null, streamID);
                 return;
            }
            HashMap<String, String> mapUnits = new HashMap<String, String>();
            mapUnits.put(datastream.getDatastreamUnitsList().get(0).getUnitID(), datastream.getDatastreamUnitsList().get(0).getUnitID());
            Gson gson = new Gson();
            int debug = 1;
            if (debug == 1) {
                System.out.println("debuging.....going to hbase");
                HBaseDatapointDAO diDao = new HBaseDatapointDAO();
                System.out.println("datastreamID:" + datastream.getStreamId());
                HBaseDataImport hbaseexport = null;
                try {
                    hbaseexport = diDao.exportDatapointsForSingleUnit(streamID, start, end, blockid,datastream.getDatastreamUnitsList().get(0).getUnitID(),null,null);
                } catch (ErrorCodeException ex) {
                    ex.printStackTrace();
                    ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                    return;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                    return;
                }
                if (hbaseexport != null) {
                    hbaseexport.setUnits_list(dbtoJUtil.convertDatastream(datastream, mapUnits).getUnits_list());
                } else {
                    hbaseexport = new HBaseDataImport();
                    hbaseexport.setBlock_id(blockid);
                    hbaseexport.setData_points_single_list(new ArrayList<JsonSingleDataPoints>());
                    hbaseexport.setDatastream_id(streamID);
                    hbaseexport.setUnits_list(dbtoJUtil.convertDatastream(datastream, mapUnits).getUnits_list());
//                    hbaseexport.setDeviceid(streamID);
                }
                outStream = null;
                boolean iftoZip = true;
                String encodings = request.getHeader("Accept-Encoding");
                if (encodings != null && encodings.indexOf("gzip") != -1 && iftoZip == true) {
                    // Go with GZIP
                    response.setHeader("Content-Encoding", "gzip");
                    System.out.println("Using Gzip to compress.......");
                    outStream = new GZIPOutputStream(response.getOutputStream());
                } else {
                    outStream = response.getOutputStream();
                }
                response.setHeader("Vary", "Accept-Encoding");
                Date timerStart = new Date();
                JsonElement je = gson.toJsonTree(hbaseexport);
                JsonObject jo = new JsonObject();
                jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
                jo.add("datapoints_list", je);
                OutputStreamWriter osWriter = new OutputStreamWriter(outStream);
                JsonWriter jwriter = new JsonWriter(osWriter);
                String callbackStr = null;
                if (request.getParameter(AllConstants.api_entryPoints.requset_api_callback) != null) {
                    callbackStr = request.getParameter(AllConstants.api_entryPoints.requset_api_callback);
                    osWriter.append(callbackStr + "(");
                }
                gson.toJson(jo, jwriter);
                if (callbackStr != null) {
                    osWriter.append(");");
                }
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
                    System.out.println("Using Gzip to compress.......");
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
