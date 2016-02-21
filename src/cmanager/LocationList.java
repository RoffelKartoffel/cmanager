package cmanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;

public class LocationList 
{
	private static LocationList theList;
	
	public static LocationList getList()
	{
		if( theList == null )
			theList = new LocationList();
		return theList;
	}

	
	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	//			Member Variables
	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	
	private ArrayList<Location> locations = null;
	
	private LocationList()
	{
	}
	
	private void load() throws ClassNotFoundException, IOException
	{
		String base64 = Settings.getS(Settings.Key.LOCATION_LIST);
		
		if(base64 != null)
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(base64));
			locations = FileHelper.deserialize(bis);
		}
	}
	
	public ArrayList<Location> getLocations()
	{
		if( locations == null )
			try {
				load();
			} catch (ClassNotFoundException | IOException e) {
			}
		
		// loading failed
		if( locations == null )
			locations = new ArrayList<>();
		
		return locations;
	}
	
	public void setLocations(ArrayList<Location> newLocations) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileHelper.serialize(newLocations, bos);
		byte[] bytes = bos.toByteArray();
		
		String base64 = Base64.encodeBase64String(bytes);
		Settings.set(Settings.Key.LOCATION_LIST, base64);
		
		locations = newLocations;
	}
	
}
