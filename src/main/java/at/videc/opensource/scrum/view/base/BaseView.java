package at.videc.opensource.scrum.view.base;

import com.vaadin.flow.component.html.Div;

public class BaseView extends Div {

    public BaseView () {
        setClassName("scrum-" + this.getClass().getSimpleName().toLowerCase());
    }

    /**
     * Since @{@link com.vaadin.flow.component.page.Push} is enabled in {@link at.videc.opensource.scrum.view.MainView} this Method can be used
     * to update UI Components.
     *
     * @param r the method which should run on the UI
     */
    protected void updateUI(Runnable r) {
        getUI().ifPresent(ui -> ui.access(r::run));
    }

}
