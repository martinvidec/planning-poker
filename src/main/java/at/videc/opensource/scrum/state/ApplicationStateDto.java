package at.videc.opensource.scrum.state;

import at.videc.opensource.scrum.broadcast.constants.Action;
import at.videc.opensource.scrum.domain.Player;
import at.videc.opensource.scrum.state.control.BooleanContext;
import at.videc.opensource.scrum.state.control.CoffeeBreakContext;
import at.videc.opensource.scrum.state.control.ControlContext;
import at.videc.opensource.scrum.state.control.StoryContext;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ApplicationStateDto implements Serializable {

    private Map<Player, Float> estimationValues;
    private Map<Action, ControlContext<?>> controlValues;

    public ApplicationStateDto() {
        estimationValues = new HashMap<>();
        controlValues = new EnumMap<>(Action.class);

        controlValues.put(Action.NO_CLUE, BooleanContext.create(false));
        controlValues.put(Action.COFFEE, CoffeeBreakContext.create(false));
        controlValues.put(Action.PARTICIPATE, BooleanContext.create(false));
        controlValues.put(Action.CLEAR, BooleanContext.create(false));
        controlValues.put(Action.SHOW, BooleanContext.create(false));
        controlValues.put(Action.STORY, StoryContext.create(false));

    }

    public Map<Player, Float> getEstimationValues() {
        return estimationValues;
    }

    public void setEstimationValues(Map<Player, Float> estimationValues) {
        this.estimationValues = estimationValues;
    }

    public Map<Action, ControlContext<?>> getControlValues() {
        return controlValues;
    }

    public void setControlValues(Map<Action, ControlContext<?>> controlValues) {
        this.controlValues = controlValues;
    }
}
