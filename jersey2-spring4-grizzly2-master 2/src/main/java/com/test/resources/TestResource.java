package com.test.resources;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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

	@GET
	@Path("/test")
	@Produces("text/html")
	public String test() {
		return "test";
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
            final String requestTokenSecret = requestTokenAndSecret.get("requestTokenSecret");

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
			@QueryParam("oauth_token") String oauthToken,
			@QueryParam("oauth_verifier") String oauthVerifier,
			@QueryParam("realmId") String realmId,
			@QueryParam("dataSource") String dataSource) {
		return "hello";
	}	
}