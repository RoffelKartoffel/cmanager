package cmanager;

import java.util.ArrayList;

public class UndoAction
{
    private ArrayList<Geocache> state;

    public UndoAction(ArrayList<Geocache> list)
    {
        state = new ArrayList<>(list);
    }

    public ArrayList<Geocache> getState()
    {
        return state;
    }
}
