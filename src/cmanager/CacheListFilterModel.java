package cmanager;

import java.util.ArrayList;

public abstract class CacheListFilterModel extends CacheListFilter
{
	private static final long serialVersionUID = 6947085305393841410L;

	
	public ArrayList<Geocache> getFiltered(final ArrayList<Geocache> originalList)
	{
		ArrayList<Geocache> list = new ArrayList<>();
		for( Geocache g : originalList )
			if( (!inverted && isGood(g)) || (inverted && !isGood(g)) )
				list.add(g);
		
		return list;
	}
	
	abstract protected boolean isGood(Geocache g);

}
