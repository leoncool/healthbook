package servlets.datamarket;

import health.database.DAO.as.AnalysisServiceDAO;
import health.database.DAO.datamarket.DataMarketDAO;
import health.database.datamarket.DataMarket;
import health.database.models.Datastream;
import health.database.models.as.AnalysisModel;
import health.database.models.as.AnalysisModelEntry;
import health.database.models.as.AnalysisService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.MarketplaceContants;
import util.AllConstants;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetModelMetadata
 */
@WebServlet("/GetDataMarketList")
public class GetDataMarketList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetDataMarketList() {
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
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		response.setHeader("Access-Control-Allow-Methods",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_METHODS);
		response.setHeader("Access-Control-Expose-Headers",
				MarketplaceContants.ACCESS_CONTROL_ALLOW_HEADERS);
		PrintWriter out = response.getWriter();
		try {
			Gson gson = new Gson();
			DataMarketDAO dmDao=new DataMarketDAO();
			String searchName = null;
			searchName=request.getParameter(MarketplaceContants.RequestParameters.streamTitle);
			List<DataMarket> dmListRaw=dmDao.getDataMarketListing(searchName);
			List<DataMarket> dmList=new ArrayList<DataMarket>();
			for(DataMarket dm:dmListRaw)
			{
				Datastream stream=dm.getStreamID();
				stream.setDatastreamBlocksList(null);
				stream.setDatastreamUnitsList(null);
				dm.setStreamID(stream);
				dmList.add(dm);	
			}
			
		
			JsonObject jo = new JsonObject();
			jo.addProperty(AllConstants.ProgramConts.result,
					AllConstants.ProgramConts.succeed);
			JsonElement jelement=gson.toJsonTree(dmList);
			jo.add("data_market",jelement );
			System.out.println(gson.toJson(jo));
			out.println(gson.toJson(jo));

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
		Gson gson = new Gson();
		DataMarketDAO dmDao=new DataMarketDAO();
		String searchName = null;
		List<DataMarket> dmListRaw=dmDao.getDataMarketListing(searchName);
		List<DataMarket> dmList=new ArrayList<DataMarket>();
		for(DataMarket dm:dmListRaw)
		{
			Datastream stream=dm.getStreamID();
			stream.setDatastreamBlocksList(null);
			stream.setDatastreamUnitsList(null);
			dm.setStreamID(stream);
			dmList.add(dm);	
		}
		
	
		JsonObject jo = new JsonObject();
		jo.addProperty(AllConstants.ProgramConts.result,
				AllConstants.ProgramConts.succeed);
		JsonElement jelement=gson.toJsonTree(dmList);
		jo.add("data_market_list",jelement );
		System.out.println(gson.toJson(jo));
//		out.println(gson.toJson(jo));

		// for (int i = 0; i < m.groupCount(); i++)
		// System.out.println("Group" + i + ": " + m.group(i));
	}

}
