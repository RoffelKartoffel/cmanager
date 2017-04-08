package cmanager.okapi;

import static org.junit.Assert.*;

import org.junit.Test;

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
}
