package render.renderers;

import render.GraphicsListener;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * L-System
 * Created by s154796 on 22-6-2017.
 */
public class GraphicsRenderer implements GraphicsListener {
    final Graphics2D graphics;

    public GraphicsRenderer(Graphics2D g) {
        graphics = g;
    }

    @Override
    public void drawLine(double x1, double x2, double y1, double y2, double width, Color color) {
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke((float) (width >= 0 ? width : 0)));
        graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }

    @Override
    public void fillPath(GeneralPath polygon, Color color) {
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke((float) 0));
        //graphics.fillPolygon(polygon);
        graphics.fill(polygon);
    }

    @Override
    public void end() {

    }
}
