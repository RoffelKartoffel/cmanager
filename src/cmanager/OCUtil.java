package cmanager;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class OCUtil {
	
	public static void findOnOc(final CacheListModel clm, final OutputInterface oi, final OCUser user, final String uuid) throws Throwable
	{
		final ArrayList<Geocache> offlineCacheStore = new ArrayList<>();
		final AtomicInteger count = new AtomicInteger(0);
		
		final ExecutorService service = Executors.newFixedThreadPool( 10 );
		final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>(null);
		
		for(final Geocache gc : clm.getList() )
		{
			if( throwable.get() != null )
				break;
			
			Callable<Void> callable = new Callable<Void>() {
				public Void call() 
				{
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
	
}
