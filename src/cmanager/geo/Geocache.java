package cmanager.geo;


import java.io.Serializable;
import java.util.ArrayList;

import org.joda.time.DateTime;

import cmanager.util.ObjectHelper;


public class Geocache implements Serializable, Comparable<String>
{
    private static final long serialVersionUID = 6173771530979347662L;

    private String code;
    private String name;
    private Coordinate coordinate;
    private Double difficulty;
    private Double terrain;
    private GeocacheType type;
    private GeocacheContainerType container;
    private String owner = null;
    private String code_gc = null; // linked cache on gc
    private String listing = null;
    private String listing_short = null;
    private String hint = null;

    private Boolean archived = null;
    private Boolean available = null;
    private Integer id = null; // required by "Garmin etrex 10"

    private Boolean gcPremium = null;
    private Integer favPoints = null;

    private Boolean isFound = null;

    private ArrayList<GeocacheAttribute> attributes = new ArrayList<>();
    private ArrayList<GeocacheLog> logs = new ArrayList<GeocacheLog>();
    private ArrayList<Waypoint> wps = new ArrayList<Waypoint>();


    public Geocache(String code, String name, Coordinate coordinate, Double difficulty,
                    Double terrain, String type) throws NullPointerException
    {
        if (code == null || name == null || coordinate == null || difficulty == null ||
            terrain == null || type == null)
            throw new NullPointerException();

        this.code = code;
        this.name = name;
        this.coordinate = coordinate;
        this.difficulty = difficulty;
        this.terrain = terrain;
        this.type = new GeocacheType(type);
    }

    public String toString()
    {
        return difficulty.toString() + "/" + terrain.toString() + " " + code + " (" +
            type.asNiceType() + ") -- " + coordinate.toString() + " -- " + name;
    }

    public boolean hasVolatileStart()
    {
        return this.type.equals(GeocacheType.getMysteryType());
    }

    public void update(Geocache g)
    {
        update(g, true, true);
    }

    public void update(Geocache g, boolean override, boolean copyLogs)
    {
        if (!code.equals(g.code))
            return;

        if (override)
        {
            name = ObjectHelper.getBest(name, g.name);
            coordinate = ObjectHelper.getBest(coordinate, g.coordinate);
            difficulty = ObjectHelper.getBest(this.getDifficulty(), g.getDifficulty());
            terrain = ObjectHelper.getBest(terrain, g.terrain);
            type = g.type;
            container = ObjectHelper.getBest(container, g.container);
            owner = ObjectHelper.getBest(owner, g.owner);
            code_gc = ObjectHelper.getBest(code_gc, g.code_gc);
            setListing(ObjectHelper.getBest(getListing(), g.getListing()));
            listing_short = ObjectHelper.getBest(listing_short, g.listing_short);
            hint = ObjectHelper.getBest(hint, g.hint);
            archived = ObjectHelper.getBest(archived, g.archived);
            available = ObjectHelper.getBest(available, g.available);

            attributes = ObjectHelper.getBest(attributes, g.attributes);
        }
        if (copyLogs)
        {
            for (GeocacheLog newLog : g.logs)
            {
                boolean match = false;
                for (GeocacheLog oldLog : logs)
                    if (newLog.equals(oldLog))
                    {
                        match = true;
                        break;
                    }
                if (!match)
                    logs.add(newLog);
            }
        }
    }

    public String getURL()
    {
        if (isGC())
            return "https://www.geocaching.com/geocache/" + code;
        if (isOC())
            return "https://www.opencaching.de/" + code;

        return null;
    }

    public String statusAsString()
    {
        if (archived == null || available == null)
            return null;

        if (archived)
            return "archived";
        if (available)
            return "available";
        return "disabled";
    }

    public boolean isOC()
    {
        return code.substring(0, 2).toUpperCase().equals("OC");
    }

    public boolean isGC()
    {
        return code.substring(0, 2).toUpperCase().equals("GC");
    }

    public void add(GeocacheLog gl)
    {
        logs.add(gl);
    }

    public void add(ArrayList<GeocacheLog> logs)
    {
        for (GeocacheLog gl : logs)
            this.logs.add(gl);
    }

    public ArrayList<GeocacheLog> getLogs()
    {
        return logs;
    }

    public DateTime getMostRecentFoundLog(String usernameGC, String usernameOC)
    {
        GeocacheLog mostRecentLog = null;
        for (GeocacheLog log : logs)
        {
            if (log.isFoundLog())
                if ((usernameGC != null && log.isAuthor(usernameGC)) ||
                    (usernameOC != null && log.isAuthor(usernameOC)))
                {
                    if (mostRecentLog == null)
                        mostRecentLog = log;
                    else if (log.getDate().isAfter(mostRecentLog.getDate()))
                        mostRecentLog = log;
                }
        }
        return mostRecentLog == null ? null : mostRecentLog.getDate();
    }

    public void add(Waypoint wp)
    {
        wp.setParent(code);
        wps.add(wp);
    }

    public void addWaypoints(ArrayList<GeocacheAttribute> attributes)
    {
        for (GeocacheAttribute a : attributes)
            this.addAttribute(a);
    }

    public ArrayList<GeocacheAttribute> getAttributes()
    {
        return attributes;
    }

    public void addAttribute(GeocacheAttribute a)
    {
        attributes.add(a);
    }


    public ArrayList<Waypoint> getWaypoints()
    {
        return wps;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public Integer getFavPoints()
    {
        return favPoints;
    }
    public void setFavPoints(Integer favPoints)
    {
        this.favPoints = favPoints;
    }

    public Boolean getGcPremium()
    {
        return gcPremium;
    }
    public void setGcPremium(Boolean gcPremium)
    {
        this.gcPremium = gcPremium;
    }

    public void setArchived(Boolean archived)
    {
        this.archived = archived;
    }

    public Boolean getArchived()
    {
        return archived;
    }

    public void setAvailable(Boolean available)
    {
        this.available = available;
    }

    public Boolean getAvailable()
    {
        return available;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
    }

    public String getHint()
    {
        return hint;
    }

    public void setCodeGC(String gc)
    {
        code_gc = gc;
    }

    public String getCodeGC()
    {
        return code_gc;
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public Double getDifficulty()
    {
        return difficulty;
    }

    public Double getTerrain()
    {
        return terrain;
    }

    public GeocacheType getType()
    {
        return type;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getListing()
    {
        return listing;
    }

    public void setListing(String listing)
    {
        this.listing = listing;
    }

    public GeocacheContainerType getContainer()
    {
        return this.container;
    }

    public void setContainer(String container)
    {
        this.container = new GeocacheContainerType(container);
    }

    public String getListing_short()
    {
        return listing_short;
    }
    public void setListing_short(String listing_short)
    {
        this.listing_short = listing_short;
    }
    public Boolean getIsFound()
    {
        return isFound;
    }
    public void setIsFound(Boolean isFound)
    {
        this.isFound = isFound;
    }

    @Override
    public int compareTo(String s)
    {
        return code.compareTo(s);
    }
}
