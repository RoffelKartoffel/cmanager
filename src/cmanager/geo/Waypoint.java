package cmanager.geo;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Waypoint implements Serializable
{
    private static final long serialVersionUID = 3154357724453317729L;

    private Coordinate coords = null;
    private String code = null;
    private String description = null;
    private String symbol = null;
    private String type = null;
    private String parent = null;
    private DateTime date = null;


    public Waypoint(Coordinate coords, String code, String description, String symbol, String type,
                    String parent)
    {
        if (code == null)
            throw new NullPointerException();

        this.coords = coords;
        this.code = code;
        this.description = description;
        this.symbol = symbol;
        this.type = type;
        this.parent = parent;
    }


    public void setDate(String date)
    {
        this.date = date == null ? null : new DateTime(date, DateTimeZone.UTC);
    }

    public String getDateStrISO8601()
    {
        if (date == null)
            return null;

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return fmt.print(date);
    }


    public Coordinate getCoordinate()
    {
        return coords;
    }

    public String getCode()
    {
        return code;
    }


    public String getDescription()
    {
        return description;
    }


    public String getSymbol()
    {
        return symbol;
    }


    public String getType()
    {
        return type;
    }


    public String getParent()
    {
        return parent;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }
}
