package at.videc.opensource.scrum.broadcast;

import at.videc.opensource.scrum.broadcast.base.MessageObject;

public class CoffeeBreak implements MessageObject {

    private final int duration;

    public CoffeeBreak(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
