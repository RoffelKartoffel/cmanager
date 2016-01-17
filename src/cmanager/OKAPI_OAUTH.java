package cmanager;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;



public class OKAPI_OAUTH extends DefaultApi10a
{
  @Override
  public String getAccessTokenEndpoint()
  {
    return "http://www.opencaching.de/okapi/services/oauth/access_token";
  }

  @Override
  public String getRequestTokenEndpoint()
  {
    return "http://www.opencaching.de/okapi/services/oauth/request_token";
  }

  @Override
  public String getAuthorizationUrl(Token requestToken)
  {
    return String.format("http://www.opencaching.de/okapi/services/oauth/authorize?oauth_token=%s", 
    		requestToken.getToken());
  }
}