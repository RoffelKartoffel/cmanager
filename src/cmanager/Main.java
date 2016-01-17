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
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CacheManagerBuilder;
import org.ehcache.config.CacheConfigurationBuilder;
import org.ehcache.config.ResourcePoolsBuilder;
import org.ehcache.config.persistence.CacheManagerPersistenceConfiguration;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Main 
{
	public static final String APP_NAME = "cmanager";
	public static final String VERSION = "0.2m";
	
	// kleine namen
	
	
	
	public static void main(String[] args) throws Throwable 
	{	
			
//		CacheListModel model = new CacheListModel();
//		
//		long start = System.currentTimeMillis();
//		
//		model.load("/tmp/test2.zip");
//		
////		try {
////		Thread.sleep(2000);
////		}catch(InterruptedException e){
////			
////		}
//		
//		
//		
////		model.store("/tmp/dummy.gpx", "dummy");
//		
//		long duration = System.currentTimeMillis() - start;
//		System.out.println("Time: " + duration);
//		
		
//		CacheManager cacheManager = null;
//		Cache<String, String> listingCache = null;
//		
//		
//		
//		cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//			    .with(new CacheManagerPersistenceConfiguration(new File("/tmp/", "myData"))) 
//			    .withCache("listing cache", CacheConfigurationBuilder.newCacheConfigurationBuilder()
//			        .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
//			            .heap(10, EntryUnit.ENTRIES)
//			            .disk(10L, MemoryUnit.MB, true)) 
//			        .buildConfig(String.class, String.class))
//			    .build(true);
//		
//		listingCache = cacheManager.getCache("listing cache", String.class, String.class);
//		
//		Geocache g = new Geocache("GC1", "einCachename", new Coordinate(0.0, 0.0), 1.0, 1.0, "Tradi");
//		
//		listingCache.put(g.getCode() + g.hashCode(), "hallo");
//		
//		String s = listingCache.get(g.getCode() + g.hashCode());
//		
		
//		System.out.println((System.getProperty("java.io.tmpdir")));
//		
//		System.exit(0);
		
		////// release //////  
		
		MainWindow frame = new MainWindow();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(APP_NAME + " " + VERSION);
		frame.setVisible(true);

		
		/////////////////////
		
		
		
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
