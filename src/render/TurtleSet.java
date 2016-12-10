package render;

import lsystem.ContextSensitiveString;
import lsystem.RuleSet;
import lsystem.StochasticString;

import java.util.HashSet;

/**
 * L-System
 * Created by s154796 on 3-7-2016.
 */
public class TurtleSet extends RuleSet {
    public TurtleSet(RuleSet productionSet){
        feedRuleSet(productionSet);
    }

    public TurtleSet(){

    }

    public void feedRuleSet(RuleSet productionSet){
        productionSet.forEach((character, s) -> {
            if(s instanceof String){
                char[] production = ((String) s).toCharArray();
                for (char aProduction : production) {
                    addEmpty(aProduction);
                }
            }else if(s instanceof StochasticString){
                ((StochasticString) s).getElements().forEach(stringPermutationCollection -> {
                    char[] production = stringPermutationCollection.getProduction().toCharArray();
                    for (char aProduction : production) {
                        addEmpty(aProduction);
                    }
                });
            } else if (s instanceof ContextSensitiveString) {
                ((ContextSensitiveString) s).getElements().forEach(contextProductionCollection -> {
                    char[] production = contextProductionCollection.getProduction().toCharArray();
                    for (char aProduction : production) {
                        addEmpty(aProduction);
                    }
                });
            }
        });
    }

    public void addFwd(char c){
        put(c, "g");
    }

    public void addStep(char c){
        put(c, "f");
    }

    public void addPush(char c){
        put(c, "[");
    }

    public void addRecover(char c){
        put(c, "]");
    }

    public void addPolygonStart(char c){
        put(c, "{");
    }

    public void addPolygonEnd(char c){
        put(c, "}");
    }

    public void addCurveStart(char c){
        put(c, "(");
    }

    public void addCurveEnd(char c){
        put(c, ")");
    }

    public void addLeft(char c){
        put(c, "-");
    }

    public void addRight(char c){
        put(c, "+");
    }

    public void addIncrementWidth(char c){
        put(c, "!");
    }

    public void addDecrementWidth(char c){
        put(c, "~");
    }

    public void addIncrementLineHue(char c){
        put(c, "#");
    }

    public void addDecrementLineHue(char c){
        put(c, "@");
    }

    public void addIncrementPolygonHue(char c){
        put(c, "*");
    }

    public void addDecrementPolygonHue(char c){
        put(c, "&");
    }

    public void addFlip(char c){
        put(c, "%");
    }

    public void setDefault(){
        addFwd('g');
        addStep('f');
        addPush('[');
        addRecover(']');
        addLeft('-');
        addRight('+');
        addIncrementWidth('!');
        addDecrementWidth('~');
        addIncrementLineHue('#');
        addDecrementLineHue('@');
        addIncrementPolygonHue('*');
        addDecrementPolygonHue('&');
        addPolygonStart('{');
        addPolygonEnd('}');
        addFlip('%');
        addCurveStart('(');
        addCurveEnd(')');
    }

    public void addEmpty(char c){
        put(c, "");
    }
}
