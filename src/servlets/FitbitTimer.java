package servlets;

import health.database.DAO.DataSummaryDAO;
import health.database.DAO.DatastreamDAO;
import health.database.DAO.Ext_API_Info_DAO;
import health.database.DAO.HealthDataStreamDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.DataSummary;
import health.database.models.Datastream;
import health.database.models.ExternalApiInfo;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;

import java.io.IOException;
import java.io.PrintWriter;
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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import server.exception.ErrorCodeException;
import util.AllConstants;
import util.DateUtil;

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
import com.fitbit.api.common.model.timeseries.Data;
import com.fitbit.api.common.model.timeseries.IntradayData;
import com.fitbit.api.common.model.timeseries.IntradaySummary;
import com.fitbit.api.common.model.timeseries.TimeSeriesResourceType;
import com.fitbit.api.model.FitbitUser;

/**
 * Servlet implementation class FitbitTimer
 */
@WebServlet(name = "FitbitTimer", urlPatterns = { "/FitbitTimer" }
// ,
// loadOnStartup = 0
)
// loadOnStartup=0
public class FitbitTimer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Timer timer=null;
	private String apiBaseUrl;
	private String fitbitSiteBaseUrl;
	private String clientConsumerKey;
	private String clientSecret;
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_VERIFIER = "oauth_verifier";

	private FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
	private FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
	private FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();
	private static FitbitAPIClientService<FitbitApiClientAgent> apiClientService;
	private DatastreamDAO dstreamDao = new DatastreamDAO();
	private HealthDataStreamDAO healthDSdao = new HealthDataStreamDAO();
	private HBaseDatapointDAO datapointDao = null;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		timer.cancel();
		timer = null;

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
			System.out.println("------After getting Property File-------");
			apiBaseUrl = properties.getProperty("apiBaseUrl");
			System.out.println("------Getting apiBaseUrl Property File-------");
			fitbitSiteBaseUrl = properties.getProperty("fitbitSiteBaseUrl");
			System.out
					.println("------Getting fitbitSiteBaseUrl Property File-------");
			clientConsumerKey = properties.getProperty("clientConsumerKey");
			System.out
					.println("------Getting clientConsumerKey Property File-------");
			clientSecret = properties.getProperty("clientSecret");
			System.out
					.println("------Getting clientSecret Property File-------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println("------Initializing DataPointDAO-------");
			datapointDao = new HBaseDatapointDAO();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("before Fitbit Timer.......");
		apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
				new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl,
						credentialsCache), clientConsumerKey, clientSecret,
				credentialsCache, entityCache, subscriptionStore);
		System.out.println("starting Fitbit Timer.......");
//		timer = new Timer();
//		timer.schedule(new RemindTask(), 0, // initial delay
//				5 * 60 * 1000); // subsequent rate

	}

	class RemindTask extends TimerTask {
		Ext_API_Info_DAO extDao = new Ext_API_Info_DAO();

		public void run() {
			List<ExternalApiInfo> apiinfoList = extDao
					.getExt_API_INFO_List(AllConstants.ExternalAPIConsts.fitbit_device);

			for (ExternalApiInfo apiinfo : apiinfoList) {

				try {
					Date start = new Date();

					if (apiinfo.getLateDataUpdate() == null) {
						long one_month = 1000 * 60 * 60 * 24 * 30L;
						long day = 1000 * 60 * 60 * 24L;
						// long calculate=start.getTime()-one_month;
						// start=new Date(calculate);
						for (int i = 0; i < 30; i++) {
							long calculate = start.getTime() - i * day;
							Date temp_date = new Date(calculate);
							System.out.println("Getting " + temp_date + " ");
							LocalDate fitbitDate = LocalDate
									.fromDateFields(temp_date);
							importStepsData(apiinfo, fitbitDate,
									apiClientService);
							importFloorsData(apiinfo, fitbitDate,
									apiClientService);
							import_Calories_burned_Data(apiinfo, fitbitDate,
									apiClientService);

							Date now = new Date();
							apiinfo.setLateDataUpdate(now);
							extDao.Update_A_ExtAPI(apiinfo);
						}
						LocalDate endDate = LocalDate.now();

						LocalDate startDate = endDate.minusDays(30);
						importdistanceSummary(apiinfo, startDate, endDate,
								apiClientService);
//						importCaloriesSummary(apiinfo, startDate, endDate,
//								apiClientService);
					} else {
						start = apiinfo.getLateDataUpdate();
						long one_month = 1000 * 60 * 60 * 24 * 30L;
						long day = 1000 * 60 * 60 * 24L;
						// long calculate=start.getTime()-one_month;
						// start=new Date(calculate);
						for (int i = 0; i < 5; i++) {
							long calculate = start.getTime() - i * day;
							Date temp_date = new Date(calculate);
							System.out.println("Getting " + temp_date + " ");
							LocalDate fitbitDate = LocalDate
									.fromDateFields(temp_date);
							importStepsData(apiinfo, fitbitDate,
									apiClientService);
							importFloorsData(apiinfo, fitbitDate,
									apiClientService);
							import_Calories_burned_Data(apiinfo, fitbitDate,
									apiClientService);
							Date now = new Date();
							apiinfo.setLateDataUpdate(now);
							extDao.Update_A_ExtAPI(apiinfo);
						}
						LocalDate endDate = LocalDate.now();

						LocalDate startDate = endDate.minusDays(30);
						importdistanceSummary(apiinfo, startDate, endDate,
								apiClientService);
//						importCaloriesSummary(apiinfo, startDate, endDate,
//								apiClientService);
						//
						// start=apiinfo.getLateDataUpdate();
						// LocalDate fitbitDate=LocalDate.fromDateFields(start);
						// importStepsData(apiinfo, fitbitDate,
						// apiClientService);
						// importFloorsData(apiinfo, fitbitDate,
						// apiClientService);
						// import_Calories_burned_Data(apiinfo, fitbitDate,
						// apiClientService);
						// Date now=new Date();
						// apiinfo.setLateDataUpdate(now);
						// extDao.Update_A_ExtAPI(apiinfo);
					}

				} catch (Exception ex) {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("command");
		PrintWriter out = response.getWriter();

		if (command == null) {
			out.write("no command");
			return;
		}
		if (command.equalsIgnoreCase("on")) {
			if (timer == null) {
				timer = new Timer();
				timer.schedule(new RemindTask(), 0, // initial delay
						5 * 60 * 1000); // subsequent rate
			}
			out.write("Timer is on");
			return;
		}
		else if(command.equalsIgnoreCase("off")){
			if(timer!=null)
			{
			timer.cancel();
			timer = null;
			}
			out.write("Timer is off");
			return;
		}
		else{
			out.write("no command");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	
		
		String command = request.getParameter("command");
		PrintWriter out = response.getWriter();

		if (command == null) {
			out.write("no command");
			return;
		}
		if (command.equalsIgnoreCase("on")) {
			if (timer == null) {
				timer = new Timer();
				timer.schedule(new RemindTask(), 0, // initial delay
						5 * 60 * 1000); // subsequent rate
			}
			out.write("Timer is on");
			return;
		}
		else if(command.equalsIgnoreCase("off")){
			timer.cancel();
			timer = null;
			out.write("Timer is off");
			return;
		}
		else{
			out.write("no command");
			return;
		}
	}

	public void importStepsData(ExternalApiInfo apiinfo, LocalDate fitbitDate,
			FitbitAPIClientService<FitbitApiClientAgent> apiClientService)
			throws FitbitAPIException, NumberFormatException,
			ErrorCodeException, IOException, ParseException {
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());

		IntradaySummary stepSummary = apiClientService.getClient()
				.getIntraDayTimeSeries(
						new LocalUserDetail(apiinfo.getLoginID(),
								apiinfo.getExtId()), fitbitUser,
						TimeSeriesResourceType.STEPS, fitbitDate);
		if (stepSummary == null || stepSummary.getIntradayDataset() == null
				|| stepSummary.getIntradayDataset().getDataset() == null) {
			System.out.println("-------No record-----" + fitbitDate.toString());
			return;
		}
		// System.out.println("Size Summary:"
		// + stepSummary.getIntradayDataset().getDataset().size());
		List<IntradayData> intraDataList = stepSummary.getIntradayDataset()
				.getDataset();
		HBaseDataImport hbaseImport = new HBaseDataImport();

		List<JsonDataPoints> jdataPList = new ArrayList<JsonDataPoints>();
		Datastream ds = healthDSdao.getDefaultDatastreamOfType(
				apiinfo.getLoginID(), "steps");

		double summaryValue = 0;
		for (IntradayData data : intraDataList) {
			Calendar cal = Calendar.getInstance(DateUtil.UTC);
			cal.setTime(fitbitDate.toDate());
			LocalTime tempLocalTime = LocalTime.parse(data.getTime());
			cal.set(Calendar.HOUR_OF_DAY, tempLocalTime.getHourOfDay());
			cal.set(Calendar.MINUTE, tempLocalTime.getMinuteOfHour());
			cal.set(Calendar.SECOND, tempLocalTime.getSecondOfMinute());
			// System.out.println(" "+cal.getTime()+" "+data.getValue());
			JsonDataPoints jdatapoint = new JsonDataPoints();
			jdatapoint.setAt(Long.toString(cal.getTime().getTime()));
			JsonDataValues jvalue = new JsonDataValues();
			jvalue.setUnit_id(ds.getDatastreamUnitsList().get(0).getUnitID());
			jvalue.setVal(Double.toString(data.getValue()));
			List<JsonDataValues> jsonvalueList = new ArrayList<JsonDataValues>();
			jsonvalueList.add(jvalue);

			jdatapoint.setValue_list(jsonvalueList);
			jdataPList.add(jdatapoint);
			summaryValue = summaryValue + data.getValue();
		}

		hbaseImport.setData_points(jdataPList);
		hbaseImport.setDatastream_id(ds.getStreamId());
		datapointDao.importDatapointsDatapoints(hbaseImport);
		DataSummary: {
			DataSummary datasummry = new DataSummary();
			DataSummaryDAO dsummaryDao = new DataSummaryDAO();
			datasummry.setValue(summaryValue);
			datasummry.setDstreamID(ds.getStreamId());
			datasummry.setDate(fitbitDate.toDate());
			datasummry.setTitle(ds.getTitle());
			dsummaryDao.create_A_DataSummary(datasummry);
		}
	}

	public void importFloorsData(ExternalApiInfo apiinfo, LocalDate fitbitDate,
			FitbitAPIClientService<FitbitApiClientAgent> apiClientService)
			throws FitbitAPIException, NumberFormatException,
			ErrorCodeException, IOException, ParseException {
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());

		IntradaySummary stepSummary = apiClientService.getClient()
				.getIntraDayTimeSeries(
						new LocalUserDetail(apiinfo.getLoginID(),
								apiinfo.getExtId()), fitbitUser,
						TimeSeriesResourceType.FLOORS, fitbitDate);
		if (stepSummary == null || stepSummary.getIntradayDataset() == null
				|| stepSummary.getIntradayDataset().getDataset() == null) {
			System.out.println("-------No record-----" + fitbitDate.toString());
			return;
		}
		// System.out.println("Size Summary:"
		// + stepSummary.getIntradayDataset().getDataset().size());
		List<IntradayData> intraDataList = stepSummary.getIntradayDataset()
				.getDataset();
		HBaseDataImport hbaseImport = new HBaseDataImport();

		List<JsonDataPoints> jdataPList = new ArrayList<JsonDataPoints>();
		Datastream ds = healthDSdao.getDefaultDatastreamOfType(
				apiinfo.getLoginID(), "floor_climbed");
		double summaryValue = 0;
		for (IntradayData data : intraDataList) {
			Calendar cal = Calendar.getInstance(DateUtil.UTC);
			cal.setTime(fitbitDate.toDate());
			LocalTime tempLocalTime = LocalTime.parse(data.getTime());
			cal.set(Calendar.HOUR_OF_DAY, tempLocalTime.getHourOfDay());
			cal.set(Calendar.MINUTE, tempLocalTime.getMinuteOfHour());
			cal.set(Calendar.SECOND, tempLocalTime.getSecondOfMinute());
			// System.out.println(" "+cal.getTime()+" "+data.getValue());
			JsonDataPoints jdatapoint = new JsonDataPoints();
			jdatapoint.setAt(Long.toString(cal.getTime().getTime()));
			JsonDataValues jvalue = new JsonDataValues();
			jvalue.setUnit_id(ds.getDatastreamUnitsList().get(0).getUnitID());
			jvalue.setVal(Double.toString(data.getValue()));
			List<JsonDataValues> jsonvalueList = new ArrayList<JsonDataValues>();
			jsonvalueList.add(jvalue);
			jdatapoint.setValue_list(jsonvalueList);
			jdataPList.add(jdatapoint);
			summaryValue = summaryValue + data.getValue();
		}
		hbaseImport.setData_points(jdataPList);
		hbaseImport.setDatastream_id(ds.getStreamId());
		datapointDao.importDatapointsDatapoints(hbaseImport);
		DataSummary: {
			DataSummary datasummry = new DataSummary();
			DataSummaryDAO dsummaryDao = new DataSummaryDAO();
			datasummry.setValue(summaryValue);
			datasummry.setDstreamID(ds.getStreamId());
			datasummry.setDate(fitbitDate.toDate());
			datasummry.setTitle(ds.getTitle());
			dsummaryDao.create_A_DataSummary(datasummry);
		}
	}

	public void import_Calories_burned_Data(ExternalApiInfo apiinfo,
			LocalDate fitbitDate,
			FitbitAPIClientService<FitbitApiClientAgent> apiClientService)
			throws FitbitAPIException, NumberFormatException,
			ErrorCodeException, IOException, ParseException {
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());

		IntradaySummary stepSummary = apiClientService.getClient()
				.getIntraDayTimeSeries(
						new LocalUserDetail(apiinfo.getLoginID(),
								apiinfo.getExtId()), fitbitUser,
						TimeSeriesResourceType.CALORIES_OUT, fitbitDate);
		if (stepSummary == null || stepSummary.getIntradayDataset() == null
				|| stepSummary.getIntradayDataset().getDataset() == null) {
			System.out.println("-------No record-----" + fitbitDate.toString());
			return;
		}
		// System.out.println("Size Summary:"
		// + stepSummary.getIntradayDataset().getDataset().size());
		List<IntradayData> intraDataList = stepSummary.getIntradayDataset()
				.getDataset();
		HBaseDataImport hbaseImport = new HBaseDataImport();

		List<JsonDataPoints> jdataPList = new ArrayList<JsonDataPoints>();
		Datastream ds = healthDSdao.getDefaultDatastreamOfType(
				apiinfo.getLoginID(), "calories_burned");
		double summaryValue = 0;
		for (IntradayData data : intraDataList) {
			Calendar cal = Calendar.getInstance(DateUtil.UTC);
			cal.setTime(fitbitDate.toDate());
			LocalTime tempLocalTime = LocalTime.parse(data.getTime());
			cal.set(Calendar.HOUR_OF_DAY, tempLocalTime.getHourOfDay());
			cal.set(Calendar.MINUTE, tempLocalTime.getMinuteOfHour());
			cal.set(Calendar.SECOND, tempLocalTime.getSecondOfMinute());
			// System.out.println(" "+cal.getTime()+" "+data.getValue());
			JsonDataPoints jdatapoint = new JsonDataPoints();
			jdatapoint.setAt(Long.toString(cal.getTime().getTime()));
			JsonDataValues jvalue = new JsonDataValues();
			jvalue.setUnit_id(ds.getDatastreamUnitsList().get(0).getUnitID());
			jvalue.setVal(Double.toString(data.getValue()));
			List<JsonDataValues> jsonvalueList = new ArrayList<JsonDataValues>();
			jsonvalueList.add(jvalue);
			jdatapoint.setValue_list(jsonvalueList);
			jdataPList.add(jdatapoint);
			summaryValue = summaryValue + data.getValue();
		}
		hbaseImport.setData_points(jdataPList);
		hbaseImport.setDatastream_id(ds.getStreamId());
		datapointDao.importDatapointsDatapoints(hbaseImport);
		DataSummary: {
			DataSummary datasummry = new DataSummary();
			DataSummaryDAO dsummaryDao = new DataSummaryDAO();
			datasummry.setValue(summaryValue);
			datasummry.setDstreamID(ds.getStreamId());
			datasummry.setDate(fitbitDate.toDate());
			datasummry.setTitle(ds.getTitle());
			dsummaryDao.create_A_DataSummary(datasummry);
		}
	}

	public void importdistanceSummary(ExternalApiInfo apiinfo, LocalDate start,
			LocalDate end,
			FitbitAPIClientService<FitbitApiClientAgent> apiClientService)
			throws FitbitAPIException, NumberFormatException,
			ErrorCodeException, IOException, ParseException {
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());
		DateUtil dateUtil = new DateUtil();
		// LocalDate startDate = LocalDate.parse("2013-01-07",
		// DateTimeFormat.forPattern(dateUtil.YearMonthDay_DateFormat_pattern));
		// LocalDate endDate = LocalDate.parse("2013-01-11",
		// DateTimeFormat.forPattern(dateUtil.YearMonthDay_DateFormat_pattern));

		List<Data> dataSummaries = apiClientService.getClient().getTimeSeries(
				new LocalUserDetail(apiinfo.getLoginID(), apiinfo.getExtId()),
				fitbitUser, TimeSeriesResourceType.DISTANCE, start, end);
		Datastream ds = healthDSdao.getDefaultDatastreamOfType(
				apiinfo.getLoginID(), "distance_travel");
		for (Data data : dataSummaries) {
//			System.out.println(data.getDateTime() + "," + data.getValue());
			DataSummary: {
				DataSummary datasummry = new DataSummary();
				DataSummaryDAO dsummaryDao = new DataSummaryDAO();
				datasummry.setValue(Double.parseDouble(data.getValue()));
				datasummry.setDstreamID(ds.getStreamId());
				datasummry
						.setDate(LocalDate
								.parse(data.getDateTime(),
										DateTimeFormat
												.forPattern(dateUtil.YearMonthDay_DateFormat_pattern))
								.toDate());
				datasummry.setTitle(ds.getTitle());
				dsummaryDao.create_A_DataSummary(datasummry);
			}
		}
	}

	public void importCaloriesSummary(ExternalApiInfo apiinfo, LocalDate start,
			LocalDate end,
			FitbitAPIClientService<FitbitApiClientAgent> apiClientService)
			throws FitbitAPIException, NumberFormatException,
			ErrorCodeException, IOException, ParseException {
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());
		DateUtil dateUtil = new DateUtil();
		// LocalDate startDate = LocalDate.parse("2013-01-07",
		// DateTimeFormat.forPattern(dateUtil.YearMonthDay_DateFormat_pattern));
		// LocalDate endDate = LocalDate.parse("2013-01-11",
		// DateTimeFormat.forPattern(dateUtil.YearMonthDay_DateFormat_pattern));

		List<Data> dataSummaries = apiClientService.getClient().getTimeSeries(
				new LocalUserDetail(apiinfo.getLoginID(), apiinfo.getExtId()),
				fitbitUser, TimeSeriesResourceType.ACTIVITY_CALORIES, start,
				end);
		Datastream ds = healthDSdao.getDefaultDatastreamOfType(
				apiinfo.getLoginID(), "calories_burned");
		for (Data data : dataSummaries) {
			System.out.println(data.getDateTime() + "," + data.getValue());
			DataSummary: {
				DataSummary datasummry = new DataSummary();
				DataSummaryDAO dsummaryDao = new DataSummaryDAO();
				datasummry.setValue(Double.parseDouble(data.getValue()));
				datasummry.setDstreamID(ds.getStreamId());
				datasummry
						.setDate(LocalDate
								.parse(data.getDateTime(),
										DateTimeFormat
												.forPattern(dateUtil.YearMonthDay_DateFormat_pattern))
								.toDate());
				datasummry.setTitle(ds.getTitle());
				dsummaryDao.create_A_DataSummary(datasummry);
			}
		}
	}

	public static void main(String args[]) throws ServletException,
			NumberFormatException, FitbitAPIException, ErrorCodeException,
			IOException, ParseException {
		FitbitTimer timer = new FitbitTimer();
		timer.init();
		Ext_API_Info_DAO extDao = new Ext_API_Info_DAO();
		ExternalApiInfo info = extDao.getExt_API_INFO("dongdong", "fitbit",
				null);
		LocalDate endDate = LocalDate.now();

		LocalDate startDate = endDate.minusDays(90);
		timer.importCaloriesSummary(info, startDate, endDate, apiClientService);
	}
}
