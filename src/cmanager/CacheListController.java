package cmanager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.TableColumn;

import cmanager.CacheListModel.CLMTableModel;
import cmanager.geo.Geocache;
import cmanager.geo.Location;
import cmanager.gui.CacheListView;
import cmanager.gui.ExceptionPanel;
import cmanager.settings.Settings;
import cmanager.util.ObjectHelper;


public class CacheListController
{

    private static ArrayList<CacheListController> controllerList =
        new ArrayList<CacheListController>();

    public static CacheListController newCLC(JDesktopPane desktop, JMenu mnWindows,
                                             Location relativeLocation, String path,
                                             CacheListView.RunLocationDialogI ldi) throws Throwable
    {
        CacheListController clc =
            new CacheListController(desktop, mnWindows, relativeLocation, path, ldi);
        controllerList.add(clc);
        return clc;
    }

    public static void remove(CacheListController clc)
    {
        controllerList.remove(clc);
    }

    private static CacheListController getCacheListController(JInternalFrame jif)
    {
        for (CacheListController clc : controllerList)
            if (clc.view == jif)
                return clc;
        return null;
    }

    public static CacheListController getTopViewCacheController(JDesktopPane desktop)
    {
        if (desktop.getAllFrames().length == 0)
            return null;

        JInternalFrame jif = desktop.getAllFrames()[0];
        return CacheListController.getCacheListController(jif);
    }

    public static CacheListView getTopView(JDesktopPane desktop)
    {
        return (CacheListView)desktop.getAllFrames()[0];
    }

    public static void setAllRelativeLocations(Location rl)
    {
        for (CacheListController clc : controllerList)
            clc.setRelativeLocation(rl);
    }

    public static void storePersistanceInfo(JDesktopPane desktop) throws IOException
    {
        CacheListController top = getTopViewCacheController(desktop);

        ArrayList<PersistenceInfo> pi = new ArrayList<>();
        for (CacheListController clc : controllerList)
        {
            if (clc == top)
                continue;
            pi.add(clc.getPersistenceO());
        }
        if (top != null)
            pi.add(top.getPersistenceO());

        Settings.setSerialized(Settings.Key.CLC_LIST, pi);
    }

    public static void reopenPersitantCLCs(JDesktopPane desktop, JMenu mnWindows,
                                           Location relativeLocation,
                                           CacheListView.RunLocationDialogI ldi)
    {
        ArrayList<PersistenceInfo> pi;
        try
        {
            pi = Settings.getSerialized(Settings.Key.CLC_LIST);
            if (pi == null)
                return;
        }
        catch (Throwable t)
        {
            ExceptionPanel.showErrorDialog(desktop, t);
            return;
        }

        for (PersistenceInfo aPI : pi)
        {
            try
            {
                if (new File(aPI.getPath()).exists())
                    newCLC(desktop, mnWindows, relativeLocation, aPI.getPath(), ldi);
            }
            catch (Throwable t)
            {
                ExceptionPanel.showErrorDialog(desktop, t);
            }
        }
    }

    //////////////////////////////////////
    //////////////////////////////////////
    /////// Member functions
    //////////////////////////////////////
    //////////////////////////////////////

    private CacheListModel model = new CacheListModel();
    private CacheListView view = null;
    private Path path = null;
    private CacheListController THIS = this;
    private Boolean modifiedAndUnsafed = null;
    private JMenuItem mnWindow = null;


    @SuppressWarnings("unused")
    private CacheListController()
    {
    }


    public CacheListController(final JDesktopPane desktop, final JMenu mnWindows,
                               Location relativeLocation, String path,
                               CacheListView.RunLocationDialogI ldi) throws Throwable
    {
        if (path != null)
            model.load(path);

        setRelativeLocation(relativeLocation);

        // set up the view
        view = new CacheListView(this, ldi);
        //		view.setMaximizable(true);
        view.setMinimumSize(new Dimension(100, 100));
        view.setClosable(true);
        view.setResizable(true);
        view.setVisible(true);
        //		view.setIconifiable(false);

        if (path == null)
            modifiedAndUnsafed = true;
        else
        {
            modifiedAndUnsafed = false;
            this.path = Paths.get(path);
        }

        view.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e)
            {
                CacheListController.remove(THIS);
                mnWindows.remove(mnWindow);
            }
        });

        desktop.add(view);
        try
        {
            view.setMaximum(true);
        }
        catch (PropertyVetoException e)
        {
        }

        JTable table = view.getTable();
        setWidth(table, 1, 150);
        setWidth(table, 2, 60);
        setWidth(table, 3, 60);
        setWidth(table, 4, 60);
        setWidth(table, 7, 150);

        this.mnWindow = new JMenuItem("");
        mnWindows.add(this.mnWindow);
        this.mnWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                desktop.moveToFront(view);
            }
        });

        updateOverallOptik();
    }

    public void addFromFile(String path) throws Throwable
    {
        model.load(path);
        cachesAddedOrRemoved();
    }

    public CacheListModel getModel()
    {
        return model;
    }

    public CacheListView getView()
    {
        return view;
    }

    public void setRelativeLocation(Location rl)
    {
        model.setRelativeLocation(rl);
    }


    private String getName()
    {
        if (path != null)
            return path.getFileName().toString();
        return null;
    }

    private void updateTitleAndCount()
    {
        String title = getName();
        if (title == null)
            title = "unnamed";
        if (modifiedAndUnsafed)
            title += "*";
        view.setTitle(title);
        mnWindow.setText(title);

        int count = model.size();
        view.setCacheCount(count);

        for (Geocache g : model.getList())
            count += g.getWaypoints().size();
        view.setWaypointCount(count, model.getOrphans().size());
    }

    private void cachesAddedOrRemoved()
    {
        modifiedAndUnsafed = true;
        updateOverallOptik();
    }

    private void updateOverallOptik()
    {
        updateTitleAndCount();
        view.resetView();
        view.updateCachePanelToSelection();
        view.updateMapMarkers();
    }

    public void addFilter(CacheListFilterModel filter)
    {
        model.addFilter(filter);
        view.addFilter(filter);
        updateOverallOptik();
    }

    public void removeFilter(CacheListFilterModel filter)
    {
        model.removeFilter(filter);
        updateOverallOptik();
    }

    public void filtersUpdated()
    {
        updateOverallOptik();
    }

    public void removeCachesNotInFilter()
    {
        model.removeCachesNotInFilter();
        cachesAddedOrRemoved();
    }


    public void store(Path pathToGPX) throws Throwable
    {
        this.path = pathToGPX;
        model.store(getName(), pathToGPX.toString());

        modifiedAndUnsafed = false;
        updateTitleAndCount();
    }

    public Path getPath()
    {
        return path;
    }


    public static void setWidth(JTable table, int index, int size)
    {
        TableColumn column = table.getColumnModel().getColumn(index);
        column.setPreferredWidth(size);
    }


    public void removeSelectedCaches()
    {
        ArrayList<Geocache> removeList = view.getSelectedCaches();
        if (removeList.size() > 0)
        {
            model.removeCaches(removeList);
            cachesAddedOrRemoved();
        }
    }

    private static ArrayList<Geocache> copyList = new ArrayList<>();
    public void copySelected()
    {
        ArrayList<Geocache> selected = view.getSelectedCaches();
        copyList.clear();
        copyList.ensureCapacity(selected.size());
        for (Geocache g : selected)
            copyList.add(ObjectHelper.copy(g));
    }

    public void cutSelected()
    {
        ArrayList<Geocache> selected = view.getSelectedCaches();
        copyList.clear();
        copyList.ensureCapacity(selected.size());
        copyList.addAll(selected);
        if (!copyList.isEmpty())
        {
            model.removeCaches(copyList);
            cachesAddedOrRemoved();
        }
    }

    public void pasteSelected()
    {
        if (!copyList.isEmpty())
        {
            model.addCaches(copyList);
            cachesAddedOrRemoved();
        }
    }

    public CLMTableModel getTableModel()
    {
        return model.getTableModel();
    }

    public void replayLastUndoAction()
    {
        model.replayLastUndoAction();
        modifiedAndUnsafed = true;
        cachesAddedOrRemoved();
    }

    public int getUndoActionCount()
    {
        return model.getUndoActionCount();
    }

    private PersistenceInfo getPersistenceO()
    {
        return new PersistenceInfo(path.toString());
    }

    //////////////////////////////////////
    //////////////////////////////////////
    /////// Persistence
    //////////////////////////////////////
    //////////////////////////////////////

    public static class PersistenceInfo implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private String path;

        public PersistenceInfo(String path)
        {
            this.path = path;
        }

        public String getPath()
        {
            return path;
        }
    }
}
