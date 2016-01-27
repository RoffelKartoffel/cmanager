package cmanager;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;

public class SettingsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6008083400079798934L;
	private JDialog THIS = this;
	private JPanel contentPane;
	private JLabel lblOkapiToken;
	private JButton btnRequestNewToken;
	private JTextField txtNameGC;
	private JTextField txtHeapSize;



	/**
	 * Create the frame.
	 */
	public SettingsDialog() {
		setTitle("Settings");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtons = new JPanel();
		contentPane.add(panelButtons, BorderLayout.SOUTH);
		panelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnSaveApply = new JButton("Save & Apply");
		btnSaveApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Settings.set(Settings.Key.GC_USERNAME, txtNameGC.getText());
				Settings.set(Settings.Key.HEAP_SIZE, txtHeapSize.getText());
				THIS.setVisible(false);
			}
		});
		panelButtons.add(btnSaveApply);
		
		JButton btnDiscard = new JButton("Discard");
		btnDiscard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				THIS.setVisible(false);
			}
		});
		panelButtons.add(btnDiscard);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panelOC = new JPanel();
		panelOC.setBorder(new EmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab("opencaching.de", null, panelOC, null);
		GridBagLayout gbl_panelOC = new GridBagLayout();
		gbl_panelOC.columnWidths = new int[]{411, 0};
		gbl_panelOC.rowHeights = new int[]{19, 0, 0};
		gbl_panelOC.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelOC.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panelOC.setLayout(gbl_panelOC);
		
		JPanel panelOKAPI = new JPanel();
		GridBagConstraints gbc_panelOKAPI = new GridBagConstraints();
		gbc_panelOKAPI.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelOKAPI.anchor = GridBagConstraints.NORTHWEST;
		gbc_panelOKAPI.gridx = 0;
		gbc_panelOKAPI.gridy = 1;
		panelOC.add(panelOKAPI, gbc_panelOKAPI);
		panelOKAPI.setLayout(new BorderLayout(0, 0));
		
		lblOkapiToken = new JLabel("New label");
		panelOKAPI.add(lblOkapiToken, BorderLayout.WEST);
		
		btnRequestNewToken = new JButton("Request new token");
		btnRequestNewToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				try {
					OCUser.getOCUser().requestOkapiToken();
					displayOkapiTokenStatus();
				}
				catch( Throwable e){
					ExceptionPanel.showErrorDialog(e);
				}
				
			}
		});
		panelOKAPI.add(btnRequestNewToken, BorderLayout.EAST);
		
		JPanel panelGC = new JPanel();
		tabbedPane.addTab("geocaching.com", null, panelGC, null);
		GridBagLayout gbl_panelGC = new GridBagLayout();
		gbl_panelGC.columnWidths = new int[]{215, 215, 0};
		gbl_panelGC.rowHeights = new int[]{201, 0};
		gbl_panelGC.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelGC.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelGC.setLayout(gbl_panelGC);
		
		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.gridwidth = 50;
		gbc_lblUsername.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblUsername.insets = new Insets(20, 20, 0, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 0;
		panelGC.add(lblUsername, gbc_lblUsername);
		
		txtNameGC = new JTextField();
		GridBagConstraints gbc_txtNameGC = new GridBagConstraints();
		gbc_txtNameGC.weighty = 0.5;
		gbc_txtNameGC.insets = new Insets(20, 0, 0, 0);
		gbc_txtNameGC.anchor = GridBagConstraints.NORTH;
		gbc_txtNameGC.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNameGC.gridx = 1;
		gbc_txtNameGC.gridy = 0;
		panelGC.add(txtNameGC, gbc_txtNameGC);
		txtNameGC.setColumns(10);
		
		
		displayOkapiTokenStatus();
		txtNameGC.setText( Settings.getS(Settings.Key.GC_USERNAME) );
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setBorder(new EmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab("General", null, panelGeneral, null);
		GridBagLayout gbl_panelGeneral = new GridBagLayout();
		gbl_panelGeneral.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panelGeneral.columnWeights = new double[]{1.0, 1.0, 0.0};
		panelGeneral.setLayout(gbl_panelGeneral);
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.weighty = 0.1;
		gbc_verticalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_1.gridx = 0;
		gbc_verticalStrut_1.gridy = 0;
		panelGeneral.add(verticalStrut_1, gbc_verticalStrut_1);
		
		JLabel lblCurrentHeapSize = new JLabel("$size");
		GridBagConstraints gbc_lblCurrentHeapSize = new GridBagConstraints();
		gbc_lblCurrentHeapSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrentHeapSize.gridx = 1;
		gbc_lblCurrentHeapSize.gridy = 1;
		panelGeneral.add(lblCurrentHeapSize, gbc_lblCurrentHeapSize);
		lblCurrentHeapSize.setText( new Long(Runtime.getRuntime().maxMemory() /1024/1024 ).toString() );
		
		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.weighty = 0.5;
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 3;
		panelGeneral.add(verticalStrut, gbc_verticalStrut);
		
		JLabel lblNewLabel_2 = new JLabel("(*) Application restart required.");
		lblNewLabel_2.setFont(new Font("Dialog", Font.PLAIN, 11));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.gridwidth = 3;
		gbc_lblNewLabel_2.anchor = GridBagConstraints.ABOVE_BASELINE;
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 4;
		panelGeneral.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		txtHeapSize = new JTextField();
		txtHeapSize.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_textHeapSize = new GridBagConstraints();
		gbc_textHeapSize.insets = new Insets(0, 0, 5, 0);
		gbc_textHeapSize.gridwidth = 2;
		gbc_textHeapSize.weightx = 1.0;
		gbc_textHeapSize.anchor = GridBagConstraints.NORTHWEST;
		gbc_textHeapSize.gridx = 1;
		gbc_textHeapSize.gridy = 2;
		gbc_textHeapSize.fill = GridBagConstraints.HORIZONTAL;
		panelGeneral.add(txtHeapSize, gbc_textHeapSize);
		txtHeapSize.setColumns(10);
		txtHeapSize.setText( Settings.getS(Settings.Key.HEAP_SIZE) );
		
		JLabel lblNewLabel = new JLabel("Heap size* (MB):");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel.gridy = 2;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		panelGeneral.add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Current heap size (MB):");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panelGeneral.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
	}
	
	private void displayOkapiTokenStatus()
	{
		lblOkapiToken.setText("OKAPI Token:   missing or offline" );
		Font f = lblOkapiToken.getFont();
		lblOkapiToken.setFont(f.deriveFont(f.getStyle() | Font.ITALIC));
		btnRequestNewToken.setVisible(true);
		
		OCUser user = OCUser.getOCUser();
		try {
			if( user.getOkapiToken() != null && OKAPI.getUUID(user) != null )
			{
				lblOkapiToken.setText("OKAPI Token:   okay" );
				f = lblOkapiToken.getFont();
				lblOkapiToken.setFont(f.deriveFont(f.getStyle() & ~Font.ITALIC));
				btnRequestNewToken.setVisible(false);
			}
		} catch (Exception e) {
		}
			
	}
}
