/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.delete;

import static util.JsonUtil.ServletPath;
import health.database.DAO.SubjectDAO;
import health.database.models.Subject;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.ServerUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 *
 * @author Leon
 */
public class DeleteASubject extends HttpServlet {

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
                  SubjectDAO subjDao = new SubjectDAO();
                  int subID = 	ServerUtil.getSubjectID(ServletPath(request));//Retrieve Subject ID from URL
                  
                  Subject subject = (Subject) subjDao.getObjectByID(Subject.class, subID); //Retreive Subject from DB
                  if (subject == null) {
                      ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Unknown_SubjectID, null, Integer.toString(subID));
                      return;
                  }
                 try{
                	 subjDao.deleteSubjectByID(subject);
                 }catch(Exception ex)
                 {
                	 ex.printStackTrace();
                	  ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, Integer.toString(subID));
                      return;
                 }
                 Gson gson = new Gson();
                 JsonObject jo = new JsonObject();
                 jo.addProperty(AllConstants.ProgramConts.result, AllConstants.ProgramConts.succeed);
                 JsonWriter jwriter = new JsonWriter(response.getWriter());
                 gson.toJson(jo, jwriter);
                 jwriter.close();  
                         
        } catch (Exception ex) {
        	   ex.printStackTrace();
        	  ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
              return;
         
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
