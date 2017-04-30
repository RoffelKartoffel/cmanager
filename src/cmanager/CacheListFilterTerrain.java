package cmanager;

import cmanager.geo.Geocache;

public class CacheListFilterTerrain extends CacheListFilterModel
{
    private static final long serialVersionUID = -6582495781375197847L;
    private Double minTerrain = 1.0;
    private Double maxTerrain = 5.0;

    public CacheListFilterTerrain()
    {
        super(FILTER_TYPE.BETWEEN_ONE_AND_FIVE_FILTER_VALUE);
        getLblLinks().setText("min Terrain:");
        getLblRechts().setText("max Terrain:");

        runDoModelUpdateNow = new Runnable() {
            @Override
            public void run()
            {
                minTerrain = getValueLeft();
                maxTerrain = getValueRight();
            }
        };
    }

    @Override
    protected boolean isGood(Geocache g)
    {
        return g.getTerrain() >= minTerrain && g.getTerrain() <= maxTerrain;
    }
}
