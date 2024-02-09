package at.videc.opensource.scrum.state.control;

import at.videc.opensource.scrum.broadcast.constants.Action;

public interface ControlContext<T> {

    T getCtxObject();

}
