package cmanager;

import java.util.ArrayList;

import cmanager.geo.Geocache;

public abstract class CacheListFilterModel extends CacheListFilterPanel
{
    private static final long serialVersionUID = 6947085305393841410L;

    public CacheListFilterModel(FILTER_TYPE filterType)
    {
        super(filterType);
    }

    public ArrayList<Geocache> getFiltered(final ArrayList<Geocache> originalList)
    {

        //		for( Geocache g : originalList )
        //			if( (!inverted && isGood(g)) || (inverted &&
        //! isGood(g)) )
        //				list.add(g);

        int listSize = originalList.size();
        ThreadStore ts = new ThreadStore();
        int cores = ts.getCores(listSize);
        int perProcess = listSize / cores;

        final ArrayList<ArrayList<Geocache>> lists = new ArrayList<>(5);
        for (int c = 0; c < cores; c++)
            lists.add(new ArrayList<Geocache>());
        for (int c = 0; c < cores; c++)
        {
            final int start = perProcess * c;
            final int c_final = c;

            int tmp = perProcess * (c + 1) < listSize ? perProcess * (c + 1) : listSize;
            if (c == cores - 1)
                tmp = listSize;
            final int end = tmp;

            ts.addAndRun(new Thread(new Runnable() {
                public void run()
                {
                    ArrayList<Geocache> list = lists.get(c_final);
                    try
                    {
                        for (int i = start; i < end; i++)
                        {
                            Geocache g = originalList.get(i);
                            if ((!inverted && isGood(g)) || (inverted && !isGood(g)))
                                list.add(g);
                        }
                    }
                    catch (Throwable ex)
                    {
                        Thread t = Thread.currentThread();
                        t.getUncaughtExceptionHandler().uncaughtException(t, ex);
                    }
                }
            }));
        }
        try
        {
            ts.joinAndThrow();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        ArrayList<Geocache> list_final = new ArrayList<>();
        for (ArrayList<Geocache> list : lists)
            list_final.addAll(list);

        return list_final;
    }

    abstract protected boolean isGood(Geocache g);
}
