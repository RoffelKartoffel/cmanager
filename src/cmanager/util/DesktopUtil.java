package cmanager.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import cmanager.gui.ExceptionPanel;

public class DesktopUtil
{
    public static void openUrl(String uri)
    {
        if (Desktop.isDesktopSupported())
        {
            try
            {
                Desktop.getDesktop().browse(new URI(uri));
            }
            catch (URISyntaxException | IOException e)
            {
                ExceptionPanel.showErrorDialog(null, e);
            }
        }
        else
        {
            Exception e = new UnsupportedOperationException("Desktop unsupported.");
            ExceptionPanel.showErrorDialog(null, e);
        }
    }
}
