package servlets.analysis.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AScontants;
import util.AllConstants;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetModelMetadata")
public class GetModelMetadata extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetModelMetadata() {
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

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				AScontants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				AScontants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			Gson gson = new Gson();
			String modelName = request
					.getParameter(AScontants.RequestParameters.ModelName);
			if (modelName != null) {
				ReturnParser
						.outputErrorException(
								response,
								AllConstants.ErrorDictionary.unknown_analysis_model_name,
								null, modelName);
				return;
				// JsonElement je = gson.toJsonTree(jobject);
				/*
				 * JsonObject jo = new JsonObject();
				 * jo.addProperty(AllConstants.ProgramConts.result,
				 * AllConstants.ProgramConts.succeed); // jo.add("datastream",
				 * je); JsonWriter jwriter = new JsonWriter(out);
				 * gson.toJson(jo, jwriter);
				 * System.out.println(gson.toJson(jo));
				 */
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doGet(request, response);
	}

	public static void main(String args[]) {
		String s = "function [TimeStamp,UnitID,ValueList,TagList]=main(aaa)";
		Pattern p = Pattern.compile("^function \\[(.+?)\\]=main[(](.*?)[)]");
		Matcher m = p.matcher(s);
		if(m.matches())
		{
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}
	
		String aaa="ccccccc";
		String[] aaa_split=aaa.split(",");
		for(String a:aaa_split)
		{
			System.out.println(a);
		}
		
//		for (int i = 0; i < m.groupCount(); i++)
//			System.out.println("Group" + i + ": " + m.group(i));
	}

}
