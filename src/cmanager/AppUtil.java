package cmanager;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import cmanager.global.Version;

public class AppUtil
{
    private static final String JAR_NAME = "cm-" + Version.VERSION + ".jar";
    private static final String PARAM_HEAP_RESIZED = "resized";

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

    public static void resizeHeapAndRestart(String[] args) throws IOException
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
}
