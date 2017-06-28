package lsystem;

/**
 * L-System
 * Created by s154796 on 8-7-2016.
 */
public class StringPermutationCollection{
    private String production;
    private double probability;

    public StringPermutationCollection(String str, double p) {
        this.production = str;
        this.probability = p;
    }

    public double getProbability() {
        return probability;
    }

    public String getProduction(){
        return production;
    }
}