package cmanager.okapi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.model.OAuth1AccessToken;

import cmanager.settings.Settings;

public class OKAPIUser
{
    private static OKAPIUser user = null;

    public static OKAPIUser getOKAPIUser()
    {
        if (user == null)
            user = new OKAPIUser();
        return user;
    }


    //////////////////////////////////////
    //////////////////////////////////////
    /////// Member functions
    //////////////////////////////////////
    //////////////////////////////////////


    private OAuth1AccessToken okapiAccessToken = null;


    private OKAPIUser()
    {
        try
        {
            okapiAccessToken = new OAuth1AccessToken(
                Settings.getS(Settings.Key.OKAPI_TOKEN),
                Settings.getS(Settings.Key.OKAPI_TOKEN_SECRET));
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
            Settings.set(Settings.Key.OKAPI_TOKEN_SECRET,
                         okapiAccessToken.getTokenSecret());
        }
    }
}
