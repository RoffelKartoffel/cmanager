package cmanager;

import cmanager.geo.Geocache;
import cmanager.geo.Location;

public class CacheListFilterDistance extends CacheListFilterModel
{

    private static final long serialVersionUID = 1L;
    private Double maxDistance;
    private Location location;

    public CacheListFilterDistance()
    {
        super(FILTER_TYPE.SINGLE_FILTER_VALUE);
        lblLinks2.setText("Maximum distance to location (km): ");
        runDoModelUpdateNow = new Runnable() {
            @Override
            public void run()
            {
                maxDistance = new Double(textField.getText());
            }
        };
    }

    public void setLocation(Location l)
    {
        location = l;
    }

    @Override
    protected boolean isGood(Geocache g)
    {
        if (location == null || maxDistance == null)
            return true;

        double distance = g.getCoordinate().distanceHaversine(location);
        return distance < maxDistance;
    }
}
