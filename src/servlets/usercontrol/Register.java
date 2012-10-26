/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.usercontrol;

import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Users;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import server.exception.ReturnParser;
import util.AllConstants.ErrorDictionary;
import util.AllConstants.ValidDictionary;
import util.HibernateUtil;

/**
 *
 * @author leon
 */
public class Register {

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
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
            if (request.getParameter("loginID") == null || request.getParameter("password") == null
                    || request.getParameter("email") == null) {
                //out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                ReturnParser.outputErrorException(response, ErrorDictionary.MISSING_DATA, null, null);
                out.close();
                return;
            }
            HttpSession httpsession = request.getSession(true);
//            String code = (String) httpsession.getAttribute("verification.code");
//            String attempt = request.getParameter("code");
//            if (code != null && code.toLowerCase().equals(attempt.toLowerCase()))
//            {
//            } else
//            {
//                out.println("<status>WRONG_CODE</status>");
//                out.close();
//                return;
//            }
            String loginID = request.getParameter("loginID");
            String Password = request.getParameter("password");
            String Email = request.getParameter("email");


            UserDAO userdao = new UserDAO();
            if (userdao.EmailExists(Email) == true) {
                ReturnParser.outputErrorException(response, ErrorDictionary.email_Exist, null, Email);
                out.close();
                return;
            }
            if (userdao.getLogin(loginID) == null) {
                Users user = new Users();
                user.setLoginID(loginID);
                user.setEmail(Email);
                Date now = new Date();
                DateFormat df = DateFormat.getDateTimeInstance();
                user.setCreatedTime(now);

                //save MD5 password
                String MD5password = null;
                MessageDigest md5;
                md5 = MessageDigest.getInstance("MD5");
                md5.update(Password.getBytes());
                BigInteger hashPw = new BigInteger(1, md5.digest());
                MD5password = hashPw.toString(16);
                while (MD5password.length() < 32) {
                    MD5password = "0" + MD5password;
                }
                user.setPassword(MD5password);
                user.setLanguage("en");
                Session session = HibernateUtil.beginTransaction();
                session.save(user);
                HibernateUtil.commitTransaction();
                
                SubjectDAO subDao=new SubjectDAO();// create Default Subject and Datastreams
                // out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.println(ReturnParser.returnValidResult(ValidDictionary.Valid));
                
                String emailSubject = "";
//                if (language.equalsIgnoreCase("cn")) {
//                    emailSubject = "Via Cloud 通知-注册确认信！";
//                } else if (language.equalsIgnoreCase("en")) {
//                    emailSubject = "Notification-Registration Completed！";
//                }
                String MD5username = null;
                md5 = MessageDigest.getInstance("MD5");
                md5.update(loginID.getBytes());
                BigInteger hashlogin = new BigInteger(1, md5.digest());
                MD5username = hashlogin.toString(16);
                while (MD5username.length() < 32) {
                    MD5username = "0" + MD5username;
                }
//                propertiesDAO pptDao = new propertiesDAO();
//                String fromEmailAddress = pptDao.getPropertySettingByNameID("supportEmailAddress");
//                String billingServletUrl = pptDao.getPropertySettingByNameID("billingServletUrl");
//
//                EmailTemplateEngine ete = new EmailTemplateEngine();
//                String activeLink = billingServletUrl + "/billingCN/UserControl?RequestType=Verify&&login=" + LoginUsername + "&&token=" + MD5username;
//                String emailBody = ete.register(LoginUsername, Password, activeLink, language);
//                new EmailThread(emailSubject, emailBody, user.getEmail(), fromEmailAddress).start();

            } else {
                out.println(ReturnParser.returnErrorException(ErrorDictionary.LoginID_Exist, null, loginID));
                out.close();
                return;
            }

        } catch (Exception e) {
            //  out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println(ReturnParser.returnErrorException(ErrorDictionary.unknownFault, null, null));
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}
