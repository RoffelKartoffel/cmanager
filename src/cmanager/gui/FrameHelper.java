package cmanager.gui;

import java.util.ArrayList;

import javax.swing.JFrame;

public class FrameHelper
{
    private static ArrayList<JFrame> reaktivationQue = new ArrayList<>();


    synchronized private static void addToQue(JFrame frame)
    {
        reaktivationQue.add(frame);
    }

    synchronized private static boolean isFirstInQue(JFrame frame)
    {
        return reaktivationQue.get(reaktivationQue.size() - 1) == frame;
    }

    synchronized private static void removeFromQue(JFrame frame)
    {
        reaktivationQue.remove(frame);
    }


    public static void showModalFrame(final JFrame newFrame, final JFrame owner)
    {
        addToQue(owner);

        newFrame.setLocationRelativeTo(owner);
        owner.setVisible(false);
        owner.setEnabled(false);
        newFrame.setVisible(true);
        newFrame.toFront();

        Thread t = new Thread(new Runnable() {
            public void run()
            {
                while (newFrame.isVisible() || !isFirstInQue(owner))
                {
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }

                owner.setVisible(true);
                owner.setEnabled(true);
                owner.toFront();
                removeFromQue(owner);
            }
        });
        t.start();
    }
}
