package cmanager.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class ApacheHTTP
{
    //	private final static String USER_AGENT = Constants.APP_NAME + " " + Version.VERSION;
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    // HTTP GET request
    public String get(String url) throws UnexpectedStatusCode, IOException
    {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);

        StringBuffer http = new StringBuffer();
        try
        {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "UTF8"));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                http.append(inputLine);
            }
            in.close();
        }
        finally
        {
            response.close();
        }
        return http.toString();
    }

    // HTTP POST request
    public String post(String url, List<NameValuePair> nvps)
        throws UnexpectedStatusCode, IOException
    {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = httpClient.execute(httpPost);

        StringBuffer http = new StringBuffer();
        try
        {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), "UTF8"));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                http.append(inputLine);
            }
            in.close();
        }
        finally
        {
            response.close();
        }
        return http.toString();
    }
}
