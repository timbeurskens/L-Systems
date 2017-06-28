package lsystem;

import java.util.HashSet;
import java.util.Set;

/**
 * L-System
 * Created by s154796 on 8-7-2016.
 */
public class StochasticString {
    private HashSet<StringPermutationCollection> elements = new HashSet<>();

    public void addProduction(String str, double p){
        StringPermutationCollection spc = new StringPermutationCollection(str, p);
        elements.add(spc);
    }

    public String getRandomProduction(){
        double pc = Math.random();
        for(StringPermutationCollection spc : elements){
            pc -= spc.getProbability();
            if(pc <= 0){
                return spc.getProduction();
            }
        }
        return null;
    }

    public Set<StringPermutationCollection> getElements(){
        return elements;
    }

    @Override
    public String toString(){
        StringBuilder strb = new StringBuilder();
        elements.forEach(stringPermutationCollection -> {
            strb.append(stringPermutationCollection.getProduction()).append(":").append(stringPermutationCollection.getProbability()).append(", ");
        });
        return strb.toString();
    }
}


