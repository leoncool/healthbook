package servlets.datamarket;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.datamarket.DataMarketDAO;
import health.database.datamarket.DataMarket;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.exception.ReturnParser;
import util.AllConstants;
import util.MarketplaceContants;

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
				Datastream stream=dm.getDatastream();
				stream.setDatastreamBlocksList(null);
				stream.setDatastreamUnitsList(null);
				dm.setDatastream(stream);
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
			ReturnParser.outputErrorException(response,
					AllConstants.ErrorDictionary.Internal_Fault, null, null);
			return;
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
		DatastreamDAO dsDao=new DatastreamDAO();
		for(DataMarket dm:dmListRaw)
		{
			Datastream stream=dm.getDatastream();
			stream.setDatastreamBlocksList(null);
			String streamID=stream.getStreamId();
			List<DatastreamUnits> unitList=dsDao.getDatastreamUnits(streamID);
			dm.setDatastreamUnitsList(unitList);
			stream.setDatastreamBlocksList(null);
			stream.setDatastreamUnitsList(null);
			dm.setDatastream(stream);
			dmList.add(dm);	
		}
		
//		
		JsonObject jo = new JsonObject();
		jo.addProperty(AllConstants.ProgramConts.result,
				AllConstants.ProgramConts.succeed);
		JsonElement jelement=gson.toJsonTree(dmListRaw);
		jo.add("data_market_list",jelement );
		System.out.println(gson.toJson(jo));
//		System.out.println(dmListRaw.get(0).getStreamID().getDatastreamUnitsList().size());
//		out.println(gson.toJson(jo));

		// for (int i = 0; i < m.groupCount(); i++)
		// System.out.println("Group" + i + ": " + m.group(i));
	}

}
