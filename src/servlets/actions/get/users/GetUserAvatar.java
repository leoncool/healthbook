/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.get.users;

import static util.JsonUtil.ServletPath;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.io.Util;

import server.exception.ReturnParser;
import util.AllConstants;
import util.ServerConfigUtil;

/**
 *
 * @author Leon
 */
public class GetUserAvatar extends HttpServlet {

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
        try {
        	String avatarFileName=ServletPath(request).replaceFirst(Pattern.quote(AllConstants.api_entryPoints.api_url + AllConstants.api_entryPoints.api_user+"/"+AllConstants.api_entryPoints.api_avatar+"/"), "");
        //	System.out.println("avatarFileName:"+avatarFileName);
        	
        	File avatarFile=new File(ServerConfigUtil.getConfigValue(AllConstants.ServerConfigs.UserAvatarLocation)+avatarFileName);
       // 	System.out.println("file location trying to find:"+ServerConfigs.getConfigValue(AllConstants.ServerConfigs.UserAvatarLocation)+avatarFileName);
        	if(avatarFile.exists())
        	{
        		  response.setContentType("image/gif");
                  response.setContentLength((int) avatarFile.length());
                  response.setHeader("Content-Disposition", "inline; filename=\"" + avatarFile.getName() + "\"");
                  int length = 0;
                  ServletOutputStream op = response.getOutputStream();
                  byte[] bbuf = new byte[1024];
                  DataInputStream in = new DataInputStream(new FileInputStream(avatarFile));
                  Util.copyStream(in, op);
                  in.close();
                  op.flush();
                  op.close();
        	}
        	else{
        		//  ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Input_Json_Format_Error, null, null);
        		  response.sendError(HttpServletResponse.SC_NOT_FOUND);
        		  return;
        	}
        }catch(Exception ex)
        {
        	ex.printStackTrace();
        	  response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         //   ReturnParser.outputErrorException(response, AllConstants.ErrorDictionary.Internal_Fault, null, null);
        }
        finally {
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
