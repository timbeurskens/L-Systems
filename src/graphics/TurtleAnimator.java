package graphics;

import render.GraphicsListener;
import render.StepTurtle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

/**
 * L-System
 * Created by s154796 on 8-7-2016.
 */
public class TurtleAnimator extends JFrame {
    private Color background;
    private int width, height;
    private StepTurtle turtle;
    private JLabel statusLabel = new JLabel();
    private Timer animationTimer = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean result = step();
            if(!result){
                animationTimer.stop();
            }
        }
    });

    public TurtleAnimator(StepTurtle t, int width, int height, Color background){
        super("Animator");
        this.background = background;
        this.width = width;
        this.height = height;
        this.turtle = t;
    }

    public boolean step(){
        boolean result = turtle.step();
        double p = turtle.getPositionFraction();
        statusLabel.setText(String.valueOf(Math.round(p * 1000) / 10.0) + "%");
        return result;
    }

    public void initialize(){
        SwingUtilities.invokeLater(() -> {
            JPanel controls = new JPanel();
            this.add(controls, BorderLayout.NORTH);

            Button stepButton = new Button("Step");
            controls.add(stepButton);

            Button resetButton = new Button("Reset");
            controls.add(resetButton);

            JSlider slider = new JSlider(1, 1000);
            slider.setOrientation(JSlider.HORIZONTAL);

            slider.addChangeListener(e -> animationTimer.setDelay(1000 / slider.getValue()));

            slider.setValue(100);

            controls.add(slider);

            Button startStopButton = new Button("Start / Stop");
            controls.add(startStopButton);

            statusLabel.setText("0%");
            controls.add(statusLabel);

            startStopButton.addActionListener(e -> {
                if(animationTimer.isRunning()){
                    animationTimer.stop();
                }else{
                    animationTimer.start();
                }
            });

            stepButton.addActionListener(e -> step());

            GraphicsPanel p = new GraphicsPanel(background, width, height);
            this.add(p, BorderLayout.CENTER);

            this.turtle.setListener(new GraphicsListener() {
                @Override
                public void drawLine(double x1, double x2, double y1, double y2, double width, Color color) {
                    p.imageGraphics.setColor(color);
                    p.imageGraphics.setStroke(new BasicStroke((float)(width >= 0 ? width : 0)));
                    p.imageGraphics.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
                    p.repaint();
                }

                @Override
                public void fillPath(GeneralPath polygon, Color color) {
                    p.imageGraphics.setColor(color);
                    p.imageGraphics.setStroke(new BasicStroke((float) 0));
                    //p.imageGraphics.fillPolygon(polygon);
                    p.imageGraphics.fill(polygon);
                    p.repaint();
                }

                @Override
                public void end() {

                }
            });

            resetButton.addActionListener(e -> {
                turtle.reset();
                turtle.resetTapePosition();
                p.clear();
                p.repaint();
            });

            this.pack();
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.setResizable(false);
            this.setVisible(true);
        });
    }
}

class GraphicsPanel extends JPanel{
    BufferedImage drawnImage = null;
    Graphics2D imageGraphics = null;
    private int maxOutputWidth = 1600;
    private int maxOutputHeight = 900;

    public GraphicsPanel(Color background, int width, int height){
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        // image is not optimized, so create a new image that is
        drawnImage = gfx_config.createCompatibleImage(
                width, height, BufferedImage.TYPE_INT_ARGB);

        imageGraphics = (Graphics2D) drawnImage.getGraphics();

        imageGraphics.setBackground(background);
        imageGraphics.clearRect(0, 0, drawnImage.getWidth(), drawnImage.getHeight());

        double pWidth = (double)maxOutputWidth / (double)drawnImage.getWidth();
        double pHeight = (double)maxOutputHeight / (double)drawnImage.getHeight();
        double minProp = Math.min(pHeight, pWidth);
        setPreferredSize(new Dimension((int)(drawnImage.getWidth() * minProp), (int)(drawnImage.getHeight() * minProp)));
    }

    public void clear(){
        imageGraphics.clearRect(0, 0, drawnImage.getWidth(), drawnImage.getHeight());
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D)g;
        graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));
        graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
        graphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
        double pWidth = (double)maxOutputWidth / (double)drawnImage.getWidth();
        double pHeight = (double)maxOutputHeight / (double)drawnImage.getHeight();
        double minProp = Math.min(pHeight, pWidth);
        graphics.drawImage(drawnImage, 0, 0, (int)(drawnImage.getWidth() * minProp), (int)(drawnImage.getHeight() * minProp), null);
    }
}