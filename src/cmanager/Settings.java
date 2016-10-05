package cmanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.prefs.Preferences;

import org.apache.commons.codec.binary.Base64;

public class Settings
{

    private static Preferences prefs =
        Preferences.userRoot().node(Main.APP_NAME);

    public enum Key {
        HEAP_SIZE,

        GC_USERNAME,
        OC_USERNAME,
        FILE_CHOOSER_LOAD_GPX,

        OKAPI_TOKEN,
        OKAPI_SECRET,

        LOCATION_LIST,

        CLC_LIST,
        OC_SITE
    }

    public static String key(Key key)
    {
        switch (key)
        {
        case HEAP_SIZE:
            return "javaHeapSize";
        case GC_USERNAME:
            return "gcUsername";
        case OC_USERNAME:
            return "ocUsername";
        case FILE_CHOOSER_LOAD_GPX:
            return "fileChooserGpx";
        case OKAPI_TOKEN:
            return "OKAPIToken";
        case OKAPI_SECRET:
            return "OKAPISecret";
        case LOCATION_LIST:
            return "locationList";
        case CLC_LIST:
            return "clcList";
        case OC_SITE:
            return "ocSite";
        default:
            return null;
        }
    }

    public static String defaultS(Key key)
    {
        switch (key)
        {
        case GC_USERNAME:
        case FILE_CHOOSER_LOAD_GPX:
            return "";
        case OC_SITE:
            return "oc.DE";
        default:
            return null;
        }
    }

    public static void set(Key key, String val)
    {
        prefs.put(key(key), val);
    }

    public static String getS(Key key)
    {
        return prefs.get(key(key), defaultS(key));
    }

    public static void setSerialized(Key key, Serializable val)
        throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileHelper.serialize(val, bos);
        byte[] bytes = bos.toByteArray();

        String base64 = Base64.encodeBase64String(bytes);
        Settings.set(key, base64);
    }

    public static <T extends Serializable> T getSerialized(Key key)
        throws ClassNotFoundException, IOException
    {
        String base64 = Settings.getS(key);

        if (base64 == null)
            return null;

        ByteArrayInputStream bis =
            new ByteArrayInputStream(Base64.decodeBase64(base64));
        return FileHelper.deserialize(bis);
    }
}
