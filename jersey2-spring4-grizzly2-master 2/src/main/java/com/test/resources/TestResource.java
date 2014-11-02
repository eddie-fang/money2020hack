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
	@Produces("text/html")
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
			Response response = request.send();
			System.out.println(response.getBody());
			
			/*
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/json"); 

			Calendar tempcal = Calendar.getInstance();
			long ts = tempcal.getTimeInMillis();// get current time in milliseconds
			String oauth_timestamp = (new Long(ts/1000)).toString(); // then divide by 1000 to get seconds			
            
            String uuid_string = UUID.randomUUID().toString();
    			uuid_string = uuid_string.replaceAll("-", "");
    		
			String[][] data = {
					{"oauth_token", accessToken},
					{"oauth_consumer_key", "qyprdn18xF8aWuyGTAnOGjbFwOscBw"},
					{"oauth_nonce",  uuid_string},
					{"oauth_signature", ""},
					{"oauth_signature_method", "HMAC-SHA1"},
					{"oauth_timestamp", oauth_timestamp},
					{"oauth_version", "1.0"}
			};

			String signature_base_string = 
	                "GET&"+URLEncoder.encode(uri, "UTF-8")+"&";
	            for(int i = 0; i < data.length; i++) {
	                signature_base_string +=
	                    URLEncoder.encode(data[i][0], "UTF-8") + "%3D" +
	                    URLEncoder.encode(data[i][1], "UTF-8") + "%26";
	            }
	            // cut the last appended %26 
	            signature_base_string = signature_base_string.substring(0,
	                signature_base_string.length()-3);

	            Mac m = Mac.getInstance("HmacSHA1");
	            m.init(new SecretKeySpec("b3Ce11KHkGmjb44NO9vpGUTMuxeoryvcHA5Qpu9L".getBytes(), "HmacSHA1"));
	            m.update(signature_base_string.getBytes());
	            byte[] res = m.doFinal();
	            String sig = String.valueOf(Base64Coder.encode(res));
	            data[3][1] = sig;
	            
			String header = "OAuth ";
			for(String[] item : data) {
				header += item[0]+"=\""+item[1]+"\", ";
			}
			// cut off last appended comma
			header = header.substring(0, header.length()-2);

			connection.setRequestProperty("Authorization", header);
			
			// just want to do an HTTP GET here
			connection.setRequestMethod("GET");

			// give it 15 seconds to respond
			connection.setReadTimeout(15*1000);
			connection.connect();
			
			BufferedReader reader = null;
		    StringBuilder stringBuilder;
		    
			 // read the output from the server
		      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		      stringBuilder = new StringBuilder();
		 
		      String line = null;
		      while ((line = reader.readLine()) != null)
		      {
		        stringBuilder.append(line + "\n");
		      }
		    
		      return stringBuilder.toString();
		   */
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