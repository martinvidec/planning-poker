package at.videc.opensource.scrum.state;

import at.videc.opensource.scrum.broadcast.NoClue;
import at.videc.opensource.scrum.broadcast.constants.Action;
import at.videc.opensource.scrum.broadcast.CoffeeBreak;
import at.videc.opensource.scrum.broadcast.Estimation;
import at.videc.opensource.scrum.broadcast.BroadcastMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ApplicationStateHolder {

    private ApplicationStateDto applicationStateDto;

    public ApplicationStateHolder() {
        applicationStateDto = new ApplicationStateDto();
    }

    public void reset() {
        applicationStateDto.getEstimationValues().clear();
        applicationStateDto.getControlValues().put(Action.COFFEE, false);
        applicationStateDto.getControlValues().put(Action.SHOW, false);
        applicationStateDto.getControlValues().put(Action.CLEAR, false);
        applicationStateDto.getControlValues().put(Action.NO_CLUE, false);
        applicationStateDto.getControlValues().put(Action.PARTICIPATE, false);
        applicationStateDto.setTargetTime(null);
    }

    public void modify(BroadcastMessage broadcastMessage) {
        switch (broadcastMessage.getAction()) {
            case CLEAR:
                reset();
                return;
            case ESTIMATE:
                Estimation estimation = (Estimation) broadcastMessage.getMsgObject();
                applicationStateDto.getEstimationValues().put(
                        estimation.getPlayerName(),
                        estimation.getEstimated()
                );
                return;
            case NO_CLUE:
                NoClue noClue = (NoClue) broadcastMessage.getMsgObject();
                applicationStateDto.getEstimationValues().put(
                        noClue.getPlayerName(),
                        -1.0f
                );
                return;
            case COFFEE:
                CoffeeBreak coffeeBreak = (CoffeeBreak) broadcastMessage.getMsgObject();
                applicationStateDto.setTargetTime(LocalDateTime.now().plus(coffeeBreak.getDuration(), ChronoUnit.SECONDS));
                break;
            default:
                break;
        }

        // add up control Values
        applicationStateDto.getControlValues().put(broadcastMessage.getAction(), true);
    }

    public ApplicationStateDto getApplicationStateDto() {
        return applicationStateDto;
    }

}
