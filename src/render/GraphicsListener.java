package render;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * L-System
 * Created by s154796 on 5-7-2016.
 */
public interface GraphicsListener {
    void drawLine(double x1, double x2, double y1, double y2, double width, Color color);
    void fillPath(GeneralPath polygon, Color color);

    void end();
}
