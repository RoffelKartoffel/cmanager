package cmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.joda.time.DateTime;

public class OCUtil {
	
	static final ArrayList<Geocache> offlineCacheStore = new ArrayList<>();
	
	
	private final static String SHADOWLIST_FOLDER = Main.CACHE_FOLDER + "OC.shadowlist";
	private final static String SHADOWLIST_PATH = SHADOWLIST_FOLDER + "/gc2oc.gz";
	
	
	/**
	 * 
	 * @param stopBackgroundThread Processing is interrupted if this boolean is set true
	 * @param clm The model supplying the caches to check
	 * @param oi Callback functions
	 * @param user OCUser object for OKAPI authentication
	 * @param uuid The uuid of the OC user to exclude caches already found by this user
	 * @throws Throwable
	 */
	public static void findOnOc(
			final AtomicBoolean stopBackgroundThread, 
			final CacheListModel clm, 
			final OutputInterface oi, 
			final OCUser user, 
			final String uuid) throws Throwable
	{
		// update local copy of shadow list
		updateShadowList();
		
		
		// Number of found duplicates
		final AtomicInteger count = new AtomicInteger(0);
		// Thread pool which establishes 10 concurrent connection at max
		final ExecutorService service = Executors.newFixedThreadPool( 10 );
		// Variable to hold an exception throwable if one is thrown by a task
		final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>(null);
		
		// Create a task for each cache and submit it to the thread pool.
		for(final Geocache gc : clm.getList() )
		{
			if( throwable.get() != null )
				break;
			
			if( stopBackgroundThread.get() )
				break;
			
			Callable<Void> callable = new Callable<Void>() 
			{
				public Void call() 
				{
					if( stopBackgroundThread.get() )
						return null;
					
					try {
						oi.setProgress(count.get(), clm.getList().size());
						count.getAndIncrement();
						
						if( OKAPISearchCache.isEmptySearch(gc, uuid) )
							return null;
						
						double searchRadius = gc.hasVolatileStart() ? 1 : 0.05 ;
						ArrayList<Geocache> similar = OKAPI.getCachesAround(gc, searchRadius, offlineCacheStore, user, uuid);
						boolean match = false;
						for( Geocache oc : similar )
							if( oc.isSimilar(gc) )
							{
								OKAPI.completeCacheDetails(oc);
								oi.match(gc, oc);
								match = true;
							}
						
						if(!match)
							OKAPISearchCache.setEmptySearch(gc, uuid);
					}catch(Throwable t){
						throwable.set(t);
					}
					
					return null;
				}
			};
			service.submit(callable);
		}
		
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);	// incredible high delay but still ugly
		
		if( throwable.get() != null )
			throw throwable.get();
		
		oi.setProgress(clm.getList().size(), clm.getList().size());
	}
	
	public interface OutputInterface {
	    void setProgress(Integer count, Integer max);
	    void match(Geocache gc, Geocache oc);
	}
	
	
	private static void updateShadowList() throws IOException
	{
		// delete list if it is older than 1 month
		File file = new File(SHADOWLIST_PATH);
		if( file.exists() )
		{
			DateTime fileTime = new DateTime( file.lastModified() );
			DateTime now = new DateTime();
			fileTime = fileTime.plusMonths( 1 );
			if( fileTime.isAfter( now ) )
				return;
				
			file.delete();
		}
		
		new File(SHADOWLIST_FOLDER).mkdirs();
		
		// download list
		URL url = new URL("https://www.opencaching.de/api/gc2oc.php");
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(SHADOWLIST_PATH);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
	
}
