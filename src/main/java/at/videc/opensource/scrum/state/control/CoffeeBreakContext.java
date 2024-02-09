package at.videc.opensource.scrum.state.control;

import at.videc.opensource.scrum.broadcast.CoffeeBreak;
import at.videc.opensource.scrum.broadcast.constants.Action;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CoffeeBreakContext extends BooleanContext {

    private LocalDateTime targetTime;

    protected CoffeeBreakContext(Boolean ctxObject) {
        super(ctxObject);
    }

    public LocalDateTime getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(LocalDateTime targetTime) {
        this.targetTime = targetTime;
    }

    public static CoffeeBreakContext create(Boolean ctxObject) {
        return new CoffeeBreakContext(ctxObject);
    }

    public static CoffeeBreakContext from(CoffeeBreak coffeeBreak) {
        CoffeeBreakContext coffeeContext = new CoffeeBreakContext(true);
        coffeeContext.setTargetTime(LocalDateTime.now().plusSeconds(coffeeBreak.getDuration()));
        return coffeeContext;
    }

}
