package render.renderers;

import render.GraphicsListener;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.HashSet;
import java.util.Set;

/**
 * L-System
 * Created by s154796 on 22-6-2017.
 */
public class CompoundListener implements GraphicsListener {
    Set<GraphicsListener> listenerSet = new HashSet<>();

    public void addListener(GraphicsListener l) {
        listenerSet.add(l);
    }

    public void removeListener(GraphicsListener l) {
        listenerSet.remove(l);
    }

    @Override
    public void drawLine(double x1, double x2, double y1, double y2, double width, Color color) {
        for (GraphicsListener l : listenerSet) {
            l.drawLine(x1, x2, y1, y2, width, color);
        }
    }

    @Override
    public void fillPath(GeneralPath polygon, Color color) {
        for (GraphicsListener l : listenerSet) {
            l.fillPath(polygon, color);
        }
    }

    @Override
    public void end() {
        for (GraphicsListener l : listenerSet) {
            l.end();
        }
    }
}
