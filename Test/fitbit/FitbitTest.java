package fitbit;

import health.database.DAO.Ext_API_Info_DAO;
import health.database.models.ExternalApiInfo;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

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
import com.fitbit.api.common.model.devices.Device;
import com.fitbit.api.model.FitbitUser;

public class FitbitTest {
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
		FitbitTest test=new FitbitTest();
		LocalUserDetail userDetail=new LocalUserDetail(apiinfo.getLoginID(), apiinfo.getExtId());
		FitbitUser fitbitUser = new FitbitUser(apiinfo.getExtId());
		test.init();
		System.out.println(apiClientService.getClient().getDevices(userDetail).get(0).getLastSyncTime());
		System.out.println(apiClientService.getClient().getDevices(userDetail).get(0).getId());
		List<Device> deviceList = apiClientService.getClient()
				.getDevices(
						new LocalUserDetail(apiinfo.getLoginID(),
								apiinfo.getExtId()));
		System.out.println(deviceList.size());
	//	test.apiClientService.getClient().subscribe("1", userDetail, fitbitUser, APICollectionType.user,"dongdong");
//		List<ApiSubscription> subList=test.apiClientService.getClient().getSubscriptions(userDetail);
//		for(ApiSubscription sub:subList)
//		{
//			System.out.println(sub.getSubscriptionId());
//			System.out.println(sub.getOwnerId());
//		}
//	}
	}
}
