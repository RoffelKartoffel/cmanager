package cmanager;

import java.util.ArrayList;

public class OCUtil {
	
	public static void findOnOc(CacheListModel clm, OutputInterface oi, OCUser user, String uuid) throws Exception
	{
		ArrayList<Geocache> offlineCacheStore = new ArrayList<>();
		
		int count = 0;
		for(Geocache gc : clm.getList() )
		{
			oi.setProgress(count,  clm.getList().size());
			count++;
			
			if( OKAPISearchCache.isEmptySearch(gc, uuid) )
				continue;
			
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
		}
		oi.setProgress(clm.getList() .size(), clm.getList() .size());
	}
	
	public interface OutputInterface {
	    void setProgress(Integer count, Integer max);
	    void match(Geocache gc, Geocache oc);
	}
	
}
