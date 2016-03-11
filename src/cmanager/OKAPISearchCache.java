package cmanager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.DateTime;

public class OKAPISearchCache 
{
	
	private static final String LEGACY_CACHE_FOLDER = Main.CACHE_FOLDER;
	private static final String CACHE_FOLDER = Main.CACHE_FOLDER + "OC.OKAPI.emptySearches/";
	private static boolean initDone = false;
	
	private static String searchToFileName(Geocache g, String excludeUUID)
	{
		String name = g.getCode()
					+ (excludeUUID == null ? "" : " " + excludeUUID);
		return CACHE_FOLDER + name;
	}
	

	public static synchronized void setEmptySearch(Geocache g, String excludeUUID) throws IOException
	{
		File f = new File(searchToFileName(g, excludeUUID));
		if( f.exists() )
			f.delete();
		
		CacheEntry e = new CacheEntry();
		e.timeStamp = new DateTime();
		
		FileHelper.serializeToFile(e, f.getAbsolutePath());
	}
	
	public static synchronized boolean isEmptySearch(Geocache g, String excludeUUID) throws ClassNotFoundException, IOException
	{
		if( !initDone )
		{
			new File(CACHE_FOLDER).mkdirs();
			
			// if there are files in the legacy foler, move them
			// into the new folder
			for(File f : new File(LEGACY_CACHE_FOLDER).listFiles())
				if( f.getName().startsWith("GC") )
					f.renameTo( new File(CACHE_FOLDER + f.getName()) );
			
			initDone = true;
		}
	
		File f = new File(searchToFileName(g, excludeUUID));
		if( f.exists() )
		{
			CacheEntry e = FileHelper.deserializeFromFile(f.getAbsolutePath());
			
			int randomMonthCount = -1* ThreadLocalRandom.current().nextInt(4, 12 + 1);
			int randomDayCount = -1* ThreadLocalRandom.current().nextInt(0, 31 + 1);
			DateTime now = new DateTime();
			now = now.plusMonths( randomMonthCount );	
			now = now.plusDays( randomDayCount );
			
			// outdated?
			if( now.isAfter( e.timeStamp ) )
			{
				f.delete();
				return false;
			}
			else
				return true;
		}
		
		return false;
	}
	
	
	private static class CacheEntry implements Serializable
	{
		private static final long serialVersionUID = 7106170690451900376L;
		

		public DateTime timeStamp;
	}
	
}
