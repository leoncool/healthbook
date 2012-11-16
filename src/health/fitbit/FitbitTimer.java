package health.fitbit;

import health.database.DAO.DataPointDAO;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.Ext_API_Info_DAO;
import health.database.DAO.HealthDataStreamDAO;
import health.database.models.Datastream;
import health.database.models.ExternalApiInfo;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import server.exception.ErrorCodeException;
import util.AllConstants;

import com.fitbit.api.FitbitAPIException;
import com.fitbit.api.client.FitbitAPIEntityCache;
import com.fitbit.api.client.FitbitApiClientAgent;
import com.fitbit.api.client.FitbitApiCredentialsCache;
import com.fitbit.api.client.FitbitApiCredentialsCacheMapImpl;
import com.fitbit.api.client.FitbitApiEntityCacheMapImpl;
import com.fitbit.api.client.FitbitApiSubscriptionStorage;
import com.fitbit.api.client.FitbitApiSubscriptionStorageInMemoryImpl;
import com.fitbit.api.client.LocalUserDetail;
import com.fitbit.api.client.service.FitbitAPIClientService;
import com.fitbit.api.common.model.timeseries.IntradayData;
import com.fitbit.api.common.model.timeseries.IntradaySummary;
import com.fitbit.api.common.model.timeseries.TimeSeriesResourceType;
import com.fitbit.api.model.FitbitUser;

/**
 * Servlet implementation class FitbitTimer
 */
@WebServlet("/FitbitTimer")
public class FitbitTimer extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	private static Timer timer;
	private String apiBaseUrl;
	private String fitbitSiteBaseUrl;
	private String clientConsumerKey;
	private String clientSecret;
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_VERIFIER = "oauth_verifier";

	private FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
	private FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
	private FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();
	private static FitbitAPIClientService<FitbitApiClientAgent> apiClientService ;
	private DatastreamDAO dstreamDao=new DatastreamDAO();
	private HealthDataStreamDAO healthDSdao=new HealthDataStreamDAO();
	private DataPointDAO datapointDao=null;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		timer.cancel();
		timer=null;
		
	}
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		System.out.println(getClass().getClassLoader().toString());
		try {
			Properties properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"config.properties"));

			System.out.println(getClass().getClassLoader().toString());
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"config.properties"));
			apiBaseUrl = properties.getProperty("apiBaseUrl");
			fitbitSiteBaseUrl = properties.getProperty("fitbitSiteBaseUrl");

			clientConsumerKey = properties.getProperty("clientConsumerKey");
			clientSecret = properties.getProperty("clientSecret");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			datapointDao = new DataPointDAO();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
				new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl,
						credentialsCache), clientConsumerKey, clientSecret,
				credentialsCache, entityCache, subscriptionStore);
		 timer = new Timer();
	        timer.schedule(new RemindTask(),
	                       0,        //initial delay
	                       5*60*1000);  //subsequent rate
	}
	 class RemindTask extends TimerTask {
	       Ext_API_Info_DAO extDao=new Ext_API_Info_DAO();
	       
	        public void run() {
	        	List<ExternalApiInfo> apiinfoList=  extDao.getExt_API_INFO_List(AllConstants.ExternalAPIConsts.fitbit_device);
									
	        	for(ExternalApiInfo apiinfo:apiinfoList)
	        	{
	        		
	        		try{
	        			Date start=new Date();	        	
	        			
	        			if(apiinfo.getLateDataUpdate()==null)
	        			{
	        				long one_month=1000*60*60*24*30L;
	        				long day=1000*60*60*24L;
	        			//	long calculate=start.getTime()-one_month;
	        			//	start=new Date(calculate);
	        				for(int i=5;i<30;i++)
	        				{
	        					long calculate=start.getTime()-i*day;
	        					Date temp_date=new Date(calculate);
	        					LocalDate fitbitDate=LocalDate.fromDateFields(temp_date);
	        					importStepsData(apiinfo, fitbitDate, apiClientService);	    
	        					importFloorsData(apiinfo, fitbitDate, apiClientService);
	        					import_Calories_burned_Data(apiinfo, fitbitDate, apiClientService);
	        					Date now=new Date();
	        					apiinfo.setLateDataUpdate(now); 
	        					extDao.Update_A_ExtAPI(apiinfo);
	        				}
	        			}
	        			else{
	        				start=apiinfo.getLateDataUpdate();
	        				LocalDate fitbitDate=LocalDate.fromDateFields(start);
	        				importStepsData(apiinfo, fitbitDate, apiClientService);	    
        					importFloorsData(apiinfo, fitbitDate, apiClientService);
        					import_Calories_burned_Data(apiinfo, fitbitDate, apiClientService);
        					Date now=new Date();
        					apiinfo.setLateDataUpdate(now); 
        					extDao.Update_A_ExtAPI(apiinfo);
	        			}
	        			
	        		}
	        		catch(Exception ex)
	        		{
	        			ex.printStackTrace();
	        		}
	        	}
	           
	        }
	    }
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FitbitTimer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	public void importStepsData(ExternalApiInfo apiinfo,LocalDate fitbitDate,FitbitAPIClientService<FitbitApiClientAgent> apiClientService ) throws FitbitAPIException, NumberFormatException, ErrorCodeException, IOException, ParseException
	{
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());
		
		IntradaySummary stepSummary = apiClientService.getClient()
				.getIntraDayTimeSeries(
						new LocalUserDetail(apiinfo.getLoginID(),apiinfo.getExtId()),
						fitbitUser, TimeSeriesResourceType.STEPS, fitbitDate
						);
		System.out.println("Size Summary:"
				+ stepSummary.getIntradayDataset().getDataset().size());
		List<IntradayData> intraDataList=stepSummary.getIntradayDataset().getDataset();	 
		HBaseDataImport hbaseImport=new HBaseDataImport();
		
		List<JsonDataPoints> jdataPList=new ArrayList<JsonDataPoints>();
		Datastream ds=healthDSdao.getDefaultDatastreamOfType(apiinfo.getLoginID(),"steps");

		for(IntradayData data:intraDataList)
		{	
			Calendar cal=Calendar.getInstance();
			cal.setTime(fitbitDate.toDate());
			LocalTime tempLocalTime=LocalTime.parse(data.getTime());
			cal.set(Calendar.HOUR_OF_DAY, tempLocalTime.getHourOfDay());
			cal.set(Calendar.MINUTE, tempLocalTime.getMinuteOfHour());
			cal.set(Calendar.SECOND, tempLocalTime.getSecondOfMinute());
			System.out.println(" "+cal.getTime()+" "+data.getValue());
			JsonDataPoints jdatapoint=new JsonDataPoints();
			jdatapoint.setAt(Long.toString(cal.getTime().getTime()));
			JsonDataValues jvalue=new JsonDataValues();
			jvalue.setUnit_id(ds.getDatastreamUnitsList().get(0).getUnitID());
			jvalue.setVal(Double.toString(data.getValue()));
			List<JsonDataValues> jsonvalueList=new ArrayList<JsonDataValues>();
			jsonvalueList.add(jvalue);
			jdatapoint.setValue_list(jsonvalueList);
			jdataPList.add(jdatapoint);			        				
		}	
		hbaseImport.setData_points(jdataPList);
	hbaseImport.setDatastream_id(ds.getStreamId());
	datapointDao.importDatapoints(hbaseImport);	        	
	}
	public void importFloorsData(ExternalApiInfo apiinfo,LocalDate fitbitDate,FitbitAPIClientService<FitbitApiClientAgent> apiClientService ) throws FitbitAPIException, NumberFormatException, ErrorCodeException, IOException, ParseException
	{
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());
		
		IntradaySummary stepSummary = apiClientService.getClient()
				.getIntraDayTimeSeries(
						new LocalUserDetail(apiinfo.getLoginID(),apiinfo.getExtId()),
						fitbitUser, TimeSeriesResourceType.FLOORS, fitbitDate
						);
		System.out.println("Size Summary:"
				+ stepSummary.getIntradayDataset().getDataset().size());
		List<IntradayData> intraDataList=stepSummary.getIntradayDataset().getDataset();	 
		HBaseDataImport hbaseImport=new HBaseDataImport();
		
		List<JsonDataPoints> jdataPList=new ArrayList<JsonDataPoints>();
		Datastream ds=healthDSdao.getDefaultDatastreamOfType(apiinfo.getLoginID(),"floor_climbed");

		for(IntradayData data:intraDataList)
		{	
			Calendar cal=Calendar.getInstance();
			cal.setTime(fitbitDate.toDate());
			LocalTime tempLocalTime=LocalTime.parse(data.getTime());
			cal.set(Calendar.HOUR_OF_DAY, tempLocalTime.getHourOfDay());
			cal.set(Calendar.MINUTE, tempLocalTime.getMinuteOfHour());
			cal.set(Calendar.SECOND, tempLocalTime.getSecondOfMinute());
			System.out.println(" "+cal.getTime()+" "+data.getValue());
			JsonDataPoints jdatapoint=new JsonDataPoints();
			jdatapoint.setAt(Long.toString(cal.getTime().getTime()));
			JsonDataValues jvalue=new JsonDataValues();
			jvalue.setUnit_id(ds.getDatastreamUnitsList().get(0).getUnitID());
			jvalue.setVal(Double.toString(data.getValue()));
			List<JsonDataValues> jsonvalueList=new ArrayList<JsonDataValues>();
			jsonvalueList.add(jvalue);
			jdatapoint.setValue_list(jsonvalueList);
			jdataPList.add(jdatapoint);			        				
		}	
		hbaseImport.setData_points(jdataPList);
	hbaseImport.setDatastream_id(ds.getStreamId());
	datapointDao.importDatapoints(hbaseImport);	        	
	}
	public void import_Calories_burned_Data(ExternalApiInfo apiinfo,LocalDate fitbitDate,FitbitAPIClientService<FitbitApiClientAgent> apiClientService ) throws FitbitAPIException, NumberFormatException, ErrorCodeException, IOException, ParseException
	{
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());
		
		IntradaySummary stepSummary = apiClientService.getClient()
				.getIntraDayTimeSeries(
						new LocalUserDetail(apiinfo.getLoginID(),apiinfo.getExtId()),
						fitbitUser, TimeSeriesResourceType.CALORIES_OUT, fitbitDate
						);
		System.out.println("Size Summary:"
				+ stepSummary.getIntradayDataset().getDataset().size());
		List<IntradayData> intraDataList=stepSummary.getIntradayDataset().getDataset();	 
		HBaseDataImport hbaseImport=new HBaseDataImport();
		
		List<JsonDataPoints> jdataPList=new ArrayList<JsonDataPoints>();
		Datastream ds=healthDSdao.getDefaultDatastreamOfType(apiinfo.getLoginID(),"calories_burned");

		for(IntradayData data:intraDataList)
		{	
			Calendar cal=Calendar.getInstance();
			cal.setTime(fitbitDate.toDate());
			LocalTime tempLocalTime=LocalTime.parse(data.getTime());
			cal.set(Calendar.HOUR_OF_DAY, tempLocalTime.getHourOfDay());
			cal.set(Calendar.MINUTE, tempLocalTime.getMinuteOfHour());
			cal.set(Calendar.SECOND, tempLocalTime.getSecondOfMinute());
			System.out.println(" "+cal.getTime()+" "+data.getValue());
			JsonDataPoints jdatapoint=new JsonDataPoints();
			jdatapoint.setAt(Long.toString(cal.getTime().getTime()));
			JsonDataValues jvalue=new JsonDataValues();
			jvalue.setUnit_id(ds.getDatastreamUnitsList().get(0).getUnitID());
			jvalue.setVal(Double.toString(data.getValue()));
			List<JsonDataValues> jsonvalueList=new ArrayList<JsonDataValues>();
			jsonvalueList.add(jvalue);
			jdatapoint.setValue_list(jsonvalueList);
			jdataPList.add(jdatapoint);			        				
		}	
		hbaseImport.setData_points(jdataPList);
	hbaseImport.setDatastream_id(ds.getStreamId());
	datapointDao.importDatapoints(hbaseImport);	        	
	}
}
