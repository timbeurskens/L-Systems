package render;

import java.awt.geom.GeneralPath;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Stack;

/**
 * L-System
 * Created by s154796 on 3-7-2016.
 */
public class Turtle {
    public double minX = 0, minY = 0, maxX = 0, maxY = 0;
    protected String tape;
    protected GraphicsListener listener;
    BufferedWriter log;
    private Stack<TurtleConfig> configStack;
    private TurtleConfig currentConfig;

    public Turtle(String input, TurtleConfig initialConfig) {
        log = new BufferedWriter(new OutputStreamWriter(System.out));

        configStack = new Stack<>();
        //pathStack = new Stack<>();
        setInitialConfig(initialConfig);
        //setInitialPath(null);
        /* TODO: only allow pre-configured turtle tapes; */
        tape = input;
    }

    void updateProgress(double progressPercentage) {
        final int width = 10;

        try {
            log.write("\r[");
            int i = 0;
            for (; i <= (int) (progressPercentage * width); i++) {
                log.write(".");
            }
            for (; i < width; i++) {
                log.write(" ");
            }
            log.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBoundingBox() {
        minX = 0;
        minY = 0;
        maxX = 0;
        maxY = 0;
    }

    public void render() throws Exception {
        /* TODO: pre-render and boundary render at the same time (rewrite string during first render sequence) */
        final String currentTape = this.tape;

        final Field tapeField = String.class.getDeclaredField("value");
        tapeField.setAccessible(true);

        final char[] chars = (char[]) tapeField.get(currentTape);
        final int len = chars.length;
        updateProgress(0);
        for (int i = 0; i < len; i++) {
            if (i % 5000 == 0)
                updateProgress(((double) i) / len);

            doTurtleAction(chars[i]);
        }
        updateProgress(1);
        if (listener != null) {
            listener.end();
        }
    }

    boolean doTurtleAction(char tapeSymbol) {
        switch (tapeSymbol) {
            case 'g':
                return forward(true);
            case 'f':
                return forward(false);
            case '[':
                return pushStack();
            case ']':
                return popStack();
            case '-':
                return turn(-currentConfig.angleIncrement);
            case '+':
                return turn(currentConfig.angleIncrement);
            case '!':
                return widthChange(currentConfig.widthIncrement);
            case '~':
                return widthChange(-currentConfig.widthIncrement);
            case '#':
                return lineColorChange(currentConfig.colorIncrement);
            case '@':
                return lineColorChange(-currentConfig.colorIncrement);
            case '*':
                return polygonColorChange(currentConfig.colorIncrement);
            case '&':
                return polygonColorChange(-currentConfig.colorIncrement);
            case '%':
                return turn(180);
            case '{':
                return startPath();
            case '}':
                return endPath();
            case '/':
                return startLine();
            case '\\':
                return endLine();
            default:
                    /* TODO: Error checking? */
                try {
                    log.write("unknown tape symbol: '" + tapeSymbol + "'\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    private boolean pushStack() {
        configStack.push(currentConfig.clone());
        return false;
    }

    private boolean popStack() {
        if (configStack.empty()) {
            return false;
        }
        currentConfig = configStack.pop();
        return false;
    }


    private boolean startPath() {
        currentConfig.path = newPath();
        return false;
    }

    private GeneralPath newPath() {
        GeneralPath np = new GeneralPath();
        np.moveTo(currentConfig.x, currentConfig.y);
        return np;
    }

    private boolean endPath() {
        currentConfig.path.lineTo(currentConfig.x, currentConfig.y);
        if (listener != null) {
            listener.fillPath(currentConfig.path, currentConfig.getPolygonColor());
            return true;
        }
        return false;
    }

    private boolean startLine() {
        currentConfig.lnStartX = currentConfig.x;
        currentConfig.lnStartY = currentConfig.y;
        return false;
    }

    private boolean endLine() {
        if (listener != null) {
            listener.drawLine(currentConfig.lnStartX, currentConfig.x, currentConfig.lnStartY, currentConfig.y, currentConfig.width, currentConfig.getLineColor());
            return true;
        }
        return false;
    }

    private boolean forward(boolean penDown) {
        double radians = Math.toRadians(currentConfig.angle);
        double dx = Math.sin(radians) * currentConfig.length;
        double dy = -Math.cos(radians) * currentConfig.length;

        double px = currentConfig.x;
        double py = currentConfig.y;

        currentConfig.x += dx;
        currentConfig.y += dy;
        checkBoundingBox();

        if (currentConfig.path != null) {
            if (penDown) {
                currentConfig.path.lineTo(px, py);
            } else {
                currentConfig.path.moveTo(px, py);
            }
        } else if (penDown && listener != null) {
            listener.drawLine(px, currentConfig.x, py, currentConfig.y, currentConfig.width, currentConfig.getLineColor());
            return true;
        }
        return false;
    }

    private void checkBoundingBox() {
        if (currentConfig.x < minX) {
            minX = currentConfig.x;
        } else if (currentConfig.x > maxX) {
            maxX = currentConfig.x;
        }
        if (currentConfig.y < minY) {
            minY = currentConfig.y;
        } else if (currentConfig.y > maxY) {
            maxY = currentConfig.y;
        }
    }

    private boolean turn(double angle) {
        currentConfig.angle += angle;

        currentConfig.angle = currentConfig.angle % (360.0);
        return false;
    }

    private boolean lineColorChange(float hueChange) {
        currentConfig.addLineHue(hueChange);
        return false;
    }

    private boolean polygonColorChange(float hueChange) {
        currentConfig.addPolygonHue(hueChange);
        return false;
    }

    private boolean widthChange(double addWidth) {
        currentConfig.width += addWidth;
        return false;
    }

    public void setListener(GraphicsListener listener) {
        this.listener = listener;
    }

    public void reset() {
        emptyStack();
        pushStack();
        initBoundingBox();
    }

    private void emptyStack() {
        while (!configStack.empty()) {
            popStack();
        }
    }

    public void setInitialConfig(TurtleConfig config) {
        emptyStack();
        currentConfig = config;
        pushStack();
    }

    public TurtleConfig getCurrentConfig() {
        return currentConfig;
    }
}
