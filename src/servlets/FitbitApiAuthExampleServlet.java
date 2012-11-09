package servlets;


import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;

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
import com.fitbit.api.common.model.timeseries.TimeSeriesResourceType;
import com.fitbit.api.common.model.user.UserInfo;
import com.fitbit.api.model.APIResourceCredentials;
import com.fitbit.api.model.FitbitUser;

/**
 * Servlet implementation class FitbitApiAuthExampleServlet
 */
@WebServlet("/FitbitApiAuthExampleServlet")
public class FitbitApiAuthExampleServlet extends HttpServlet {

    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_VERIFIER = "oauth_verifier";

    private FitbitAPIEntityCache entityCache = new FitbitApiEntityCacheMapImpl();
    private FitbitApiCredentialsCache credentialsCache = new FitbitApiCredentialsCacheMapImpl();
    private FitbitApiSubscriptionStorage subscriptionStore = new FitbitApiSubscriptionStorageInMemoryImpl();

    private String apiBaseUrl;
    private String fitbitSiteBaseUrl;
    private String exampleBaseUrl;
    private String clientConsumerKey;
    private String clientSecret;
    public static APIResourceCredentials resourceCredentials=null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Properties properties = new Properties();
            System.out.println(getClass().getClassLoader().toString());
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            apiBaseUrl = properties.getProperty("apiBaseUrl");
            fitbitSiteBaseUrl = properties.getProperty("fitbitSiteBaseUrl");
            exampleBaseUrl = properties.getProperty("exampleBaseUrl").replace("/app", "");
            clientConsumerKey = properties.getProperty("clientConsumerKey");
            clientSecret = properties.getProperty("clientSecret");
            FitbitAPIClientService<FitbitApiClientAgent> apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
                    new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl, credentialsCache),
                    clientConsumerKey,
                    clientSecret,
                    credentialsCache,
                    entityCache,
                    subscriptionStore
            );
            if(resourceCredentials==null)
        	{
            	resourceCredentials=new APIResourceCredentials("-", "d09e22d73e9e05b9f1f609eb7341f966", "132d21616c4580e428214c83a5b293e7");
            	resourceCredentials.setTempTokenVerifier("en9jhe0ho77p1ci4isg4sbb75b");
            	resourceCredentials.setLocalUserId("23KT43");
            	resourceCredentials.setAccessToken("e5b795607038440788ffe2fb70bc0a64");
            	resourceCredentials.setAccessTokenSecret("132d21616c4580e428214c83a5b293e7");
            	resourceCredentials.setTempToken("75dc7ee3a3b2cbf14f36d3d1ba1d68cd");
            	//resourceCredentials.setResourceId("75dc7ee3a3b2cbf14f36d3d1ba1d68cd");
          //  	resourceCredentials = apiClientService.getResourceCredentialsByTempToken("3f3cbc74cd9e535396d700cb1ef5b571");

                if (resourceCredentials == null) {
                    System.out.println("Unrecognized temporary token when atte");
                    throw new ServletException("Unrecognized temporary token when attempting to complete authorization: ");
                }
//             	System.out.println("running here:"+resourceCredentials.getLocalUserId());
//            	System.out.println("getTempToken:"+resourceCredentials.getTempToken());
//            	System.out.println("getAccessToken:"+resourceCredentials.getAccessToken());
//            	System.out.println("getResourceId:"+resourceCredentials.getResourceId());
//            	System.out.println("getTempTokenVerifier:"+resourceCredentials.getTempTokenVerifier());
//             	System.out.println("getAccessTokenSecret:"+resourceCredentials.getAccessTokenSecret());
            
        	//	resourceCredentials.setTempTokenVerifier("en9jhe0ho77p1ci4isg4sbb75b");
        	}
          
        } catch (IOException e) {
            throw new ServletException("Exception during loading properties", e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	
        FitbitAPIClientService<FitbitApiClientAgent> apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
                new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl, credentialsCache),
                clientConsumerKey,
                clientSecret,
                credentialsCache,
                entityCache,
                subscriptionStore
        );
 
        if (request.getParameter("completeAuthorization") != null) {
            String tempTokenReceived = request.getParameter(OAUTH_TOKEN);
            String tempTokenVerifier = request.getParameter(OAUTH_VERIFIER);
            System.out.println(tempTokenReceived+" "+tempTokenVerifier);
            resourceCredentials = apiClientService.getResourceCredentialsByTempToken(tempTokenReceived);
          
            if (resourceCredentials == null) {
                System.out.println("Unrecognized temporary token when atte");
                throw new ServletException("Unrecognized temporary token when attempting to complete authorization: " + tempTokenReceived);
            }
            // Get token credentials only if necessary:
    
            if (!resourceCredentials.isAuthorized()) {
                // The verifier is required in the request to get token credentials:
                resourceCredentials.setTempTokenVerifier(tempTokenVerifier);
                
                try {
                    // Get token credentials for user:
                    apiClientService.getTokenCredentials(new LocalUserDetail("leoncool",resourceCredentials.getLocalUserId()));
                } catch (FitbitAPIException e) {
                    throw new ServletException("Unable to finish authorization with Fitbit.", e);
                }
            }
            try {
//            	System.out.println("running here:"+resourceCredentials.getLocalUserId());
//            	System.out.println("getTempToken:"+resourceCredentials.getTempToken());
//            	System.out.println("getAccessToken:"+resourceCredentials.getAccessToken());
//            	System.out.println("getResourceId:"+resourceCredentials.getResourceId());
//            	System.out.println("getTempTokenVerifier:"+resourceCredentials.getTempTokenVerifier());
//             	System.out.println("getAccessTokenSecret:"+resourceCredentials.getAccessTokenSecret());
            
                UserInfo userInfo = apiClientService.getClient().getUserInfo(new LocalUserDetail("leoncool",resourceCredentials.getLocalUserId()));

            
               request.setAttribute("userInfo", userInfo);
                System.out.println(userInfo.getAvatar());
                
                request.getRequestDispatcher("/fitbitApiAuthExample.jsp").forward(request, response);
            } catch (FitbitAPIException e) {
            	e.printStackTrace();
                throw new ServletException("Exception during getting user info", e);
            }
        } else {
        	if(resourceCredentials!=null)
        	{
        	      UserInfo userInfo;
				try {
		
					userInfo = apiClientService.getClient().getUserInfo(new LocalUserDetail("leoncool","23KT43"));
					 request.setAttribute("userInfo", userInfo);
					 LocalDate date=LocalDate.parse("2012-10-08");
					 LocalDate start=LocalDate.parse("2012-11-07");
					 LocalDate end=LocalDate.parse("2012-11-08");
					    String localUserID=resourceCredentials.getLocalUserId();
		                FitbitUser fitbitUser=new FitbitUser(localUserID);
		            
		                Activities activities=apiClientService.getClient().getActivities(new LocalUserDetail("leoncool","23KT43"), fitbitUser,date);
		              List<ActivityLog> logList=activities.getActivities();
		              
		              List<Data> dataList=apiClientService.getClient().getTimeSeries(new LocalUserDetail("leoncool","23KT43"), fitbitUser, TimeSeriesResourceType.STEPS_TRACKER, start, end);

		              System.out.println("logListSize:"+logList.size());	
		              System.out.println("dataListSize:"+dataList.size());	
		              for(Data log:dataList)
		              {
		            	  System.out.println(log.getDateTime()+" : "+log.getValue());
		              }
		              for(ActivityLog log:logList)
		              {
		            	  System.out.println(log.getSteps());
		              }
		              System.out.println("totalStepsGoal:"+activities.getActivityGoals().getSteps());
		              System.out.println("totalSteps:"+activities.getSummary().getSteps());
					 System.out.println(userInfo.getAvatar());
				} catch (FitbitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                                  
                  request.getRequestDispatcher("/fitbitApiAuthExample.jsp").forward(request, response);
        	}
        	else{
            try {
            	System.out.println("going to authroisess");            	
                response.sendRedirect(apiClientService.getResourceOwnerAuthorizationURL(new LocalUserDetail("leoncool","23KT43"), exampleBaseUrl + "/FitbitApiAuthExampleServlet?completeAuthorization="));
            } catch (FitbitAPIException e) {
                throw new ServletException("Exception during performing authorization", e);
            }
        	}
        }
    }
}
