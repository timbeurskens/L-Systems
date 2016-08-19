package lsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * L-System
 * Created by s154796 on 19-8-2016.
 */
public class ContextSensitiveString {
    private ArrayList<ContextProductionCollection> elements = new ArrayList<>();

    public void addProduction(String p, String b, String a) {
        ContextProductionCollection cpc = new ContextProductionCollection(p, b, a);
        elements.add(cpc);
    }

    public List<ContextProductionCollection> getElements() {
        return elements;
    }

    public String getProduction(ContextChecker checker) {

        return "";
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        elements.forEach(contextProductionCollection -> {
            strb.append(contextProductionCollection.getBefore() + " < " + contextProductionCollection.getProduction() + " > " + contextProductionCollection.getAfter() + ", ");
        });
        return strb.toString();
    }
}
