package cmanager.geo;


import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class GeocacheLog implements Serializable
{
    private static final long serialVersionUID = -2611937420437874774L;


    public static final TypeMap TYPE = new TypeMap();
    static
    {
        TYPE.add("Found it");
        TYPE.add("Didn't find it");
        TYPE.add("Write note", "Note");
        TYPE.add("Needs Maintenance");
        TYPE.add("Needs Archived");

        TYPE.add("Will Attend");
        TYPE.add("Attended");
        TYPE.add("Announcement");

        TYPE.add("Webcam Photo Taken");

        TYPE.add("Temporarily Disable Listing");
        TYPE.add("Enable Listing");
        TYPE.add("Owner Maintenance");
        TYPE.add("Update Coordinates");

        TYPE.add("Post Reviewer Note");
        TYPE.add("Publish Listing");
        TYPE.add("Retract Listing");
        TYPE.add("Archive");
        TYPE.add("Unarchive");
    }

    public void setType(String type)
    {
        type = type.toLowerCase();
        this.type = TYPE.getLowercase(type);
    }

    public String getTypeStr()
    {
        return TYPE.get(type, 0);
    }

    private int type;
    private String author;
    private String text;
    private DateTime date;

    public GeocacheLog(String type, String author, String text, String date)
    {
        setType(type);
        setDate(date);

        if (author == null || text == null)
            throw new NullPointerException();

        this.author = author;
        this.text = text;
    }

    public void setDate(String date)
    {
        // <groundspeak:date>2015-08-16T19:00:00Z</groundspeak:date>
        // ISO 8601
        this.date = new DateTime(date, DateTimeZone.UTC);
    }

    public String getAuthor()
    {
        return author;
    }

    public boolean isAuthor(String name)
    {
        return author.toLowerCase().equals(name.toLowerCase());
    }

    public boolean isFoundLog()
    {
        String typeStr = getTypeStr();
        if (typeStr.equals("Found it") || typeStr.equals("Attended") ||
            typeStr.equals("Webcam Photo Taken"))
            return true;

        return false;
    }

    public String getText()
    {
        return text;
    }

    public DateTime getDate()
    {
        return date;
    }

    public String getDateStr()
    {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
        return fmt.print(date);
    }

    public String getDateStrISO8601()
    {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return fmt.print(date);
    }

    public static String getDateStrISO8601NoTime(DateTime date)
    {
        DateTimeFormatter fmt = ISODateTimeFormat.date();
        ;
        return fmt.print(date);
    }

    public String getDateStrISO8601NoTime()
    {
        return getDateStrISO8601NoTime(date);
    }

    public boolean equals(GeocacheLog log)
    {
        return type == log.type && date.equals(log.date) && author.equals(log.author) &&
            text.equals(log.text);
    }
}
