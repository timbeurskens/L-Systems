package lsystem;

import java.lang.reflect.Field;

/**
 * L-System
 * Created by s154796 on 3-7-2016.
 */
public class LSystem {
    private RuleSet productionRules;
    private String tape;

    public LSystem(String axiom, RuleSet productions){
        this.tape = axiom;
        this.productionRules = productions;
    }

    public void step() throws Exception {
        StringBuilder newTape = new StringBuilder();
        final String oldTape = this.tape;
        final Field tapeField = String.class.getDeclaredField("value");
        tapeField.setAccessible(true);

        final char[] chars = (char[]) tapeField.get(oldTape);
        final int len = chars.length;
        for(int i = 0; i < len; i++){
            Object production = productionRules.get(chars[i]);
            if(production != null){
                if(production instanceof String) {
                    newTape.append(production);
                }else if(production instanceof StochasticString){
                    newTape.append(((StochasticString) production).getRandomProduction());
                } else if (production instanceof ContextSensitiveString) {
                    newTape.append(((ContextSensitiveString) production).getProduction((before, after) -> {
                        System.out.println("hi");
                        return false;
                    }));
                }
            }else{
                newTape.append(chars[i]);
            }
        }

        tape = newTape.toString();
    }

    public String getTape(){
        return tape;
    }

    public int getTapeLength(){
        return tape.length();
    }

    public RuleSet getProductionRules(){
        return productionRules;
    }

    public void print(){
        System.out.println(tape);
    }
}
