package oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.oauth.OAuthService;


public class test2 {
public static void main(String args[]) 
{
	
	OAuthService service = new ServiceBuilder()
	.provider(LinkedInApi.class)
	.apiKey("cc672faa37124ce1bb6389eb31c4d6c2")
	.apiSecret("903896b388a04beeabaa476f48fc9127")
	.build();
	
}
}
