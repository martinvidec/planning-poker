package at.videc.opensource.scrum.broadcast;

import at.videc.opensource.scrum.broadcast.base.MessageObject;
import at.videc.opensource.scrum.domain.Player;

public class NoClue implements MessageObject {

    private final Player player;

    public NoClue(Player player) {
        this.player = player;
    }

    public NoClue(String playerName) {
        this(new Player(playerName));
    }

    public Player getPlayer() {
        return player;
    }
}
