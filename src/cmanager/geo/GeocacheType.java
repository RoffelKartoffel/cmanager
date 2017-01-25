package cmanager.geo;

import java.io.Serializable;

public class GeocacheType implements Serializable
{
    private static final long serialVersionUID = 3852716526968673048L;

    private static final TypeMap TYPE = new TypeMap();
    static
    {
        //  pretty name 	-- 	GC 			-- OC
        TYPE.add("Tradi", "Traditional Cache", "Traditional");
        TYPE.add("Drive-In", null, "Drive-In");
        TYPE.add("Moving", null, "Moving");
        TYPE.add("Other", null, "Other");
        TYPE.add("Math/Physics", null, "Math/Physics");
        TYPE.add("Multi", "Multi-cache", "Multi");
        TYPE.add("Mystery", "Unknown Cache", "Quiz");

        TYPE.add("Virtual", "Virtual Cache", "Virtual");
        TYPE.add("Webcam", "Webcam Cache", null);
        TYPE.add("Earth", "Earthcache", null);
        TYPE.add("Reverse", null, "Locationless (Reverse) Cache");

        TYPE.add("L.Box", "Letterbox Hybrid", null);
        TYPE.add("Event", "Event Cache", "Event");
        TYPE.add("Mega-Event", "Mega-Event Cache", null);
        TYPE.add("Giga-Event", "Giga-Event Cache", null);
        TYPE.add("CITO", "Cache In Trash Out Event", null);
        TYPE.add("Wherigo", "Wherigo Cache", null);
        TYPE.add("GPS AE", "GPS Adventures Exhibit", null);
    }

    public static GeocacheType getTradiType()
    {
        return new GeocacheType("Tradi");
    }

    public static GeocacheType getMultiType()
    {
        return new GeocacheType("Multi");
    }

    public static GeocacheType getMysteryType()
    {
        return new GeocacheType("Mystery");
    }


    private int type;

    public GeocacheType(int type)
    {
        this.type = type;
    }

    public GeocacheType(String type)
    {
        this.type = TYPE.getLowercase(type);
    }

    public String asNiceType()
    {
        return TYPE.get(type, 0);
    }

    public String asGCType()
    {
        String gc = TYPE.get(type, 1);
        return gc != null ? gc : TYPE.get(type, 2);
    }

    public boolean equals(GeocacheType other)
    {
        return this.type == other.type;
    }
}
