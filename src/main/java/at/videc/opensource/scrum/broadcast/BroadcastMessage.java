package at.videc.opensource.scrum.broadcast;

import at.videc.opensource.scrum.broadcast.base.MessageObject;
import at.videc.opensource.scrum.broadcast.constants.Action;

public class BroadcastMessage {

    private static class EmptyMessageObject implements MessageObject {}
    public static final MessageObject EMPTY_MESSAGE_OBJECT = new EmptyMessageObject();

    private final Action action;
    private final MessageObject msgObject;

    public BroadcastMessage(Action action, MessageObject msgObject) {
        if(action == null) {
            throw new IllegalArgumentException("action is null");
        }
        if(msgObject == null) {
            throw new IllegalArgumentException("msgObject is null");
        }

        this.action = action;
        this.msgObject = msgObject;
    }

    public Action getAction() {
        return action;
    }

    public MessageObject getMsgObject() {
        return msgObject;
    }
}
