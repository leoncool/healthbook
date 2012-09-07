/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.models.Datastream;
import health.database.models.Subject;
import health.database.models.Subject;
import health.input.jsonmodels.JsonSubject;
import health.input.util.DBtoJsonUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import server.exception.ReturnParser;
import util.AllConstants;
import util.HibernateUtil;

/**
 *
 * @author Leon
 */
public class GetSubjectList extends HttpServlet {

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
            SubjectDAO subdao = new SubjectDAO();
            DatastreamDAO dstreamDao = new DatastreamDAO();
            List<JsonSubject> jsubList = new ArrayList<JsonSubject>();
            List<Subject> subList = new ArrayList<Subject>();
            String loginID = "leoncool";
            if (request.getParameter(AllConstants.api_entryPoints.request_api_loginid) != null) {
                loginID = request.getParameter(AllConstants.api_entryPoints.request_api_loginid);
            }
            UserDAO userDao = new UserDAO();
            if (!userDao.existLogin(loginID)) {
                ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unauthorized_Access, null, null);
                return;
            }
            if (request.getParameter(AllConstants.api_entryPoints.request_api_onlyParentSubjects) != null
                    && request.getParameter(AllConstants.api_entryPoints.request_api_onlyParentSubjects)
                    .equalsIgnoreCase(AllConstants.api_entryPoints.request_api_true)) {
                subList = subdao.findOnlyParentSubjectsByLoginID(loginID);
            } else {
                subList = subdao.findSubjectsByLoginID(loginID);
            }
            for (Subject sub : subList) {
                DBtoJsonUtil dbtoJUtil = new DBtoJsonUtil();
                jsubList.add(dbtoJUtil.convert_a_Subject(sub));
            }
            System.out.println(jsubList.size());
            Gson gson = new Gson();
            JsonElement je = gson.toJsonTree(jsubList);
            JsonObject jo = new JsonObject();
            jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
            jo.add("subject_list", je);
            JsonWriter jwriter = new JsonWriter(response.getWriter());
//            if (request.getParameter("callback") != null) {
//                System.out.println("using callback");
//                out.print(request.getParameter("callback")+"("+ gson.toJson(jo) + ");");
//            } else {
//                gson.toJson(jo, jwriter);
//                jwriter.close();
//            }
            gson.toJson(jo, jwriter);
            jwriter.close();

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
