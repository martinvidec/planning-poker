package at.videc.opensource.scrum.state.control;

import at.videc.opensource.scrum.broadcast.Story;
import at.videc.opensource.scrum.broadcast.constants.Action;

public class StoryContext extends BooleanContext {

    private String storyUrl;
    protected StoryContext(Boolean ctxObject) {
        super(ctxObject);
    }

    public String getStoryUrl() {
        return storyUrl;
    }

    public void setStoryUrl(String storyUrl) {
        this.storyUrl = storyUrl;
    }

    public static StoryContext create(Boolean ctxObject) {
        return new StoryContext(ctxObject);
    }

    public static StoryContext from(Story story) {
        StoryContext storyContext = new StoryContext(true);
        storyContext.setStoryUrl(story.getUrl());
        return storyContext;
    }
}
