package main;

import graphics.ImagePreviewer;
import graphics.TurtleAnimator;
import lsystem.ContextSensitiveString;
import lsystem.LSystem;
import lsystem.RuleSet;
import lsystem.StochasticString;
import render.*;
import render.renderers.CompoundListener;
import render.renderers.GraphicsRenderer;
import render.renderers.SVGCompiler;

import javax.imageio.ImageIO;
import java.awt.*;
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
    static final Pattern sectionPattern = Pattern.compile("\\s*\\[([^]]*)]\\s*");
    static final Pattern keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");
    static final Pattern stochasticPattern = Pattern.compile("(\\S)[0-9]?\\[([^]]*)]");
    static final Pattern contextSensitivePattern = Pattern.compile("((\\S)?<)?(\\S)(>(\\S)?)?");

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
                        HashMap<String, String> sectionEntries = configuration.computeIfAbsent(section, k -> new HashMap<>());
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
            String svgOutputFile = inputFileName + "_output_vector.svg";

            TurtleConfig config = new TurtleConfig();
            TurtleConfig secondConfig = null;

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
            char[] blockStartChars = new char[0];
            char[] blockEndChars = new char[0];

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

                blockStartChars = settingsCollection.getOrDefault("block_start", "").toCharArray();
                blockEndChars = settingsCollection.getOrDefault("block_end", "").toCharArray();
                ignoreChars = settingsCollection.getOrDefault("ignore", "").toCharArray();
                backgroundColor = Color.decode(settingsCollection.getOrDefault("background_color", "#FFFFFF"));
                outputImageContent = Boolean.parseBoolean(settingsCollection.getOrDefault("image_output", "True"));
                outputSystemContent = Boolean.parseBoolean(settingsCollection.getOrDefault("system_output", "False"));
                outputTurtleContent = Boolean.parseBoolean(settingsCollection.getOrDefault("turtle_output", "False"));
                outputSVGContent = Boolean.parseBoolean(settingsCollection.getOrDefault("svg_output", "False"));
                imagePreview = Boolean.parseBoolean(settingsCollection.getOrDefault("image_preview", "False"));
                imageAnimation = Boolean.parseBoolean(settingsCollection.getOrDefault("image_animation", "False"));
            }

            System.out.println("Axiom: " + axiom);

            secondConfig = config.clone();

            LSystem mainSystem = new LSystem(axiom, systemRules);
            mainSystem.addToIgnoreList(ignoreChars);
            mainSystem.addToBlockStartList(blockEndChars);
            mainSystem.addToBlockEndList(blockEndChars);


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
            }

            if (outputTurtleContent) {
                System.out.println(outputTextContent(turtleOutputFile, turtleInputString));
            }

            int width = -1;
            int height = -1;

            Turtle turtle = new Turtle(turtleInputString, config);
            TurtleConfig svgConfig = null;
            CompoundListener listener = new CompoundListener();

            try {
                turtle.render();
            } catch (Exception e) {
                e.printStackTrace();
            }

            secondConfig.x = -turtle.minX + imageBorder;
            secondConfig.y = -turtle.minY + imageBorder;

            //turtle.reset();
            turtle.setInitialConfig(secondConfig);

            width = (int) Math.ceil(turtle.maxX - turtle.minX) + (2 * imageBorder);
            height = (int) Math.ceil(turtle.maxY - turtle.minY) + (2 * imageBorder);

            System.out.println("Image size: " + width + "x" + height);

            //turtle.reset();
            turtle.setListener(listener);

            BufferedImage img = null;
            Graphics2D graphics = null;

            if (imageAnimation) {
                StepTurtle sTurtle;

                sTurtle = new StepTurtle(turtleInputString, secondConfig.clone());

                TurtleAnimator animator = new TurtleAnimator(sTurtle, width, height, backgroundColor);
                animator.initialize();
            }

            if (outputImageContent || imagePreview) {
                img = createCompatibleImage(width, height);

                graphics = img.createGraphics();
                graphics.setBackground(backgroundColor);
                graphics.clearRect(0, 0, img.getWidth(), img.getHeight());

                listener.addListener(new GraphicsRenderer(graphics));
            }

            if (outputSVGContent) {
                System.out.println("SVG size: " + width + "x" + height);

                try {
                    GraphicsListener gl = new SVGCompiler(svgOutputFile, width, height);
                    listener.addListener(gl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                System.out.println(turtle.getCurrentConfig());
                turtle.render();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (graphics != null) {
                graphics.dispose();
            }

            if (imagePreview) {
                ImagePreviewer imgp = new ImagePreviewer(img);
                imgp.initialize();
            }

            if (outputImageContent) {
                System.out.println("Saving image in background..");

                BufferedImage finalImg = img;
                new Thread(() -> {
                    File imageOutput = new File(imageOutputFile);
                    try {
                        ImageIO.write(finalImg, "png", imageOutput);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(imageOutput.getAbsolutePath());
                }).start();
            }
        }
    }
}
