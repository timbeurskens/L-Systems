package lsystem;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * L-System
 * Created by s154796 on 3-7-2016.
 */
public class RuleSet extends HashMap<Character, Object>{
    public void printRules(){
        System.out.println("Ruleset:");
        this.forEach((character, s) -> {
            System.out.println(character + " -> " + s);
        });
    }
}
