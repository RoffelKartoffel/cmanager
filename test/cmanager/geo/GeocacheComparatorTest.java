package cmanager.geo;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cmanager.geo.Coordinate.UnparsableException;

public class GeocacheComparatorTest
{

    private ArrayList<Geocache[]> matching = new ArrayList<Geocache[]>();
    private ArrayList<Geocache[]> not_matching = new ArrayList<Geocache[]>();


    private void addGood(String code1, String name1, String coords1, Double d1, Double t1,
                         String type1, String owner1, String container1, Boolean archived1,
                         Boolean available1, String code2, String name2, String coords2, Double d2,
                         Double t2, String type2, String owner2, String container2,
                         Boolean archived2, Boolean available2)
    {
        add(matching, code1, name1, coords1, d1, t1, type1, owner1, container1, archived1,
            available1, code2, name2, coords2, d2, t2, type2, owner2, container2, archived2,
            available2, null);
    }

    private void addGood(String code1, String name1, String coords1, Double d1, Double t1,
                         String type1, String owner1, String container1, Boolean archived1,
                         Boolean available1, String code2, String name2, String coords2, Double d2,
                         Double t2, String type2, String owner2, String container2,
                         Boolean archived2, Boolean available2, String code_gc)
    {
        add(matching, code1, name1, coords1, d1, t1, type1, owner1, container1, archived1,
            available1, code2, name2, coords2, d2, t2, type2, owner2, container2, archived2,
            available2, code_gc);
    }

    private void addBad(String code1, String name1, String coords1, Double d1, Double t1,
                        String type1, String owner1, String container1, Boolean archived1,
                        Boolean available1, String code2, String name2, String coords2, Double d2,
                        Double t2, String type2, String owner2, String container2,
                        Boolean archived2, Boolean available2)
    {
        add(not_matching, code1, name1, coords1, d1, t1, type1, owner1, container1, archived1,
            available1, code2, name2, coords2, d2, t2, type2, owner2, container2, archived2,
            available2, null);
    }

    private void addBad(String code1, String name1, String coords1, Double d1, Double t1,
                        String type1, String owner1, String container1, Boolean archived1,
                        Boolean available1, String code2, String name2, String coords2, Double d2,
                        Double t2, String type2, String owner2, String container2,
                        Boolean archived2, Boolean available2, String code_gc)
    {
        add(not_matching, code1, name1, coords1, d1, t1, type1, owner1, container1, archived1,
            available1, code2, name2, coords2, d2, t2, type2, owner2, container2, archived2,
            available2, code_gc);
    }


    private void add(ArrayList<Geocache[]> list, String code1, String name1, String coords1,
                     Double d1, Double t1, String type1, String owner1, String container1,
                     Boolean archived1, Boolean available1, String code2, String name2,
                     String coords2, Double d2, Double t2, String type2, String owner2,
                     String container2, Boolean archived2, Boolean available2, String code_gc)
    {
        try
        {
            Geocache g1 = new Geocache(code1, name1, new Coordinate(coords1), d1, t1, type1);
            g1.setOwner(owner1);
            g1.setContainer(container1);
            g1.setArchived(archived1);
            g1.setAvailable(available1);

            Geocache g2 = new Geocache(code2, name2, new Coordinate(coords2), d2, t2, type2);
            g2.setOwner(owner2);
            g2.setContainer(container2);
            g2.setArchived(archived2);
            g2.setAvailable(available2);
            g2.setCodeGC(code_gc);

            list.add(new Geocache[] {g1, g2});
        }
        catch (NullPointerException | UnparsableException e1)
        {
            e1.printStackTrace();
            fail("Unable to initialize list.");
        }
    }


    @Test
    public void testMatching()
    {
        // Real life samples

        addGood("GCC681", "Moorleiche", "N 53° 06.438' E 008° 07.767'", 2.0, 3.0, "Multi",
                "digitali", "regular", false,
                true, //
                "OC0BEF", "Moorleiche", "N 53° 06.438' E 008° 07.767'", 2.0, 3.0, "Multi",
                "digitali", "Regular", false, true);

        addGood("GC1F9JP", "TB-Hotel Nr. 333", "N 53° 08.245' E 008° 16.700'", 1.0, 2.0, "Tradi",
                "TravelMad", "regular", false, true, //
                "OC6544", "TB-Hotel Nr. 333", "N 53° 08.245' E 008° 16.700'", 1.0, 1.5, "Tradi",
                "TravelMad", "Regular", false, true);

        addGood("GC3314B", "Zeche Gottessegen - III - Stollen", "N 51° 26.334 E 007° 27.874", 2.0,
                2.0, "Tradi", "Wir_4", "micro", true, false, //
                "OCD346", "Zeche Gottessegen - III - Stollen", "N 51° 26.334' E 007° 27.874'", 2.0,
                2.0, "Tradi", "Wir_4", "Micro", true, false);

        addGood("GC3314V", "Zeche Gottessegen - IV - Förderturm", "N 51° 26.334 E 007° 28.077", 2.5,
                2.0, "Tradi", "Wir_4", "small", true, false, //
                "OCD347", "Zeche Gottessegen - IV - Förderturm", "N 51° 26.334' E 007° 28.077'",
                2.0, 2.0, "Tradi", "Wir_4", "Small", true, false);

        addGood("GC4675C", "Klaus Autowerkstatt (Kinder-Cache)", "N 51° 18.767' E 007° 26.629'",
                1.5, 2.0, "Tradi", "Atomaffe", "small", true, false, //
                "OCF83C", "Klaus Autowerkstatt (Kinder-Cache)", "N 51° 18.767' E 007° 26.629'", 1.5,
                2.0, "Tradi", "Atomaffe", "Regular", true, false);

        addGood("GC39105", "Ein “Schatz” aus der Antike", "N 50° 56.410' E 006° 49.730'", 2.0, 1.0,
                "Unknown Cache", "Rheingeister", "micro", false, true, //
                "OCD85A", "Ein “Schatz” aus der Antike", "N 50° 56.410' E 006° 49.710'", 2.0, 1.0,
                "Unknown Cache", "Rheingeister", "Micro", false, true);

        addGood("GC58YWX", "Ein Nano an der Kreuzung klebt...", "N 51° 24.661 E 007° 50.056", 3.0,
                1.5, "Tradi", "Keks579", "micro", true, false, //
                "OC1120F", "Ein Nano an der Kreuzung klebt...", "N 51° 24.684' E 007° 50.035'", 3.0,
                1.5, "Tradi", "Keks579_Unidos", "Micro", true, false);

        addGood("GC53AX3", "Piep Piep Piep", "N 51° 22.067 E 007° 29.565", 1.5, 1.5, "Tradi",
                "geyerwally", "micro", false, true, //
                "OC111B6", "Piep Piep Piep", "N 51° 22.067' E 007° 29.565'", 1.5, 1.5, "Tradi",
                "geyerwally", "Micro", false, true);

        // voliatile start!

        //        addGood("GC1P7V2", "Donald´s Badewanne", "N 51° 20.593 E 007°
        //        31.486",
        //                3.0, 1.5, "Unknown Cache", "Jerry_the_Dog", "small",
        //                true,
        //                false, //
        //                "OC8F33", "Donald´s Badewanne", "N 51° 20.907' E 007°
        //                31.788'",
        //                4.0, 1.5, "Unknown Cache", "Jerry_the_Dog", "Small",
        //                true,
        //                false);

        addGood("GC1P7V2", "Donald´s Badewanne", "N 51° 20.593 E 007° 31.486", 3.0, 1.5,
                "Unknown Cache", "Jerry_the_Dog", "small", true,
                false, //
                "OC8F33", "Donald´s Badewanne", "N 51° 20.907' E 007° 31.788'", 4.0, 1.5,
                "Unknown Cache", "Jerry_the_Dog", "Small", true, false, "gc1p7v2");


        // Interesting tuples

        //        	OC1158E / GC4VRCT Händel oder Bruckner
        //        	OC110D9 / GC58CJT Tu was Gutes...
        //        	OC11577 / GC598KJ SPY
        //        	OC948B / GC1ZRDV Reiglersbachsee
        //        	OC174D / GCTBWH Crailsheim per Auto
        //        	OC1467 / GCQJZP Helgoland Catamaran Quicky
        //        	OC4551 / GC16Q2H Motte Keyenberg


        // Template


        //        addGood("", "", "",
        //                0.0, 0.0, "", "", "", null, null, //
        //                "", "", "",
        //                0.0, 0.0, "", "", "", null, null);


        // Unmatched edge cases

        //        addGood("GCJWEN", "Die Bärenhöhle", "N 51° 47.700' E 006°
        //        06.914'", 3.0,
        //                4.0, "Tradi", "geoBONE", "micro", false, true, //
        //                "OC001B", "Die Baerenhoehle", "N 51° 47.700' E 006°
        //                06.914'",
        //                3.0, 4.5, "Tradi", "geoBONE", "Micro", true, false);

        //        addGood("GC5N4RW", "Schützenplatz ? !", "N 52° 30.020 E 009°
        //        51.363",
        //                3.0, 1.5, "Tradi", "TommyKFB", "micro", false, true,
        //                //
        //                "OC11BB9", "Schützenplatz ?!", "N 52° 30.020' E 009°
        //                51.365'",
        //                1.0, 1.0, "Tradi", "TommyKFB", "Micro", true, false);

        //        addGood("GC33W4R", "Kleine Prinzessin in der Stemke", "N 51°
        //        21.900' E 007° 22.650'",
        //                1.5, 3.0, "Unknown Cache", "Quickcreek",
        //                "regular",true, false, //
        //                "OCE5D1", "Kleine Prinzessin in der Stemke", "N 51°
        //                21.841' E 007° 22.601'",
        //                1.0, 2.5, "Unknown Cache", "Quickcreek",
        //                "Regular",false, true);


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
                    fail("Unexpected match: " + gc.toString() + " " + oc.toString());
                }
            }
        }
    }

    @Test
    public void testNotMatching()
    {
        // Real life samples

        addBad("GC6321G", "Konkurrenz belebt das Geschäft -GC-", "N 53° 35.565 E 009° 55.200", 1.5,
               3.5, "Multi", "rbx270", "regular", false, true, //
               "OC12599", "Konkurrenz belebt das Geschäft -OC-", "N 53° 35.569' E 009° 55.207'",
               1.5, 3.5, "Multi", "rbx270", "Regular", false, true);


        // generics

        addBad("GC", "cache", "N 1° 11.111 E 2° 22.222", 1.0, 3.0, "Multi", "author", "regular",
               false, true, //
               "OC", "cache", "N 1° 11.111 E 2° 22.222", 2.0, 4.0, "Multi", "author", "Regular",
               false, true);

        addBad("GC", "cache", "N 1° 22.222 E 2° 22.222", 1.0, 1.0, "Tradi", "author", "regular",
               false, true, //
               "OC", "cache", "N 1° 11.111 E 2° 22.222", 1.0, 1.0, "Multi", "author", "Regular",
               false, true);

        for (Geocache[] tuple : not_matching)
        {
            Geocache gc = tuple[0];
            Geocache oc = tuple[1];
            if (GeocacheComparator.similar(gc, oc))
            {
                fail("Match: " + gc.toString() + " " + oc.toString());
            }
        }
    }
}
