/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.delete;

import health.database.DAO.FollowingDAO;
import health.database.DAO.UserDAO;
import health.database.models.Follower;
import health.input.jsonmodels.JsonFollower;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.JsonUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author Leon
 */
public class DeleteFollower extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws InvocationTargetException 
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, UnsupportedEncodingException, IOException, InvocationTargetException {
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
            if (existingFollowerList.isEmpty()) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Not_exist_follow, null, null);
                return;
            }
            for(int i=0;i<existingFollowerList.size();i++)
            {
            	flDao.deleteFollower(existingFollowerList.get(i));
            }
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            out.println(gson.toJson(jo));
            System.out.println(gson.toJson(jo));
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
        try {
			processRequest(request, response);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        try {
			processRequest(request, response);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
