package cmanager;

import java.util.ArrayList;

public abstract class CacheListFilterModel extends CacheListFilter
{
	private static final long serialVersionUID = 6947085305393841410L;

	
	public ArrayList<Geocache> getFiltered(final ArrayList<Geocache> originalList)
	{
		final ArrayList<Geocache> list = new ArrayList<>();
//		for( Geocache g : originalList )
//			if( (!inverted && isGood(g)) || (inverted && !isGood(g)) )
//				list.add(g);

		int listSize = originalList.size();
		ThreadStore ts = new ThreadStore();
		int cores = ts.getCores(listSize);
		int perProcess = listSize / cores;
		
		for(int c=0; c<cores; c++)
		{
			final int start = perProcess*c;
			
			int tmp = perProcess*(c+1) < listSize ? perProcess*(c+1) : listSize;
			if( c == cores -1 )
				tmp = listSize;
			final int end = tmp;
			
			ts.addAndRun( new Thread(new Runnable(){
				public void run() 
				{
					try {
						for(int i=start; i<end; i++)
						{
							Geocache g = originalList.get(i);
							if( (!inverted && isGood(g)) || (inverted && !isGood(g)) )
								synchronized(originalList) {
									list.add(g);
								}
						}
					}catch(Throwable ex){
						Thread t = Thread.currentThread();
					    t.getUncaughtExceptionHandler().uncaughtException(t, ex);
					}
				}
			}));
		}
		try {
			ts.joinAndThrow();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	abstract protected boolean isGood(Geocache g);

}
