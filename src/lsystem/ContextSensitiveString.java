package lsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * L-System
 * Created by s154796 on 19-8-2016.
 */
public class ContextSensitiveString {
    private ArrayList<ContextProductionCollection> elements = new ArrayList<>();

    public void addProduction(String p, char b, char a) {
        ContextProductionCollection cpc = new ContextProductionCollection(p, b, a);
        elements.add(cpc);
    }

    public List<ContextProductionCollection> getElements() {
        return elements;
    }

    public String getProduction(ContextChecker checker) {
        int numMatches = -1;
        String currentProduction = "";
        for (ContextProductionCollection element : elements) {
            if (checker.check(element)) {
                int score = element.getScore();
                if (score > numMatches) {
                    numMatches = score;
                    currentProduction = element.getProduction();
                }
            }
        }
        return currentProduction;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        elements.forEach(contextProductionCollection -> {
            strb.append(contextProductionCollection.getBefore()).append(" < ").append(contextProductionCollection.getProduction()).append(" > ").append(contextProductionCollection.getAfter()).append(", ");
        });
        return strb.toString();
    }
}
