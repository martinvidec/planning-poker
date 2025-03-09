package at.videc.opensource.scrum.view.base;

import com.vaadin.flow.component.html.Div;

import java.util.UUID;

public class BaseView extends Div {

    public BaseView () {
        setId("pp-" + this.getClass().getSimpleName().toLowerCase());
        setClassName("pp-" + this.getClass().getSimpleName().toLowerCase());
    }

}
