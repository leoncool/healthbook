/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.usercontrol.Register;

/**
 *
 * @author Leon
 */
public class specialOperations {

    public boolean special(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ServletException, IOException {
        if (request.getParameter("RequestType")!=null&&request.getParameter("RequestType").equals("register")) {
            Register register = new Register();
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            register.processRequest(request, response);
            return true;
        }
        return false;
    }
}
