package render;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * L-System
 * Created by s154796 on 4-7-2016.
 */
public class TurtleConfig implements Cloneable {
    public double width;
    public double length;
    public GeneralPath path = null;
    public double angle;
    public double x;
    public double y;
    public double angleIncrement;
    public double widthIncrement;
    public float colorIncrement;
    private Color lineColor;
    private boolean lineColorGenerated = false;
    private Color polygonColor;
    private boolean polygonColorGenerated = false;
    private float lineHue;
    private float lineSaturation;
    private float lineBrightness;
    private float polygonHue;
    private float polygonSaturation;
    private float polygonBrightness;

    public TurtleConfig(double x, double y, double a, Color lineColor, Color polygonColor, double l, double w, double ai, double wi, float ci){
        this.width = w;
        this.length = l;
        setLineColor(lineColor);
        setPolygonColor(polygonColor);
        this.angle = a;
        this.x = x;
        this.y = y;
        this.angleIncrement = ai;
        this.widthIncrement = wi;
        this.colorIncrement = ci;
    }

    public TurtleConfig() {
        this(0, 0, 0, Color.black, Color.blue, 15, 1, 15, 1, 0.1f);
    }

    public TurtleConfig(double x, double y) {
        this(x, y, 0, Color.black, Color.blue, 15, 1, 15, 1, 0.1f);
    }

    public TurtleConfig(double x, double y, double a) {
        this(x, y, a, Color.black, Color.blue, 15, 1, 15, 1, 0.1f);
    }

    public Color getLineColor() {
        if (!lineColorGenerated) {
            lineColor = new Color(Color.HSBtoRGB(lineHue, lineSaturation, lineBrightness));
            lineColorGenerated = true;
        }
        return lineColor;
    }

    public void setLineColor(Color c){
        lineColor = c;
        lineColorGenerated = true;
        float[] hsb = new float[3];
        Color.RGBtoHSB(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), hsb);
        lineHue = hsb[0];
        lineSaturation = hsb[1];
        lineBrightness = hsb[2];
    }

    public Color getPolygonColor() {
        if (!polygonColorGenerated) {
            polygonColor = new Color(Color.HSBtoRGB(polygonHue, polygonSaturation, polygonBrightness));
            polygonColorGenerated = true;
        }
        return polygonColor;
    }

    public void setPolygonColor(Color c){
        polygonColor = c;
        polygonColorGenerated = true;
        float[] hsb = new float[3];
        Color.RGBtoHSB(polygonColor.getRed(), polygonColor.getGreen(), polygonColor.getBlue(), hsb);
        polygonHue = hsb[0];
        polygonSaturation = hsb[1];
        polygonBrightness = hsb[2];
    }

    public void addLineHue(float h){
        lineHue += h / 360;
        lineColorGenerated = false;
    }

    public void addPolygonHue(float h){
        polygonHue += h / 360;
        polygonColorGenerated = false;
    }

    @Override
    public TurtleConfig clone() {
        try {
            return (TurtleConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return angle + " - " + x + " - " + y;
    }
}
