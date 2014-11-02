package com.test.resources;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Component;

import com.intuit.ia.connection.IAPlatformClient;
import com.intuit.ia.exception.OAuthException;

/**
 * TestResource
 * @author Janus Dam Nielsen
 */
@Component
@Path("/")
public class TestResource {

	private String requestTokenSecret = null;
	private String accessToken = null;
	private String accessTokenSecret = null;


	@GET
	@Path("/test")
	@Produces("text/json")
	public String test() {
		String uri = "https://sandbox-quickbooks.api.intuit.com/v3/company/1292735740/query?query=select%20%2A%20from%20Invoice";

		try
		{
			OAuthService service = new ServiceBuilder()
            .provider(QuickbooksAPI.class)
            .apiKey("qyprdn18xF8aWuyGTAnOGjbFwOscBw")
            .apiSecret("b3Ce11KHkGmjb44NO9vpGUTMuxeoryvcHA5Qpu9L")
            .build();
			
			Token token = new Token(accessToken, accessTokenSecret);
			
			OAuthRequest request = new OAuthRequest(Verb.GET, uri);
			service.signRequest(token, request); 
			request.addHeader("Accept", "application/json");
			Response response = request.send();
			return response.getBody(); 			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@GET
	@Path("/oauth")
	@Produces("text/html")
	public String oauth() {
		//Instantiate the QuickBook SDK's IAPlatformClient object
		IAPlatformClient client = new IAPlatformClient();
		try {
			//Use the IAPlatformClient to get a Request Token and Request Token Secret from Intuit
			final Map<String, String> requestTokenAndSecret =
					client.getRequestTokenAndSecret(
							"qyprdn18xF8aWuyGTAnOGjbFwOscBw", 
							"b3Ce11KHkGmjb44NO9vpGUTMuxeoryvcHA5Qpu9L");

			//Pull the values out of the map
			final String requestToken = requestTokenAndSecret.get("requestToken");
			requestTokenSecret = requestTokenAndSecret.get("requestTokenSecret");



			// Retrieve the Authorize URL
			final String authURL = client.getOauthAuthorizeUrl(requestToken);

			return authURL;

		} catch (OAuthException e) {
			throw new RuntimeException(e);
		}
	}

	@GET
	@Path("/request-token-ready")
	@Produces("text/html")
	public String tokenReady(
			@Context final HttpServletResponse res,
			@QueryParam("oauth_token") String requestToken,
			@QueryParam("oauth_verifier") String verifierCode,
			@QueryParam("realmId") String realmID,
			@QueryParam("dataSource") String dataSource) {
		IAPlatformClient client = new IAPlatformClient();

		try {
			final Map<String, String> oAuthAccessToken = client.getOAuthAccessToken(verifierCode, requestToken, requestTokenSecret,
					"qyprdn18xF8aWuyGTAnOGjbFwOscBw", 
					"b3Ce11KHkGmjb44NO9vpGUTMuxeoryvcHA5Qpu9L");

			accessToken = oAuthAccessToken.get("accessToken");
			accessTokenSecret = oAuthAccessToken.get("accessTokenSecret");

			try {
				res.sendRedirect("http://localhost:8100/#/app/browse");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (OAuthException e) {
			throw new RuntimeException(e);
		}
		return null;
	}	
}