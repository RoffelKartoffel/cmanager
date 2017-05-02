package cmanager.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import cmanager.ThreadStore;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.Location;
import cmanager.geo.LocationList;
import cmanager.okapi.User;
import cmanager.okapi.OKAPI;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JSeparator;
import java.awt.Font;

public class LocationDialog extends JDialog
{

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private JTextField txtName;
    private JTextField txtLat;
    private JTextField txtLon;

    public boolean modified = false;

    /**
     * Launch the application.
     */
    //	public static void main(String[] args) {
    //		try {
    //			LocationDialog dialog = new LocationDialog();
    //			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    //			dialog.setVisible(true);
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //		}
    //	}

    private final LocationDialog THIS = this;

    /**
     * Create the dialog.
     */
    public LocationDialog(JFrame owner)
    {
        super(owner);

        setTitle("Locations");
        getContentPane().setLayout(new BorderLayout());

        String columnNames[] = {"Name", "Lat", "Lon"};
        String dataValues[][] = {};
        DefaultTableModel dtm = new DefaultTableModel(dataValues, columnNames);

        try
        {
            ArrayList<Location> locations = LocationList.getList().getLocations();
            for (Location l : locations)
            {
                String values[] = {l.getName(), l.getLat().toString(), l.getLon().toString()};
                dtm.addRow(values);
            }
        }
        catch (Exception e)
        {
            //			e.printStackTrace();
            ExceptionPanel.showErrorDialog(this, e);
        }

        JPanel panelMaster = new JPanel();
        panelMaster.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panelMaster, BorderLayout.CENTER);
        panelMaster.setLayout(new BorderLayout(0, 0));

        JPanel buttonPaneOutter = new JPanel();
        panelMaster.add(buttonPaneOutter, BorderLayout.SOUTH);
        buttonPaneOutter.setBorder(null);
        buttonPaneOutter.setLayout(new BorderLayout(0, 0));

        JPanel buttonPaneOkCancel = new JPanel();
        buttonPaneOkCancel.setBorder(null);
        buttonPaneOutter.add(buttonPaneOkCancel, BorderLayout.SOUTH);
        buttonPaneOkCancel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    ArrayList<Location> locations = new ArrayList<>();

                    DefaultTableModel dtm = ((DefaultTableModel)table.getModel());
                    for (int i = 0; i < dtm.getRowCount(); i++)
                    {
                        Location l = new Location((String)table.getValueAt(i, 0),
                                                  new Double((String)table.getValueAt(i, 1)),
                                                  new Double((String)table.getValueAt(i, 2)));
                        locations.add(l);
                    }

                    LocationList.getList().setLocations(locations);
                }
                catch (Throwable t)
                {
                    ExceptionPanel.showErrorDialog(THIS, t);
                    return;
                }

                modified = true;
                dispose();
            }
        });
        buttonPaneOkCancel.add(btnOK);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        buttonPaneOkCancel.add(btnCancel);

        JPanel buttonPanelEdit = new JPanel();
        buttonPanelEdit.setBorder(new LineBorder(new Color(0, 0, 0)));
        buttonPaneOutter.add(buttonPanelEdit, BorderLayout.NORTH);
        buttonPanelEdit.setLayout(new BorderLayout(0, 0));

        JPanel panelText = new JPanel();
        buttonPanelEdit.add(panelText, BorderLayout.NORTH);
        GridBagLayout gbl_panelText = new GridBagLayout();
        gbl_panelText.columnWidths = new int[] {215, 215, 0};
        gbl_panelText.rowHeights = new int[] {19, 19, 19, 0};
        gbl_panelText.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        gbl_panelText.rowWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
        panelText.setLayout(gbl_panelText);

        JLabel lblNewLabel = new JLabel("Name:");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        panelText.add(lblNewLabel, gbc_lblNewLabel);

        txtName = new JTextField();
        GridBagConstraints gbc_txtName = new GridBagConstraints();
        gbc_txtName.fill = GridBagConstraints.BOTH;
        gbc_txtName.insets = new Insets(0, 0, 5, 0);
        gbc_txtName.gridx = 1;
        gbc_txtName.gridy = 0;
        panelText.add(txtName, gbc_txtName);
        txtName.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("Lat:");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel_1.gridx = 0;
        gbc_lblNewLabel_1.gridy = 1;
        panelText.add(lblNewLabel_1, gbc_lblNewLabel_1);

        txtLat = new JTextField();
        GridBagConstraints gbc_txtLat = new GridBagConstraints();
        gbc_txtLat.fill = GridBagConstraints.BOTH;
        gbc_txtLat.insets = new Insets(0, 0, 5, 0);
        gbc_txtLat.gridx = 1;
        gbc_txtLat.gridy = 1;
        panelText.add(txtLat, gbc_txtLat);
        txtLat.setColumns(10);

        JLabel lblNewLabel_2 = new JLabel("Lon:");
        GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
        gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
        gbc_lblNewLabel_2.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel_2.gridx = 0;
        gbc_lblNewLabel_2.gridy = 2;
        panelText.add(lblNewLabel_2, gbc_lblNewLabel_2);

        txtLon = new JTextField();
        GridBagConstraints gbc_txtLon = new GridBagConstraints();
        gbc_txtLon.fill = GridBagConstraints.BOTH;
        gbc_txtLon.gridx = 1;
        gbc_txtLon.gridy = 2;
        panelText.add(txtLon, gbc_txtLon);
        txtLon.setColumns(10);

        JPanel panelButton = new JPanel();
        buttonPanelEdit.add(panelButton, BorderLayout.SOUTH);

        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                int row = table.getSelectedRow();
                if (row == -1)
                    return;
                ((DefaultTableModel)table.getModel()).removeRow(row);
            }
        });
        panelButton.add(btnRemove);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                int row = table.getSelectedRow();
                if (row == -1)
                    return;

                try
                {
                    new Location(txtName.getText(), new Double(txtLat.getText()),
                                 new Double(txtLon.getText()));
                }
                catch (Throwable t)
                {
                    ExceptionPanel.showErrorDialog(THIS, t);
                    return;
                }

                DefaultTableModel dtm = ((DefaultTableModel)table.getModel());
                dtm.setValueAt(txtName.getText(), row, 0);
                dtm.setValueAt(txtLat.getText(), row, 1);
                dtm.setValueAt(txtLon.getText(), row, 2);
            }
        });
        panelButton.add(btnUpdate);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    new Location(txtName.getText(), new Double(txtLat.getText()),
                                 new Double(txtLon.getText()));
                }
                catch (Throwable t)
                {
                    ExceptionPanel.showErrorDialog(THIS, t);
                    return;
                }

                DefaultTableModel dtm = ((DefaultTableModel)table.getModel());
                String values[] = {txtName.getText(), txtLat.getText(), txtLon.getText()};
                dtm.addRow(values);
            }
        });
        panelButton.add(btnAdd);

        JSeparator separator = new JSeparator();
        panelButton.add(separator);

        final JButton btnRetrieve = new JButton("OKAPI Coordinates");
        btnRetrieve.setEnabled(false);
        btnRetrieve.setFont(new Font("Dialog", Font.BOLD, 9));
        btnRetrieve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                User user = User.getOKAPIUser();
                try
                {
                    Coordinate c = OKAPI.getHomeCoordinates(user);
                    txtName.setText("OKAPI Home Coordinate");
                    txtLat.setText(c.getLat().toString());
                    txtLon.setText(c.getLon().toString());
                }
                catch (Exception ex)
                {
                    ExceptionPanel.showErrorDialog(THIS, ex);
                }
            }
        });
        panelButton.add(btnRetrieve);


        panelMaster.add(contentPanel);
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(null);
        table = new JTable(dtm);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent arg0)
            {
                int row = table.getSelectedRow();
                if (row == -1)
                    return;

                txtName.setText((String)table.getValueAt(row, 0));
                txtLat.setText((String)table.getValueAt(row, 1));
                txtLon.setText((String)table.getValueAt(row, 2));
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane);

        pack();

        ThreadStore ts = new ThreadStore();
        ts.addAndRun(new Thread(new Runnable() {
            public void run()
            {
                User user = User.getOKAPIUser();
                try
                {
                    if (user.getOkapiToken() != null && OKAPI.getUUID(user) != null)
                    {
                        btnRetrieve.setEnabled(true);
                    }
                }
                catch (Exception e)
                {
                }
            }
        }));
    }

    public void setGeocache(Geocache g)
    {
        txtName.setText(g.getName());
        txtLat.setText(g.getCoordinate().getLat().toString());
        txtLon.setText(g.getCoordinate().getLon().toString());
    }
}
