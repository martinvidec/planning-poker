package at.videc.opensource.scrum.state;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationState {

    private Map<String, Float> estimationValues;
    private Map<String, Boolean> controlValues;
    
    public ApplicationState() {
        estimationValues = new HashMap<>();
        controlValues = new HashMap<>();
        initControlValues();
    }

    public void reset() {
        estimationValues.clear();
        initControlValues();
    }

    public void modify(String message) {
        // decode message
        String[] payloads = message.split(StateConstants.KEY_VALUE_DELIMITER);
        String key = payloads[0];
        Float value = Float.valueOf(payloads[1]);

        if(StateConstants.CLEAR.equals(value)) {
            reset();
            return;
        }

        if(value > 0) {
            // values > 0 are estimation values
            estimationValues.put(key, value);
        } else {
            // values < 0 are control values
            controlValues.put(StateConstants.LOOKUP_MAP.get(value), true);
        }
    }

    @Override
    public String toString() {
        String estimationPayload = encodeValues(estimationValues);
        String controlPayload = encodeValues(controlValues);
        return estimationPayload + StateConstants.PAYLOAD_DELIMITER + controlPayload;
    }

    private void initControlValues() {
        controlValues.put(StateConstants.LOOKUP_MAP.get(StateConstants.COFFEE), false);
        controlValues.put(StateConstants.LOOKUP_MAP.get(StateConstants.SHOW), false);
    }

    private String encodeValues(Map<String, ?> values) {
        return values.entrySet().stream().map(entry -> entry.getKey() + StateConstants.KEY_VALUE_DELIMITER + entry.getValue()).collect(Collectors.joining(StateConstants.DELIMITER));
    }

}
