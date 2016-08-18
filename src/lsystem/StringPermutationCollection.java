package lsystem;

/**
 * L-System
 * Created by s154796 on 8-7-2016.
 */
public class StringPermutationCollection{
    private String production;
    private int chance;
    public StringPermutationCollection(String str, int p){
        this.production = str;
        this.chance = p;
    }

    public int getChance(){
        return chance;
    }

    public String getProduction(){
        return production;
    }
}