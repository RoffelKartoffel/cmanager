package cmanager.network;

import cmanager.global.Version;
import cmanager.xml.Element;
import cmanager.xml.Parser;

public class Updates
{
    private static Boolean updateAvailable = null;
    private static String newVersion = null;

    public static synchronized boolean updateAvailable_block()
    {
        if (updateAvailable == null)
        {
            try
            {
                String url =
                    "https://github.com/RoffelKartoffel/cmanager/releases.atom";
                String http = HTTP.get(url);

                Element root = Parser.parse(http);

                Element child =
                    root.getChild("feed").getChild("entry").getChild("title");
                newVersion = child.getUnescapedBody();

                updateAvailable = !newVersion.equals(Version.VERSION);
            }
            catch (Throwable t)
            {
                // Errors might be due to missing internet connection.
                updateAvailable = false;
            }
        }

        return updateAvailable;
    }

    public static String getNewVersion()
    {
        return newVersion;
    }
}
