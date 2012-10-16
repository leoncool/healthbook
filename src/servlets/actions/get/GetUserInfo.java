/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import health.database.DAO.FollowingDAO;
import health.database.DAO.UserDAO;
import health.database.models.Follower;
import health.database.models.Users;
import health.database.models.merge.UserInfo;
import health.input.jsonmodels.JsonUserInfo;
import health.input.util.DBtoJsonUtil;
import server.exception.ReturnParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.AllConstants;
import util.JsonUtil;

/**
 *
 * @author Leon
 */
public class GetUserInfo extends HttpServlet {

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
            if (request.getParameter(AllConstants.api_entryPoints.request_api_loginid) == null
                    || request.getParameter(AllConstants.api_entryPoints.request_api_loginid).length() < 1) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, null, null);
                return;
            }
            String targetID=null;
            targetID= request.getParameter(AllConstants.api_entryPoints.request_api_targetid);
            if(targetID==null)
            {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, null, null);
                return;
            }
            String loginID=null;
            loginID= request.getParameter(AllConstants.api_entryPoints.request_api_loginid);
            UserDAO userDao = new UserDAO();
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
//            dbtoJUtil.convert_a_Subject(null)
            UserInfo userinfo = userDao.getUserInfo(targetID);
            if (userinfo == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_LoginID, null, null);
                return;
            }
            FollowingDAO followingDao = new FollowingDAO();
            List<Follower> follwerList = followingDao.getFollowers(targetID);
            List<Follower> follweringList = followingDao.getFollowerings(targetID);
            Map<String,String> followerMap=null;
         	Map<String,String> followeringsMap=null;
            if(loginID!=null)
            {
            	followerMap=followingDao.followerToMap(loginID);
            	followeringsMap=followingDao.followingsToMap(loginID);
            	            	
            }
            JsonUserInfo juserinfo = dbtoJUtil.convert_a_userinfo(userinfo,followerMap,followeringsMap);
            juserinfo.setTotal_followers(Integer.toString(follwerList.size()));
            juserinfo.setTotal_followings(Integer.toString(follweringList.size()));
            JsonElement je = gson.toJsonTree(juserinfo);
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.add("userinfo", je);
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
