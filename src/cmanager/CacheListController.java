package cmanager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;


public class CacheListController {
	
	private static ArrayList<CacheListController> controllerList = new ArrayList<CacheListController>();
	
	public static CacheListController newCLC(JDesktopPane desktop, JMenu mnWindows, String path) throws Throwable
	{
		CacheListController clc = new CacheListController(desktop, mnWindows, path);
		controllerList.add(clc);
		return clc;
	}
	
	public static void remove(CacheListController clc){
		controllerList.remove(clc);
	}
	
	private static CacheListController getCacheListController(JInternalFrame jif){
		for(CacheListController clc : controllerList )
			if( clc.view == jif )
				return clc;
		return null;
	}
	
	public static CacheListController getTopViewCacheController(JDesktopPane desktop)
	{
		JInternalFrame jif = desktop.getAllFrames()[0];
		return CacheListController.getCacheListController(jif);
	}
	
	public static CacheListView getTopView(JDesktopPane desktop)
	{
		return (CacheListView)desktop.getAllFrames()[0];
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
	private CacheListController(){
	}
	

	
	public CacheListController(final JDesktopPane desktop, final JMenu mnWindows, String path) throws Throwable
	{
		if(path != null)
			model.load(path);
		
		// set up the view
		view = new CacheListView(this);
//		view.setMaximizable(true);
		view.setMinimumSize(new Dimension(100, 100));
		view.setClosable(true);
		view.setResizable(true);
		view.setVisible(true);
//		view.setIconifiable(false);
		
		if( path == null)
			modifiedAndUnsafed = true;
		else
		{
			modifiedAndUnsafed = false;
			this.path = Paths.get(path);	
		}
		
		view.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				CacheListController.remove(THIS);
				mnWindows.remove(mnWindow);
			}
		});
		
		desktop.add(view);
		try {
			view.setMaximum(true);
		} 
		catch (PropertyVetoException e) {
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
	
//	public void setPath(String path)
//	{
//		this.path = Paths.get(path);
//		updateTitleAndCount();
//	}
	
	public CacheListModel getModel() {
		return model;
	}
	
	public CacheListView getView(){
		return view;
	}
	
	
	
	private String getName(){
		if( path != null )
			return path.getFileName().toString();
		return null;
	}
	
	private void updateTitleAndCount()
	{
		String title = getName();
		if( title == null )
			title = "unnamed";
		if( modifiedAndUnsafed )
			title += "*";
		view.setTitle(title);
		mnWindow.setText(title);
		
		int count = model.size();
		view.setCacheCount( count );
		
		for(Geocache g : model.getList() )
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
	
	public void filtersUpdated(){
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
		model.store(pathToGPX.toString(), getName());
		
		modifiedAndUnsafed = false;
		updateTitleAndCount();
	}
	
	public Path getPath(){
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
		if( removeList.size() > 0 )
		{
			model.removeCaches( removeList );
			cachesAddedOrRemoved();
		}
	}
	
	private static ArrayList<Geocache> copyList = null;
	public void copySelected()
	{
		ArrayList<Geocache> selected = view.getSelectedCaches();
		copyList = new ArrayList<>(selected.size());
		for(Geocache g : selected )
			copyList.add( ObjectHelper.copy(g) );
	}
	
	public void cutSelected()
	{
		copyList = view.getSelectedCaches();
		if( copyList != null && copyList.size() > 0 )
		{
			model.removeCaches(copyList);
			cachesAddedOrRemoved();
		}	
	}
	
	public void pasteSelected()
	{
		if( copyList != null && copyList.size() > 0 )
		{
			model.addCaches( copyList );
			cachesAddedOrRemoved();
		}	
	}
	
//////////////////////////////////////
//////////////////////////////////////
/////// Table model
//////////////////////////////////////
//////////////////////////////////////
	
	public CLCTableModel getTableModel(){
		return new CLCTableModel();
	}
	
	public class CLCTableModel extends AbstractTableModel {

		 /**
		 * 
		 */
		private static final long serialVersionUID = -6159661237715863643L;

		public String getColumnName(int col) {
			 switch (col)
			 {
			 case 0:
				 return "Code";
			 case 1:
				 return "Name";
			 case 2:
				 return "Type";
			 case 3:
				 return "Difficulty";
			 case 4:
				 return "Terrain";
			 case 5:
				 return "Lat";
			 case 6:
				 return "Lon";
			 case 7:
				 return "Owner";
			 }
			 
		     return null;
	    }
		
		@Override
		public int getColumnCount() {
			return 8;
		}

		@Override
		public int getRowCount() {
			return model.size();
		}

		public Geocache getObject(int row){
			return model.get(row);
		}
		
		@Override
		public Object getValueAt(int arg0, int arg1) {
			Geocache g = getObject(arg0);
			
			switch( arg1 )
			{
			case 0:
				return g.getCode();
			case 1:
				return g.getName();
			case 2:
				return g.getTypeAsNice();
			case 3:
				return g.getDifficulty().toString();
			case 4:
				return g.getTerrain().toString();
			case 5:
				return g.getCoordinate().getLat().toString();
			case 6:
				return g.getCoordinate().getLon().toString();
			case 7:
				String owner = g.getOwner();
				return owner != null ? owner : "";
				
			default:
				return null;
			}
		}
		
	}

}
