package cmanager.geo;

public class Location extends Coordinate
{
    private static final long serialVersionUID = 1L;

    private String name;

    public Location(String name, double lat, double lon) throws Exception
    {
        super(lat, lon);
        setName(name);
    }

    public void setName(String name) throws Exception
    {
        name = name.trim();
        if (name.equals(""))
            throw new Exception("Name must not be empty.");

        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return getName();
    }
}
