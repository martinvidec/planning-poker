package at.videc.opensource.scrum.state.control;

import at.videc.opensource.scrum.broadcast.constants.Action;

public class BooleanContext implements ControlContext<Boolean> {

    private final Boolean ctxObject;

    protected BooleanContext(Boolean ctxObject) {
        this.ctxObject = ctxObject;
    }

    public static BooleanContext create(Boolean ctxObject) {
        return new BooleanContext(ctxObject);
    }

    @Override
    public Boolean getCtxObject() {
        return ctxObject;
    }

}
