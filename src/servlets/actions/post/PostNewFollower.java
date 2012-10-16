/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.FollowingDAO;
import server.exception.ReturnParser;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Datastream;
import health.database.models.Follower;
import health.database.models.Subject;
import health.input.jsonmodels.JsonFollower;
import health.input.util.DBtoJsonUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PostNewFollower extends HttpServlet {

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

            JsonFollower jfollower = gson.fromJson(jutil.readJsonStrFromHttpRequest(request), JsonFollower.class);
            if (jfollower.getLoginid() == null || jfollower.getFollower_id() == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, null, null);
                return;
            }
            UserDAO userDao = new UserDAO();
            if (!userDao.existLogin(jfollower.getLoginid())) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_LoginID, null, null);
                return;
            }
            if (!userDao.existLogin(jfollower.getFollower_id())) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Invalid_Target_LoginID, null, null);
                return;
            }

            FollowingDAO flDao = new FollowingDAO();
            List<Follower> existingFollowerList = flDao.getFollowers(jfollower.getLoginid(), jfollower.getFollower_id());
            if (!existingFollowerList.isEmpty()) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.following_exist, null, null);
                return;
            }
            Follower follower = new Follower();
            Date now = new Date();
            follower.setLoginID(jfollower.getLoginid());
            follower.setNote(jfollower.getNote());
            follower.setFollowerID(jfollower.getFollower_id());
            follower.setCreatedTime(now);
            follower.setStatus(AllConstants.ProgramConts.follower_status_pending);
            follower = flDao.creatNewFollowing(follower);
            if (follower == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
                return;
            }
            DBtoJsonUtil djUtil = new DBtoJsonUtil();
            jfollower = djUtil.convert_a_Follower(follower);
            out.println(gson.toJson(jfollower));
            System.out.println(gson.toJson(jfollower));
        } catch (ParseException ex) {
            ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
            ex.printStackTrace();
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
