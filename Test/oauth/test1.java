//package oauth;
//
//import java.io.IOException;
//
//import javax.xml.crypto.dsig.SignatureMethod;
//
//import oauth.signpost.OAuthConsumer;
//import oauth.signpost.OAuthProvider;
//import oauth.signpost.basic.DefaultOAuthConsumer;
//import oauth.signpost.basic.DefaultOAuthProvider;
//import oauth.signpost.exception.OAuthCommunicationException;
//import oauth.signpost.exception.OAuthExpectationFailedException;
//import oauth.signpost.exception.OAuthMessageSignerException;
//import oauth.signpost.exception.OAuthNotAuthorizedException;
//
//public class test1 {
//public static void main(String args[]) throws IOException, OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException 
//{
//	String REQUEST_TOKEN_ENDPOINT_URL="http://platform.fatsecret.com/rest/server.api";
//	String ACCESS_TOKEN_ENDPOINT_URL="http://platform.fatsecret.com/rest/server.api";
//	String AUTHORIZE_WEBSITE_URL="http://platform.fatsecret.com/rest/server.api";
//	 OAuthProvider provider = new DefaultOAuthProvider(
//			 REQUEST_TOKEN_ENDPOINT_URL,
//			 ACCESS_TOKEN_ENDPOINT_URL,
//			 AUTHORIZE_WEBSITE_URL);
//	  OAuthConsumer consumer = new DefaultOAuthConsumer(
//              "cc672faa37124ce1bb6389eb31c4d6c2",
//              "903896b388a04beeabaa476f48fc9127");
////	  URL url = new URL(REQUEST_TOKEN_ENDPOINT_URL);
////      HttpURLConnection request = (HttpURLConnection) url.openConnection();
////      System.out.println("Try Signing...");
////	  consumer.sign(request);
////	  System.out.println("After Signing..."+consumer.getToken());
//    // fetches a request token from the service provider and builds
//    // a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
//    // which your app must now send the user
//	  System.out.println("Fetching request token from Twitter...");
//
//      // we do not support callbacks, thus pass OOB
//      String authUrl = null;
//	try {
//		
//		authUrl = provider.retrieveRequestToken(consumer,"http://www.example.com");
//	} catch (OAuthMessageSignerException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (OAuthNotAuthorizedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (OAuthExpectationFailedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (OAuthCommunicationException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//
//      System.out.println("Request token: " + consumer.getToken());
//      System.out.println("Token secret: " + consumer.getTokenSecret());
//
//      System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
//      System.out.println("Enter the PIN code and hit ENTER when you're done:");
//}
//}
