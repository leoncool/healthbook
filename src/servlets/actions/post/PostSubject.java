/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post;

import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Subject;
import health.input.jsonmodels.JsonSubject;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

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
public class PostSubject extends HttpServlet {

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
            JsonSubject jsubject = gson.fromJson(jutil.readJsonStrFromHttpRequest(request), JsonSubject.class);
            if (jsubject.getLoginid() == null || jsubject.getTitle() == null) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.MISSING_DATA, null, null);
                return;
            }
            UserDAO userDao = new UserDAO();
            if (!userDao.existLogin(jsubject.getLoginid())) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unauthorized_Access, null, null);
                return;
            }
            Date now = new Date();
            Subject subject = new Subject();
            subject.setUpdated(now);
            subject.setCreatedTime(now);
            subject.setLoginID(jsubject.getLoginid());
            subject.setTitle(jsubject.getTitle());
            subject.setDescription(jsubject.getDescription());
            subject.setPrivateSet("private");
            SubjectDAO dao = new SubjectDAO();
//            if (1 == 1) {
//                return;
//            }
            subject = dao.createSubject(subject);
            DBtoJsonUtil djUtil = new DBtoJsonUtil();
            try {
                JsonSubject jsub = djUtil.convert_a_Subject(subject);
                JsonElement je = gson.toJsonTree(jsub);
                JsonObject jo = new JsonObject();
                jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
                jo.add("subject", je);
                JsonWriter jwriter = new JsonWriter(out);
                gson.toJson(jo, jwriter);
                System.out.println(gson.toJson(jsub));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
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
