package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import server.exception.ReturnParser;
import util.MarketplaceContants;
import util.AllConstants;
import util.ServerConfigUtil;
import util.AllConstants.ServerConfigs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

/**
 * Servlet implementation class PostAnalysisService1
 */
@WebServlet("/PostAnalysisService1")
public class PostAnalysisService1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PostAnalysisService1() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	private static final String UPLOAD_DIRECTORY = "upload";
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 10; // 10MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 60; // 50MB

	public static String unZip(File zipFile, String folder) {
		try {
			ZipFile toZip = new ZipFile(zipFile);
			if (toZip.isEncrypted()) {
				return "encrypted";
			} else {
				toZip.extractAll(folder);
				return "valid";
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return "fault";
		}

	}

	public HashMap<String, String[]> getFunctionEntries(File mainFunction) {
		try {
			FileReader namereader = new FileReader(mainFunction);
			BufferedReader in = new BufferedReader(namereader);
			String firstLine = in.readLine();
			System.out.println("mainFunction firstLine:" + firstLine);
			in.close();
			// String s =
			// "function [TimeStamp,UnitID,ValueList,TagList]=main(aaa)";
//			Pattern p = Pattern
//					.compile("^function \\[(.+?)\\]=main[(](.*?)[)]");
			Pattern p = Pattern
					.compile("^function *\\[ *(.+?)\\ *] *= *main *[(] *(.*?) *[)] *");
			Matcher m = p.matcher(firstLine);
			HashMap<String, String[]> returnValues = new HashMap<>();
			if (m.matches()) {
				System.out.println(m.group(1));
				String inputsRaw = m.group(1);
				System.out.println(m.group(2));
				String outputsRaw = m.group(2);
				String[] inputs = inputsRaw.split(",");
				String[] outputs = outputsRaw.split(",");
				returnValues.put("outputs", inputs);
				returnValues.put("inputs", outputs);
				return returnValues;
			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}

	}

	public List<String> getOutputEntries(File mainFunction) {
		return null;
	}

	public static boolean isZipFile(File file) throws IOException {
		if (file.isDirectory()) {
			return false;
		}
		if (!file.canRead()) {
			throw new IOException("Cannot read file " + file.getAbsolutePath());
		}
		if (file.length() < 4) {
			return false;
		}
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));
		int test = in.readInt();
		in.close();
		return test == 0x504b0304;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				util.MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				util.MarketplaceContants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				util.MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);

		String modelRepository = "F:/model_repository/";
		String tmpFolder = "F:/model_repository/temp/";
		String thumbnailsFolder = "F:/model_repository/thumbnails/";
		if (!new File(modelRepository).exists()) {
			modelRepository = ServerConfigUtil
					.getConfigValue(ServerConfigs.modelRepository);
			tmpFolder = ServerConfigUtil
					.getConfigValue(ServerConfigs.tmpRepository);
			thumbnailsFolder = ServerConfigUtil
					.getConfigValue(ServerConfigs.modelThumbnailDir);
		}

		PrintWriter out = response.getWriter();
		try {

			Enumeration<?> allParameterNames = request.getParameterNames();
			while (allParameterNames.hasMoreElements()) {
				Object object = allParameterNames.nextElement();
				String param = (String) object;
				String value = request.getParameter(param);
				System.out.println("Parameter Name is '" + param
						+ "' and Parameter Value is '" + value + "'");
			}
			try {
				if (request.getContentType() != null
						&& request.getContentType().toLowerCase()
								.indexOf("multipart/form-data") > -1) {
					// Multipart logic here
					UUID uuid = UUID.randomUUID();
					AnalysisModel model = new AnalysisModel();
					List<AnalysisModelEntry> entryList = new ArrayList<>();
					List<AnalysisModelEntry> inputEntryList = new ArrayList<>();
					List<AnalysisModelEntry> outputEntryList = new ArrayList<>();
					model.setCreatedTime(new Date());
					model.setPublisher("publisher");
					model.setId(uuid.toString());
					model.setCreatedTime(new Date());
					model.setStatus(MarketplaceContants.status_draft);
					DiskFileItemFactory factory = new DiskFileItemFactory();
					factory.setSizeThreshold(THRESHOLD_SIZE);
					boolean modelZipFileUploaded = false;
					factory.setRepository(new File(tmpFolder));

					ServletFileUpload upload = new ServletFileUpload(factory);
					upload.setFileSizeMax(MAX_FILE_SIZE);
					upload.setSizeMax(MAX_REQUEST_SIZE);

					List<FileItem> formItems = upload.parseRequest(request);
					Iterator iter = formItems.iterator();
					for (FileItem item : formItems) {
						if (item.isFormField()) {
							// for request parameter entry
							String fieldname = item.getFieldName();

							String fieldvalue = item.getString();
							System.out.println("field:" + fieldname + ",value:"
									+ fieldvalue);

							if (fieldname
									.equals(MarketplaceContants.RequestParameters.ModelName)) {
								model.setName(fieldvalue);
							} else if (fieldname
									.equals(MarketplaceContants.RequestParameters.ModelPriceModel)) {
								model.setPricingModel(fieldvalue);
							} else if (fieldname
									.equals(MarketplaceContants.RequestParameters.ModelPrice)) {

								try {
									if (fieldvalue.length() > 0) {
										model.setPrice(Double
												.parseDouble(fieldvalue));
									}
								} catch (Exception ex) {
									ReturnParser
											.outputErrorException(
													response,
													AllConstants.ErrorDictionary.model_metadata_format_error,
													null,
													MarketplaceContants.RequestParameters.ModelPrice);
									return;
								}
							} else if (fieldname
									.equals(MarketplaceContants.RequestParameters.ModelTerms)) {
								model.setTerms(fieldvalue);
							} else if (fieldname
									.equals(MarketplaceContants.RequestParameters.ModelDescription)) {
								model.setDesp(fieldvalue);
							}

						} else {
							// below is for file entry
							String fieldname = item.getFieldName();
							String filename = FilenameUtils.getName(item
									.getName());
							System.out.println("**FileUpload--fieldname:"
									+ fieldname + ", filename:" + filename);
							if (fieldname
									.equals(util.MarketplaceContants.RequestParameters.ModelZipFile)) {

								File newModelFolder = new File(modelRepository
										+ uuid);
								if (!newModelFolder.exists())
									newModelFolder.mkdir();
								File modelZipFile = new File(modelRepository
										+ uuid + "/" + filename);
								item.write(modelZipFile);
								if (isZipFile(modelZipFile)) {
									System.out.println("isZipFile");
									String unzipResult = unZip(modelZipFile,
											modelRepository + uuid + "/");
									modelZipFile.delete();
									if (unzipResult.equals("valid")) {
										File modelEntryFile = new File(
												modelRepository + uuid
														+ "/main.m");
										if (!modelEntryFile.exists()) {
											ReturnParser
													.outputErrorException(
															response,
															AllConstants.ErrorDictionary.model_main_function_not_found,
															null, "");
											return;
										}
										HashMap<String, String[]> functionEntries = getFunctionEntries(modelEntryFile);
										if (functionEntries == null) {
											ReturnParser
													.outputErrorException(
															response,
															AllConstants.ErrorDictionary.model_main_function_format_error,
															null, filename);
											return;
										}
										String[] inputs = functionEntries
												.get("inputs");
										String[] outputs = functionEntries
												.get("outputs");
										if (inputs.length < 1
												|| outputs.length < 1) {
											ReturnParser
													.outputErrorException(
															response,
															AllConstants.ErrorDictionary.model_main_function_input_or_output_format_error,
															null, filename);
											return;
										}

										int inputOrder = 1;
										int outputOrder = 1;
										for (String variable : inputs) {
											AnalysisModelEntry entry = new AnalysisModelEntry();
											entry.setModel_id(uuid.toString());
											entry.setEntryType(MarketplaceContants.as_input);
											entry.setOrder(inputOrder);
											entryList.add(entry);
											inputEntryList.add(entry);
											inputOrder++;
										}
										for (String variable : outputs) {
											AnalysisModelEntry entry = new AnalysisModelEntry();
											entry.setModel_id(uuid.toString());
											entry.setEntryType(MarketplaceContants.as_output);
											entry.setOrder(outputOrder);
											entryList.add(entry);
											outputEntryList.add(entry);
											outputOrder++;
										}
										model.setTotalInputs(inputOrder - 1);
										model.setTotalOutputs(outputOrder - 1);

									} else if (unzipResult.equals("error")) {
										ReturnParser
												.outputErrorException(
														response,
														AllConstants.ErrorDictionary.model_zip_error,
														null, filename);
										return;
									} else if (unzipResult.equals("encrypted")) {
										ReturnParser
												.outputErrorException(
														response,
														AllConstants.ErrorDictionary.model_zip_is_encrypted,
														null, filename);
										return;
									}
									modelZipFileUploaded = true;
								} else {
									System.out.println("is Not ZipFile");
								}

							} else if (filename
									.equals(util.MarketplaceContants.RequestParameters.ModelThumbnail)) {

							}

						}
					}
					// logic for next step

					AnalysisServiceDAO asDao = new AnalysisServiceDAO();
					if (model.getName() == null || model.getPublisher() == null) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.model_metadata_format_error,
										null, "missing name or publisher");
						return;
					}
					if (!modelZipFileUploaded) {
						ReturnParser
								.outputErrorException(
										response,
										AllConstants.ErrorDictionary.model_zip_file_missing,
										null, "missing model zip file");
						return;
					}

					AnalysisModel returnModel = asDao.createModel(model);
					boolean hasError = false;
					if (returnModel == null) {
						asDao.deleteModelByID(uuid.toString());
						hasError = true;
					}
					if (returnModel != null) {
						int num_entries = asDao.createModelEntries(entryList);
						if (num_entries < 1) {
							asDao.deleteModelByID(uuid.toString());
							hasError = true;
						}
					}
					if (hasError) {
						ReturnParser.outputErrorException(response,
								AllConstants.ErrorDictionary.Internal_Fault,
								null, "");
						return;
					} else {
						Gson gson = new Gson();
						JsonElement jmodel = gson.toJsonTree(model);
						JsonElement jinputEntryList = gson
								.toJsonTree(inputEntryList);
						JsonElement joutputEntryList = gson
								.toJsonTree(outputEntryList);
						JsonObject jo = new JsonObject();
						jo.addProperty(AllConstants.ProgramConts.result,
								AllConstants.ProgramConts.succeed);
						jo.add("analysis_model", jmodel);
						jo.add("analysis_model_inputs", jinputEntryList);
						jo.add("analysis_model_outputs", joutputEntryList);
						// JsonWriter jwriter = new JsonWriter(out);
						out.println(gson.toJson(jo));
						System.out.println(gson.toJson(jo));
						return;
					}

				} else {
					ReturnParser
							.outputErrorException(
									response,
									AllConstants.ErrorDictionary.model_metadata_format_error,
									null, "use official publisher");
					return;
				}

			} catch (FileUploadException e) {
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Internal_Fault, null,
						"file upload exception");
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, "");
			return;
		} finally {
			out.close();
		}
	}
}
