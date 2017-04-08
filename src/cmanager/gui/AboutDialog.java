package cmanager.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cmanager.global.Constants;
import cmanager.global.Version;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JLayeredPane;

public class AboutDialog extends JDialog
{

    private static final long serialVersionUID = 1L;
    private final JLayeredPane contentPanel = new JLayeredPane();


    private AboutDialog THIS = this;

    /**
     * Create the dialog.
     */
    public AboutDialog()
    {
        setTitle("About " + Constants.APP_NAME);

        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] {0, 0};
        gbl_contentPanel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblAppname = new JLabel(Constants.APP_NAME);
            lblAppname.setFont(new Font("Dialog", Font.BOLD, 15));
            GridBagConstraints gbc_lblAppname = new GridBagConstraints();
            gbc_lblAppname.insets = new Insets(0, 0, 5, 0);
            gbc_lblAppname.anchor = GridBagConstraints.NORTH;
            gbc_lblAppname.gridx = 0;
            gbc_lblAppname.gridy = 0;
            contentPanel.add(lblAppname, gbc_lblAppname);
        }
        {
            JLabel lblVersion = new JLabel(Version.VERSION);
            lblVersion.setFont(new Font("Dialog", Font.PLAIN, 12));
            GridBagConstraints gbc_lblVersion = new GridBagConstraints();
            gbc_lblVersion.insets = new Insets(0, 0, 5, 0);
            gbc_lblVersion.gridx = 0;
            gbc_lblVersion.gridy = 1;
            contentPanel.add(lblVersion, gbc_lblVersion);
        }
        {
            JLabel lblAuthor = new JLabel("Samsung1 - jm@rq-project.net");
            lblAuthor.setFont(new Font("Dialog", Font.PLAIN, 12));
            GridBagConstraints gbc_lblAuthor = new GridBagConstraints();
            gbc_lblAuthor.insets = new Insets(40, 0, 5, 0);
            gbc_lblAuthor.gridx = 0;
            gbc_lblAuthor.gridy = 3;
            contentPanel.add(lblAuthor, gbc_lblAuthor);
        }
        {
            JLabel lblThanks =
                new JLabel("Special thanks to the great people at forum.opencaching.de .");
            lblThanks.setFont(new Font("Dialog", Font.PLAIN, 12));
            GridBagConstraints gbc_lblThanks = new GridBagConstraints();
            gbc_lblThanks.insets = new Insets(80, 0, 5, 0);
            gbc_lblThanks.gridx = 0;
            gbc_lblThanks.gridy = 5;
            contentPanel.add(lblThanks, gbc_lblThanks);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton btnClose = new JButton("Close");
                btnClose.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0)
                    {
                        THIS.setVisible(false);
                    }
                });
                btnClose.setActionCommand("OK");
                buttonPane.add(btnClose);
                getRootPane().setDefaultButton(btnClose);
            }
        }

        setResizable(false);
        super.pack();
    }
}
