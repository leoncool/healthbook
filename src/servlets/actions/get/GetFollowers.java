/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import health.database.DAO.FollowingDAO;
import health.database.DAO.UserDAO;
import health.database.models.Follower;
import health.input.jsonmodels.JsonFollower;
import health.input.util.DBtoJsonUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.AllConstants;

/**
 *
 * @author Leon
 */
public class GetFollowers extends HttpServlet {

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

            String loginID = "leoncool";
            if (request.getParameter(AllConstants.api_entryPoints.request_api_loginid) != null) {
                loginID = request.getParameter(AllConstants.api_entryPoints.request_api_loginid);
            }
            FollowingDAO flDao = new FollowingDAO();
            List<Follower> follwerList = flDao.getFollowers(loginID, null);
            List<JsonFollower> jfollowerList = new ArrayList<JsonFollower>();
            DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
            UserDAO userDao=new UserDAO();
            if (follwerList != null && !follwerList.isEmpty()) {
                for (Follower follower : follwerList) {
                    JsonFollower jfollower = dbtoJUtil.convert_a_Follower(follower);
                    jfollower.setFollower_info(dbtoJUtil.convert_a_userinfo(userDao.getUserInfo(follower.getFollowerID())));
                    jfollowerList.add(jfollower);
                }
            }
            Gson gson = new Gson();
            JsonElement je = gson.toJsonTree(jfollowerList);
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.addProperty(AllConstants.api_entryPoints.request_api_loginid, loginID);
            jo.add("follower_list", je);
            System.out.println(jo.toString());
            out.println(gson.toJson(jo));
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
