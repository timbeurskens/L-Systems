package lsystem;

/**
 * L-System
 * Created by s154796 on 19-8-2016.
 */
public class ContextProductionCollection {
    private String production;
    private char before = '\0';
    private char after = '\0';

    public ContextProductionCollection(String p, char b, char a) {
        this.production = p;
        this.before = b;
        this.after = a;
    }

    public int getScore() {
        return (isEmptyBefore() ? 0 : 1) + (isEmptyAfter() ? 0 : 1);
    }

    public boolean isEmptyBefore() {
        return before == '\0';
    }

    public boolean isEmptyAfter() {
        return after == '\0';
    }

    public char getBefore() {
        return before;
    }

    public char getAfter() {
        return after;
    }

    public String getProduction() {
        return production;
    }
}
