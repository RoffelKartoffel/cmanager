package cmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.DateTime;
import org.joda.time.Months;

public class OKAPISearchCache 
{
	
	private static final String cacheFolder = System.getProperty("user.home") + "/." + Main.APP_NAME + "/cache/";
	private static boolean initDone = false;
	
	private static String searchToFileName(Geocache g, String excludeUUID)
	{
		String name = g.getCode()
					+ (excludeUUID == null ? "" : " " + excludeUUID);
		return cacheFolder + name;
	}
	

	public static void setEmptySearch(Geocache g, String excludeUUID) throws IOException
	{
		File f = new File(searchToFileName(g, excludeUUID));
		if( f.exists() )
			f.delete();
		
		CacheEntry e = new CacheEntry();
		e.timeStamp = new DateTime();
		
		writeEntryToFile(e, f.getAbsolutePath());
	}
	
	public static boolean isEmptySearch(Geocache g, String excludeUUID) throws ClassNotFoundException, IOException
	{
		if( !initDone )
		{
			new File(cacheFolder).mkdirs();
			initDone = true;
		}
	
		File f = new File(searchToFileName(g, excludeUUID));
		if( f.exists() )
		{
			CacheEntry e = readEntryFromFile(f.getAbsolutePath());
			
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
	
	
	private static CacheEntry readEntryFromFile(String path) throws IOException, ClassNotFoundException 
	{
		FileInputStream fileIn = new FileInputStream(path);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        CacheEntry e = (CacheEntry) in.readObject();
        in.close();
        fileIn.close();
        return e;
	}
	
	private static void writeEntryToFile(CacheEntry e, String path) throws IOException
	{
		FileOutputStream fileOut =
		         new FileOutputStream(path);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(e);
		out.close();
		fileOut.close();
	}
	
	private static class CacheEntry implements Serializable
	{
		private static final long serialVersionUID = 7106170690451900376L;
		
		public int protocolVersion = 0; 
		
		public DateTime timeStamp;
		
//		public Double lat;
//		public Double lon;
//		public String excludeUUID;
//		public Double searchRadius;
//		public ArrayList<String> codes;
	}
	
}
