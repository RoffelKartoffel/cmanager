package cmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

public class CacheListModel 
{
	private ArrayList<Geocache> list = new ArrayList<Geocache>();
	private LinkedList<Waypoint> orphandWaypoints = new LinkedList<>();
	private CacheListModel THIS = this;
	private Location relativeLocation;
	
	private boolean refilteringRequired = true;
	private ArrayList<CacheListFilterModel> filters = new ArrayList<>();
	private ArrayList<Geocache> list_filtered;
	
	
	public void addCache(Geocache g){
		list.add(g);
		matchOrphands(g);
		
		refilteringRequired = true;
	}
	
	public void addFilter(CacheListFilterModel filter)
	{
		filter.addRunOnFilterUpdate(new Runnable() {
			public void run() {
				refilteringRequired = true;
			}
		});
		filters.add(filter);
		
		refilteringRequired = true;
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
		
		refilteringRequired = true;
	}
	
	public void filterUpdate()
	{
		refilteringRequired = true;
	}
	
	public void setRelativeLocation(Location rl)
	{
		relativeLocation = rl;
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
		
		refilteringRequired = true;
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
		
		refilteringRequired = true;
	}
	
	public ArrayList<Geocache> getList() 
	{
		if( !refilteringRequired )
			return list_filtered;
		
		ArrayList<Geocache> filtered = new ArrayList<>( list );
		for(CacheListFilterModel filter : filters)
			filtered = filter.getFiltered( filtered );
		
		refilteringRequired = false;
		list_filtered = filtered;
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
		
		refilteringRequired = true;
	}
	
	public void store(String pathToGPX, String name) throws Throwable
	{
		OutputStream os = FileHelper.openFileWrite(pathToGPX);
		GPX.cachlistToBuffer(list, name, os);
		os.close();
		
		System.gc();
	}
	
//////////////////////////////////////
//////////////////////////////////////
/////// Table model
//////////////////////////////////////
//////////////////////////////////////
	
	public CLMTableModel getTableModel(){
		return new CLMTableModel();
	}
	
	public class CLMTableModel extends AbstractTableModel {
	
		/**
		* 
		*/
		private static final long serialVersionUID = -6159661237715863643L;
		
		public String getColumnName(int col) {
			switch (col)
			{
				case 0:
					return "Code";
				case 1:
					return "Name";
				case 2:
					return "Type";
				case 3:
					return "Difficulty";
				case 4:
					return "Terrain";
				case 5:
					return "Lat";
				case 6:
					return "Lon";
				case 7:
					return "Owner";
				case 8:
					return "Distance (km)";
			}
			
			return null;
		}
		
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex)
			{
				case 0:
				case 1:
				case 2:
					return String.class;
					
				case 3:
				case 4:
				case 5:
				case 6:
					return Double.class;
					
				
				case 7:
					return String.class;
					
				case 8:
					return Double.class;
			}
	        
	        return null;
	    }
		
		@Override
		public int getColumnCount() {
			return 9;
		}
		
		@Override
		public int getRowCount() {
			return THIS.size();
		}
		
		public Geocache getObject(int row){
			return THIS.get(row);
		}
		
		@Override
		public Object getValueAt(int arg0, int arg1) {
			Geocache g = getObject(arg0);
			
			switch( arg1 )
			{
				case 0:
					return g.getCode();
				case 1:
					return g.getName();
				case 2:
					return g.getTypeAsNice();
				case 3:
					return g.getDifficulty();
				case 4:
					return g.getTerrain();
				case 5:
					return g.getCoordinate().getLat();
				case 6:
					return g.getCoordinate().getLon();
				case 7:
					String owner = g.getOwner();
					return owner != null ? owner : "";
				case 8:
					return relativeLocation != null ? g.getCoordinate().distanceSphereRounded(relativeLocation) : "";
				
				default:
				return null;
			}
		}
	
	}
	
}
