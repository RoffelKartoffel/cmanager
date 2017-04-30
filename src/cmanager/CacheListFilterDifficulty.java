package cmanager;

import cmanager.geo.Geocache;

public class CacheListFilterDifficulty extends CacheListFilterModel
{
    private static final long serialVersionUID = -6582495781375197847L;
    private Double minD = 1.0;
    private Double maxD = 5.0;

    public CacheListFilterDifficulty()
    {
        super(FILTER_TYPE.BETWEEN_ONE_AND_FIVE_FILTER_VALUE);
        getLblLinks().setText("min Difficulty:");
        getLblRechts().setText("max Difficulty:");

        runDoModelUpdateNow = new Runnable() {
            @Override
            public void run()
            {
                minD = getValueLeft();
                maxD = getValueRight();
            }
        };
    }

    @Override
    protected boolean isGood(Geocache g)
    {
        return g.getDifficulty() >= minD && g.getDifficulty() <= maxD;
    }
}
