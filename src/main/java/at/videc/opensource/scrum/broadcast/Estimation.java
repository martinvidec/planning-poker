package at.videc.opensource.scrum.broadcast;

import at.videc.opensource.scrum.broadcast.base.MessageObject;

public class Estimation implements MessageObject {

    private final String playerName;
    private final Float estimated;

    public Estimation(String playerName, Float estimated) {
        this.playerName = playerName;
        this.estimated = estimated;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Float getEstimated() {
        return estimated;
    }
}
