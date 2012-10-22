package servlets.actions.post;

import javax.servlet.Servlet;
import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class Upload extends HttpServlet implements Servlet {
	  public void processRequest(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
		  System.out.println("Getting this...2");
	        PrintWriter out = response.getWriter();
	        response.setContentType("text/plain");
	        if (request.getContentLength() > 1024 * 1024 * 60) {
	            out.println("{\"error\":\"" + "oversize" + "\"}");
	            out.close();
	            return;
	        }

	        String id = Long.toString(System.currentTimeMillis());
	        FileUploadListener listener = new FileUploadListener(id + ".gif");
	        HttpSession session = request.getSession(true);
	        session.setAttribute("LISTENER", listener);
	      
	        FileItemFactory factory = new DiskFileItemFactory();
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        upload.setProgressListener(listener);
	        List uploadedItems = null;
	        FileItem fileItem = null;
	        String filePath = "/usr/data/";
	        if (!new File(filePath).exists()) {
	            filePath = "E:/";
	        }
	        filePath = filePath + id + "/";
	        File createDir = new File(filePath);

	        System.out.println("Getting this...3");
	        int publicSetting = 0;
	        String publishValue = request.getParameter("publish");
	        if (publishValue == null) {
	            System.out.println("fileItem none");
	        } else {
	            if (publishValue.equalsIgnoreCase("true")) {
	                publicSetting = 1;
	            } else {
	            }
	        }
	        double secondsBetweenFrames = 0.2;

	        try {
	            uploadedItems = upload.parseRequest(request);
	            Iterator i = uploadedItems.iterator();
	            while (i.hasNext()) {
	                fileItem = (FileItem) i.next();
	                if (fileItem.isFormField() == false) {
	                    if (fileItem.getSize() > 0) {
	                        File uploadedFile = null;
	                        String myFullFileName = fileItem.getName(), myFileName = "", slashType = (myFullFileName.lastIndexOf("\\") > 0) ? "\\" : "/";
	                        int startIndex = myFullFileName.lastIndexOf(slashType);
	                        myFileName = myFullFileName.substring(startIndex + 1, myFullFileName.length());
	                        //   uploadedFile = new File(filePath, myFileName);
	                        //    fileItem.write(uploadedFile);
	                        int mid = myFileName.lastIndexOf(".");
	                        String convertedgifName = "";
	                        if (request.getParameter("gifname") != null & request.getParameter("gifname").length() > 0) {
	                            convertedgifName = request.getParameter("gifname");
	                            System.out.println("convertedgifName:" + convertedgifName);
	                        } else {
	                            convertedgifName = myFileName;
	                        }
	                        if (request.getParameter("framesPerSecond") != null & request.getParameter("framesPerSecond").length() > 0) {
	                            double framesPerSecond = Integer.parseInt(request.getParameter("framesPerSecond"));
	                            secondsBetweenFrames = 1 / framesPerSecond;

	                        } else {
	                        }
	                        if (secondsBetweenFrames < 0.2) {
	                            secondsBetweenFrames = 0.2;
	                        }
	                        String ext = myFileName.substring(mid + 1, myFileName.length());
	                        System.out.println("fileItem.getSize():" + fileItem.getSize());
//	                        if (fileItem.getSize() > (double) 1024 * 1024 * 50) {
//	                            out.println("{\"error\":\"" + "oversize" + "\"}");
//	                            out.close();
//	                            return;
//	                        }
	                        createDir.mkdir();
	                        uploadedFile = new File(filePath + id + "." + ext);
	                        fileItem.write(uploadedFile);
	                        System.out.println("Getting this...4");
	                        try {
	                            System.out.println("secondsBetweenFrames:" + secondsBetweenFrames);
	                            System.out.println("Getting this...5");

	                       
	                            System.out.println("Getting this...6");
	                            int size = 0;
	                            size = (int) new File(filePath + id + ".gif").length();
	                            System.out.println("@gif size:" + size);
	                            System.out.println("@gif size:" + new File(filePath + id + ".gif").length());
	                            session.setAttribute("LASTID", id + ".gif");

	                            out.println("{\"id\":\"" + id + ".gif" + "\"}");
	                            System.out.println("Finished converting");

	                        } catch (Exception ex) {
	                            ex.printStackTrace();
//	                            session.setAttribute("LISTENER", null);
//	                            session.setAttribute("CON_LISTENER", null);
	                        }

	                    }
	                } else {
	                    System.out.println("fileItem" + fileItem.getName());
	                }
	            }
	        } catch (FileUploadException e) {
	            e.printStackTrace();
//	            session.setAttribute("LISTENER", null);
//	            session.setAttribute("CON_LISTENER", null);
	        } catch (Exception e) {
	            e.printStackTrace();
	            session.setAttribute("LISTENER", null);
	            session.setAttribute("CON_LISTENER", null);
//	            session.setAttribute("LISTENER", null);
//	            session.setAttribute("CON_LISTENER", null);
	        } finally {
//	            session.removeAttribute("CON_LISTENER");
//	            session.removeAttribute("LISTENER");
	        }
		  
	  }
    private static final long serialVersionUID = 2740693677625051632L;

    public Upload() {
        super();
    }

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
