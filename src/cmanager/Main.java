package cmanager;


import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.gui.MainWindow;


public class Main
{
    //	private static final String OC_TEST_CACHE = "OC827D";


    public static void main(String[] args)
    {
        nagToUpdateFromJava7();

        try
        {
            AppUtil.resizeHeapAndRestart(args);
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
}
