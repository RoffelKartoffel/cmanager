package cmanager.geo;

public class GeocacheComparator
{
    public static double calculateSimilarity(Geocache g1, Geocache g2)
    {
        if ((g1.getCodeGC() != null && g1.getCodeGC().equals(g2.getCode())) ||
            (g2.getCodeGC() != null && g2.getCodeGC().equals(g1.getCode())))
            return 1;

        // If a non premium member downloads her/his founds via geotoad,
        // premium caches are mislocated at 0.0/0.0 which falsely matches
        // many OC dummies in the ocean.
        if (g1.getCoordinate().equals(new Coordinate(0.0, 0.0)) &&
            g2.getCoordinate().equals(new Coordinate(0.0, 0.0)))
        {
            return 0;
        }

        double dividend = 0;
        double divisor = 0;

        divisor++;
        if (g1.getName().equals(g2.getName()))
            dividend++;

        divisor++;
        if (g1.getCoordinate().distanceHaversine(g2.getCoordinate()) < 0.001)
            dividend++;

        divisor++;
        if (Double.compare(g1.getDifficulty(), g2.getDifficulty()) == 0)
            dividend++;

        divisor++;
        if (Double.compare(g1.getTerrain(), g2.getTerrain()) == 0)
            dividend++;

        divisor++;
        if (g1.getType().equals(g2.getType()))
            dividend++;

        if (g1.getOwner() != null)
        {
            divisor++;
            if (g1.getOwner().equals(g2.getOwner()))
                dividend++;
        }

        if (g1.getContainer() != null)
        {
            divisor++;
            if (g1.getContainer().equals(g2.getContainer()))
                dividend++;
        }

        if (g1.getAvailable() != null && g1.getArchived() != null)
        {
            divisor++;
            if (g1.statusAsString().equals(g2.statusAsString()))
                dividend++;
        }

        return dividend / divisor;
    }

    public static boolean similar(Geocache g1, Geocache g2)
    {
        return calculateSimilarity(g1, g2) >= 0.8;
    }
}
