package health.fitbit;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

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
import com.fitbit.api.common.model.activities.Activities;
import com.fitbit.api.common.model.activities.ActivityLog;
import com.fitbit.api.common.model.timeseries.Data;
import com.fitbit.api.common.model.timeseries.IntradayData;
import com.fitbit.api.common.model.timeseries.IntradaySummary;
import com.fitbit.api.common.model.timeseries.TimeSeriesResourceType;
import com.fitbit.api.common.model.user.UserInfo;
import com.fitbit.api.model.FitbitUser;

/**
 * Servlet implementation class FitbitMiner
 */
@WebServlet("/FitbitMiner")
public class FitbitMiner extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String apiBaseUrl;
	private String fitbitSiteBaseUrl;
	private String clientConsumerKey;
	private String clientSecret;
	public static final String OAUTH_TOKEN = "oauth_token";
	public static final String OAUTH_VERIFIER = "oauth_verifier";

	private FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
	private FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
	private FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();

	/**
	 * @return
	 * @throws ServletException
	 * @see HttpServlet#HttpServlet()
	 */
	public void init() throws ServletException {
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

	}

	public FitbitMiner() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserInfo userInfo;
		try {
			FitbitAPIClientService<FitbitApiClientAgent> apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
					new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl,
							credentialsCache), clientConsumerKey, clientSecret,
					credentialsCache, entityCache, subscriptionStore);
			userInfo = apiClientService.getClient().getUserInfo(
					new LocalUserDetail("leoncool", "23KT43"));
			request.setAttribute("userInfo", userInfo);
			LocalDate date = LocalDate.parse("2012-11-08");
			LocalDate start = LocalDate.parse("2012-11-07");
			LocalDate end = LocalDate.parse("2012-11-08");
			FitbitUser fitbitUser = new FitbitUser("23KT43");
			Activities activities = apiClientService.getClient()
					.getActivities(new LocalUserDetail("leoncool", "23KT43"),
							fitbitUser, date);
			LocalTime localtime1 = LocalTime.parse("01:00");
			LocalTime localtime2 = LocalTime.parse("22:00");
			IntradaySummary summary = apiClientService.getClient()
					.getIntraDayTimeSeries(
							new LocalUserDetail("leoncool", "23KT43"),
							fitbitUser, TimeSeriesResourceType.STEPS, date,
							localtime1, localtime2);
			System.out.println("Size Summary:"
					+ summary.getIntradayDataset().getDataset().size());
			List<IntradayData> intraDataList=summary.getIntradayDataset().getDataset();
			for(IntradayData data:intraDataList)
			{
				
				System.out.println(data.getTime()+" "+data.getValue()+" ,level:"+data.getLevel());
			}
			List<ActivityLog> logList = activities.getActivities();

			List<Data> dataList = apiClientService.getClient().getTimeSeries(
					new LocalUserDetail("leoncool", "23KT43"), fitbitUser,
					TimeSeriesResourceType.STEPS_TRACKER, start, end);

			System.out.println("logListSize:" + logList.size());
			System.out.println("dataListSize:" + dataList.size());
			for (Data log : dataList) {
				System.out.println(log.getDateTime() + " : " + log.getValue());
			}
			for (ActivityLog log : logList) {
				System.out.println(log.getSteps());
			}
			System.out.println("totalStepsGoal:"
					+ activities.getActivityGoals().getSteps());
			System.out.println("totalSteps:"
					+ activities.getSummary().getSteps());
			System.out.println(userInfo.getAvatar());
		} catch (FitbitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
