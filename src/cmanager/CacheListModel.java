package cmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class CacheListModel 
{
	private ArrayList<Geocache> list = new ArrayList<Geocache>();
	private LinkedList<Waypoint> orphandWaypoints = new LinkedList<>();
	
	private ArrayList<CacheListFilterModel> filters = new ArrayList<>();
	
	
	public void addCache(Geocache g){
		list.add(g);
		matchOrphands(g);
	}
	
	public void addFilter(CacheListFilterModel filter)
	{
		filters.add(filter);
	}
	
	public void removeFilter(CacheListFilterModel filter)
	{
		Iterator<CacheListFilterModel> i = filters.iterator();
		while(i.hasNext())
		{
			CacheListFilterModel f = i.next();
			if( f == filter )
			{
				i.remove();
				break;
			}
		}
	}
	
	private void matchOrphands(Geocache g) 
	{
		Iterator<Waypoint>  it = orphandWaypoints.iterator();
		while( it.hasNext() )
			if( addWaypointToCache(g, it.next()) )
				it.remove();
	}
	
	private static boolean addWaypointToCache(Geocache g, Waypoint w)
	{
		String parent = w.getParent();			
		if( parent != null )
		{
			if( g.getCode().equals(parent) )
			{
				g.add(w);
				return true;
			}
		}
		else
		{
			String name = w.getCode();
			name = name.substring(2);
			name = "GC" + name;
		
			if( g.getCode().equals(name) )
			{
				g.add(w);
				return true;
			}						
		}
		return false;
	}
	
	public void removeCaches(ArrayList<Geocache> removeList)
	{
		for(Geocache remove : removeList)
			for(int i=0; i < list.size(); i++)
				if( list.get(i) == remove )
				{
					list.remove(i);
					break;
				}
	}
	
	public void addCaches(ArrayList<Geocache> addList)
	{
		for( Geocache gAdd : addList )
		{
			boolean match = false;
			for( Geocache gOld : list )
				if( gOld.getCode().equals(gAdd.getCode()) )
				{
					match = true;
					gOld.update(gAdd);
					break;
				}
			if( !match )
				addCache(gAdd);
		}
		
	}
	
	public ArrayList<Geocache> getList() 
	{
		ArrayList<Geocache> filtered = new ArrayList<>( list );
		for(CacheListFilterModel filter : filters)
			filtered = filter.getFiltered( filtered );
		
		return filtered;
	}
	
	public void removeCachesNotInFilter()
	{
		ArrayList<Geocache> filterList = getList();
		
		Iterator<Geocache> i = list.iterator();
		while( i.hasNext() )
		{
			Geocache g = i.next();
			if( !filterList.contains(g) )
			{
				i.remove();
			}
		}
	}
	
	public LinkedList<Waypoint> getOrphans() {
		return orphandWaypoints;
	}

	public int size(){
		return getList().size();
	}
	
	public Geocache get(int index)
	{	
		return getList().get(index);
	}
	
	public void load(String pathToGPX) throws Throwable
	{
		FileHelper.processFiles(pathToGPX, new FileHelper.InputAction() {
			public void process(InputStream is) throws Throwable 
			{
				ArrayList<Geocache> gList = new ArrayList<>();
				ArrayList<Waypoint> wList = new ArrayList<>();
				
				GPX.fileToXmlToCachlist(is, gList, wList);
				
				orphandWaypoints.addAll(wList);
				for(Geocache g : list )
					matchOrphands(g);
				
				for(Geocache g : gList )
					addCache(g);
				
				System.gc();
			}
		});
		
	}
	
	public void store(String pathToGPX, String name) throws Throwable
	{
		OutputStream os = FileHelper.openFileWrite(pathToGPX);
		GPX.cachlistToBuffer(list, name, os);
		os.close();
		
		System.gc();
	}
}
