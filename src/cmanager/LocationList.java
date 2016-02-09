package cmanager;

import java.util.ArrayList;

public class LocationList 
{
	private static LocationList theList;
	private ArrayList<Location> locations = new ArrayList<>();
	
	private LocationList(){
	}
	
	public static LocationList getList()
	{
		if( theList == null )
			theList = new LocationList();
		return theList;
	}

	
}
