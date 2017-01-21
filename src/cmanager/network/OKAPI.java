package cmanager.network;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import cmanager.Coordinate;
import cmanager.ExceptionPanel;
import cmanager.Geocache;
import cmanager.GeocacheLog;
import cmanager.Main;
import cmanager.MalFormedException;
import cmanager.OCUser;
import cmanager.OKAPI_OAUTH;
import cmanager.XMLElement;
import cmanager.XMLParser;

public class OKAPI
{
    private final static String CONSUMER_API_KEY =
        OKAPIKeys.get_CONSUMER_API_KEY();
    private final static String CONSUMER_SECRET_KEY =
        OKAPIKeys.get_CONSUMER_SECRET_KEY();

    public static String usernameToUUID(String username) throws Exception
    {
        String url =
            "https://www.opencaching.de/okapi/services/users/by_username"
            + "?consumer_key=" + CONSUMER_API_KEY + "&format=xmlmap2"
            + "&username=" + URLEncoder.encode(username, "UTF-8") +
            "&fields=uuid";
        String http = HTTP.get(url);

        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
            if (e.attrIs("key", "uuid"))
                return e.getUnescapedBody();

        return null;
    }


    public static ArrayList<Geocache>
    getCachesAround(Geocache g, double searchRadius,
                    ArrayList<Geocache> okapiCacheDetailsCache, OCUser user,
                    String excludeUUID) throws Exception
    {
        Coordinate c = g.getCoordinate();
        return getCachesAround(c.getLat(), c.getLon(), searchRadius,
                               okapiCacheDetailsCache, user, excludeUUID);
    }

    public static ArrayList<Geocache>
    getCachesAround(Double lat, Double lon, Double searchRadius,
                    ArrayList<Geocache> okapiCacheDetailsCache, OCUser user,
                    String excludeUUID) throws Exception
    {
        ArrayList<Geocache> caches = new ArrayList<Geocache>();

        boolean useOAuth = excludeUUID != null;
        String url =
            "https://www.opencaching.de/okapi/services/caches/search/nearest"
            + "?consumer_key=" + CONSUMER_API_KEY + "&format=xmlmap2"
            + "&center=" + lat.toString() + "|" + lon.toString() + "&radius=" +
            searchRadius.toString() +
            "&status=Available|Temporarily%20unavailable|Archived"
            + "&limit=500" +
            (useOAuth ? "&ignored_status=notignored_only" : "") +
            (useOAuth ? "&not_found_by=" + excludeUUID : "");

        String http;
        if (useOAuth)
        {
            http = authedHttpGet(user, url);
        }
        else
            http = HTTP.get(url);

        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
            if (e.attrIs("key", "results"))
                for (XMLElement ee : e.getChildren())
                    if (ee.is("string"))
                        try
                        {
                            String code = ee.getUnescapedBody();
                            Geocache g = getCache(code, okapiCacheDetailsCache);
                            caches.add(g);
                        }
                        catch (MalFormedException ex)
                        {
                            ExceptionPanel.display(ex);
                        }

        return caches;
    }


    public static Geocache getCache(String code,
                                    ArrayList<Geocache> okapiCacheDetailsCache)
        throws Exception
    {
        synchronized (okapiCacheDetailsCache)
        {
            int index = Collections.binarySearch(okapiCacheDetailsCache, code);
            if (index >= 0)
                return okapiCacheDetailsCache.get(index);
        }

        String http = HTTP.get(
            "https://www.opencaching.de/okapi/services/caches/geocache"
            + "?consumer_key=" + CONSUMER_API_KEY + "&format=xmlmap2"
            + "&cache_code=" + code +
            "&fields=code|name|location|type|gc_code|difficulty|terrain|status");

        String name = null;
        Coordinate coordinate = null;
        Double difficulty = null;
        Double terrain = null;
        String type = null;
        String code_gc = null;
        String status = null;

        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
        {
            if (e.attrIs("key", "name"))
                name = e.getUnescapedBody();
            if (e.attrIs("key", "location"))
            {
                String[] parts = e.getUnescapedBody().split("\\|");
                coordinate = new Coordinate(parts[0], parts[1]);
            }
            if (e.attrIs("key", "type"))
                type = e.getUnescapedBody();
            if (e.attrIs("key", "gc_code"))
                code_gc = e.getUnescapedBody();
            if (e.attrIs("key", "difficulty"))
                difficulty = e.getBodyD();
            if (e.attrIs("key", "terrain"))
                terrain = e.getBodyD();
            if (e.attrIs("key", "status"))
                status = e.getUnescapedBody();
        }

        Geocache g =
            new Geocache(code, name, coordinate, difficulty, terrain, type);
        g.setCodeGC(code_gc);
        if (status != null)
            if (status.equals("Available"))
            {
                g.setAvailable(true);
                g.setArchived(false);
            }
        if (status.equals("Temporarily unavailable"))
        {
            g.setAvailable(false);
            g.setArchived(false);
        }
        if (status.equals("Archived"))
        {
            g.setAvailable(false);
            g.setArchived(true);
        }

        synchronized (okapiCacheDetailsCache)
        {
            okapiCacheDetailsCache.add(g);
            Collections.sort(
                okapiCacheDetailsCache, new Comparator<Geocache>() {
                    public int compare(Geocache o1, Geocache o2)
                    {
                        return o1.getCode().compareTo(o2.getCode());
                    }
                });
        }

        return g;
    }

    public static void updateFoundStatus(OCUser user, Geocache oc)
        throws MalFormedException, IOException, InterruptedException,
               ExecutionException
    {
        if (user == null)
            return;

        String url = "https://www.opencaching.de/okapi/services/caches/geocache"
                     + "?consumer_key=" + CONSUMER_API_KEY + "&format=xmlmap2"
                     + "&cache_code=" + oc.getCode() + "&fields=is_found";
        String http = authedHttpGet(user, url);

        Boolean isFound = null;
        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
        {
            if (e.attrIs("key", "is_found"))
                isFound = e.getBodyB();
        }
        oc.setIsFound(isFound);
    }

    public static Geocache completeCacheDetails(Geocache g) throws Exception
    {
        String http = HTTP.get(
            "https://www.opencaching.de/okapi/services/caches/geocache"
            + "?consumer_key=" + CONSUMER_API_KEY + "&format=xmlmap2"
            + "&cache_code=" + g.getCode() + "&fields=" +
            URLEncoder.encode("size2|short_description|description|owner|hint2",
                              "UTF-8"));

        String size = null;
        String shortDescription = null;
        String description = null;
        String owner = null;
        String hint = null;

        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
        {
            if (e.attrIs("key", "size2"))
                size = e.getUnescapedBody();
            if (e.attrIs("key", "short_description"))
                shortDescription = e.getUnescapedBody();
            if (e.attrIs("key", "description"))
                description = e.getUnescapedBody();
            if (e.attrIs("key", "hint2"))
                hint = e.getUnescapedBody();
            if (e.attrIs("key", "owner"))
                for (XMLElement ee : e.getChildren())
                    if (ee.attrIs("key", "username"))
                        owner = ee.getUnescapedBody();
        }

        g.setContainer(size);
        g.setListing_short(shortDescription);
        g.setListing(description);
        g.setOwner(owner);
        g.setHint(hint);
        return g;
    }

    private static OAuth10aService getOAuthService()
    {
        return new ServiceBuilder()
            .apiKey(CONSUMER_API_KEY)
            .apiSecret(CONSUMER_SECRET_KEY)
            .build(new OKAPI_OAUTH());
    }

    public static OAuth1AccessToken requestAuthorization()
        throws IOException, InterruptedException, ExecutionException
    {
        // Step One: Create the OAuthService object
        OAuth10aService service = getOAuthService();

        // Step Two: Get the request token
        OAuth1RequestToken requestToken = service.getRequestToken();

        // Step Three: Making the user validate your request token
        String authUrl = service.getAuthorizationUrl(requestToken);
        Main.openUrl(authUrl);

        String pin = JOptionPane.showInputDialog(
            null, "Please enter the PIN from opencaching.de");
        if (pin == null)
            return null;

        // Step Four: Get the access Token
        OAuth1AccessToken accessToken = service.getAccessToken(
            requestToken, pin); // the requestToken you had from step 2

        return accessToken;
    }

    private static String authedHttpGet(final OCUser user, final String url)
        throws InterruptedException, ExecutionException, IOException
    {
        OAuth10aService service = getOAuthService();
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(user.getOkapiToken(),
                            request); // the access token from step 4
        final Response response = service.execute(request);
        return response.getBody();
    }

    public static String getUUID(OCUser user)
        throws MalFormedException, IOException, InterruptedException,
               ExecutionException
    {
        String url = "https://www.opencaching.de/okapi/services/users/user"
                     + "?format=xmlmap2"
                     + "&fields=uuid";
        String http = authedHttpGet(user, url);

        // <object><string
        // key="uuid">0b34f954-ee48-11e4-89ed-525400e33611</string></object>
        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
            if (e.attrIs("key", "uuid"))
                return e.getUnescapedBody();

        return null;
    }

    public static String getUsername(OCUser user)
        throws MalFormedException, IOException, InterruptedException,
               ExecutionException
    {
        String url = "https://www.opencaching.de/okapi/services/users/user"
                     + "?format=xmlmap2"
                     + "&fields=username";
        String http = authedHttpGet(user, url);

        // <object><string
        // key="uuid">0b34f954-ee48-11e4-89ed-525400e33611</string></object>
        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
            if (e.attrIs("key", "username"))
                return e.getUnescapedBody();

        return null;
    }

    public static void postLog(OCUser user, Geocache cache, GeocacheLog log)
        throws MalFormedException, InterruptedException, ExecutionException,
               IOException
    {
        String url = "https://www.opencaching.de/okapi/services/logs/submit"
                     + "?format=xmlmap2"
                     + "&cache_code=" +
                     URLEncoder.encode(cache.getCode(), "UTF-8") + "&logtype=" +
                     URLEncoder.encode("Found it", "UTF-8") + "&comment=" +
                     URLEncoder.encode(log.getText(), "UTF-8") + "&when=" +
                     URLEncoder.encode(log.getDateStrISO8601NoTime(), "UTF-8");
        authedHttpGet(user, url);

        //		String http = authedHttpGet(user, url);
        //		object><boolean key="success">true</boolean><string
        // key="message">Your cache log entry was posted
        // successfully.</string><string
        // key="log_uuid">e1d494fc-0ab4-48b0-b709-aa566f20dc4d</string></object>
    }


    public static Coordinate getHomeCoordinates(OCUser user)
        throws MalFormedException, IOException, InterruptedException,
               ExecutionException
    {
        String uuid = getUUID(user);

        String url = "https://www.opencaching.de/okapi/services/users/user"
                     + "?format=xmlmap2"
                     + "&fields=home_location"
                     + "&user_uuid=" + uuid;
        String http = authedHttpGet(user, url);

        // <object><string key="home_location">53.047117|9.608</string></object>
        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
            if (e.attrIs("key", "home_location"))
            {
                String[] parts = e.getUnescapedBody().split("\\|");
                return new Coordinate(parts[0], parts[1]);
            }

        return null;
    }
}
