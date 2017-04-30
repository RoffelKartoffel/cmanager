package cmanager.oc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import org.joda.time.DateTime;

import cmanager.FileHelper;
import cmanager.FileHelper.InputAction;
import cmanager.geo.Geocache;
import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.network.HTTP;

public class ShadowList
{
    private final static String SHADOWLIST_FOLDER = Constants.CACHE_FOLDER + "OC.shadowlist";
    private final static String SHADOWLIST_PATH = SHADOWLIST_FOLDER + "/gc2oc.gz";
    private final static String SHADOWLIST_POSTED_FOLDER =
        Constants.CACHE_FOLDER + "OC.shadowlist.posted";

    public static void updateShadowList() throws IOException
    {
        // delete list if it is older than 1 month
        File file = new File(SHADOWLIST_PATH);
        if (file.exists())
        {
            DateTime fileTime = new DateTime(file.lastModified());
            DateTime now = new DateTime();
            fileTime = fileTime.plusMonths(1);
            if (fileTime.isAfter(now))
                return;

            file.delete();
        }

        new File(SHADOWLIST_FOLDER).mkdirs();

        // download list
        URL url = new URL("https://www.opencaching.de/api/gc2oc.php");
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(SHADOWLIST_PATH);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
    }

    public static ShadowList loadShadowList() throws Throwable
    {
        final HashMap<String, String> shadowList = new HashMap<>();
        FileHelper.processFiles(SHADOWLIST_PATH, new InputAction() {
            @Override
            public void process(InputStream is) throws Throwable
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null)
                {
                    String token[] = line.split(",");
                    // Column 2 == "1" means verified by a human
                    if (token[2].equals("1"))
                    {
                        // <GC, OC>
                        shadowList.put(token[0], token[1]);
                    }
                }
            }
        });
        return new ShadowList(shadowList);
    }


    //
    //	Member functions
    //

    private HashMap<String, String> shadowList;

    private ShadowList(HashMap<String, String> shadowList)
    {
        this.shadowList = shadowList;
    }

    public String getMatchingOCCode(String gcCode)
    {
        return shadowList.get(gcCode);
    }

    public boolean contains(String gcCode)
    {
        return shadowList.get(gcCode) != null;
    }

    public void postToShadowList(Geocache gc, Geocache oc) throws Exception
    {
        // do not repost items which are already upstream
        if (contains(gc.getCode()))
            return;

        // do not repost local findings
        File f = new File(SHADOWLIST_POSTED_FOLDER + "/" + gc.getCode());
        if (f.exists())
            return;

        String url = "https://www.opencaching.de/api/gc2oc.php"
                     + "?report=1"
                     + "&ocwp=" + oc.getCode() + "&gcwp=" + gc.getCode() +
                     "&source=" + Constants.APP_NAME + "+" + Version.VERSION;

        // post
        HTTP.get(url);

        // remember our post
        new File(SHADOWLIST_POSTED_FOLDER).mkdirs();
        f.createNewFile();
    }
}
