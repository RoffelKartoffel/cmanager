package cmanager;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.model.OAuth1AccessToken;

import cmanager.network.OKAPI;

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
    }

    public OAuth1AccessToken getOkapiToken()
    {
        return okapiAccessToken;
    }

    public void requestOkapiToken()
        throws IOException, InterruptedException, ExecutionException
    {
        okapiAccessToken = OKAPI.requestAuthorization();
        if (okapiAccessToken != null)
        {
            Settings.set(Settings.Key.OKAPI_TOKEN, okapiAccessToken.getToken());
            Settings.set(Settings.Key.OKAPI_SECRET,
                         okapiAccessToken.getTokenSecret());
        }
    }
}
