package main;

import graphics.ImagePreviewer;
import graphics.TurtleAnimator;
import lsystem.ContextSensitiveString;
import lsystem.LSystem;
import lsystem.RuleSet;
import lsystem.StochasticString;
import render.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * L-System
 * Created by s154796 on 3-7-2016.
 */
public class ApplicationLoader {
    static Pattern sectionPattern = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    static Pattern keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");
    static Pattern stochasticPattern = Pattern.compile("(\\S)[0-9]?\\[([^]]*)\\]");
    static Pattern contextSensitivePattern = Pattern.compile("((\\S)<)?(\\S)(>(\\S))?");

    private static HashMap<String, HashMap<String, String>> readConfiguration(String filename) throws IOException {
        HashMap<String, HashMap<String, String>> configuration = new HashMap<>();

        BufferedReader fileReader = null;
        File inputFile = new File(filename);
        try {
            fileReader = new BufferedReader(new FileReader(inputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;
        String section = null;
        if (fileReader != null) {
            while ((line = fileReader.readLine()) != null) {
                Matcher sectionMatcher = sectionPattern.matcher(line);
                Matcher keyValueMatcher = keyValuePattern.matcher(line);
                if (sectionMatcher.matches()) {
                    section = sectionMatcher.group(1).trim();
                } else if (section != null) {
                    if (keyValueMatcher.matches()) {
                        String key = keyValueMatcher.group(1).trim();
                        String value = keyValueMatcher.group(2).trim();
                        HashMap<String, String> sectionEntries = configuration.get(section);
                        if (sectionEntries == null) {
                            configuration.put(section, sectionEntries = new HashMap<>());
                        }
                        sectionEntries.put(key, value);
                    }
                }
            }
            fileReader.close();
        }
        return configuration;
    }

    public static String outputTextContent(String filename, String content) {
        File systemOutputFilePointer = new File(filename);
        FileWriter systemOutputStream = null;
        try {
            systemOutputStream = new FileWriter(systemOutputFilePointer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (systemOutputStream != null) {
            try {
                systemOutputStream.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                systemOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                systemOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return systemOutputFilePointer.getAbsolutePath();
    }

    private static BufferedImage createCompatibleImage(int width, int height) {
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        // image is not optimized, so create a new image that is
        BufferedImage new_image = gfx_config.createCompatibleImage(
                width, height, BufferedImage.TYPE_INT_ARGB);

        return new_image;
    }

    public static void main(String[] args) {
        for (String inputFileName : args) {
            File inputFile = new File(inputFileName);
            HashMap<String, HashMap<String, String>> inputConfig = null;
            if (!inputFile.exists() || !inputFile.canRead()) {
                System.out.println("Cannot read file!");
                return;
            }

            try {
                inputConfig = readConfiguration(inputFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (inputConfig == null) {
                System.out.println("Something went wrong reading the config file");
                return;
            }

            String imageOutputFile = inputFileName + "_output_image.png";
            String systemOutputFile = inputFileName + "_output_system.txt";
            String turtleOutputFile = inputFileName + "_output_turtle.txt";
            String svgOutputFile = inputFileName + "_output_turtle.svg";

            TurtleConfig config = new TurtleConfig();
            TurtleConfig secondConfig = new TurtleConfig();
            RuleSet systemRules = new RuleSet();
            TurtleSet turtleRules = new TurtleSet();
            String axiom = "";
            int numGenerations = 4;
            int imageBorder = 10;
            Color backgroundColor = Color.white;

            boolean outputSystemContent = false;
            boolean outputImageContent = true;
            boolean imagePreview = false;
            boolean outputTurtleContent = false;
            boolean outputSVGContent = false;
            boolean imageAnimation = false;
            char[] ignoreChars = new char[0];

            //read rules
            HashMap<String, String> systemRuleCollection = inputConfig.get("rules");
            if (systemRuleCollection != null) {
                systemRuleCollection.forEach((s, s2) -> {
                    Matcher stochasticMatcher = stochasticPattern.matcher(s);
                    Matcher sensitiveMatcher = contextSensitivePattern.matcher(s);
                    if (s.length() == 1) {
                        char key = s.charAt(0);
                        if (systemRules.containsKey(key)) {
                            if (systemRules.get(key) instanceof StochasticString) {
                                ((StochasticString) systemRules.get(key)).addProduction(s2, 1);
                            } else if (systemRules.get(key) instanceof ContextSensitiveString) {
                                ((ContextSensitiveString) systemRules.get(key)).addProduction(s2);
                            }
                        } else {
                            systemRules.put(key, s2);
                        }
                    } else if (stochasticMatcher.matches()) {
                        char symbol = stochasticMatcher.group(1).trim().charAt(0);
                        if (systemRules.containsKey(symbol)) {
                            Object ruleProduction = systemRules.get(symbol);
                            if (ruleProduction instanceof StochasticString) {
                                ((StochasticString) ruleProduction).addProduction(s2, Double.parseDouble(stochasticMatcher.group(2).trim()));
                            } else {
                                StochasticString ss = new StochasticString();
                                ss.addProduction(s2, Double.parseDouble(stochasticMatcher.group(2).trim()));
                                systemRules.put(symbol, ss);
                            }
                        } else {
                            StochasticString ss = new StochasticString();
                            ss.addProduction(s2, Double.parseDouble(stochasticMatcher.group(2).trim()));
                            systemRules.put(symbol, ss);
                        }
                    } else if (sensitiveMatcher.matches()) {
                        char symbol = sensitiveMatcher.group(3).trim().charAt(0);
                        if (systemRules.containsKey(symbol) && systemRules.get(symbol) instanceof ContextSensitiveString) {
                            String before = sensitiveMatcher.group(2);
                            String after = sensitiveMatcher.group(5);

                            char beforeChar = before != null ? before.trim().charAt(0) : '\0';
                            char afterChar = after != null ? after.trim().charAt(0) : '\0';
                            ((ContextSensitiveString) systemRules.get(symbol)).addProduction(s2, beforeChar, afterChar);
                        } else if (systemRules.containsKey(symbol) && systemRules.get(symbol) instanceof String) {
                            ContextSensitiveString cxs = new ContextSensitiveString();
                            cxs.addProduction((String) systemRules.get(symbol));
                            String before = sensitiveMatcher.group(2);
                            String after = sensitiveMatcher.group(5);

                            char beforeChar = before != null ? before.trim().charAt(0) : '\0';
                            char afterChar = after != null ? after.trim().charAt(0) : '\0';
                            cxs.addProduction(s2, beforeChar, afterChar);
                            systemRules.put(symbol, cxs);
                        } else {
                            ContextSensitiveString cxs = new ContextSensitiveString();
                            String before = sensitiveMatcher.group(2);
                            String after = sensitiveMatcher.group(5);

                            char beforeChar = before != null ? before.trim().charAt(0) : '\0';
                            char afterChar = after != null ? after.trim().charAt(0) : '\0';
                            cxs.addProduction(s2, beforeChar, afterChar);
                            systemRules.put(symbol, cxs);
                        }
                    }
                });
            }

            systemRules.printRules();

            turtleRules.feedRuleSet(systemRules);
            turtleRules.setDefault();

            //read turtle rules
            HashMap<String, String> turtleRuleCollection = inputConfig.get("turtle");
            if (turtleRuleCollection != null) {
                turtleRuleCollection.forEach((s, s2) -> {
                    if (s.length() == 1) {
                        turtleRules.put(s.charAt(0), s2);
                    }
                });
            }

            turtleRules.printRules();

            //read settings
            HashMap<String, String> settingsCollection = inputConfig.get("settings");
            if (settingsCollection != null) {
                axiom = settingsCollection.getOrDefault("axiom", "");
                numGenerations = Integer.parseInt(settingsCollection.getOrDefault("generations", "4"));
                imageBorder = Integer.parseInt(settingsCollection.getOrDefault("imageborder", "10"));
                config.angleIncrement = Double.parseDouble(settingsCollection.getOrDefault("angle", "15"));
                config.angle = Double.parseDouble(settingsCollection.getOrDefault("start_angle", "0"));
                config.length = Double.parseDouble(settingsCollection.getOrDefault("length", "15.0"));
                config.width = Double.parseDouble(settingsCollection.getOrDefault("width", "1.0"));
                config.widthIncrement = Double.parseDouble(settingsCollection.getOrDefault("width_increment", "1.0"));
                config.setLineColor(Color.decode(settingsCollection.getOrDefault("color", "#000000")));
                config.setPolygonColor(Color.decode(settingsCollection.getOrDefault("polygon_color", "#0000FF")));
                config.colorIncrement = Float.parseFloat(settingsCollection.getOrDefault("color_increment", "10.0"));

                ignoreChars = settingsCollection.getOrDefault("ignore", "").toCharArray();
                backgroundColor = Color.decode(settingsCollection.getOrDefault("background_color", "#FFFFFF"));
                outputImageContent = Boolean.parseBoolean(settingsCollection.getOrDefault("image_output", "True"));
                outputSystemContent = Boolean.parseBoolean(settingsCollection.getOrDefault("system_output", "False"));
                outputTurtleContent = Boolean.parseBoolean(settingsCollection.getOrDefault("turtle_output", "False"));
                outputSVGContent = Boolean.parseBoolean(settingsCollection.getOrDefault("svg_output", "False"));
                imagePreview = Boolean.parseBoolean(settingsCollection.getOrDefault("image_preview", "False"));
                imageAnimation = Boolean.parseBoolean(settingsCollection.getOrDefault("image_animation", "False"));
            }

            settingsCollection = null;

            System.out.println("Axiom: " + axiom);


            secondConfig = config.clone();


            LSystem mainSystem = new LSystem(axiom, systemRules);
            mainSystem.addToIgnoreList(ignoreChars);
            long startTime = System.nanoTime();
            for (int i = 1; i <= numGenerations; i++) {
                try {
                    mainSystem.step();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int sysLength = mainSystem.getTapeLength();
                if (sysLength < 150) {
                    System.out.println("Generation " + i + ": " + mainSystem.getTape());
                } else {
                    System.out.println("Generation " + i + ": [large output:" + sysLength + "]");
                }
            }
            long timeDiff = (System.nanoTime() - startTime) / 1000000;
            System.out.println("Duration: " + timeDiff + "ms");

            String systemOutput = mainSystem.getTape();

            mainSystem = null;

            if (outputSystemContent) {
                System.out.println(outputTextContent(systemOutputFile, systemOutput));
            }

            String turtleInputString = "";
            if (outputTurtleContent || outputImageContent || imagePreview || imageAnimation || outputSVGContent) {
                LSystem turtlePrepareSystem = new LSystem(systemOutput, turtleRules);
                try {
                    turtlePrepareSystem.step();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                turtleInputString = turtlePrepareSystem.getTape();
                turtlePrepareSystem = null;
            }

            if (outputTurtleContent) {
                System.out.println(outputTextContent(turtleOutputFile, turtleInputString));
            }

            Turtle turtle = null;
            TurtleConfig svgConfig = null;

            int width = -1;
            int height = -1;

            if (outputImageContent || imagePreview || imageAnimation) {
                turtle = new Turtle(turtleInputString, config);
                try {
                    turtle.render();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                secondConfig.x = -turtle.minX + imageBorder;
                secondConfig.y = -turtle.minY + imageBorder;
                svgConfig = secondConfig.clone();
                turtle.setInitialConfig(secondConfig);

                width = (int) Math.ceil(turtle.maxX - turtle.minX) + (2 * imageBorder);
                height = (int) Math.ceil(turtle.maxY - turtle.minY) + (2 * imageBorder);

                System.out.println("Image size: " + width + "x" + height);

                if (imageAnimation) {
                    StepTurtle sTurtle = null;

                    sTurtle = new StepTurtle(turtleInputString, secondConfig.clone());

                    TurtleAnimator animator = new TurtleAnimator(sTurtle, width, height, backgroundColor);
                    animator.initialize();
                    turtle.reset();
                }

                BufferedImage img = createCompatibleImage(width, height);

                Graphics2D graphics = img.createGraphics();
                graphics.setBackground(backgroundColor);
                graphics.clearRect(0, 0, img.getWidth(), img.getHeight());

                turtle.setGraphicsListener(new GraphicsListener() {
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
                });

                try {
                    turtle.render();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //turtle = null;
                graphics.dispose();

                if (imagePreview) {
                    ImagePreviewer imgp = new ImagePreviewer(img);
                    imgp.initialize();
                }

                if (outputImageContent) {
                    System.out.println("Saving image in background..");

                    new Thread(() -> {
                        File imageOutput = new File(imageOutputFile);
                        try {
                            ImageIO.write(img, "png", imageOutput);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(imageOutput.getAbsolutePath());
                    }).start();
                }
            }

            if (outputSVGContent) {
                if (turtle == null) {
                    turtle = new Turtle(turtleInputString, config);
                    try {
                        turtle.render();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    secondConfig.x = -turtle.minX + imageBorder;
                    secondConfig.y = -turtle.minY + imageBorder;

                    turtle.setInitialConfig(secondConfig);

                    width = (int) Math.ceil(turtle.maxX - turtle.minX) + (2 * imageBorder);
                    height = (int) Math.ceil(turtle.maxY - turtle.minY) + (2 * imageBorder);
                } else {
                    turtle.setInitialConfig(svgConfig);
                }

                System.out.println("SVG size: " + width + "x" + height);

                try {
                    FileWriter svgOut = new FileWriter(svgOutputFile);

                    String startSvgFile = "<svg width=\"" + width + "\" height=\"" + height + "\" viewPort=\"0 0 " + width + " " + height + "\" xmlns=\"http://www.w3.org/2000/svg\">";
                    String endSvgFile = "</svg>";

                    svgOut.write(startSvgFile);

                    GraphicsListener gl = new GraphicsListener() {
                        Color lastColor = null;
                        double lastWidth = -1;

                        boolean activePath = false;

                        double px = -1;
                        double py = -1;

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
                                svgOut.write(pathStart);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        public void appendPath(double x1, double y1, double x2, double y2) {
                            if (activePath) {
                                String append = "";
                                if (!(x1 == px && y1 == py)) {
                                    append += " M " + x1 + " " + y1;
                                }

                                append += " L " + x2 + " " + y2;
                                try {
                                    svgOut.write(append);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                px = x2;
                                py = y2;
                            }
                        }

                        public void endPath() {
                            if (activePath) {
                                activePath = false;
                                String pathEnd = "\"/>";
                                try {
                                    svgOut.write(pathEnd);
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
                            int type;
                            while (!pi.isDone()) {
                                type = pi.currentSegment(coords);
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
                        }
                    };

                    turtle.setGraphicsListener(gl);

                    turtle.render();

                    svgOut.write(endSvgFile);

                    svgOut.flush();
                    svgOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
