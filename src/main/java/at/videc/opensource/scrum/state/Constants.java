package at.videc.opensource.scrum.state;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final Float NO_CLUE = -1f;
    public static final Float COFFEE = -2f;
    public static final Float PARTICIPATE = -3f;
    public static final Float CLEAR = -4f;
    public static final Float SHOW = -5f;

    public static final Map<Float, String> LOOKUP_MAP = new HashMap<>();

    static {
        LOOKUP_MAP.put(NO_CLUE, "no_clue");
        LOOKUP_MAP.put(COFFEE, "coffee");
        LOOKUP_MAP.put(PARTICIPATE, "participate");
        LOOKUP_MAP.put(CLEAR, "clear");
        LOOKUP_MAP.put(SHOW, "show");
    }

    public static final String PAYLOAD_DELIMITER = "#";
    public static final String KEY_VALUE_DELIMITER = ":";
    public static final String DELIMITER = ",";

}
