package cmanager;

import static org.junit.Assert.*;

import org.junit.Test;

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
        assertEquals(c1.distanceSphere(c2), 0, 0);

        Coordinate c3 = new Coordinate(1.23, 4.567);
        assertFalse(c1.equals(c3));
        assertEquals(c1.distanceSphere(c3), 0.779055, 0.00001);
        assertEquals(c1.distanceSphereRounded(c3), 0.779, 0);
    }
}
