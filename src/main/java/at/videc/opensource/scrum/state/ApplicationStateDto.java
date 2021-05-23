package at.videc.opensource.scrum.state;

import at.videc.opensource.scrum.broadcast.constants.Action;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ApplicationStateDto implements Serializable {

    private Map<String, Float> estimationValues;
    private Map<Action, Boolean> controlValues;
    private String playerName;
    private LocalDateTime targetTime;

    public ApplicationStateDto() {
        estimationValues = new HashMap<>();
        controlValues = new EnumMap<>(Action.class);

        controlValues.put(Action.NO_CLUE, false);
        controlValues.put(Action.COFFEE, false);
        controlValues.put(Action.PARTICIPATE, false);
        controlValues.put(Action.CLEAR, false);
        controlValues.put(Action.SHOW, false);
    }

    public Map<String, Float> getEstimationValues() {
        return estimationValues;
    }

    public void setEstimationValues(Map<String, Float> estimationValues) {
        this.estimationValues = estimationValues;
    }

    public Map<Action, Boolean> getControlValues() {
        return controlValues;
    }

    public void setControlValues(Map<Action, Boolean> controlValues) {
        this.controlValues = controlValues;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Float getPlayerEstimation() {
        return estimationValues.get(playerName);
    }

    public LocalDateTime getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(LocalDateTime targetTime) {
        this.targetTime = targetTime;
    }
}
