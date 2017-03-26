package cmanager;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cmanager.geo.Geocache;
import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.gui.ExceptionPanel;
import cmanager.gui.MainWindow;


public class Main
{
    private static final String JAR_NAME = "cm-" + Version.VERSION + ".jar";
    private static final String PARAM_HEAP_RESIZED = "resized";

    //	private static final String OC_TEST_CACHE = "OC827D";


    public static void main(String[] args)
    {
        nagToUpdateFromJava7();

        try
        {
            resizeHeap(args);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }


        ////// release //////

        MainWindow frame = new MainWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Constants.APP_NAME + " " + Version.VERSION);
        frame.setVisible(true);


        /////////////////////
    }

    private static void nagToUpdateFromJava7()
    {
        if (System.getProperty("java.version").startsWith("1.7."))
        {
            String message = "You are using the outdated Java version 1.7.\n"
                             + "Please update to Java 1.8 or later.";

            JOptionPane.showMessageDialog(null, message, "Java version",
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private static String getJarPath()
    {
        //
        // Determinate name of our jar. Will only work when run as .jar.
        //
        File jarFile = new java.io.File(Main.class.getProtectionDomain()
                                            .getCodeSource()
                                            .getLocation()
                                            .getPath());


        String jarPath = jarFile.getAbsolutePath();
        if (jarPath.endsWith("."))
            jarPath = jarPath.substring(0, jarPath.length() - 1);
        if (!jarPath.endsWith(JAR_NAME))
            jarPath += JAR_NAME;
        return jarPath;
    }

    private static void showInvalidJarPathMessage(String jarPath)
    {
        String message =
            "Unable to start cmanager. Settings could not be applied.\n"
            + "Expected path: " + jarPath;
        JOptionPane.showMessageDialog(null, message, "jar path",
                                      JOptionPane.ERROR_MESSAGE);
    }

    public static void runCopyAndExit() throws IOException
    {
        String jarPath = getJarPath();
        if (!new File(jarPath).exists())
        {
            showInvalidJarPathMessage(jarPath);
            return;
        }

        //
        // Run new vm
        //
        ProcessBuilder pb =
            new ProcessBuilder("java", "-jar", jarPath, JAR_NAME);
        pb.start();

        System.exit(0);
    }

    private static void resizeHeap(String[] args) throws IOException
    {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals(PARAM_HEAP_RESIZED))
                return;
        }

        String jarPath = getJarPath();
        if (!new File(jarPath).exists())
        {
            showInvalidJarPathMessage(jarPath);
            return;
        }

        //
        // Read settings
        //
        String heapSizeS = Settings.getS(Settings.Key.HEAP_SIZE);
        Integer heapSizeI = null;
        try
        {
            heapSizeI = new Integer(heapSizeS);
        }
        catch (Throwable t)
        {
        }

        if (heapSizeI == null)
            return;
        if (heapSizeI < 128)
            return;

        //
        // Run new vm
        //
        ProcessBuilder pb =
            new ProcessBuilder("java", "-Xmx" + heapSizeI.toString() + "m",
                               "-jar", jarPath, JAR_NAME, PARAM_HEAP_RESIZED);
        Process p = pb.start();
        int retval = -1;
        try
        {
            retval = p.waitFor();
        }
        catch (InterruptedException e)
        {
        }

        if (retval == 0)
            System.exit(0); // New vm ran fine
        else
        {
            String message = "The chosen heap size could not be applied. \n"
                             + "Maybe there are insufficient system resources.";
            JOptionPane.showMessageDialog(null, message, "Memory Settings",
                                          JOptionPane.INFORMATION_MESSAGE);

            if (System.getProperty("sun.arch.data.model").equals("32"))
            {
                message =
                    "You are running a 32bit Java VM. \n"
                    + "This limits your available memory to less than 4096MB \n"
                    + "and in some configurations to less than 2048MB. \n\n"
                    + "Install a 64bit VM to get rid of this limitation!";
                JOptionPane.showMessageDialog(null, message, "Memory Settings",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


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
                ExceptionPanel.showErrorDialog(e);
            }
        }
        else
        {
            Exception e =
                new UnsupportedOperationException("Desktop unsupported.");
            ExceptionPanel.showErrorDialog(e);
        }
    }
}
