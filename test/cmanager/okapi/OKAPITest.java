package cmanager.okapi;

import static org.junit.Assert.*;

import org.junit.Test;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;

public class OKAPITest
{

    @Test public void testUsernameToUUID() throws Exception
    {
        {
            String uuid = OKAPI.usernameToUUID("This.User.Does.Not.Exist");
            assertEquals(uuid, null);
        }

        {
            String uuid = OKAPI.usernameToUUID("cmanagerTestAccount");
            assertEquals(uuid, "a912cccd-1c60-11e7-8e90-86c6a7325f31");
        }
    }

    @Test public void testGetCache() throws Exception
    {
        {
            Geocache g = OKAPI.getCache("OC827D");
            assertTrue(g != null);
            assertEquals(g.getName(), "auftanken");
            assertTrue(g.getCoordinate().equals(new Coordinate(49.955717, 8.332967)));
            assertEquals(g.getType().asNiceType(), "Drive-In");
            assertEquals(g.getCodeGC(), null);
            assertTrue(g.getDifficulty() == 1);
            assertTrue(g.getTerrain() == 2);
            assertTrue(g.getArchived() == true);
        }

        {
            Geocache g = OKAPI.getCache("OC11ECF");
            assertTrue(g != null);
            assertEquals(g.getName(), "Gehüpft wie gesprungen");
            assertTrue(g.getCoordinate().equals(new Coordinate(53.019517, 8.5344)));
            assertEquals(g.getType().asNiceType(), "Tradi");
            assertEquals(g.getCodeGC(), "GC46PY8");
            assertTrue(g.getDifficulty() == 2);
            assertTrue(g.getTerrain() == 1.5);
            assertTrue(g.getArchived() == true);
        }
    }
}
