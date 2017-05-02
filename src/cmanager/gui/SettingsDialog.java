package cmanager.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cmanager.global.Constants;
import cmanager.okapi.User;
import cmanager.okapi.OKAPI;
import cmanager.settings.Settings;
import cmanager.util.DesktopUtil;
import cmanager.util.ForkUtil;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SpringLayout;

public class SettingsDialog extends JDialog
{

    /**
     *
     */
    private static final long serialVersionUID = -6008083400079798934L;
    private JDialog THIS = this;
    private JPanel contentPane;
    private JLabel lblOkapiToken;
    private JLabel lblOCUsername;
    private JButton btnRequestNewToken;
    private JTextField txtNameGC;
    private JTextField txtHeapSize;


    /**
     * Create the frame.
     */
    public SettingsDialog(JFrame owner)
    {
        super(owner);

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
                boolean changesWhichNeedRestart = false;

                final String newHeapSize = txtHeapSize.getText();
                final String oldHeapSize = Settings.getS(Settings.Key.HEAP_SIZE);
                if ((oldHeapSize != null && !oldHeapSize.equals(newHeapSize)) ||
                    (oldHeapSize == null && newHeapSize.length() > 0))
                    changesWhichNeedRestart = true;

                Settings.set(Settings.Key.GC_USERNAME, txtNameGC.getText());
                Settings.set(Settings.Key.HEAP_SIZE, newHeapSize);

                if (changesWhichNeedRestart)
                {
                    String message =
                        "You have made changes which need cmanager to restart in order be applied.\n"
                        + "Do you want to restart " + Constants.APP_NAME + " now?";
                    int dialogResult = JOptionPane.showConfirmDialog(
                        THIS, message, "Restart " + Constants.APP_NAME + " now?",
                        JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION)
                    {
                        try
                        {
                            ForkUtil.runCopyAndExit();
                        }
                        catch (Throwable t)
                        {
                            ExceptionPanel.showErrorDialog(THIS, t);
                        }
                    }
                }

                THIS.setVisible(false);
            }
        });
        panelButtons.add(btnSaveApply);

        JButton btnDiscard = new JButton("Discard");
        btnDiscard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                THIS.setVisible(false);
            }
        });
        panelButtons.add(btnDiscard);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        JPanel panelOC = new JPanel();
        panelOC.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("opencaching.de", null, panelOC, null);
        SpringLayout sl_panelOC = new SpringLayout();
        panelOC.setLayout(sl_panelOC);

        JLabel label = new JLabel("OKAPI Token:");
        sl_panelOC.putConstraint(SpringLayout.NORTH, label, 40, SpringLayout.NORTH, panelOC);
        sl_panelOC.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, panelOC);
        panelOC.add(label);

        lblOkapiToken = new JLabel("New label");
        sl_panelOC.putConstraint(SpringLayout.NORTH, lblOkapiToken, 0, SpringLayout.NORTH, label);
        sl_panelOC.putConstraint(SpringLayout.WEST, lblOkapiToken, 101, SpringLayout.EAST, label);
        panelOC.add(lblOkapiToken);

        btnRequestNewToken = new JButton("Request new token");
        sl_panelOC.putConstraint(SpringLayout.SOUTH, btnRequestNewToken, 0, SpringLayout.SOUTH,
                                 panelOC);
        sl_panelOC.putConstraint(SpringLayout.EAST, btnRequestNewToken, 0, SpringLayout.EAST,
                                 panelOC);
        btnRequestNewToken.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                try
                {
                    User.getOKAPIUser().requestOkapiToken(new OKAPI.RequestAuthorizationCallbackI() {
                        @Override
                        public String getPin()
                        {
                            String pin = JOptionPane.showInputDialog(
                                null,
                                "Please look at your browser and enter the PIN from opencaching.de");
                            return pin;
                        }

                        @Override
                        public void redirectUrlToUser(String authUrl)
                        {
                            DesktopUtil.openUrl(authUrl);
                        }
                    });
                    displayOkapiTokenStatus();
                }
                catch (Throwable e)
                {
                    ExceptionPanel.showErrorDialog(THIS, e);
                }
            }
        });
        panelOC.add(btnRequestNewToken);


        JLabel lblNewLabel_3 = new JLabel("OC Username: ");
        sl_panelOC.putConstraint(SpringLayout.NORTH, lblNewLabel_3, 6, SpringLayout.SOUTH, label);
        sl_panelOC.putConstraint(SpringLayout.WEST, lblNewLabel_3, 0, SpringLayout.WEST, label);
        panelOC.add(lblNewLabel_3);

        lblOCUsername = new JLabel("");
        sl_panelOC.putConstraint(SpringLayout.NORTH, lblOCUsername, 6, SpringLayout.SOUTH,
                                 lblOkapiToken);
        sl_panelOC.putConstraint(SpringLayout.WEST, lblOCUsername, 204, SpringLayout.WEST, panelOC);
        sl_panelOC.putConstraint(SpringLayout.EAST, lblOCUsername, -31, SpringLayout.EAST, panelOC);
        lblOCUsername.setHorizontalAlignment(SwingConstants.LEFT);
        lblOCUsername.setText(Settings.getS(Settings.Key.OC_USERNAME));
        panelOC.add(lblOCUsername);

        JPanel panelGC = new JPanel();
        tabbedPane.addTab("geocaching.com", null, panelGC, null);
        GridBagLayout gbl_panelGC = new GridBagLayout();
        gbl_panelGC.columnWidths = new int[] {215, 215, 0};
        gbl_panelGC.rowHeights = new int[] {201, 0};
        gbl_panelGC.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        gbl_panelGC.rowWeights = new double[] {0.0, Double.MIN_VALUE};
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
        txtNameGC.setText(Settings.getS(Settings.Key.GC_USERNAME));

        JPanel panelGeneral = new JPanel();
        panelGeneral.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("General", null, panelGeneral, null);
        GridBagLayout gbl_panelGeneral = new GridBagLayout();
        gbl_panelGeneral.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0};
        gbl_panelGeneral.columnWeights = new double[] {1.0, 1.0, 0.0};
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
        lblCurrentHeapSize.setText(
            new Long(Runtime.getRuntime().maxMemory() / 1024 / 1024).toString());

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
        txtHeapSize.setText(Settings.getS(Settings.Key.HEAP_SIZE));

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
        lblOkapiToken.setText("missing or offline");
        Font f = lblOkapiToken.getFont();
        lblOkapiToken.setFont(f.deriveFont(f.getStyle() | Font.ITALIC));
        btnRequestNewToken.setVisible(true);

        User user = User.getOKAPIUser();
        try
        {
            if (user.getOkapiToken() != null && OKAPI.getUUID(user) != null)
            {
                lblOkapiToken.setText("okay");
                f = lblOkapiToken.getFont();
                lblOkapiToken.setFont(f.deriveFont(f.getStyle() & ~Font.ITALIC));
                btnRequestNewToken.setVisible(false);

                String username = OKAPI.getUsername(user);
                Settings.set(Settings.Key.OC_USERNAME, username);
                lblOCUsername.setText(username);
            }
        }
        catch (Exception e)
        {
        }
    }
}
