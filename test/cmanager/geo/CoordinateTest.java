package cmanager.geo;

import static org.junit.Assert.*;
import org.junit.Test;
import cmanager.geo.Coordinate;
import cmanager.geo.Coordinate.UnparsableException;

public class CoordinateTest
{

    @Test
    public void test()
    {
        Coordinate c1 = new Coordinate("1.23", "4.56");
        assertEquals(c1.getLat(), 1.23, 0.0);
        assertEquals(c1.getLon(), 4.56, 0.0);

        Coordinate c2 = new Coordinate(1.23, 4.56);
        assertEquals(c2.getLat(), 1.23, 0.0);
        assertEquals(c2.getLon(), 4.56, 0.0);
        assertEquals(c2.toString(), "1.23, 4.56");

        assertTrue(c1.equals(c2));
        assertEquals(c1.distanceHaversine(c2), 0, 0);

        Coordinate c3 = new Coordinate(1.23, 4.567);
        assertFalse(c1.equals(c3));
        assertEquals(c1.distanceHaversine(c3), 778.1851, 0.00009);
        assertEquals(c1.distanceHaversineRounded(c3), 778.185, 0);
    }


    @Test
    public void testDistance()
    {
        Coordinate c1 = new Coordinate(53.09780, 8.74908);
        Coordinate c2 = new Coordinate(53.05735, 8.59148);
        assertEquals(c1.distanceHaversine(c2), 11448.0325, 0.0009);
    }


    private void parse(String string, double lat, double lon)
    {
        try
        {
            Coordinate c = new Coordinate(string);
            assertEquals(c.getLat(), lat, 0.0);
            assertEquals(c.getLon(), lon, 0.00009);
        }
        catch (UnparsableException e)
        {
            fail(e.getStackTrace().toString());
        }
    }

    private void parse(String string)
    {
        parse(string, 53.1073, 8.12945);
    }

    @Test
    public void testParsing()
    {
        parse(" N 53° 06.438' E 008° 07.767' (WGS84)");
        parse("  N53° 06.438' E 008° 07.767' (WGS84)");
        parse("N 53°06.438' E 008° 07.767' (WGS84)");
        parse("N 53 06.438' E 008° 07.767'");
        parse("N 53 06.438 E 008° 07.767' (WGS84)");
        parse("N 53 06.438 E 008 07.767' (WGS84)");
        parse("N 53 06.438E008 07.767' (WGS84)");
        parse("N 53 06E008 07' (WGS84)", 53.1, 8.1166);
        parse("    N 53° 06.438' E 008° 07.767' ");
    }
}
