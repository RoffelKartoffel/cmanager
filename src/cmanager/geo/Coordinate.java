package cmanager.geo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinate implements Serializable
{
    private static final long serialVersionUID = -2526305963690482539L;

    public class UnparsableException extends Exception
    {
        private static final long serialVersionUID = -3199033370349089535L;
    }


    private double lat, lon;

    public Coordinate(String lat, String lon)
    {
        this(new Double(lat), new Double(lon));
    }

    public Coordinate(String input) throws UnparsableException
    {
        Pattern pattern = Pattern.compile(
            "N\\s*(\\d+)[\\s|°]\\s*((?:\\d+\\.\\d+)|(?:\\d+))'*\\s*E\\s*(\\d+)[\\s|°]\\s*((?:\\d+\\.\\d+)|(?:\\d+))'*\\s*");
        final Matcher matcher = pattern.matcher(input);

        if (!matcher.find())
        {
            throw new UnparsableException();
        }

        lat = new Double(matcher.group(1)) + new Double(matcher.group(2)) / 60;
        lon = new Double(matcher.group(3)) + new Double(matcher.group(4)) / 60;

        if (matcher.find())
        {
            throw new UnparsableException();
        }
    }

    public Coordinate(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat()
    {
        return lat;
    }

    public Double getLon()
    {
        return lon;
    }

    public boolean equals(Coordinate c)
    {
        return lat == c.getLat() && lon == c.getLon();
    }

    public String toString()
    {
        return new Double(lat).toString() + ", " + new Double(lon).toString();
    }

    public double distanceHaversine(Coordinate other)
    {
        // "haversine" distance
        // http://www.movable-type.co.uk/scripts/latlong.html

        final double radianFactor = 2 * Math.PI / 360;

        final double φ1 = lat * radianFactor;
        final double φ2 = other.lat * radianFactor;
        final double Δφ = (other.lat - lat) * radianFactor;
        final double Δλ = (other.lon - lon) * radianFactor;

        final double a =
            Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
            Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        final double R = 6371e3; // metres
        return R * c;
    }

    public double distanceHaversineRounded(Coordinate c2)
    {
        return round(distanceHaversine(c2), 3);
    }

    public static double round(double value, int places)
    {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
