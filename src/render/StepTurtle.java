package render;

/**
 * L-System
 * Created by s154796 on 8-7-2016.
 */
public class StepTurtle extends Turtle{
    private int currentTapePosition = 0;
    private char[] chars;


    public StepTurtle(String input, TurtleConfig initialConfig) {
        super(input, initialConfig);
        initializeTape();
    }

    public boolean step(){
        if(currentTapePosition < chars.length){
            doTurtleAction(chars[currentTapePosition]);
            currentTapePosition++;
            return true;
        }else{
            if (listener != null) {
                listener.end();
            }
            return false;
        }
    }

    private void initializeTape(){
        chars = tape.toCharArray();
    }

    public int getCurrentTapePosition(){
        return currentTapePosition;
    }

    public double getPositionFraction(){
        return (double)currentTapePosition / (double)tape.length();
    }

    public void resetTapePosition(){
        initializeTape();
        currentTapePosition = 0;
    }
}
