package lsystem;

import java.util.HashSet;
import java.util.Set;

/**
 * L-System
 * Created by s154796 on 19-8-2016.
 */
public class ContextSensitiveString {
    private HashSet<ContextProductionCollection> elements = new HashSet<>();

    public void addProduction(String p, char b, char a) {
        ContextProductionCollection cpc = new ContextProductionCollection(p, b, a);
        elements.add(cpc);
    }

    public void addProduction(String p) {
        ContextProductionCollection cpc = new ContextProductionCollection(p);
        elements.add(cpc);
    }

    public Set<ContextProductionCollection> getElements() {
        return elements;
    }

    public String getProduction(ContextChecker checker) {
        int numMatches = -1;
        String currentProduction = null;
        for (ContextProductionCollection element : elements) {
            if (checker.check(element)) {
                //System.out.println("Match");
                //ApplicationLoader.logLine("!");
                int score = element.getScore();
                if (score > numMatches) {
                    numMatches = score;
                    currentProduction = element.getProduction();
                }
            }else{
                //System.out.println("No match");
            }
        }
        //ApplicationLoader.logLine("---");
        return currentProduction;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        elements.forEach(contextProductionCollection -> strb
            .append(contextProductionCollection.getProduction())
            .append("(")
            .append(contextProductionCollection.getBefore())
            .append("<>")
            .append(contextProductionCollection.getAfter())
            .append(")").append(", "));
        return strb.toString();
    }
}
