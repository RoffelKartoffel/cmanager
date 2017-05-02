package cmanager.gui;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import cmanager.util.ForkUtil;

public class Logo
{
    private static ImageIcon logo = null;

    public static void setLogo(JFrame frame)
    {
        if (logo == null)
        {
            URL iconURL = frame.getClass().getResource("/ressources/images/logo.jpg");
            if (iconURL == null)
            {
                // Not packed as jar
                try
                {
                    iconURL = new URL("file://" + ForkUtil.getCodeSource() + "/"
                                      + "../ressources/images/logo.jpg");
                }
                catch (MalformedURLException e)
                {
                }
            }
            if (iconURL != null)
            {
                logo = new ImageIcon(iconURL);
            }
        }

        frame.setIconImage(logo.getImage());
    }
}
