package cmanager;

import com.github.scribejava.core.model.OAuth1AccessToken;

public class OCUser
{
    private static OCUser user = null;

    public static OCUser getOCUser()
    {
        if (user == null)
            user = new OCUser();
        return user;
    }

    //////////////////////////////////////
    //////////////////////////////////////
    /////// Member functions
    //////////////////////////////////////
    //////////////////////////////////////

    private OAuth1AccessToken okapiAccessToken = null;
    private String ocSite = null;

    private OCUser()
    {
        try
        {
            okapiAccessToken =
                new OAuth1AccessToken(Settings.getS(Settings.Key.OKAPI_TOKEN),
                                      Settings.getS(Settings.Key.OKAPI_SECRET));
        }
        catch (IllegalArgumentException e)
        {
            okapiAccessToken = null;
        }

        ocSite = Settings.getS(Settings.Key.OC_SITE);
    }

    public OAuth1AccessToken getOkapiToken()
    {
        return okapiAccessToken;
    }

    public String getOcSite()
    {
        return ocSite;
    }

    public void requestOkapiToken(final String ocSite)
    {
        this.ocSite = ocSite;
        okapiAccessToken = OKAPI.requestAuthorization(this.ocSite);
        if (okapiAccessToken != null)
        {
            Settings.set(Settings.Key.OKAPI_TOKEN, okapiAccessToken.getToken());
            Settings.set(Settings.Key.OKAPI_SECRET,
                         okapiAccessToken.getTokenSecret());
        }
    }
}
