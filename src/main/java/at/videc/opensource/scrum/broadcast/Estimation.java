package at.videc.opensource.scrum.broadcast;

import at.videc.opensource.scrum.broadcast.base.MessageObject;
import at.videc.opensource.scrum.domain.Player;

public class Estimation implements MessageObject {

    private final Player player;
    private final Float estimated;

    public Estimation(Player player, Float estimated) {
        this.player = player;
        this.estimated = estimated;
    }

    public Estimation(String playerName, Float estimated) {
        this(new Player(playerName), estimated);
    }

    public Player getPlayer() {
        return player;
    }

    public Float getEstimated() {
        return estimated;
    }
}
