package cmanager.gui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;


public class CustomJMapViewer extends JMapViewer
{
    private static final long serialVersionUID = 7714507963907032312L;

    private Point p1 = null;
    private Point p2 = null;


    public CustomJMapViewer(TileCache cache)
    {
        super(cache);
    }


    public void setPoints(Point p1, Point p2)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.repaint();
    }


    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if (p1 != null && p2 != null)
        {
            int x1 = p1.x < p2.x ? p1.x : p2.x;
            int x2 = p1.x >= p2.x ? p1.x : p2.x;
            int y1 = p1.y < p2.y ? p1.y : p2.y;
            int y2 = p1.y >= p2.y ? p1.y : p2.y;

            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(2));
            //            g2.setColor(new Color(0x2554C7));
            g2.draw(new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
            g2.dispose();
        }
    }
}
