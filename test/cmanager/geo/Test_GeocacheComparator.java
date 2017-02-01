package cmanager.geo;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cmanager.geo.Coordinate.UnparsableException;

public class Test_GeocacheComparator
{

    private ArrayList<Geocache[]> matching = new ArrayList<Geocache[]>();

    private void addGood(String code1, String name1, String coords1, Double d1,
                         Double t1, String type1, String owner1,
                         String container1, boolean archived1,
                         boolean available1, String code2, String name2,
                         String coords2, Double d2, Double t2, String type2,
                         String owner2, String container2, boolean archived2,
                         boolean available2)
    {
        try
        {
            Geocache g1 = new Geocache(code1, name1, new Coordinate(coords1),
                                       d1, t1, type1);
            g1.setOwner(owner1);
            g1.setContainer(container1);
            g1.setArchived(archived1);
            g1.setAvailable(available1);

            Geocache g2 = new Geocache(code2, name2, new Coordinate(coords2),
                                       d2, t2, type2);
            g2.setOwner(owner2);
            g2.setContainer(container2);
            g2.setArchived(archived2);
            g2.setAvailable(available2);

            matching.add(new Geocache[] {g1, g2});
        }
        catch (NullPointerException | UnparsableException e1)
        {
            e1.printStackTrace();
            fail("Unable to initialize list.");
        }
    }


    @Test public void testMatching()
    {
        // Real life samples

        addGood("GCC681", "Moorleiche", "N 53° 06.438' E 008° 07.767'", 2.0,
                3.0, "Multi", "digitali", "regular", false, true, //
                "OC0BEF", "Moorleiche", "N 53° 06.438' E 008° 07.767'", 2.0,
                3.0, "Multi", "digitali", "Regular", false, true);

        addGood("GC1F9JP", "TB-Hotel Nr. 333", "N 53° 08.245' E 008° 16.700'",
                1.0, 2.0, "Tradi", "TravelMad", "regular", false, true, //
                "OC6544", "TB-Hotel Nr. 333", "N 53° 08.245' E 008° 16.700'",
                1.0, 1.5, "Tradi", "TravelMad", "Regular", false, true);

        //        addGood("GCJWEN", "Die Bärenhöhle", "N 51° 47.700' E 006°
        //        06.914'", 3.0,
        //                4.0, "Tradi", "geoBONE", "micro", false, true, //
        //                "OC001B", "Die Baerenhoehle", "N 51° 47.700' E 006°
        //                06.914'",
        //                3.0, 4.5, "Tradi", "geoBONE", "Micro", true, false);

        addGood("GC3314B", "Zeche Gottessegen - III - Stollen",
                "N 51° 26.334 E 007° 27.874", 2.0, 2.0, "Tradi", "Wir_4",
                "micro", true, false, //
                "OCD346", "Zeche Gottessegen - III - Stollen",
                "N 51° 26.334' E 007° 27.874'", 2.0, 2.0, "Tradi", "Wir_4",
                "Micro", true, false);

        addGood("GC3314V", "Zeche Gottessegen - IV - Förderturm",
                "N 51° 26.334 E 007° 28.077", 2.5, 2.0, "Tradi", "Wir_4",
                "small", true, false, //
                "OCD347", "Zeche Gottessegen - IV - Förderturm",
                "N 51° 26.334' E 007° 28.077'", 2.0, 2.0, "Tradi", "Wir_4",
                "Small", true, false);

        addGood("GC53AX3", "Piep Piep Piep", "N 51° 22.067 E 007° 29.565", 1.5,
                1.5, "Tradi", "geyerwally", "micro", false, true, //
                "OC111B6", "Piep Piep Piep", "N 51° 22.067' E 007° 29.565'",
                1.5, 1.5, "Tradi", "geyerwally", "Micro", false, true);

        for (Geocache[] tuple : matching)
        {
            Geocache gc = tuple[0];
            Geocache oc = tuple[1];
            if (!GeocacheComparator.similar(gc, oc))
            {
                fail("No match: " + gc.toString() + " " + oc.toString());
            }
        }

        for (Geocache[] tuple1 : matching)
        {
            for (Geocache[] tuple2 : matching)
            {
                if (tuple1 == tuple2)
                {
                    continue;
                }

                Geocache gc = tuple1[0];
                Geocache oc = tuple2[1];
                if (GeocacheComparator.similar(gc, oc))
                {
                    fail("Unexpected match: " + gc.toString() + " " +
                         oc.toString());
                }
            }
        }
    }
}
