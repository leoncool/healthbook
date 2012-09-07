/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.usercontrol;

import health.database.DAO.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author leon
 */
public class checkLogin {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {



        response.setContentType("application/json"); 
        PrintWriter out = response.getWriter();
        try {
            if (request.getParameter("LoginUsername") == null) {
                // out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println("<status>Missing Data</status>");
                out.close();
                return;
            }

            String loginID = request.getParameter("loginID");

            UserDAO userdao = new UserDAO();
            if (userdao.getLogin(loginID) == null) {
                //  out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println("<status>Login_Not_Exist</status>");
            } else {
                //System.out.println("User Exists");
                out.println("<status>User_Exists</status>");
                //   return false;
            }
        } catch (Exception e) {
            //out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<status>FAULT</status>");
            out.println("<fault>" + e + "</fault>");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}
