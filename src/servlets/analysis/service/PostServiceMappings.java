package servlets.analysis.service;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import server.exception.ReturnParser;
import util.AScontants;
import util.AllConstants;

/**
 * Servlet implementation class PostAnalysisService1
 */
@WebServlet("/PostServiceMappings")
public class PostServiceMappings extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PostServiceMappings() {
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

	public static int parseEntryID(String entry) {
		try {
			if (entry.startsWith("input")) {
				String intStr = entry.substring(5, entry.indexOf('_'));
				int value = Integer.parseInt(intStr);
				return value;
			} else if (entry.startsWith("output")) {
				String intStr = entry.substring(6, entry.indexOf('_'));
				int value = Integer.parseInt(intStr);
				return value;
			} else {
				return 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public static boolean validateInputModelEntries(
			List<AnalysisModelEntry> entryList) {
		HashMap<String, String> allowedDataTypes = new HashMap<>();
		allowedDataTypes.put(AScontants.fileType, "");
		allowedDataTypes.put(AScontants.sensordataType, "");
		allowedDataTypes.put(AScontants.integerType, "");
		allowedDataTypes.put(AScontants.doubleType, "");
		allowedDataTypes.put(AScontants.StringType, "");
		for (AnalysisModelEntry entry : entryList) {
			if (entry.getDataType() == null
					|| !allowedDataTypes.containsKey(entry.getDataType())) {
				return false;
			}

		}
		return true;
	}

	public static boolean validateOutputModelEntries(
			List<AnalysisModelEntry> entryList) {
		HashMap<String, String> allowedDataTypes = new HashMap<>();
		allowedDataTypes.put(AScontants.fileType, "");
		allowedDataTypes.put(AScontants.sensordataType, "");
		allowedDataTypes.put(AScontants.integerType, "");
		allowedDataTypes.put(AScontants.doubleType, "");
		allowedDataTypes.put(AScontants.StringType, "");
		allowedDataTypes.put(AScontants.AlertType, "");
		HashMap<String, String> allowedDataActions = new HashMap<>();
		allowedDataActions.put(AScontants.dataaction_saveOrUpdate, "");
		allowedDataActions.put(AScontants.dataaction_delete, "");
		allowedDataActions.put(AScontants.dataaction_ignore, "");
		allowedDataActions.put(AScontants.dataaction_alert, "");
		for (AnalysisModelEntry entry : entryList) {
			if (entry.getDataType() == null
					|| !allowedDataTypes.containsKey(entry.getDataType())) {
				System.out.println("missing output data type");
				return false;
			}
			if (entry.getDataAction() == null
					|| !allowedDataActions.containsKey(entry.getDataAction())) {
				System.out.println("missing output data action");
				return false;
			}
			if (entry.getEntry_name() == null) {
				System.out.println("missing output data action");
				return false;
			}
			if (entry.getDataType() == AScontants.AlertType) {
				if (entry.getDataAction() != AScontants.dataaction_alert
						&& entry.getDataAction() != AScontants.dataaction_ignore) {
					System.out.println("error with data action for alert");
					return false;
				}
			} else {
				if (entry.getDataAction() == AScontants.dataaction_alert) {
					System.out
							.println("error with data action for other types than alert");
					return false;
				}
			}

		}
		return true;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				util.AScontants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				util.AScontants.ACCESS_CONTROL_ALLOW_HEADERS);

		Gson gson = new Gson();
		AnalysisServiceDAO asDao = new AnalysisServiceDAO();
		String serviceID_String = request
				.getParameter(AScontants.RequestParameters.Service_ID);
		int serviceID=0;
		if (serviceID_String == null || serviceID_String.length() < 1) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.MISSING_DATA, null,
					AScontants.RequestParameters.Service_ID);
			return;
		}else{
			try{
				serviceID=Integer.parseInt(serviceID_String);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.Invalid_data_format, null,
						AScontants.RequestParameters.Service_ID);
				return;
			}
		}
		AnalysisService service = null;
		AnalysisModel model = null;
		service = asDao.getServicebyID(serviceID);
		if (service == null) {
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.service_id_cannot_found,
					null, AScontants.RequestParameters.Service_ID);
			return;
		}
		model=asDao.getModelByID(service.getModelId());
		
		List<AnalysisModelEntry> inputEntryList = asDao
				.getModelEntriesByModelID(model.getId(),
						AScontants.as_input);
		List<AnalysisModelEntry> outputEntryList = asDao
				.getModelEntriesByModelID(model.getId(),
						AScontants.as_output);
		
		try {

			Enumeration<?> allParameterNames = request.getParameterNames();
			while (allParameterNames.hasMoreElements()) {
				Object object = allParameterNames.nextElement();
				String param = (String) object;
				String value = request.getParameter(param);
				System.out.println("Parameter Name is '" + param
						+ "' and Parameter Value is '" + value + "'");
				if (param.startsWith("input")) {
					int order = parseEntryID(param);
					if (inputEntryList.size() < (order)) {
						break;
					}
					AnalysisModelEntry entry = inputEntryList.get(order - 1);
					if (param.contains("desp")) {
						if (value.length() > 1) {
							entry.setDescription(value);
						}
					} else if (param.contains("validation")) {
						if (value.length() > 1) {
							entry.setValidation(value);
						}

					} else if (param.contains("type")) {
						if (value.length() > 1) {
							entry.setDataType(value);
						}
					} else if (param.contains("name")) {
						if (value.length() > 1) {
							entry.setEntry_name(value);
						}
					}
					inputEntryList.set((order - 1), entry);
				} else if (param.startsWith("output")) {
					int order = parseEntryID(param);
					System.out.println("output order:" + order);
					if (outputEntryList.size() < (order)) {
						break;
					}
					AnalysisModelEntry entry = outputEntryList.get(order - 1);
					if (param.contains("desp")) {
						if (value.length() > 1) {
							entry.setDescription(value);
						}
					} else if (param.contains("validation")) {
						if (value.length() > 1) {
							entry.setValidation(value);
						}

					} else if (param.contains("type")) {
						if (value.length() > 1) {
							entry.setDataType(value);
						}
					} else if (param.contains("dataaction")) {
						if (value.length() > 1) {
							entry.setDataAction(value);
						}
					} else if (param.contains("name")) {
						if (value.length() > 1) {
							entry.setEntry_name(value);
						}
					}
					outputEntryList.set((order - 1), entry);
				}

			}
			if (validateInputModelEntries(inputEntryList) == false) {

				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						"Input Entries");
				return;
			}

			if (validateOutputModelEntries(outputEntryList) == false) {

				ReturnParser.outputErrorException(response,
						AllConstants.ErrorDictionary.MISSING_DATA, null,
						"Output Entries");
				return;
			}
			List<AnalysisModelEntry> totalEntryList = new ArrayList<>();
			totalEntryList.addAll(inputEntryList);
			totalEntryList.addAll(outputEntryList);
			asDao.updateModelEntries(totalEntryList);
			model.setStatus(AScontants.status_live);
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			System.out.println(gson.toJson(jo));
			out.println(gson.toJson(jo));

		} catch (Exception ex) {
			ex.printStackTrace();
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, "");
			return;
		} finally {
			out.close();
		}

	}

	public static void main(String args[]) {
		System.out.println(parseEntryID("output1_type123"));
	}
}
