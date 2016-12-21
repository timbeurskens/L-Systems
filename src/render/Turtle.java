package render;

import java.awt.geom.GeneralPath;
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
    private Stack<TurtleConfig> configStack;
    private TurtleConfig currentConfig;

    public Turtle(String input, TurtleConfig initialConfig) {
        configStack = new Stack<>();
        //pathStack = new Stack<>();
        setInitialConfig(initialConfig);
        //setInitialPath(null);
        /* TODO: only allow pre-configured turtle tapes; */
        tape = input;
    }

    static void updateProgress(double progressPercentage) {
        final int width = 10;

        System.out.print("\r[");
        int i = 0;
        for (; i <= (int) (progressPercentage * width); i++) {
            System.out.print(".");
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
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

    protected void doTurtleAction(char tapeSymbol) {
        switch (tapeSymbol) {
            case 'g':
                forward(true);
                break;
            case 'f':
                forward(false);
                break;
            case '[':
                pushStack();
                break;
            case ']':
                popStack();
                break;
            case '-':
                turn(-currentConfig.angleIncrement);
                break;
            case '+':
                turn(currentConfig.angleIncrement);
                break;
            case '!':
                widthChange(currentConfig.widthIncrement);
                break;
            case '~':
                widthChange(-currentConfig.widthIncrement);
                break;
            case '#':
                lineColorChange(currentConfig.colorIncrement);
                break;
            case '@':
                lineColorChange(-currentConfig.colorIncrement);
                break;
            case '*':
                polygonColorChange(currentConfig.colorIncrement);
                break;
            case '&':
                polygonColorChange(-currentConfig.colorIncrement);
                break;
            case '%':
                turn(180);
                break;
            case '{':
                startPath();
                break;
            case '}':
                endPath();
                break;
            default:
                    /* TODO: Error checking? */
                System.out.println("unknown tape symbol: '" + tapeSymbol + "'");
        }
    }

    private void pushStack() {

        configStack.push(currentConfig.clone());

    }

    private void popStack() {
        if (configStack.empty()) {
            return;
        }
        currentConfig = configStack.pop();
    }


    private void startPath() {
        currentConfig.path = newPath();
    }

    private GeneralPath newPath() {
        GeneralPath np = new GeneralPath();
        np.moveTo(currentConfig.x, currentConfig.y);
        return np;
    }

    private void endPath() {
        currentConfig.path.lineTo(currentConfig.x, currentConfig.y);
        if (listener != null) {
            listener.fillPath(currentConfig.path, currentConfig.getPolygonColor());
        }
    }

    private void forward(boolean penDown) {
        double radians = Math.toRadians(currentConfig.angle);
        double dx = Math.sin(radians) * currentConfig.length;
        double dy = -Math.cos(radians) * currentConfig.length;
        currentConfig.x += dx;
        currentConfig.y += dy;
        checkBoundingBox();

        if (currentConfig.path != null) {
            if (penDown) {
                currentConfig.path.lineTo(currentConfig.x - dx, currentConfig.y - dy);
            } else {
                currentConfig.path.moveTo(currentConfig.x - dx, currentConfig.y - dy);
            }
        } else if (penDown && listener != null) {
            listener.drawLine(currentConfig.x - dx, currentConfig.x, currentConfig.y - dy, currentConfig.y, currentConfig.width, currentConfig.getLineColor());
        }
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

    private void turn(double angle) {
        currentConfig.angle += angle;
    }

    private void lineColorChange(float hueChange) {
        currentConfig.addLineHue(hueChange);
    }

    private void polygonColorChange(float hueChange) {
        currentConfig.addPolygonHue(hueChange);
    }

    private void widthChange(double addWidth) {
        currentConfig.width += addWidth;
    }

    public void setGraphicsListener(GraphicsListener listener) {
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
