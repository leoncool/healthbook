/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.users;

import health.database.DAO.FollowingDAO;
import health.database.DAO.UserDAO;
import health.database.models.Follower;
import health.database.models.Users;
import health.database.models.merge.UserInfo;
import health.input.jsonmodels.JsonUserInfo;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
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
public class GetMyAccountData extends HttpServlet {

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
        	Users accessUser = null;
    		PermissionFilter filter = new PermissionFilter();
    		String loginID = filter.checkAndGetLoginFromToken(request, response);

    		UserDAO userDao = new UserDAO();
    		if (loginID == null) {
    			if (filter.getCheckResult().equalsIgnoreCase(
    					filter.INVALID_LOGIN_TOKEN_ID)) {
    				ReturnParser.outputErrorException(response,
    						AllConstants.ErrorDictionary.Invalid_login_token_id,
    						null, null);
    				return;
    			} else if (filter.getCheckResult().equalsIgnoreCase(
    					AllConstants.ErrorDictionary.login_token_expired)) {
    				return;
    			} else {
    				ReturnParser.outputErrorException(response,
    						AllConstants.ErrorDictionary.Invalid_login_token_id,
    						null, null);
    				return;
    			}
    		} else {
    			accessUser = userDao.getLogin(loginID);
    		}
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
//            dbtoJUtil.convert_a_Subject(null)
            UserInfo userinfo = userDao.getUserInfo(accessUser.getLoginID());
            if (userinfo == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_LoginID, null, null);
                return;
            }
            FollowingDAO followingDao = new FollowingDAO();
            List<Follower> follwerList = followingDao.getFollowers(accessUser.getLoginID());
            List<Follower> follweringList = followingDao.getFollowerings(accessUser.getLoginID());
            Map<String,String> followerMap=null;
         	Map<String,String> followeringsMap=null;
         	
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
