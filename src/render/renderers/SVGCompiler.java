package render.renderers;

import render.GraphicsListener;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * L-System
 * Created by s154796 on 11-2-2017.
 */
public class SVGCompiler implements GraphicsListener {
    Color lastColor = null;
    double lastWidth = -1;

    boolean activePath = false;

    double px = -1;
    double py = -1;

    private BufferedWriter fw;

    public SVGCompiler(String outputFile, int w, int h) throws IOException {
        FileWriter filewriter = new FileWriter(outputFile);
        fw = new BufferedWriter(filewriter);
        String startSvgFile = "<svg width=\"" + w + "\" height=\"" + h + "\" viewPort=\"0 0 " + w + " " + h + "\" xmlns=\"http://www.w3.org/2000/svg\">";

        fw.write(startSvgFile);
    }

    String getHex(Color c) {
        return String.format("#%06x", c.getRGB() & 0x00FFFFFF);
    }

    public void startPath(double x, double y, Color stroke, double width) {
        startPath(x, y, getHex(stroke), width, "transparent");
    }

    public void startPath(double x, double y, Color stroke, double width, Color fill) {
        startPath(x, y, getHex(stroke), width, getHex(fill));
    }

    public void startPath(double x, double y, String stroke, double width, String fill) {
        if (activePath) {
            endPath();
        }

        activePath = true;
        String pathStart = "<path stroke-width=\"" + width + "\" fill=\"" + fill + "\" stroke=\"" + stroke + "\" d=\"M " + x + " " + y;

        px = x;
        py = y;

        try {
            fw.write(pathStart);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendPath(double x1, double y1, double x2, double y2) {
        if (activePath) {
            String append = "";
            if (!(x1 == px && y1 == py)) {
                append += " M " + x1 + " " + y1;
            }

            append += " L " + x2 + " " + y2;
            try {
                fw.write(append);
            } catch (IOException e) {
                e.printStackTrace();
            }

            px = x2;
            py = y2;
        }
    }

    private void endPath() {
        if (activePath) {
            activePath = false;
            String pathEnd = "\"/>";
            try {
                fw.write(pathEnd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawLine(double x1, double x2, double y1, double y2, double width, Color color) {
        if (color == lastColor && lastWidth == width) {
            appendPath(x1, y1, x2, y2);
        } else {
            startPath(x1, y1, color, width);
            appendPath(x1, y1, x2, y2);
        }

        lastWidth = width;
        lastColor = color;
    }

    @Override
    public void fillPath(GeneralPath polygon, Color color) {
        endPath();
        PathIterator pi = polygon.getPathIterator(null);
        double[] coords = new double[2];

        while (!pi.isDone()) {
            pi.currentSegment(coords);
            if (!activePath) {
                startPath(coords[0], coords[1], Color.BLACK, 0, color);
            } else {
                appendPath(px, py, coords[0], coords[1]);
            }
            pi.next();
        }
    }

    @Override
    public void end() {
        endPath();
        String endSvgFile = "</svg>";
        try {
            fw.write(endSvgFile);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
