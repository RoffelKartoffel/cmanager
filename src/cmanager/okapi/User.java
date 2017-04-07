package cmanager.okapi;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.core.model.OAuth1AccessToken;

import cmanager.settings.Settings;

public class User
{
    private static User user = null;

    public static User getOKAPIUser()
    {
        if (user == null)
            user = new User();
        return user;
    }


    //////////////////////////////////////
    //////////////////////////////////////
    /////// Member functions
    //////////////////////////////////////
    //////////////////////////////////////


    private OAuth1AccessToken okapiAccessToken = null;


    private User()
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
