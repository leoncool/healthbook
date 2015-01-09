/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.actions.post.health.bytitle;

import static util.JsonUtil.ServletPath;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.HealthDataStreamDAO;
import health.database.DAO.SubjectDAO;
import health.database.DAO.UserDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.Subject;
import health.database.models.Users;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonCloudStorageFile;
import health.input.jsonmodels.JsonDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataPointsPostResult;
import health.input.jsonmodels.JsonDataValues;
import health.input.util.DBtoJsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import server.exception.ErrorCodeException;
import server.exception.ReturnParser;
import servlets.util.PermissionFilter;
import servlets.util.ServerUtil;
import util.AllConstants;
import util.JsonUtil;
import util.ServerConfigUtil;
import cloudstorage.cacss.S3Engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.zhumulangma.cloudstorage.server.entity.CloudFile;

/**
 * 
 * @author Leon
 */
public class PostFileThroughCloudStorage extends HttpServlet {
	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		System.out.println("before checkAndGetLoginFromToken");
		PrintWriter out = response.getWriter();
		try {

			Users accessUser = null;
			PermissionFilter filter = new PermissionFilter();
			String loginID = filter
					.checkAndGetLoginFromToken(request, response);

			UserDAO userDao = new UserDAO();
			if (loginID == null) {
				if (filter.getCheckResult().equalsIgnoreCase(
						filter.INVALID_LOGIN_TOKEN_ID)) {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_login_token_id,
									null, null);
					return;
				} else if (filter.getCheckResult().equalsIgnoreCase(
						AllConstants.ErrorDictionary.login_token_expired)) {
					return;
				} else {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.Invalid_login_token_id,
									null, null);
					return;
				}
			} else {
				accessUser = userDao.getLogin(loginID);
			}

			String bucketName = ServerConfigUtil
					.getConfigValue(AllConstants.ServerConfigs.CloudStorageBucket);
			String objectPrefix = loginID + "/cs/";

			String fileName = null;
			JsonCloudStorageFile csFile = new JsonCloudStorageFile();
			boolean fileUploaded = false;
			List<FileItem> items = new ServletFileUpload(
					new DiskFileItemFactory()).parseRequest(request);
			for (FileItem item : items) {
				if (item.isFormField()) {

				} else {
					// Process form file field (input type="file").
					// fileName = item.getFieldName();
					fileName = FilenameUtils.getName(item.getName());
					InputStream inputstream = item.getInputStream();
					Gson gson = new Gson();
					Hashtable<String, Object> paramters = new Hashtable<String, Object>();
					if (item.getContentType() != null) {
						paramters.put("Content-Type", item.getContentType());
					} else {
						paramters.put("Content-Type",
								"application/octet-stream");
					}

					try {
						String newObjectName = objectPrefix + fileName;
						Hashtable<String, Object> returnValues = (Hashtable<String, Object>) S3Engine.s3
								.PutObject("leoncool", bucketName,
										newObjectName, (long) item.getSize(),
										inputstream, 3, paramters, null);
						Hashtable<String, Object> returnValues2 = (Hashtable<String, Object>) S3Engine.s3
								.GetObject(bucketName, "leoncool",
										newObjectName, null, null);
						CloudFile file = (CloudFile) returnValues2.get("data");
						if (file != null
								&& (String) file.get(CloudFile.NAME) != null) {
							String absKey=(String) file.get(CloudFile.NAME);
							String relativeKey=absKey.replaceFirst(accessUser+"/cs/", "");
							csFile.setKey((String) file.get(CloudFile.NAME));
							csFile.setLastModified((String) file
									.get(CloudFile.MODIFIED));
							csFile.setETag((String) file.get(CloudFile.ETAG));
							csFile.setSize((String) file.get(CloudFile.SIZE));
						} else {
							ReturnParser
									.outputErrorException(
											response,
											AllConstants.ErrorDictionary.Internal_Fault,
											null,
											"file upload to cloud storage failed");
							return;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						ReturnParser.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, "file upload to cloud storage failed");
						inputstream.close();
						return;
					}
					fileUploaded = true;
					inputstream.close();
				}
			}
			if (fileUploaded == false) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						"missing file for uploading");
				return;
			}

			Gson gson = new Gson();

			JsonElement je = gson.toJsonTree(csFile);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			jo.add("cloudfile", je);
			JsonWriter jwriter = new JsonWriter(out);
			gson.toJson(jo, jwriter);
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;

		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
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
