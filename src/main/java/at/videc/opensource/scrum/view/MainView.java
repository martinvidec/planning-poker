package at.videc.opensource.scrum.view;

import at.videc.opensource.scrum.broadcast.Broadcaster;
import at.videc.opensource.scrum.view.base.BaseView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.*;

@Route("")
@Theme(value = Material.class, variant = Material.DARK)
@CssImport("./styles/planning-poker.css")
@Push
public class MainView extends BaseView {

    private static int NO_CLUE = -1;
    private static int COFFEE = -2;
    private static int PARTICIPATE = -3;
    private static int CLEAR = -4;
    private static int SHOW = -5;

    private Map<String, Integer> values = new HashMap<>();

    HorizontalLayout inputs = new HorizontalLayout();
    HorizontalLayout buttons = new HorizontalLayout();
    VerticalLayout results = new VerticalLayout();

    TextField textField;

    int[] estimates = new int[] { 2, 4, 8, 16, 20, 32, 40, 100 };
    List<Button> estimateBtns = new ArrayList<>();

    Registration broadcasterRegistration;

    public MainView() {

        add(new H1("Planning Poker"));

        textField = new TextField();
        textField.setPlaceholder("Vorname");


        Button participateBtn = new Button("teilnehmen", event -> {
            if(textField.isEmpty()) {
                return;
            }
            textField.setReadOnly(true);
            estimateBtns.forEach(button -> { button.setEnabled(true); });
            broadcast(PARTICIPATE);
        });

        Button showAllBtn = new Button("anzeigen", event -> {
            broadcast(SHOW);
        });

        Button clearAllBtn = new Button("löschen", event -> {
            broadcast(CLEAR);
        });

        inputs.add(textField, participateBtn, showAllBtn, clearAllBtn);

        Arrays.stream(estimates).forEach(value -> {
            Button button = new Button(String.valueOf(value));
            button.setEnabled(false);
            button.addClickListener(event -> { broadcast(value); });
            estimateBtns.add(button);
        });

        estimateBtns.add(new Button("?", event -> { broadcast(NO_CLUE); }));
        estimateBtns.add(new Button(new Icon(VaadinIcon.COFFEE), event -> { broadcast(COFFEE); }));

        estimateBtns.forEach(button -> { buttons.add(button); });

        add(inputs, new H3("Schätzung"), buttons, new H2("Ergebnisse:"), results);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register(s -> {
            if(s.equals("clear")) {
                ui.access(() -> {
                    results.removeAll();
                });
                return;
            }

            String[] userPayloads = s.split(",");
            ui.access(() -> {
                results.removeAll();
                for(String userPayload : userPayloads) {
                    String[] payload = userPayload.split(":");
                    results.add(new Span(payload[1] + " - " + payload[0]));
                }
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private void broadcast(int i) {
        Broadcaster.broadcast(textField.getValue() + ":" + String.valueOf(i));
    }

}
