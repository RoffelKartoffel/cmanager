package cmanager;

import java.awt.BorderLayout;


import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class WaitDLG extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1281433856562055074L;
	private final JPanel contentPanel = new JPanel();


	/**
	 * Create the dialog.
	 */
	public WaitDLG() {
		setBounds(100, 100, 450, 100);
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblPleaseStandBy = new JLabel("Please stand by...");
			lblPleaseStandBy.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblPleaseStandBy);
		}
	}

}
