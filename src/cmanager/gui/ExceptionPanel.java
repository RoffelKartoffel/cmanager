package cmanager.gui;

import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.FlowLayout;

public class ExceptionPanel extends JPanel
{

    private static final long serialVersionUID = 1L;
    private static ExceptionPanel THIS = null;


    private JPanel panelDetails;
    private JPanel panelMessage;
    private JTextPane textDetails;
    private JScrollPane scrollPane;


    public static ExceptionPanel getPanel()
    {
        if (THIS == null)
            THIS = new ExceptionPanel();
        return THIS;
    }


    /**
     * Create the panel.
     * @throws Exception
     */
    private ExceptionPanel()
    {
        setLayout(new BorderLayout(0, 0));

        panelMessage = new JPanel();
        add(panelMessage, BorderLayout.NORTH);

        JButton btnEnlarge = new JButton("One or more exceptions occured. Click to show/hide.");
        btnEnlarge.setForeground(Color.RED);
        btnEnlarge.setOpaque(false);
        btnEnlarge.setContentAreaFilled(false);
        btnEnlarge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                panelDetails.setVisible(!panelDetails.isVisible());
            }
        });
        panelMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelMessage.add(btnEnlarge);


        JButton btnClose = new JButton("x");
        panelMessage.add(btnClose);
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                hideUs();
            }
        });

        panelDetails = new JPanel();
        add(panelDetails, BorderLayout.CENTER);
        panelDetails.setLayout(new BorderLayout(0, 0));


        textDetails = new JTextPane();
        textDetails.setForeground(Color.RED);
        scrollPane = new JScrollPane(textDetails, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelDetails.add(scrollPane);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e)
            {
                Dimension d = new Dimension(THIS.getWidth(), 200);
                scrollPane.setPreferredSize(d);
            }
        });


        hideUs();
    }

    private void hideUs()
    {
        panelDetails.setVisible(false);
        panelMessage.setVisible(false);
        textDetails.setText("");
    }


    private void display_(String s)
    {
        String text = textDetails.getText();
        if (text.length() > 0)
            text += "\n";

        text += s;
        textDetails.setText(text);
        panelMessage.setVisible(true);
    }


    public static void display(Exception e)
    {
        e.printStackTrace();

        String s = e.getClass().getName() + "\n";
        if (e.getMessage() != null)
            s += e.getMessage() + "\n";
        s += toString(e);
        THIS.display_(s);
    }

    public static void display(StackTraceElement[] stack)
    {
        THIS.display_(toString(stack));
    }

    public static void display(String s)
    {
        System.err.println(s);
        THIS.display_(s);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////

    public static void showErrorDialog(Component parent, String errorMessage, String title)
    {
        JOptionPane.showMessageDialog(parent, errorMessage, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorDialog(Component parent, Throwable exceptionError)
    {
        String errorMessage = exceptionError.getMessage();
        errorMessage = errorMessage != null ? errorMessage : exceptionError.getClass().getName();
        errorMessage =
            "Message: " + errorMessage + "\n\nStackTrace: " + toShortString(exceptionError);

        String title = exceptionError.getClass().getName();

        showErrorDialog(parent, errorMessage, title);

        if (exceptionError instanceof OutOfMemoryError)
        {
            String message =
                "You experienced the previous crash due to insufficient memory.\n"
                + "You might want to change your memory settings under Menu->Settings->General.";
            JOptionPane.showMessageDialog(parent, message, "Memory Settings",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static String toShortString(Throwable e)
    {
        String res = "";
        int lineNr = 0;
        for (StackTraceElement ste : e.getStackTrace())
        {
            lineNr++;
            res += ste.toString() + "\n";
            if (lineNr == 12)
            {
                res += "...";
                break;
            }
        }
        return res;
    }

    public static String toString(Throwable e)
    {
        return toString(e.getStackTrace());
    }

    public static String toString(StackTraceElement[] stack)
    {
        String res = "";
        for (StackTraceElement ste : stack)
            res += ste.toString() + "\n";
        return res;
    }
}
