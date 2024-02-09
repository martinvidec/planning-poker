package at.videc.opensource.scrum.state;

import at.videc.opensource.scrum.broadcast.*;
import at.videc.opensource.scrum.broadcast.constants.Action;
import at.videc.opensource.scrum.state.control.BooleanContext;
import at.videc.opensource.scrum.state.control.StoryContext;
import at.videc.opensource.scrum.state.control.CoffeeBreakContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ApplicationStateHolder {

    private ApplicationStateDto applicationStateDto;

    public ApplicationStateHolder() {
        applicationStateDto = new ApplicationStateDto();
    }

    public void reset() {
        applicationStateDto.getEstimationValues().clear();
        applicationStateDto.getControlValues().put(Action.COFFEE, CoffeeBreakContext.create(false));
        applicationStateDto.getControlValues().put(Action.SHOW, BooleanContext.create(false));
        applicationStateDto.getControlValues().put(Action.CLEAR, BooleanContext.create(false));
        applicationStateDto.getControlValues().put(Action.NO_CLUE, BooleanContext.create(false));
        applicationStateDto.getControlValues().put(Action.PARTICIPATE, BooleanContext.create(false));
        applicationStateDto.getControlValues().put(Action.STORY, StoryContext.create(false));
    }

    public void modify(BroadcastMessage broadcastMessage) {
        switch (broadcastMessage.getAction()) {
            case CLEAR:
                reset();
                break;
            case ESTIMATE:
                Estimation estimation = (Estimation) broadcastMessage.getMsgObject();
                applicationStateDto.getEstimationValues().put(
                        estimation.getPlayer(),
                        estimation.getEstimated()
                );
                break;
            case NO_CLUE:
                NoClue noClue = (NoClue) broadcastMessage.getMsgObject();
                applicationStateDto.getEstimationValues().put(
                        noClue.getPlayer(),
                        -1.0f
                );
                break;
            case STORY:
                Story story = (Story) broadcastMessage.getMsgObject();
                applicationStateDto.getControlValues().put(broadcastMessage.getAction(), StoryContext.from(story));
                break;
            case COFFEE:
                CoffeeBreak coffeeBreak = (CoffeeBreak) broadcastMessage.getMsgObject();
                applicationStateDto.getControlValues().put(broadcastMessage.getAction(), CoffeeBreakContext.from(coffeeBreak));
                break;
            default:
                applicationStateDto.getControlValues().put(broadcastMessage.getAction(), BooleanContext.create(true));
                break;
        }
    }

    public ApplicationStateDto getApplicationStateDto() {
        return applicationStateDto;
    }

}
