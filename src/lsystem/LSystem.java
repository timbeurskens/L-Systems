package lsystem;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * L-System
 * Created by s154796 on 3-7-2016.
 */

/**
 * More optimal string replacer
 * <p>
 * Map<String,String> tokens = new HashMap<String,String>();
 * tokens.put("cat", "Garfield");
 * tokens.put("beverage", "coffee");
 * <p>
 * String template = "%cat% really needs some %beverage%.";
 * <p>
 * // Create pattern of the format "%(cat|beverage)%"
 * String patternString = "%(" + StringUtils.join(tokens.keySet(), "|") + ")%";
 * Pattern pattern = Pattern.compile(patternString);
 * Matcher matcher = pattern.matcher(template);
 * <p>
 * StringBuffer sb = new StringBuffer();
 * while(matcher.find()) {
 * matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
 * }
 * matcher.appendTail(sb);
 * <p>
 * System.out.println(sb.toString());
 */
public class LSystem {
    private RuleSet productionRules;
    private String tape;
    private Set<Character> ignoreList = new HashSet<>();
    private Set<Character> blockStartList = new HashSet<>();
    private Set<Character> blockEndList = new HashSet<>();

    public LSystem(String axiom, RuleSet productions) {
        this.tape = axiom;
        this.productionRules = productions;
    }

    public void addToIgnoreList(char[] chars) {
        for (char c : chars) {
            ignoreList.add(c);
        }
    }

    public void addToBlockStartList(char[] chars) {
        for (char c : chars) {
            blockStartList.add(c);
        }
    }

    public void addToBlockEndList(char[] chars) {
        for (char c : chars) {
            blockEndList.add(c);
        }
    }

    public void step() throws Exception {
        StringBuilder newTape = new StringBuilder();
        final String oldTape = this.tape;
        final Field tapeField = String.class.getDeclaredField("value");
        tapeField.setAccessible(true);

        final char[] chars = (char[]) tapeField.get(oldTape);
        final int len = chars.length;
        for (int i = 0; i < len; i++) {
            final int currentIndex = i;
            Object production = productionRules.get(chars[i]);
            if (production != null) {
                if (production instanceof String) {
                    newTape.append(production);
                } else if (production instanceof StochasticString) {
                    newTape.append(((StochasticString) production).getRandomProduction());
                } else if (production instanceof ContextSensitiveString) {
                    String result = ((ContextSensitiveString) production).getProduction((collection) -> {
                        //CONTEXT MATCHER:
                        char leftChar = '\0';
                        char rightChar = '\0';

                        int checkerIndex = currentIndex - 1;
                        int layer = 0;

                        while (checkerIndex >= 0 && leftChar == '\0' && layer >= 0) {
                            if (blockStartList.contains(chars[checkerIndex])) {
                                layer++;
                            } else if (blockEndList.contains(chars[checkerIndex])) {
                                layer--;
                            }
                            if (!ignoreList.contains(chars[checkerIndex]) && layer == 0) {
                                leftChar = chars[checkerIndex];
                            }
                            checkerIndex--;
                        }

                        if (checkerIndex < 0 && leftChar == '\0' && layer >= 0) {
                            leftChar = '<';
                        }

                        checkerIndex = currentIndex + 1;
                        layer = 0;

                        while (checkerIndex < chars.length && rightChar == '\0' && layer >= 0) {
                            if (blockStartList.contains(chars[checkerIndex])) {
                                layer++;
                            } else if (blockEndList.contains(chars[checkerIndex])) {
                                layer--;
                            }
                            if (!ignoreList.contains(chars[checkerIndex]) && layer == 0) {
                                rightChar = chars[checkerIndex];
                            }
                            checkerIndex++;
                        }

                        if (checkerIndex >= chars.length && rightChar == '\0' && layer >= 0) {
                            rightChar = '>';
                        }

                        //System.out.println(leftChar + "<" + chars[currentIndex] + ">" + rightChar + " - " + collection.getBefore() + "<" + chars[currentIndex] + ">" + collection.getAfter());

                        if (!collection.isEmptyBefore()) {
                            if (!collection.isAnyBefore() && collection.getBefore() != leftChar) {
                                return false;
                            }
                            /*if (currentIndex <= 0) {
                                if (collection.getBefore() != '<') {  //match begin string
                                    return false;
                                }
                            } else if (currentIndex > 0) {
                                if ((!collection.isAnyBefore()) && collection.getBefore() != leftChar) {
                                    return false;
                                }
                            }*/
                        }

                        if (!collection.isEmptyAfter()) {
                            if (!collection.isAnyAfter() && collection.getAfter() != rightChar) {
                                return false;
                            }
                            /*if (currentIndex >= chars.length - 1) {
                                if (collection.getAfter() != '>') {  //match end string
                                    return false;
                                }
                            } else if (currentIndex < chars.length - 1) {
                                if ((!collection.isAnyAfter()) && collection.getAfter() != rightChar) {
                                    return false;
                                }
                            }*/
                        }
                        return true;
                    });
                    newTape.append(result != null ? result : chars[i]);
                }
            } else {
                newTape.append(chars[i]);
            }
        }

        tape = newTape.toString();
    }

    public String getTape() {
        return tape;
    }

    public int getTapeLength() {
        return tape.length();
    }

    public RuleSet getProductionRules() {
        return productionRules;
    }

    public void print() {
        System.out.println(tape);
    }
}
