package cmanager;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JToggleButton;
import javax.swing.BoxLayout;

public abstract class CacheListFilter extends JPanel 
{
	private static final long serialVersionUID = -6181151635761995945L;
	
	private CacheListFilter THIS = this;
	private JTextField txtLinks;
	private JTextField txtRechts;
	private JLabel lblLinks;
	private JLabel lblRechts;
	private JButton btnRemove;
	
	protected boolean inverted = false;
	protected JPanel panel_1;
	private JButton btnUpdate;
	private JToggleButton tglbtnInvert;
	protected JPanel panel_2;
	protected JLabel lblLinks2;
	protected JTextField textField;
	private JPanel panel_4;
	
	private ArrayList<Runnable> runOnRemove = new ArrayList<>();
	private Runnable runOnFilterUpdate = null;
	protected Runnable runDoModelUpdateNow = null;
	
	/**
	 * Create the panel.
	 */
	public CacheListFilter() {
		
		KeyAdapter keyEnterUpdate = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode()==KeyEvent.VK_ENTER){
					btnUpdate.doClick();
				}
			}
		};
		
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtons = new JPanel();
		panel.add(panelButtons, BorderLayout.EAST);
		panelButtons.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panelButtons.add(panel_3);
		
		btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				runDoModelUpdateNow.run();
				runOnFilterUpdate.run();
			}
		});
		
		tglbtnInvert = new JToggleButton("Invert");
		tglbtnInvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				inverted = tglbtnInvert.isSelected();
				runDoModelUpdateNow.run();
				runOnFilterUpdate.run();
			}
		});
		panel_3.add(tglbtnInvert);
		panel_3.add(btnUpdate);
		
		btnRemove = new JButton("X");
		panel_3.add(btnRemove);
		
		panel_4 = new JPanel();
		panel.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));
		
		panel_2 = new JPanel();
		panel_4.add(panel_2);
		panel_2.setVisible(false);
		panel_2.setLayout(new BorderLayout(5, 10));
		
		lblLinks2 = new JLabel("New label");
		panel_2.add(lblLinks2, BorderLayout.WEST);
		
		textField = new JTextField();
		panel_2.add(textField, BorderLayout.CENTER);
		textField.addKeyListener(keyEnterUpdate);
		
		panel_1 = new JPanel();
		panel_4.add(panel_1);
		panel_1.setVisible(false);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panelLinks = new JPanel();
		panel_1.add(panelLinks);
		panelLinks.setLayout(new BorderLayout(5, 0));
		
		lblLinks = new JLabel("Label");
		panelLinks.add(lblLinks, BorderLayout.WEST);
		
		txtLinks = new JTextField();
		txtLinks.setHorizontalAlignment(SwingConstants.CENTER);
		panelLinks.add(txtLinks, BorderLayout.EAST);
		txtLinks.setColumns(10);
		txtLinks.addKeyListener(keyEnterUpdate);
		
		JPanel panelRechts = new JPanel();
		panel_1.add(panelRechts);
		panelRechts.setLayout(new BorderLayout(5, 0));
		
		lblRechts = new JLabel("Label");
		panelRechts.add(lblRechts, BorderLayout.WEST);
		
		txtRechts = new JTextField();
		txtRechts.setHorizontalAlignment(SwingConstants.CENTER);
		panelRechts.add(txtRechts, BorderLayout.EAST);
		txtRechts.setColumns(10);
		txtRechts.addKeyListener(keyEnterUpdate);
		
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				Container parent = THIS.getParent();
				parent.remove(THIS);
				parent.revalidate();
				
				for(Runnable action : runOnRemove)
					action.run();
			}
		});

	}
	
	public void addRemoveAction(Runnable action){
		runOnRemove.add(action);
	}
	
	public void addFilterUpdateAction(Runnable action){
		runOnFilterUpdate = action;
	}
	
	

	protected JLabel getLblLinks() {
		return lblLinks;
	}
	protected JTextField getTxtLinks() {
		return txtLinks;
	}
	protected JLabel getLblRechts() {
		return lblRechts;
	}
	protected JTextField getTxtRechts() {
		return txtRechts;
	}
	protected JButton getBtnRemove() {
		return btnRemove;
	}
	protected JButton getBtnUpdate() {
		return btnUpdate;
	}
}
