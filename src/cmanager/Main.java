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


public class Main 
{
	public static final String APP_NAME = "cmanager";
	public static final String VERSION = "0.2.30";
	
	public static final String CACHE_FOLDER = System.getProperty("user.home") + "/." + Main.APP_NAME + "/cache/";
	
	private static final String JAR_NAME = "cm.jar";
	private static final String PARAM_HEAP_RESIZED = "resized";
	

	
	
	
	public static void main(String[] args) 
	{
		try {
			resizeHeap(args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	

		
		////// release //////  
		
		MainWindow frame = new MainWindow();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(APP_NAME + " " + VERSION);
		frame.setVisible(true);

		
		/////////////////////
		
		
		
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
		if( jarPath.endsWith(".") )
			jarPath = jarPath.substring(0, jarPath.length()-1);
		if( !jarPath.endsWith(JAR_NAME) )
			jarPath += JAR_NAME;
		return jarPath;
	}
	
	public static void runCopyAndExit() throws Exception
	{
		String jarPath = getJarPath();
		if( !new File(jarPath).exists() )
			throw new Exception("Path of jar file could not be determined. ");
		
		//
		// Run new vm
		//
		ProcessBuilder pb = new ProcessBuilder( 
				"java", 
				"-jar", 
				jarPath, 
				JAR_NAME );
		pb.start();
		
		System.exit(0);
	}
	
	private static void resizeHeap(String[] args) throws IOException
	{
		for(int i = 0; i < args.length; i++) {
            if( args[i].equals(PARAM_HEAP_RESIZED) )
            	return;
        }
		
		String jarPath = getJarPath();
		if( !new File(jarPath).exists() )
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
		int retval = -1;
		try {
			retval = p.waitFor();
		} catch (InterruptedException e) {
		}
		
		if( retval == 0 )
			System.exit(0);	// New vm ran fine
		else
		{
			String message = "The chosen heap size could not be applied. \n" +
							"Maybe there are insufficient system resources.";
		    JOptionPane.showMessageDialog(null, message, "Memory Settings", JOptionPane.INFORMATION_MESSAGE);
		    
		    if( System.getProperty("sun.arch.data.model").equals("32") )
		    {
		    	message = "You are running a 32bit Java VM. \n" +
		    			"This limits your available memory to less than 4096MB \n" +
		    			"and in some configurations to less than 2048MB. \n\n" +
		    			"Install a 64bit VM to get rid of this limitation!";
		    	JOptionPane.showMessageDialog(null, message, "Memory Settings", JOptionPane.INFORMATION_MESSAGE);
		    }
		}
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
     WeakReference<Object> ref = new WeakReference<Object>(obj);
     obj = null;
     while(ref.get() != null) {
       System.gc();
     }
   }
}
