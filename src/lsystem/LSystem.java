package lsystem;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * L-System
 * Created by s154796 on 3-7-2016.
 */
public class LSystem {
    private RuleSet productionRules;
    private String tape;
    private ArrayList<Character> ignoreList = new ArrayList<>();

    public LSystem(String axiom, RuleSet productions){
        this.tape = axiom;
        this.productionRules = productions;
    }

    public void addToIgnoreList(char[] chars) {
        for (char c : chars) {
            ignoreList.add(c);
        }
    }

    public void step() throws Exception {
        StringBuilder newTape = new StringBuilder();
        final String oldTape = this.tape;
        final Field tapeField = String.class.getDeclaredField("value");
        tapeField.setAccessible(true);

        final char[] chars = (char[]) tapeField.get(oldTape);
        final int len = chars.length;
        for(int i = 0; i < len; i++){
            final int currentIndex = i;
            Object production = productionRules.get(chars[i]);
            if(production != null){
                if(production instanceof String) {
                    newTape.append(production);
                }else if(production instanceof StochasticString){
                    newTape.append(((StochasticString) production).getRandomProduction());
                } else if (production instanceof ContextSensitiveString) {
                    String result = ((ContextSensitiveString) production).getProduction((collection) -> {
                        //CONTEXT MATCHER:
                        char leftChar = '\0';
                        char rightChar = '\0';
                        int checkerIndex = currentIndex - 1;
                        int layer = 0;
                        while (checkerIndex >= 0 && leftChar == '\0' && layer >= 0) {
                            if (chars[checkerIndex] == '[') {
                                layer++;
                            } else if (chars[checkerIndex] == ']') {
                                layer--;
                            }
                            if (!ignoreList.contains(chars[checkerIndex]) && layer == 0) {
                                leftChar = chars[checkerIndex];
                            }
                            checkerIndex--;
                        }
                        checkerIndex = currentIndex + 1;
                        layer = 0;
                        while (checkerIndex < chars.length && rightChar == '\0' && layer >= 0) {
                            if (chars[checkerIndex] == '[') {
                                layer++;
                            } else if (chars[checkerIndex] == ']') {
                                layer--;
                            }
                            if (!ignoreList.contains(chars[checkerIndex]) && layer == 0) {
                                rightChar = chars[checkerIndex];
                            }
                            checkerIndex++;
                        }

                        if (!collection.isEmptyBefore()) {
                            if (currentIndex <= 0) {
                                if (collection.getBefore() != '<') {  //match begin string
                                    return false;
                                }
                            } else if (currentIndex > 0) {
                                if ((!collection.isAnyBefore()) && collection.getBefore() != leftChar) {
                                    return false;
                                }
                            }
                        }
                        if (!collection.isEmptyAfter()) {
                            if (currentIndex >= chars.length - 1) {
                                if (collection.getAfter() != '>') {  //match end string
                                    return false;
                                }
                            } else if (currentIndex < chars.length - 1) {
                                if ((!collection.isAnyAfter()) && collection.getAfter() != rightChar) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    });
                    newTape.append(result != null ? result : chars[i]);
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
