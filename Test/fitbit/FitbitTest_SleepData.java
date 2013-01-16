package fitbit;

import health.database.DAO.Ext_API_Info_DAO;
import health.database.models.Datastream;
import health.database.models.ExternalApiInfo;
import health.hbase.models.HBaseDataImport;
import health.input.jsonmodels.JsonDataPoints;
import health.input.jsonmodels.JsonDataValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

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
import com.fitbit.api.common.model.sleep.Sleep;
import com.fitbit.api.common.model.sleep.SleepLog;
import com.fitbit.api.common.model.sleep.SleepSummary;
import com.fitbit.api.common.model.timeseries.IntradayData;
import com.fitbit.api.common.model.timeseries.IntradaySummary;
import com.fitbit.api.common.model.timeseries.TimeSeriesResourceType;
import com.fitbit.api.model.APICollectionType;
import com.fitbit.api.model.ApiSubscription;
import com.fitbit.api.model.FitbitUser;

public class FitbitTest_SleepData {
	private String apiBaseUrl;
	private String fitbitSiteBaseUrl;
	private String clientConsumerKey;
	private String clientSecret;
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_VERIFIER = "oauth_verifier";

	private FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
	private FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
	private FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();
	public static FitbitAPIClientService<FitbitApiClientAgent> apiClientService;
	public void init() throws IOException
	{
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
		apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
				new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl,
						credentialsCache), clientConsumerKey, clientSecret,
				credentialsCache, entityCache, subscriptionStore);
	}
	public static void main(String args[]) throws FitbitAPIException, IOException
	{
		Ext_API_Info_DAO extDao = new Ext_API_Info_DAO();
		ExternalApiInfo apiinfo = extDao.getExt_API_INFO("dongdong", "fitbit",
				null);
		FitbitTest_SleepData test=new FitbitTest_SleepData();
		LocalUserDetail userDetail=new LocalUserDetail(apiinfo.getLoginID(), apiinfo.getExtId());
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());
		test.init();
		
	//	test.apiClientService.getClient().subscribe("1", userDetail, fitbitUser, APICollectionType.user,"dongdong");
		LocalDate endDate = LocalDate.now();
		System.out.println(endDate.minusDays(1).toString());
		Sleep sleep
		=test.apiClientService.getClient().getSleep(userDetail, fitbitUser, endDate.minusDays(3));
		SleepSummary sleepSummary=sleep.getSummary();
		System.out.println("getTotalMinutesAsleep:"+sleepSummary.getTotalMinutesAsleep());
		System.out.println("getTotalSleepRecords:"+sleepSummary.getTotalSleepRecords());
		System.out.println("getTotalTimeInBed:"+sleepSummary.getTotalTimeInBed());
		
		List<SleepLog> sleepLogList=sleep.getSleepLogs();
		for(SleepLog log:sleepLogList)
		{
			System.out.println(log.getStartTime());
			System.out.println(log.getTimeInBed());//minutes
			System.out.println(log.getDuration()); //millisoncds
			System.out.println(log.getEfficiency());
			System.out.println(log.getMinutesAfterWakeup());
			System.out.println(log.getMinutesAwake());
			System.out.println(log.getAwakeningsCount());
			System.out.println(log.getMinutesAsleep());
		}
		IntradaySummary stepSummary = apiClientService.getClient()
				.getIntraDayTimeSeries(
						new LocalUserDetail(apiinfo.getLoginID(),
								apiinfo.getExtId()), fitbitUser,
						TimeSeriesResourceType.CALORIES_OUT, endDate.minusDays(1));
		if (stepSummary == null || stepSummary.getIntradayDataset() == null
				|| stepSummary.getIntradayDataset().getDataset() == null) {
			System.out.println("-------No record-----");
			return;
		}
		// System.out.println("Size Summary:"
		// + stepSummary.getIntradayDataset().getDataset().size());
		List<IntradayData> intraDataList = stepSummary.getIntradayDataset()
				.getDataset();
		HBaseDataImport hbaseImport = new HBaseDataImport();

		List<JsonDataPoints> jdataPList = new ArrayList<JsonDataPoints>();
		double summaryValue = 0;
		for (IntradayData data : intraDataList) {
//			Calendar cal = Calendar.getInstance(DateUtil.UTC);
//			cal.setTime(fitbitDate.toDate());
//			LocalTime tempLocalTime = LocalTime.parse(data.getTime());
//			cal.set(Calendar.HOUR_OF_DAY, tempLocalTime.getHourOfDay());
//			cal.set(Calendar.MINUTE, tempLocalTime.getMinuteOfHour());
//			cal.set(Calendar.SECOND, tempLocalTime.getSecondOfMinute());
//			// System.out.println(" "+cal.getTime()+" "+data.getValue());
//			JsonDataPoints jdatapoint = new JsonDataPoints();
//			jdatapoint.setAt(Long.toString(cal.getTime().getTime()));
//			JsonDataValues jvalue = new JsonDataValues();
//			jvalue.setUnit_id(ds.getDatastreamUnitsList().get(0).getUnitID());
//			jvalue.setVal(Double.toString(data.getValue()));
//			List<JsonDataValues> jsonvalueList = new ArrayList<JsonDataValues>();
//			jsonvalueList.add(jvalue);
//			jdatapoint.setValue_list(jsonvalueList);
//			jdataPList.add(jdatapoint);
//			summaryValue = summaryValue + data.getValue();
		//	LocalTime tempLocalTime = LocalTime.parse(data.getTime());
			System.out.println(data.getTime()+","+data.getValue());
		}
//	}
	}
}
