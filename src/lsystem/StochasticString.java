package lsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * L-System
 * Created by s154796 on 8-7-2016.
 */
public class StochasticString {
    private ArrayList<StringPermutationCollection> elements = new ArrayList<>();

    public void addProduction(String str, double p){
        StringPermutationCollection spc = new StringPermutationCollection(str, (int) (p * 1000));
        elements.add(spc);
    }

    public String getRandomProduction(){
        int pc = (int) (Math.random() * 1000);
        for(StringPermutationCollection spc : elements){
            pc -= spc.getChance();
            if(pc <= 0){
                return spc.getProduction();
            }
        }
        return null;
    }

    public List<StringPermutationCollection> getElements(){
        return elements;
    }

    @Override
    public String toString(){
        StringBuilder strb = new StringBuilder();
        elements.forEach(stringPermutationCollection -> {
            strb.append(stringPermutationCollection.getProduction() + ":" + stringPermutationCollection.getChance() / 1000.0 + ", ");
        });
        return strb.toString();
    }
}


