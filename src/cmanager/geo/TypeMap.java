package cmanager.geo;

import java.util.ArrayList;

import cmanager.gui.ExceptionPanel;

public class TypeMap
{

    private ArrayList<ArrayList<String>> map = new ArrayList<>();

    public void add(String... key)
    {
        ArrayList<String> list = new ArrayList<>(key.length);
        for (String k : key)
            list.add(k);
        map.add(list);
    }

    public Integer getLowercase(String key)
    {
        key = key.toLowerCase();

        for (ArrayList<String> list : map)
            for (String s : list)
                if (s != null && s.toLowerCase().equals(key))
                    return map.indexOf(list);

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }


    public Integer get(String key)
    {
        for (ArrayList<String> list : map)
            for (String s : list)
                if (s != null && s.equals(key))
                    return map.indexOf(list);

        ExceptionPanel.display(" ~~ unknown key: " + key + " ~~ ");
        return null;
    }

    public String get(int i, int j)
    {
        ArrayList<String> list = map.get(i);
        return list.get(j);
    }
}
