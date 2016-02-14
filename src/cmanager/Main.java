package cmanager;



import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.lang.model.element.ExecutableElement;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


public class Main 
{
	public static final String APP_NAME = "cmanager";
	public static final String VERSION = "0.2u";
	
	private static final String JAR_NAME = "cm.jar";
	private static final String PARAM_HEAP_RESIZED = "resized";
	

	
	
	
	public static void main(String[] args) 
	{			
		try {
			resizeHeap(args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	

//		
//		System.exit(0);
		
		////// release //////  
		
		MainWindow frame = new MainWindow();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(APP_NAME + " " + VERSION);
		frame.setVisible(true);

		
		/////////////////////
		
		
		
	}
	
	private static void resizeHeap(String[] args) throws IOException
	{
		for(int i = 0; i < args.length; i++) {
            if( args[i].equals(PARAM_HEAP_RESIZED) )
            	return;
        }
		
		//
		// Determinate name of our jar. Will only work when run as .jar.
		//
		File jarFile = new java.io.File(Main.class.getProtectionDomain()
				  .getCodeSource()
				  .getLocation()
				  .getPath());
		
		String jarPath = jarFile.getAbsolutePath();
		if( jarPath.endsWith(".") )
			jarPath = jarPath.substring(0, jarPath.length()-1);
		if( !jarPath.endsWith(JAR_NAME) )
			jarPath += JAR_NAME;
		
		jarFile = new File(jarPath);
		if( !jarFile.exists() )
			return;

		//
		// Read settings
		//
		String heapSizeS = Settings.getS(Settings.Key.HEAP_SIZE);
		Integer heapSizeI = null;
		try{
			heapSizeI = new Integer(heapSizeS);
		}catch(Throwable t){
		}
		
		if( heapSizeI == null )
			return;
		if( heapSizeI < 128 )
			return;

		//
		// Run new vm
		//
		ProcessBuilder pb = new ProcessBuilder( 
				"java", 
				"-Xmx"+ heapSizeI.toString() +"m", 
				"-jar", 
				jarPath, 
				JAR_NAME, 
				PARAM_HEAP_RESIZED );
		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
		}
		
		System.exit(0);
	}
	

	
	static void print(ArrayList<Geocache> gList)
	{
		for( Geocache g : gList )
			System.out.println(g.toString());
	}

	
	static void print(XMLElement e, int level)
	{
		for(int i=0; i<level; i++)
			System.out.print(" ");
		System.out.println( e.getName() );
		
		for( XMLElement e2 : e.getChildren() )
			print(e2, level+1);
	}
	
	
	
	
	
	public static void openUrl(String uri) 
	{
	    if (Desktop.isDesktopSupported()) {
	      try {
	        Desktop.getDesktop().browse(new URI(uri));
	      } 
	      catch (URISyntaxException|IOException e)  { 
	    	  ExceptionPanel.showErrorDialog(e);
	      } 
	    } 
	    else  { 
	    	Exception e = new UnsupportedOperationException("Desktop unsupported.");
	    	ExceptionPanel.showErrorDialog(e);
	    }
	}
	
   /**
    * This method guarantees that garbage collection is
    * done unlike <code>{@link System#gc()}</code>
    */
   public static void gc() {
     Object obj = new Object();
     WeakReference ref = new WeakReference<Object>(obj);
     obj = null;
     while(ref.get() != null) {
       System.gc();
     }
   }
}
