package cmanager.geo;

import static org.junit.Assert.*;
import org.junit.Test;
import cmanager.geo.Coordinate;

public class Test_Coordinate
{

    @Test public void test()
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


    @Test public void testDistance()
    {
        Coordinate c1 = new Coordinate(53.09780, 8.74908);
        Coordinate c2 = new Coordinate(53.05735, 8.59148);
        assertEquals(c1.distanceHaversine(c2), 11448.0325, 0.0009);
    }
}
