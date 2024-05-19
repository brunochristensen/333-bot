package net.brunochristensen._333bot.features.accountability;

import java.util.HashMap;
import java.util.Map;

public enum AccountabilityComponentID {
    ADDBUTTON("addButton"),
    DELBUTTON("delButton"),
    VIEWBUTTON("viewButton"),
    SKIPBUTTON("skipButton"),
    GETBUTTON("getButton");

    private static final Map<String, AccountabilityComponentID> BY_LABEL = new HashMap<>();

    static {
        for (AccountabilityComponentID c : AccountabilityComponentID.values()) {
            BY_LABEL.put(c.id, c);
        }
    }

    public final String id;

    AccountabilityComponentID(String id) {
        this.id = id;
    }

    public static AccountabilityComponentID valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
