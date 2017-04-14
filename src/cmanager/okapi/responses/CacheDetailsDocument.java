package cmanager.okapi.responses;

public class CacheDetailsDocument
{
    private class Owner
    {

        String uuid;
        String username;
        String profile_url;
    }

    String size2;
    Owner owner;
    String short_description;
    String description;
    String hint2;

    public String getOwnerUuid()
    {
        return owner.uuid;
    }
    public String getOwnerUsername()
    {
        return owner.username;
    }
    public String getOwnerProfile_url()
    {
        return owner.profile_url;
    }

    public String getSize2()
    {
        return size2;
    }
    public Owner getOwner()
    {
        return owner;
    }
    public String getShort_description()
    {
        return short_description;
    }
    public String getDescription()
    {
        return description;
    }
    public String getHint2()
    {
        return hint2;
    }
}
