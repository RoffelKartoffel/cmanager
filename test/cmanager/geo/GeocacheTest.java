package cmanager.geo;

import org.junit.Test;

import cmanager.util.ObjectHelper;

import static org.junit.Assert.*;

public class GeocacheTest
{
    @Test
    public void testConstructor()
    {

        new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");

        try
        {
            new Geocache(null, "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            new Geocache("OC1234", null, new Coordinate(0, 0), 0.0, 0.0, "Tradi");
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            new Geocache("OC1234", "test", null, 0.0, 0.0, "Tradi");
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            new Geocache("OC1234", "test", new Coordinate(0, 0), null, 0.0, "Tradi");
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, null, "Tradi");
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, null);
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
        }
    }

    @Test
    public void testDataInterpretation()
    {
        Geocache g;
        g = new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertTrue(g.isOC());
        assertFalse(g.isGC());
        assertFalse(g.hasVolatileStart());
        assertEquals(g.getURL(), "https://www.opencaching.de/OC1234");

        g = new Geocache("GC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        assertTrue(g.isGC());
        assertFalse(g.isOC());
        assertFalse(g.hasVolatileStart());
        assertEquals(g.getURL(), "https://www.geocaching.com/geocache/GC1234");

        g = new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Mystery");
        assertTrue(g.hasVolatileStart());
    }

    @Test
    public void testSerialize()
    {
        Geocache g = new Geocache("OC1234", "test", new Coordinate(0, 0), 0.0, 0.0, "Tradi");
        Geocache g2 = ObjectHelper.copy(g);
        assertTrue(g2 != null);
    }
}
