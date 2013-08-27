package servlets;

import health.database.DAO.Ext_API_Info_DAO;
import health.database.models.ExternalApiInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.fitbit.api.common.model.user.UserInfo;
import com.fitbit.api.model.APIResourceCredentials;

/**
 * Servlet implementation class FitbitApiAuthExampleServlet
 */
@WebServlet("/FitbitApiAuth")
public class FitbitApiAuth extends HttpServlet {

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
	public static APIResourceCredentials resourceCredentials = null;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Properties properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"config.properties"));
			apiBaseUrl = properties.getProperty("apiBaseUrl");
			fitbitSiteBaseUrl = properties.getProperty("fitbitSiteBaseUrl");
			exampleBaseUrl = properties.getProperty("exampleBaseUrl").replace(
					"/app", "");
			clientConsumerKey = properties.getProperty("clientConsumerKey");
			
			clientSecret = properties.getProperty("clientSecret");
			System.out.println("clientConsumerKey"+clientConsumerKey+", \n clientSecret:"+clientSecret);
			FitbitAPIClientService<FitbitApiClientAgent> apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
					new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl,
							credentialsCache), clientConsumerKey, clientSecret,
					credentialsCache, entityCache, subscriptionStore);
		} catch (IOException e) {
			throw new ServletException("Exception during loading properties", e);
		}

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		FitbitAPIClientService<FitbitApiClientAgent> apiClientService = new FitbitAPIClientService<FitbitApiClientAgent>(
				new FitbitApiClientAgent(apiBaseUrl, fitbitSiteBaseUrl,
						credentialsCache), clientConsumerKey, clientSecret,
				credentialsCache, entityCache, subscriptionStore);
		HttpSession session = request.getSession();
		String loginID = (String) session.getAttribute("loginid");
		System.out.println(loginID);
		if (loginID != null
				&& request.getParameter("completeAuthorization") != null) {

			String tempTokenReceived = request.getParameter(OAUTH_TOKEN);
			String tempTokenVerifier = request.getParameter(OAUTH_VERIFIER);
			resourceCredentials = apiClientService
					.getResourceCredentialsByTempToken(tempTokenReceived);

			if (resourceCredentials == null) {
				System.out.println("Unrecognized temporary token when atte");
				throw new ServletException(
						"Unrecognized temporary token when attempting to complete authorization: "
								+ tempTokenReceived);
			}
			// Get token credentials only if necessary:
			ExternalApiInfo extAPI = new ExternalApiInfo();
			extAPI.setLoginID(loginID);
			extAPI.setExtId("-");

			if (!resourceCredentials.isAuthorized()) {
				// The verifier is required in the request to get token
				// credentials:
				resourceCredentials.setTempTokenVerifier(tempTokenVerifier);
				extAPI.setTempTokenVerifier(tempTokenVerifier);
				try {
					// Get token credentials for user:
					apiClientService.getTokenCredentials(new LocalUserDetail(
							loginID, resourceCredentials.getLocalUserId()));
				} catch (FitbitAPIException e) {
					throw new ServletException(
							"Unable to finish authorization with Fitbit.", e);
				}
			}
			try {
				extAPI.setTokenSecrect(resourceCredentials
						.getAccessTokenSecret());
				extAPI.setAccessToken(resourceCredentials.getAccessToken());
				extAPI.setDevice("fitbit");
				Ext_API_Info_DAO extDao = new Ext_API_Info_DAO();
				extDao.create_A_ExternalAPIInfo(extAPI);
				UserInfo userInfo = apiClientService.getClient().getUserInfo(
						new LocalUserDetail(loginID, "-"));

				request.setAttribute("userInfo", userInfo);
				System.out.println(userInfo.getAvatar());
				PrintWriter out=response.getWriter();
				out.println("Successful!!!"+"\n"+"Welcome "+userInfo.getNickname());
//
//				request.getRequestDispatcher("/fitbitApiAuthExample.jsp")
//						.forward(request, response);
			} catch (FitbitAPIException e) {
				e.printStackTrace();
				throw new ServletException(
						"Exception during getting user info", e);
			}
		} else {

			try {
				if (request.getParameter("loginid") == null) {
					PrintWriter out = response.getWriter();
					out.println("missing loginid");
					out.close();
					return;
				} else {
					session = request.getSession();
					session.setAttribute("loginid",
							request.getParameter("loginid"));
				}
				System.out.println("going to authroisess");
				response.sendRedirect(apiClientService.getResourceOwnerAuthorizationURL(
						new LocalUserDetail(request.getParameter("loginid"),
								"-"),
						exampleBaseUrl
								+ "/FitbitApiAuth?completeAuthorization="));
			} catch (FitbitAPIException e) {
				throw new ServletException(
						"Exception during performing authorization", e);
			}
		}
	}
}
