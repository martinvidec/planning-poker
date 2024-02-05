package at.videc.opensource.scrum.broadcast;

import at.videc.opensource.scrum.broadcast.base.MessageObject;

public class Story implements MessageObject {

    private final String url;

    public Story(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
