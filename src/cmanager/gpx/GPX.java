package cmanager.gpx;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import cmanager.FileHelper;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheAttribute;
import cmanager.geo.GeocacheLog;
import cmanager.geo.Waypoint;
import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.gui.ExceptionPanel;
import cmanager.xml.Element;
import cmanager.xml.Parser;
import cmanager.xml.Element.XMLAttribute;
import cmanager.xml.Parser.XMLParserCallbackI;

public class GPX
{
    public static void loadFromStream(InputStream is, final ArrayList<Geocache> gList,
                                      final ArrayList<Waypoint> wList) throws Throwable
    {
        final ExecutorService service =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        Parser.parse(is, new XMLParserCallbackI() {
            public boolean elementFinished(final Element element)
            {
                if (!element.is("wpt"))
                    return false;

                service.submit(new Runnable() {
                    public void run()
                    {
                        Geocache g = null;
                        Waypoint w = null;

                        try
                        {
                            w = toWaypoint(element);
                            g = toCache(element);
                        }
                        catch (NullPointerException ex)
                        {
                            ExceptionPanel.display(ex);
                        }

                        if (g != null)
                            synchronized (gList)
                            {
                                gList.add(g);
                            }
                        else if (w != null)
                            synchronized (wList)
                            {
                                wList.add(w);
                            }
                    }
                });
                return true;
            }

            public boolean elementLocatedCorrectly(Element element, Element parent)
            {
                if (element.is("gpx"))
                    return parent.getName() == null ? true : false;
                if (element.is("wpt"))
                    return parent.is("gpx");

                return true;
            }
        });

        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE,
                                 TimeUnit.DAYS); // incredible high delay but still ugly
    }


    private static Waypoint toWaypoint(Element wpt)
    {
        Coordinate coordinate = null;
        String code = null;
        String description = null;
        String symbol = null;
        String type = null;
        String parent = null;
        String date = null;


        double lat = 0.0, lon = 0.0;
        for (XMLAttribute a : wpt.getAttributes())
        {
            if (a.is("lat"))
                lat = a.getValueD();
            else if (a.is("lon"))
                lon = a.getValueD();
        }
        coordinate = new Coordinate(lat, lon);

        for (Element e : wpt.getChildren())
        {
            if (e.is("name"))
                code = e.getUnescapedBody();
            else if (e.is("desc"))
                description = e.getUnescapedBody();
            else if (e.is("sym"))
                symbol = e.getUnescapedBody();
            else if (e.is("type"))
                type = e.getUnescapedBody();
            else if (e.is("time"))
                date = e.getUnescapedBody();

            else if (e.is("gsak:wptExtension"))
                for (Element ee : e.getChildren())
                    if (ee.is("gsak:Parent"))
                        parent = ee.getUnescapedBody();
        }

        Waypoint waypoint = new Waypoint(coordinate, code, description, symbol, type, parent);
        waypoint.setDate(date);
        return waypoint;
    }


    private static Geocache toCache(Element wpt)
    {
        String code = null;
        String urlName = null;
        String cacheName = null;
        Coordinate coordinate = null;
        Double difficulty = null;
        Double terrain = null;
        String type = null;
        String owner = null;
        String container = null;
        String listing = null;
        String listingShort = null;
        String hint = null;
        Integer id = null;
        Boolean archived = null;
        Boolean available = null;
        Boolean gcPremium = null;
        Integer favPoints = null;

        ArrayList<GeocacheAttribute> attributes = new ArrayList<>();
        ArrayList<GeocacheLog> logs = new ArrayList<>();

        double lat = 0.0, lon = 0.0;
        for (XMLAttribute a : wpt.getAttributes())
        {
            if (a.is("lat"))
                lat = a.getValueD();
            else if (a.is("lon"))
                lon = a.getValueD();
        }
        coordinate = new Coordinate(lat, lon);


        boolean groundspeak_cache = false;
        for (Element e : wpt.getChildren())
        {
            if (e.is("name"))
                code = e.getUnescapedBody();
            else if (e.is("urlname"))
                urlName = e.getUnescapedBody();

            else if (e.is("groundspeak:cache"))
            {
                groundspeak_cache = true;

                for (XMLAttribute a : e.getAttributes())
                {
                    if (a.is("id"))
                    {
                        try
                        {
                            id = new Integer(a.getValue());
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                    if (a.is("archived"))
                        archived = new Boolean(a.getValue());
                    else if (a.is("available"))
                        available = new Boolean(a.getValue());
                }

                for (Element ee : e.getChildren())
                {
                    if (ee.is("groundspeak:name"))
                        cacheName = ee.getUnescapedBody();
                    else if (ee.is("groundspeak:difficulty"))
                        difficulty = ee.getBodyD();
                    else if (ee.is("groundspeak:terrain"))
                        terrain = ee.getBodyD();
                    else if (ee.is("groundspeak:type"))
                        type = ee.getUnescapedBody();
                    else if (ee.is("groundspeak:owner"))
                        owner = ee.getUnescapedBody();
                    else if (ee.is("groundspeak:container"))
                        container = ee.getUnescapedBody();
                    else if (ee.is("groundspeak:long_description"))
                        listing = ee.getUnescapedBody();
                    else if (ee.is("groundspeak:short_description"))
                        listingShort = ee.getUnescapedBody();
                    else if (ee.is("groundspeak:encoded_hints"))
                        hint = ee.getUnescapedBody();
                    else if (ee.is("groundspeak:logs"))
                    {
                        for (Element eee : ee.getChildren())
                            if (eee.is("groundspeak:log"))
                            {
                                // skip geotoad info log
                                if (eee.attrIs("id", "-2"))
                                    continue;

                                String ltype = null;
                                String author = null;
                                String text = null;
                                String date = null;

                                for (Element eeee : eee.getChildren())
                                {
                                    if (eeee.is("groundspeak:date"))
                                        date = eeee.getUnescapedBody();
                                    else if (eeee.is("groundspeak:type"))
                                        ltype = eeee.getUnescapedBody();
                                    else if (eeee.is("groundspeak:finder"))
                                        author = eeee.getUnescapedBody();
                                    else if (eeee.is("groundspeak:text"))
                                        text = eeee.getUnescapedBody();
                                }

                                if (ltype != null && ltype.equals("Other"))
                                    continue;

                                try
                                {
                                    GeocacheLog gl = new GeocacheLog(ltype, author, text, date);
                                    logs.add(gl);
                                }
                                catch (NullPointerException | IllegalArgumentException ex)
                                {
                                    ExceptionPanel.display(ex);
                                }
                            }
                    }
                    else if (ee.is("groundspeak:attributes"))
                        for (Element eee : ee.getChildren())
                            if (eee.is("groundspeak:attribute"))
                            {
                                Integer ID = null;
                                Integer inc = null;
                                String desc = null;

                                for (XMLAttribute a : eee.getAttributes())
                                    if (a.is("id"))
                                        ID = a.getValueI();
                                    else if (a.is("inc"))
                                        inc = a.getValueI();
                                desc = eee.getUnescapedBody();

                                try
                                {
                                    GeocacheAttribute attr = new GeocacheAttribute(ID, inc, desc);
                                    attributes.add(attr);
                                }
                                catch (NullPointerException | IllegalArgumentException ex)
                                {
                                    ExceptionPanel.display(ex);
                                }
                            }
                }
            }
            else if (e.is("gsak:wptExtension"))
            {
                for (Element ee : e.getChildren())
                    if (ee.is("gsak:IsPremium"))
                        gcPremium = ee.getBodyB();
                    else if (ee.is("gsak:FavPoints"))
                        favPoints = ee.getBodyI();
            }
        }

        if (!groundspeak_cache)
            return null;

        if (container != null && container.equals("unknown"))
            container = null;

        Geocache g = new Geocache(code, cacheName != null ? cacheName : urlName, coordinate,
                                  difficulty, terrain, type);
        g.setOwner(owner);
        g.setContainer(container);
        g.setListing(listing);
        g.setListing_short(listingShort);
        g.setHint(hint);
        g.setId(id);
        g.setArchived(archived);
        g.setAvailable(available);
        g.setGcPremium(gcPremium);
        g.setFavPoints(favPoints);
        g.addWaypoints(attributes);
        g.add(logs);

        return g;
    }

    public static void saveToFile(ArrayList<Geocache> list, String listName, String pathToGPX)
        throws Throwable
    {
        OutputStream os = FileHelper.openFileWrite(pathToGPX);
        ZipOutputStream zos = new ZipOutputStream(os);
        zos.setLevel(7);

        if (FileHelper.getFileExtension(listName).equals("zip"))
            listName = listName.substring(0, listName.length() - 4);

        Integer subListNumber = 0;
        int baseIndex = 0;
        final int CACHES_PER_GPX = 1000;
        final boolean useSingleFile = list.size() <= CACHES_PER_GPX;
        do
        {
            ArrayList<Geocache> subList = new ArrayList<>();

            for (int index = 0; index < CACHES_PER_GPX && index + baseIndex < list.size(); index++)
                subList.add(list.get(index + baseIndex));
            baseIndex += CACHES_PER_GPX;
            subListNumber += 1;

            String subListFileName =
                useSingleFile ? listName : listName + "-" + subListNumber.toString();
            subListFileName += ".gpx";
            zos.putNextEntry(new ZipEntry(subListFileName));

            Element root = cachlistToXML(subList, listName);
            Parser.xmlToBuffer(root, zos);

            zos.closeEntry();
        } while (baseIndex < list.size());

        zos.close();
        os.close();
    }


    private static Element cachlistToXML(final ArrayList<Geocache> list, String name)
    {
        Element root = new Element();

        final Element gpx = new Element("gpx");
        gpx.add(new XMLAttribute("version", "1.0"));
        gpx.add(new XMLAttribute("creator", Constants.APP_NAME));
        gpx.add(new XMLAttribute(
            "xsi:schemaLocation",
            "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd http://www.gsak.net/xmlv1/6 http://www.gsak.net/xmlv1/6/gsak.xsd"));
        gpx.add(new XMLAttribute("xmlns", "http://www.topografix.com/GPX/1/0"));
        gpx.add(new XMLAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
        gpx.add(new XMLAttribute("xmlns:groundspeak", "http://www.groundspeak.com/cache/1/0/1"));
        gpx.add(new XMLAttribute("xmlns:gsak", "http://www.gsak.net/xmlv1/6"));
        gpx.add(new XMLAttribute("xmlns:cgeo", "http://www.cgeo.org/wptext/1/0"));
        root.add(gpx);

        gpx.add(new Element("name", name));
        gpx.add(new Element(
            "desc", "Geocache file generated by " + Constants.APP_NAME + " " + Version.VERSION));
        gpx.add(new Element("author", Constants.APP_NAME));

        DateTime dt = new DateTime();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        String strDate = fmt.print(dt);
        gpx.add(new Element("time", strDate));


        //		for(Geocache g : list)
        //		{
        //			gpx.add( cacheToXML(g) );
        //			for( Waypoint w : g.getWaypoints() )
        //				gpx.add( waypointToXML(w) );
        //		}

        for (Geocache g : list)
        {
            gpx.add(cacheToXML(g));
            for (Waypoint w : g.getWaypoints())
                gpx.add(waypointToXML(w));
        }

        return root;
    }

    private static Element waypointToXML(Waypoint w)
    {
        Element wpt = new Element("wpt");
        wpt.add(new XMLAttribute("lat", w.getCoordinate().getLat()));
        wpt.add(new XMLAttribute("lon", w.getCoordinate().getLon()));

        wpt.add(new Element("time", w.getDateStrISO8601()));
        wpt.add(new Element("name", w.getCode()));
        wpt.add(new Element("desc", w.getDescription()));
        wpt.add(new Element("sym", w.getSymbol()));
        wpt.add(new Element("type", w.getType()));

        Element gsakExtension = new Element("gsak:wptExtension");
        wpt.add(gsakExtension);
        gsakExtension.add(new Element("gsak:Parent", w.getParent()));

        return wpt;
    }


    private static Element cacheToXML(Geocache g)
    {
        Element wpt = new Element("wpt");
        wpt.add(new XMLAttribute("lat", g.getCoordinate().getLat()));
        wpt.add(new XMLAttribute("lon", g.getCoordinate().getLon()));

        wpt.add(new Element("name", g.getCode()));
        wpt.add(new Element("urlname", g.getName()));

        Element gspkCache = new Element("groundspeak:cache");
        gspkCache.add(new XMLAttribute("id", g.getId()));
        gspkCache.add(new XMLAttribute("available", g.getAvailable()));
        gspkCache.add(new XMLAttribute("archived", g.getArchived()));
        wpt.add(gspkCache);


        Element gspkAttrs = new Element("groundspeak:attributes");
        gspkCache.add(gspkAttrs);
        for (GeocacheAttribute attr : g.getAttributes())
        {
            Element gspkAttr = new Element("groundspeak:attribute");
            gspkAttr.add(new XMLAttribute("id", attr.getID()));
            gspkAttr.add(new XMLAttribute("inc", attr.getInc()));
            gspkAttr.setBody(attr.getDesc());
            gspkAttrs.add(gspkAttr);
        }

        gspkCache.add(new Element("groundspeak:name", g.getName()));
        gspkCache.add(new Element("groundspeak:difficulty", g.getDifficulty()));
        gspkCache.add(new Element("groundspeak:terrain", g.getTerrain()));
        gspkCache.add(new Element("groundspeak:type", g.getType().asGCType()));
        gspkCache.add(new Element("groundspeak:owner", g.getOwner()));
        gspkCache.add(new Element("groundspeak:container", g.getContainer().asGC()));
        gspkCache.add(new Element("groundspeak:long_description", g.getListing()));
        gspkCache.add(new Element("groundspeak:short_description", g.getListing_short()));
        gspkCache.add(new Element("groundspeak:encoded_hints", g.getHint()));

        if (g.getLogs().size() > 0)
        {
            Element gspkLogs = new Element("groundspeak:logs");
            gspkCache.add(gspkLogs);

            for (GeocacheLog log : g.getLogs())
            {
                Element gspkLog = new Element("groundspeak:log");
                gspkLogs.add(gspkLog);

                gspkLog.add(new Element("groundspeak:date", log.getDateStrISO8601()));
                gspkLog.add(new Element("groundspeak:type", log.getTypeStr()));
                gspkLog.add(new Element("groundspeak:finder", log.getAuthor()));
                gspkLog.add(new Element("groundspeak:text", log.getText()));
            }
        }

        Element gsakExtension = new Element("gsak:wptExtension");
        gsakExtension.add(new Element("gsak:IsPremium", g.getGcPremium()));
        gsakExtension.add(new Element("gsak:FavPoints", g.getFavPoints()));
        wpt.add(gsakExtension);

        return wpt;
    }
}
