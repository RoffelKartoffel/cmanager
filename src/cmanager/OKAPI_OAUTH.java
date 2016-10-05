package cmanager;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

public class OKAPI_OAUTH extends DefaultApi10a
{
    private final String ocURL;

    public OKAPI_OAUTH(final String ocURL)
    {
        this.ocURL = ocURL;
    }

    @Override public String getAccessTokenEndpoint()
    {
        return ocURL + "/okapi/services/oauth/access_token";
    }

    @Override public String getRequestTokenEndpoint()
    {
        return ocURL + "/okapi/services/oauth/request_token";
    }

    @Override public String getAuthorizationUrl(OAuth1RequestToken requestToken)
    {
        return String.format("%s/okapi/services/oauth/authorize?oauth_token=%s",
                             ocURL, requestToken.getToken());
    }
}
