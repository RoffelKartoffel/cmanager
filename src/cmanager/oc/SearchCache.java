package cmanager.oc;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.DateTime;

import cmanager.geo.Geocache;
import cmanager.global.Constants;

public class SearchCache
{

    private static final String LEGACY_CACHE_FOLDER = Constants.CACHE_FOLDER;
    private static final String OKAPI_CACHE_FOLDER =
        Constants.CACHE_FOLDER + "OC.OKAPI.emptySearches/";
    private static boolean initDone = false;

    private static String searchToFileName(Geocache g, String excludeUUID)
    {
        String name = g.getCode() + (excludeUUID == null ? "" : " " + excludeUUID);
        return OKAPI_CACHE_FOLDER + name;
    }


    public static synchronized void setEmptySearch(Geocache g, String excludeUUID)
        throws IOException
    {
        File f = new File(searchToFileName(g, excludeUUID));
        if (f.exists())
            f.delete();

        f.createNewFile();
    }

    public static synchronized boolean isEmptySearch(Geocache g, String excludeUUID)
        throws ClassNotFoundException, IOException
    {
        if (!initDone)
        {
            new File(OKAPI_CACHE_FOLDER).mkdirs();

            // if there are files in the legacy foler, move them
            // into the new folder
            for (File f : new File(LEGACY_CACHE_FOLDER).listFiles())
                if (f.getName().startsWith("GC"))
                    f.renameTo(new File(OKAPI_CACHE_FOLDER + f.getName()));

            initDone = true;
        }

        File f = new File(searchToFileName(g, excludeUUID));
        if (f.exists())
        {
            int randomMonthCount = -1 * ThreadLocalRandom.current().nextInt(4, 12 + 1);
            int randomDayCount = -1 * ThreadLocalRandom.current().nextInt(0, 31 + 1);
            DateTime now = new DateTime();
            now = now.plusMonths(randomMonthCount);
            now = now.plusDays(randomDayCount);

            // outdated?
            if (now.isAfter(new DateTime(f.lastModified())))
            {
                f.delete();
                return false;
            }
            else
                return true;
        }

        return false;
    }
}
