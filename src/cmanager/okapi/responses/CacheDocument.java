package cmanager.okapi.responses;

public class CacheDocument
{
    private String code;
    private String name;
    private String location;
    private String type;
    private String gc_code;
    private Double difficulty;
    private Double terrain;
    private String status;

    public String getCode()
    {
        return code;
    }
    public String getName()
    {
        return name;
    }
    public String getLocation()
    {
        return location;
    }
    public String getType()
    {
        return type;
    }
    public String getGc_code()
    {
        return gc_code;
    }
    public Double getDifficulty()
    {
        return difficulty;
    }
    public Double getTerrain()
    {
        return terrain;
    }
    public String getStatus()
    {
        return status;
    }
}
