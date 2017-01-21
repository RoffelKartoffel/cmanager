package cmanager.gui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HidablePanel extends JPanel
{

    private static final long serialVersionUID = 1L;
    private JPanel panelHidable;

    /**
     * Create the panel.
     */
    public HidablePanel()
    {
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        add(panel, BorderLayout.WEST);
        panel.setLayout(new BorderLayout(0, 0));

        final JButton btnB = new JButton("-");
        btnB.setSize(new Dimension(20, 20));
        btnB.setMaximumSize(new Dimension(20, 20));
        btnB.setOpaque(false);
        //		btnB.setBorderPainted(false);
        btnB.setFocusPainted(false);
        btnB.setContentAreaFilled(false);


        panel.add(btnB, BorderLayout.NORTH);


        panelHidable = new JPanel();
        add(panelHidable, BorderLayout.CENTER);
        panelHidable.setBorder(new EmptyBorder(0, 5, 0, 0));
        panelHidable.setLayout(new BorderLayout(0, 0));

        btnB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                boolean state = panelHidable.isVisible();
                panelHidable.setVisible(!state);

                if (!state)
                    btnB.setText("-");
                else
                    btnB.setText("+");
            }
        });
    }

    public void addHidable(Component c)
    {
        panelHidable.add(c, BorderLayout.CENTER);
    }
}
