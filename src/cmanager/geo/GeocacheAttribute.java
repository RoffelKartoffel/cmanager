package cmanager.geo;

import java.io.Serializable;

public class GeocacheAttribute implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int ID;
    private int inc;
    private String desc;

    public GeocacheAttribute(int ID, int inc, String desc)
    {
        if (desc == null)
            throw new IllegalArgumentException();

        this.ID = ID;
        this.inc = inc;
        this.desc = desc;
    }

    public int getID()
    {
        return ID;
    }

    public int getInc()
    {
        return inc;
    }

    public String getDesc()
    {
        return desc;
    }
}
