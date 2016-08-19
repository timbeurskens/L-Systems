package lsystem;

/**
 * L-System
 * Created by s154796 on 19-8-2016.
 */
public class ContextProductionCollection {
    private String production;
    private String before;
    private String after;

    public ContextProductionCollection(String p, String b, String a) {
        this.production = p;
        this.before = b;
        this.after = a;
    }

    public int getBeforeLength() {
        return before.length();
    }

    public int getAfterLength() {
        return after.length();
    }

    public boolean isEmptyBefore() {
        return before == null || getBeforeLength() == 0;
    }

    public boolean isEmptyAfter() {
        return after == null || getAfterLength() == 0;
    }

    public String getBefore() {
        return before;
    }

    public String getAfter() {
        return after;
    }

    public String getProduction() {
        return production;
    }
}
