package cmanager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

import javax.swing.JSeparator;
import javax.swing.KeyStroke;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 6384767256902991990L;

	
	private JFrame THIS = this;
	private JPanel contentPane;
	private JDesktopPane desktopPane;
	private JMenu mnWindows;


	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(750, 550));
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
		mnWindows = new JMenu("Windows");
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				openFile(true);
			}
		});
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try {
					CacheListController.newCLC( desktopPane, mnWindows, null );
				} catch (Throwable e1) {
				}	
			}
		});
		mnMenu.add(mntmNew);
		mnMenu.add(mntmOpen);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				THIS.dispose();
			}
		});
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				SettingsDialog sw = new SettingsDialog();
				sw.setModalityType(ModalityType.APPLICATION_MODAL);
				sw.setLocationRelativeTo(THIS);
				sw.setVisible(true);
			}
		});
		
		final JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				saveFile(false);
			}
		});
		mntmSave.setEnabled(false);
		mnMenu.add(mntmSave);
		
		final JMenuItem mntmSaveAs = new JMenuItem("Save As");
//		mntmSaveAs.setAccelerator(KeyStroke.getKeyStroke('S', 
//				Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask() ));
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				saveFile(true);
			}
		});
		mntmSaveAs.setEnabled(false);
		mnMenu.add(mntmSaveAs);
		
		JSeparator separator = new JSeparator();
		mnMenu.add(separator);
		mnMenu.add(mntmSettings);
		
		JSeparator separator_1 = new JSeparator();
		mnMenu.add(separator_1);
		mnMenu.add(mntmExit);
		
		final JMenu mnList = new JMenu("List");
		mnList.setEnabled(false);
		menuBar.add(mnList);
		
		JMenuItem mntmFindSimilarOc = new JMenuItem("Find on OC");
		mntmFindSimilarOc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				findOnOC(null);
			}
		});
		mnList.add(mntmFindSimilarOc);
		
		JMenuItem mntmFind = new JMenuItem("Find on OC (exclude found)");
		mntmFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try {
					String uuid = null;
					try{
						OCUser user = OCUser.getOCUser();
						uuid = OKAPI.getUUID(user);
					}catch(Exception ex){
						JOptionPane.showMessageDialog(null,"Testing the OKAPI token faild. Check your settings!","Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					findOnOC(uuid);
				}
				catch(Throwable ex){
					ExceptionPanel.showErrorDialog(ex);
				}
			}
		});
		mnList.add(mntmFind);
		
		JSeparator separator_2 = new JSeparator();
		mnList.add(separator_2);
		
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				CacheListController.getTopViewCacheController(desktopPane).copySelected();
			}
		});
		
		JMenuItem mntmSelectAll = new JMenuItem("Select All / none");
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				CacheListController.getTopViewCacheController(desktopPane).getView().tableSelectAllNone();
			}
		});
		mnList.add(mntmSelectAll);
		
		JSeparator separator_6 = new JSeparator();
		mnList.add(separator_6);
		mnList.add(mntmCopy);
		
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				CacheListController.getTopViewCacheController(desktopPane).pasteSelected();
			}
		});
		
		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				CacheListController.getTopViewCacheController(desktopPane).cutSelected();
			}
		});
		mnList.add(mntmCut);
		mnList.add(mntmPaste);
		
		final JMenuItem mntmDeleteSelectedCaches = new JMenuItem("Delete");
		mntmDeleteSelectedCaches.setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		mntmDeleteSelectedCaches.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				CacheListController.getTopViewCacheController(desktopPane).removeSelectedCaches();
			}
		});
		mnList.add(mntmDeleteSelectedCaches);
		
		JSeparator separator_3 = new JSeparator();
		mnList.add(separator_3);
		
		JMenu mnFilter = new JMenu("Filter");
		mnList.add(mnFilter);
		
		JMenu mntmAddFilter = new JMenu("Add");
		mnFilter.add(mntmAddFilter);
		
		JMenuItem mntmTerrainFilter = new JMenuItem("Terrain");
		mntmTerrainFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				CacheListController.getTopViewCacheController(desktopPane).addFilter(
						new CacheListFilterTerrain() );
			}
		});
		mntmAddFilter.add(mntmTerrainFilter);
		
		JMenuItem mntmDifficultyFilter = new JMenuItem("Difficulty");
		mntmDifficultyFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				CacheListController.getTopViewCacheController(desktopPane).addFilter(
						new CacheListFilterDifficulty() );
			}
		});
		mntmAddFilter.add(mntmDifficultyFilter);
		
		JMenuItem mntmNotFoundBy = new JMenuItem("Not Found By");
		mntmNotFoundBy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				CacheListController.getTopViewCacheController(desktopPane).addFilter(
						new CacheListFilterNotFoundBy() );
			}
		});
		mntmAddFilter.add(mntmNotFoundBy);
		
		JSeparator separator_5 = new JSeparator();
		mnFilter.add(separator_5);
		
		JMenuItem mntmDeleteCachesNot = new JMenuItem("Delete Caches NOT in Filter");
		mntmDeleteCachesNot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				CacheListController.getTopViewCacheController(desktopPane).removeCachesNotInFilter();
			}
		});
		mnFilter.add(mntmDeleteCachesNot);

		
		JSeparator separator_4 = new JSeparator();
		mnList.add(separator_4);
		
		JMenuItem mntmAddFromFile = new JMenuItem("Add from File");
		mntmAddFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				openFile(false);
			}
		});
		mnList.add(mntmAddFromFile);
		
		
		mnWindows.setEnabled(false);
		menuBar.add(mnWindows);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		desktopPane = new JDesktopPane();
		contentPane.add(desktopPane, BorderLayout.CENTER);
		desktopPane.addContainerListener(new ContainerListener() {
			public void componentRemoved(ContainerEvent e) 
			{
				updateVisibility( desktopPane.getAllFrames().length != 0 );
			}
			public void componentAdded(ContainerEvent e) 
			{
				updateVisibility( desktopPane.getAllFrames().length != 0 );
			}
			private void updateVisibility(boolean visible)
			{
				mntmSave.setEnabled( visible );
				mntmSaveAs.setEnabled( visible );
				mnList.setEnabled( visible );
//				mntmDeleteSelectedCaches.setEnabled( visible );
				mnWindows.setEnabled( visible );
			}
		});
		desktopPane.setMinimumSize(new Dimension(200, 200));
		
		
		JPanel panelSouth = new JPanel();
		panelSouth.setLayout(new BorderLayout(0, 0));
		contentPane.add(panelSouth, BorderLayout.SOUTH);
		
		ExceptionPanel panelException = ExceptionPanel.getPanel();
		panelSouth.add(panelException, BorderLayout.CENTER);	
	}
	

	
	private void saveFile(boolean saveAs)
	{
		final CacheListController clc = CacheListController.getTopViewCacheController(desktopPane);
		
		String strPath = null;
		try{
			strPath = clc.getPath().toString();
		}catch(Exception e){
			saveAs = true;
		}
		
		if( saveAs)
		{
			if( strPath == null ) strPath = Settings.getS(Settings.Key.FILE_CHOOSER_LOAD_GPX);
			final JFileChooser chooser = new JFileChooser(strPath);
			chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archive", "zip"));
			
			if( chooser.showSaveDialog(THIS) != JFileChooser.APPROVE_OPTION )
				return;
			
        	strPath = chooser.getSelectedFile().getAbsolutePath();
		}
		
		if( !FileHelper.getFileExtension(strPath).equals("zip") )
			strPath += ".zip";
		
		if(saveAs)
		{
			File f = new File(strPath);
			if(f.exists() && !f.isDirectory()) { 
				int dialogResult = JOptionPane.showConfirmDialog (THIS, "The choosen file already exists. Overwrite it?","Warning",JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.NO_OPTION){
					return;
				}
			}
			
			Settings.set(Settings.Key.FILE_CHOOSER_LOAD_GPX, Paths.get(strPath).getParent().toString() );
		}
		
		actionWithWaitDialog(new Runnable() {
    		private Path path;
    		public Runnable setPath(Path path){
    			this.path = path;
    			return this;
    		}
    		
			public void run() {
				try {
					clc.store(path);
				}
				catch (Throwable  e) {
					ExceptionPanel.showErrorDialog(e);
				}
			}
		}.setPath( Paths.get(strPath) ), THIS);
	}
	
	private void openFile(final boolean createNewList)
	{
		String lastPath = Settings.getS(Settings.Key.FILE_CHOOSER_LOAD_GPX);
		final JFileChooser chooser = new JFileChooser(lastPath);
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileFilter(new FileNameExtensionFilter("GPS Exchange Format | ZIP Archive", "gpx", "zip"));
		
        if( chooser.showOpenDialog(THIS) == JFileChooser.APPROVE_OPTION )
        {
        	lastPath = chooser.getSelectedFile().getPath();
        	Settings.set(Settings.Key.FILE_CHOOSER_LOAD_GPX, Paths.get(lastPath).getParent().toString());
        	
        	actionWithWaitDialog(new Runnable() {
				public void run() {
					try {
						if( createNewList )
				        	CacheListController.newCLC( desktopPane, mnWindows,   
				                  chooser.getSelectedFile().getAbsolutePath() );				        	
						else
							CacheListController.getTopViewCacheController(desktopPane).addFromFile(
									chooser.getSelectedFile().getAbsolutePath() );
					}
					catch (Throwable e) {
//						Main.gc();
						ExceptionPanel.showErrorDialog(e);
					}
				}
			}, THIS);
        }
	}
	
	
	public static void actionWithWaitDialog(final Runnable task, Component parent)
	{
		final WaitDLG wait = new WaitDLG();;
		wait.setModalityType(ModalityType.APPLICATION_MODAL);
		wait.setLocationRelativeTo(parent);

		new Thread( new Runnable() {
			public void run() {
				while( !wait.isVisible()){
					try {
						Thread.sleep(25);
			        } catch (InterruptedException e) {}
				}
				
				task.run();
				wait.setVisible(false);
			}
		}).start();
		
		wait.setVisible(true);
		wait.repaint();
	}
	
	private void findOnOC(String uuid)
	{
		DuplicateDialog dd = new DuplicateDialog( CacheListController.getTopViewCacheController(desktopPane).getModel(), uuid);
//		dd.setModalityType(ModalityType.APPLICATION_MODAL);
//		dd.setVisible(true);
		
		FrameHelper.showModalFrame(dd, THIS);
		
	}
}
