package cmanager.network;

import cmanager.global.Constants;
import cmanager.global.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;


public class HTTP
{

    private final static String USER_AGENT = Constants.APP_NAME + " " + Version.VERSION;


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
        con.setRequestProperty("User-Agent", USER_AGENT);

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

    //	// HTTP POST request
    //	private void sendPost() throws Exception {
    //
    //		String url = "https://selfsolve.apple.com/wcResults.do";
    //		URL obj = new URL(url);
    //		HttpsURLConnection con = (HttpsURLConnection)
    // obj.openConnection();
    //
    //		//add reuqest header
    //		con.setRequestMethod("POST");
    //		con.setRequestProperty("User-Agent", USER_AGENT);
    //		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    //
    //		String urlParameters =
    //"sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
    //
    //		// Send post request
    //		con.setDoOutput(true);
    //		DataOutputStream wr = new
    // DataOutputStream(con.getOutputStream());
    //		wr.writeBytes(urlParameters);
    //		wr.flush();
    //		wr.close();
    //
    //		int responseCode = con.getResponseCode();
    //		System.out.println("\nSending 'POST' request to URL : " + url);
    //		System.out.println("Post parameters : " + urlParameters);
    //		System.out.println("Response Code : " + responseCode);
    //
    //		BufferedReader in = new BufferedReader(
    //		        new InputStreamReader(con.getInputStream()));
    //		String inputLine;
    //		StringBuffer response = new StringBuffer();
    //
    //		while ((inputLine = in.readLine()) != null) {
    //			response.append(inputLine);
    //		}
    //		in.close();
    //
    //		//print result
    //		System.out.println(response.toString());
    //
    //	}
}
