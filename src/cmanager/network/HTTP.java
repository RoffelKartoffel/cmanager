package cmanager.network;

import cmanager.global.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;


public class HTTP
{
    public static String get(String url) throws Exception
    {
        ConnectException ce = null;

        int count = 0;
        do
        {
            try
            {
                return get_(url);
            }
            catch (ConnectException e)
            {
                ce = e;
            }
        } while (++count < 3);

        throw ce;
    }

    // HTTP GET request
    private static String get_(String url) throws UnexpectedStatusCode, IOException
    {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection)obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("User-Agent", Constants.HTTP_USER_AGENT);

        BufferedReader in;
        try
        {
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
        }
        catch (IOException e)
        {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF8"));
        }

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();

        int statusCode = con.getResponseCode();
        if (statusCode != 200)
            throw new UnexpectedStatusCode(statusCode, response.toString());

        return response.toString();
    }
}
