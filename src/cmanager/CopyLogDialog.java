package cmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;

public class CopyLogDialog extends JFrame {

	private static final long serialVersionUID = 363313395887255591L;

	
	private final JPanel contentPanel = new JPanel();
	private CopyLogDialog THIS = this;
	private JSplitPane splitPane1;
	private JSplitPane splitPane2;
	private JPanel panelLogs;
	private JScrollPane scrollPane;



	/**
	 * Create the dialog.
	 */
	public CopyLogDialog(final Geocache gc, final Geocache oc) 
	{
		setResizable(true);
		
		setTitle("Copy Logs");
		setBounds(100, 100, 850, 500);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		splitPane1 = new JSplitPane();
		contentPanel.add(splitPane1);
		
		splitPane2 = new JSplitPane();
		splitPane1.setRightComponent(splitPane2);
		
		CachePanel cp = new CachePanel();
		cp.setMinimumSize(new Dimension(100, 100));
		cp.setCache(gc, false);
		cp.colorize(oc);
		splitPane1.setLeftComponent( cp );
		
		cp = new CachePanel();
		cp.setMinimumSize(new Dimension(100, 100));
		cp.setCache(oc, false);
		cp.colorize(gc);
		splitPane2.setLeftComponent(cp);
		
		panelLogs = new JPanel();
		panelLogs.setLayout(new GridBagLayout());
		
		{
			GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weighty = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = java.awt.GridBagConstraints.BOTH;
            
            for(GeocacheLog log : gc.getLogs())
            {
            	if( !log.getTypeStr().equals("Found it") )
            		continue;
            	
            	String gcUsername = Settings.getS(Settings.Key.GC_USERNAME);
            	if( !log.isAuthor(gcUsername) )
            		continue;
            	
            	LogPanel logPanel = new LogPanel(log);
            	panelLogs.add( logPanel, gbc );
            	gbc.gridy++;
            	
            	GridBagConstraints gbc_button = (GridBagConstraints)gbc.clone();
            	gbc_button.weighty = 0;
            	gbc_button.fill = 0;
            	gbc_button.insets = new Insets(0, 10, 10, 0);
            	gbc.gridy++;
            	
            	final JButton button = new JButton("Copy log to opencaching.de");
            	if( gc.claculateSimilarity(oc) != 1 )
            		button.setBackground(Color.RED);
            	button.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent arg0) 
					{
						MainWindow.actionWithWaitDialog(new Runnable() {
							public void run() 
							{
								try {
									OKAPI.postLog(OCUser.getOCUser(), oc, log);
									button.setVisible(false);
								} catch (UnsupportedEncodingException | MalFormedException e) {
									ExceptionPanel.showErrorDialog(e);
								}
							}
						}, THIS);
						
						
					}
					
					private Geocache oc;
					private GeocacheLog log;
					public ActionListener set(Geocache oc, GeocacheLog log, boolean dagerousMatch)
					{
						this.oc = oc;
						this.log = log;
						return this;
					}
				}.set(oc, log, false) );
            	panelLogs.add(button, gbc_button);
            }
		}

		
		scrollPane = new JScrollPane(panelLogs);
		scrollPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent arg0) 
			{
				scrollPane.getVerticalScrollBar().setValue(0);
				scrollPane.getHorizontalScrollBar().setValue(0);
			}
		});
		splitPane2.setRightComponent(scrollPane);
			


		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton btnReturn = new JButton("Return");
			buttonPane.add(btnReturn);
			btnReturn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) 
				{
					THIS.setVisible(false);
				}
			});
		}
		
		THIS.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				CacheListView.fixSplitPanes(splitPane1, splitPane2);
				
			}
			public void componentResized(ComponentEvent e) {
				CacheListView.fixSplitPanes(splitPane1, splitPane2);
				
			}
		});
	}

}
