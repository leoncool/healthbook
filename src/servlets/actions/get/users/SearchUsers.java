/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.users;

import health.database.DAO.FollowingDAO;
import health.database.DAO.UserDAO;
import health.database.models.merge.UserInfo;
import health.input.jsonmodels.JsonUserInfo;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 *
 * @author Leon
 */
public class SearchUsers extends HttpServlet {

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
            if (request.getParameter(AllConstants.api_entryPoints.request_api_keywords) == null
                    || request.getParameter(AllConstants.api_entryPoints.request_api_keywords).length() < 1) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, null, null);
                return;
            }
            int startPage = 0;
            try {
                if (request.getParameter(AllConstants.api_entryPoints.request_api_startpage) != null) {
                    startPage = Integer.parseInt(request.getParameter(AllConstants.api_entryPoints.request_api_startpage));
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.INPUT_DATE_FORMAT_ERROR, null, null);
                return;
            }
            String keywords = request.getParameter(AllConstants.api_entryPoints.request_api_keywords);
            String loginID=null;
            Map<String,String> followerMap=null;
         	Map<String,String> followeringsMap=null;
            if(request.getParameter(AllConstants.api_entryPoints.request_api_loginid)!=null)
            {
            	loginID=request.getParameter(AllConstants.api_entryPoints.request_api_loginid);
            }
           
            UserDAO userDao = new UserDAO();
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
//            dbtoJUtil.convert_a_Subject(null)
            Map<String,String> ignoreMap=new HashMap<String, String>();
            if(loginID!=null)
            {
            	ignoreMap.put(loginID, loginID);
            }
            List<UserInfo> userinfoList = userDao.searchUserInfo(keywords, startPage,ignoreMap);
            List<JsonUserInfo> juserinfoList=new ArrayList<JsonUserInfo>();
            for(UserInfo info:userinfoList)
            {
            	 if(loginID!=null)
                 {
                 	FollowingDAO followingDao=new FollowingDAO();
                 	followerMap=followingDao.followerToMap(loginID);
                 	followeringsMap=followingDao.followingsToMap(loginID);
                 	            	
                 }
                juserinfoList.add(dbtoJUtil.convert_a_userinfo(info,followerMap,followeringsMap));
            }
            JsonElement je = gson.toJsonTree(juserinfoList);
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.add("user_list", je);
            JsonWriter jwriter = new JsonWriter(response.getWriter());
            gson.toJson(jo, jwriter);
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
