package main;

import carpetdesigner.DesignerFrame;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * L-System
 * Created by s154796 on 10-8-2016.
 */
public class CarpetDesigner {
    static Pattern sizePattern  = Pattern.compile( "(\\d+)x(\\d+)" );
    public static void main(String[] args){
        DesignerFrame frame = new DesignerFrame("Carpet Designer", 3, 3);
        for(int i = 0; i < args.length; i++){
            String arg = args[i];
            switch (arg){
                case "-o":
                    i++;
                    String outputFileArg = args[i];
                    frame.setOutFile(outputFileArg);
                    break;
                case "-s":
                    i++;
                    String sizeArg = args[i];
                    Matcher sizeMatcher = sizePattern.matcher(sizeArg);
                    if(sizeMatcher.matches()){
                        int width = Integer.parseInt(sizeMatcher.group(1));
                        int height = Integer.parseInt(sizeMatcher.group(2));
                        frame.setSize(width, height);
                    }
                    break;
                case "-i":
                    frame.setDefaultValue(true);
                    break;
                case "-f":
                    frame.setJumpSequence("g");
                    break;
                case "-e":
                    frame.setExtended(true);
                    break;
                default:
                    frame.putSetting(arg);
            }
        }

        frame.initialize();
    }
}
