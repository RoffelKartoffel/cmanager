package cmanager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JOptionPane;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

public class OKAPI
{
    public enum OKAPI_INSTALLATION { DE, NL, PL, RO, UK, US }
    ;

    public static ArrayList<Geocache>
    getCachesAround(Geocache g, Double searchRadius,
                    ArrayList<Geocache> okapiCacheDetailsCache, OCUser user,
                    String excludeUUID) throws Exception
    {
        ArrayList<Geocache> caches = new ArrayList<Geocache>();

        boolean useOAuth = excludeUUID != null;
        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());
        String url = getOkapiUrlByOkapiInstallation(okapiInstallation) +
                     "/okapi/services/caches/search/nearest"
                     + "?consumer_key=" +
                     OKAPIKeys.get_CONSUMER_API_KEY(okapiInstallation) +
                     "&format=xmlmap2"
                     + "&center=" + g.getCoordinate().getLat().toString() +
                     "|" + g.getCoordinate().getLon().toString() + "&radius=" +
                     searchRadius.toString() +
                     "&status=Available|Temporarily%20unavailable|Archived"
                     + "&limit=500" +
                     (useOAuth ? "&ignored_status=notignored_only" : "") +
                     (useOAuth ? "&not_found_by=" + excludeUUID : "");

        String http;
        if (useOAuth)
        {
            OAuth10aService service = getOAuthService(user.getOcSite());
            OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
            service.signRequest(user.getOkapiToken(),
                                request); // the access token from step 4
            Response response = request.send();
            http = response.getBody();
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
                            caches.add(
                                getCache(user, code, okapiCacheDetailsCache));
                        }
                        catch (MalFormedException ex)
                        {
                            ExceptionPanel.display(ex);
                        }

        return caches;
    }


    public static Geocache getCache(OCUser user, String code,
                                    ArrayList<Geocache> okapiCacheDetailsCache)
        throws Exception
    {
        synchronized (okapiCacheDetailsCache)
        {
            int index = Collections.binarySearch(okapiCacheDetailsCache, code);
            if (index >= 0)
                return okapiCacheDetailsCache.get(index);
        }

        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());

        String http = HTTP.get(
            getOkapiUrlByOkapiInstallation(okapiInstallation) +
            "/okapi/services/caches/geocache"
            + "?consumer_key=" +
            OKAPIKeys.get_CONSUMER_API_KEY(okapiInstallation) +
            "&format=xmlmap2"
            + "&cache_code=" + code +
            "&fields=code|name|location|type|gc_code|difficulty|terrain|status|url");

        String name = null;
        Coordinate coordinate = null;
        Double difficulty = null;
        Double terrain = null;
        String type = null;
        String code_gc = null;
        String status = null;
        String url = null;

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
            if (e.attrIs("key", "url"))
                url = e.getUnescapedBody();
        }

        Geocache g =
            new Geocache(code, name, coordinate, difficulty, terrain, type);
        g.setCodeGC(code_gc);
        g.setUrl(url);

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
        throws MalFormedException, IOException
    {
        if (user == null)
            return;

        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());

        String url = getOkapiUrlByOkapiInstallation(okapiInstallation) +
                     "/okapi/services/caches/geocache"
                     + "?consumer_key=" +
                     OKAPIKeys.get_CONSUMER_API_KEY(okapiInstallation) +
                     "&format=xmlmap2"
                     + "&cache_code=" + oc.getCode() + "&fields=is_found";

        OAuth10aService service = getOAuthService(user.getOcSite());
        OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
        service.signRequest(user.getOkapiToken(),
                            request); // the access token from step 4
        Response response = request.send();
        String http = response.getBody();

        Boolean isFound = null;

        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
        {
            if (e.attrIs("key", "is_found"))
                isFound = e.getBodyB();
        }
        oc.setIsFound(isFound);
    }

    public static Geocache completeCacheDetails(OCUser user, Geocache g)
        throws Exception
    {
        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());
        String http = HTTP.get(
            getOkapiUrlByOkapiInstallation(okapiInstallation) +
            "/okapi/services/caches/geocache"
            + "?consumer_key=" +
            OKAPIKeys.get_CONSUMER_API_KEY(okapiInstallation) +
            "&format=xmlmap2"
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

    private static OAuth10aService getOAuthService(final String ocSite)
    {
        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(ocSite);
        return new ServiceBuilder()
            .apiKey(OKAPIKeys.get_CONSUMER_API_KEY(okapiInstallation))
            .apiSecret(OKAPIKeys.get_CONSUMER_SECRET_KEY(okapiInstallation))
            .build(new OKAPI_OAUTH(
                getOkapiUrlByOkapiInstallation(okapiInstallation)));
    }

    public static OAuth1AccessToken requestAuthorization(final String ocSite)
    {
        // Step One: Create the OAuthService object
        OAuth10aService service = getOAuthService(ocSite);
        OAuth1AccessToken accessToken = null;

        try
        {
            // Step Two: Get the request token
            OAuth1RequestToken requestToken = service.getRequestToken();

            // Step Three: Making the user validate your request token
            String authUrl = service.getAuthorizationUrl(requestToken);
            Main.openUrl(authUrl);

            String pin = JOptionPane.showInputDialog(
                null,
                "Please enter the PIN from your selected Opencaching site.");
            if (pin == null)
                return null;

            // Step Four: Get the access Token
            accessToken = service.getAccessToken(
                requestToken, pin); // the requestToken you had from step 2
        }
        catch (IOException ex)
        {
            ExceptionPanel.display(ex);
        }
        return accessToken;
    }

    public static String getUUID(OCUser user)
        throws MalFormedException, IOException
    {
        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());
        String url = getOkapiUrlByOkapiInstallation(okapiInstallation) +
                     "/okapi/services/users/user"
                     + "?format=xmlmap2"
                     + "&fields=uuid";

        OAuth10aService service = getOAuthService(user.getOcSite());
        OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
        service.signRequest(user.getOkapiToken(),
                            request); // the access token from step 4
        Response response = request.send();
        String http = response.getBody();

        // <object><string
        // key="uuid">0b34f954-ee48-11e4-89ed-525400e33611</string></object>
        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
            if (e.attrIs("key", "uuid"))
                return e.getUnescapedBody();

        return null;
    }

    public static String getUsername(OCUser user)
        throws MalFormedException, IOException
    {
        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());
        String url = getOkapiUrlByOkapiInstallation(okapiInstallation) +
                     "/okapi/services/users/user"
                     + "?format=xmlmap2"
                     + "&fields=username";

        OAuth10aService service = getOAuthService(user.getOcSite());
        OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
        service.signRequest(user.getOkapiToken(),
                            request); // the access token from step 4
        Response response = request.send();
        String http = response.getBody();

        // <object><string
        // key="uuid">0b34f954-ee48-11e4-89ed-525400e33611</string></object>
        XMLElement root = XMLParser.parse(http);
        for (XMLElement e : root.getChild("object").getChildren())
            if (e.attrIs("key", "username"))
                return e.getUnescapedBody();

        return null;
    }

    public static void postLog(OCUser user, Geocache cache, GeocacheLog log)
        throws MalFormedException, UnsupportedEncodingException
    {
        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());
        String url = getOkapiUrlByOkapiInstallation(okapiInstallation) +
                     "/okapi/services/logs/submit"
                     + "?format=xmlmap2"
                     + "&cache_code=" +
                     URLEncoder.encode(cache.getCode(), "UTF-8") + "&logtype=" +
                     URLEncoder.encode("Found it", "UTF-8") + "&comment=" +
                     URLEncoder.encode(log.getText(), "UTF-8") + "&when=" +
                     URLEncoder.encode(log.getDateStrISO8601NoTime(), "UTF-8");

        OAuth10aService service = getOAuthService(user.getOcSite());
        OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
        service.signRequest(user.getOkapiToken(),
                            request); // the access token from step 4
        request.send();

        //		Response response = request.send();
        //		String http = response.getBody();
        //		object><boolean key="success">true</boolean><string
        //key="message">Your cache log entry was posted
        //successfully.</string><string
        //key="log_uuid">e1d494fc-0ab4-48b0-b709-aa566f20dc4d</string></object>
    }


    public static Coordinate getHomeCoordinates(OCUser user)
        throws MalFormedException, IOException
    {
        String uuid = getUUID(user);
        final OKAPI_INSTALLATION okapiInstallation =
            ocSiteToOkapiInstallation(user.getOcSite());
        String url = getOkapiUrlByOkapiInstallation(okapiInstallation) +
                     "/okapi/services/users/user"
                     + "?format=xmlmap2"
                     + "&fields=home_location"
                     + "&user_uuid=" + uuid;

        OAuth10aService service = getOAuthService(user.getOcSite());
        OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
        service.signRequest(user.getOkapiToken(),
                            request); // the access token from step 4
        Response response = request.send();
        String http = response.getBody();

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

    private static OKAPI_INSTALLATION
    ocSiteToOkapiInstallation(final String ocSite)
    {
        switch (ocSite)
        {
        case "oc.DE":
        case "oc.ES":
        case "oc.FR":
        case "oc.IT":
            return OKAPI_INSTALLATION.DE;
        case "oc.NL":
            return OKAPI_INSTALLATION.NL;
        case "oc.PL":
            return OKAPI_INSTALLATION.PL;
        case "oc.RO":
            return OKAPI_INSTALLATION.RO;
        case "oc.UK":
            return OKAPI_INSTALLATION.UK;
        case "oc.US":
            return OKAPI_INSTALLATION.US;
        default:
            throw new IllegalArgumentException();
        }
    }

    private static String getOkapiUrlByOkapiInstallation(
        final OKAPI.OKAPI_INSTALLATION okapiInstallation)
    {
        switch (okapiInstallation)
        {
        case DE:
            return "https://www.opencaching.de";
        case NL:
            return "http://www.opencaching.nl";
        case PL:
            return "https://opencaching.pl";
        case RO:
            return "http://www.opencaching.ro";
        case UK:
            return "http://www.opencaching.org.uk";
        case US:
            return "https://www.opencaching.us";
        default:
            throw new IllegalArgumentException();
        }
    }
}
