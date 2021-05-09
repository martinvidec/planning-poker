package at.videc.opensource.scrum.view;

import at.videc.opensource.scrum.broadcast.BroadcastHelper;
import at.videc.opensource.scrum.config.ApplicationProperties;
import at.videc.opensource.scrum.state.StateConstants;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route("")
@Theme(value = Material.class, variant = Material.DARK)
@CssImport("./styles/planning-poker.css")
@Push
public class MainView extends BaseView {

    private HorizontalLayout header = new HorizontalLayout();
    private HorizontalLayout inputs = new HorizontalLayout();
    private HorizontalLayout buttons = new HorizontalLayout();
    private VerticalLayout estimations = new VerticalLayout();

    private Icon coffeeBreakIcon;
    private TextField playerNameField;
    private List<Button> estimateBtns = new ArrayList<>();

    private boolean showResults;

    private Registration broadcasterRegistration;

    @Autowired
    public MainView(ApplicationProperties properties) {
        buildHeader();
        buildInputs();
        buildEstimations(properties);
        buildLayout();
    }

    private void buildLayout() {
        add(inputs, new H3("Schätzung"), buttons, new H2("Ergebnisse:"), estimations);
    }

    private void buildEstimations(ApplicationProperties properties) {
        Arrays.stream(properties.getEstimates()).forEach(value -> {
            Button button = new Button(String.valueOf(value));
            button.setEnabled(false);
            button.addClickListener(event -> broadcast(value));
            estimateBtns.add(button);
        });

        estimateBtns.add(new Button("?", event -> broadcast(StateConstants.NO_CLUE)));
        estimateBtns.add(new Button(new Icon(VaadinIcon.COFFEE), event -> broadcast(StateConstants.COFFEE)));

        estimateBtns.forEach(button -> buttons.add(button));
    }

    private void buildInputs() {
        playerNameField = new TextField();
        playerNameField.setPlaceholder("Vorname");

        Button participateBtn = new Button("teilnehmen", event -> {
            if (playerNameField.isEmpty()) {
                return;
            }
            playerNameField.setReadOnly(true);
            estimateBtns.forEach(button -> button.setEnabled(true));
            broadcast(StateConstants.PARTICIPATE);
        });

        Button showAllBtn = new Button("anzeigen", event -> broadcast(StateConstants.SHOW));

        Button clearAllBtn = new Button("löschen", event -> broadcast(StateConstants.CLEAR));

        inputs.add(playerNameField, participateBtn, showAllBtn, clearAllBtn);
    }

    private void buildHeader() {
        coffeeBreakIcon = new Icon(VaadinIcon.COFFEE);
        coffeeBreakIcon.setSize("3rem");
        coffeeBreakIcon.setVisible(false);

        header.add(new H1("Planning Poker"), coffeeBreakIcon);

        add(header);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = BroadcastHelper.register(statePayload -> ui.access(() -> updateView(statePayload)));
    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private void updateView(String statePayload) {
        String[] statePayloads = statePayload.split(StateConstants.PAYLOAD_DELIMITER);
        String[] estimationPayloads = statePayloads[0].split(StateConstants.DELIMITER);
        String[] controlPayloads = statePayloads[1].split(StateConstants.DELIMITER);

        // first control payloads
        updateControlValues(controlPayloads);

        // then estimation payloads
        updateEstimations(estimationPayloads);
    }

    private void updateControlValues(String[] controlPayloads) {
        for (String controlPayload : controlPayloads) {
            if(controlPayload.isEmpty()) {
                continue;
            }
            String[] payload = controlPayload.split(StateConstants.KEY_VALUE_DELIMITER);
            if (StateConstants.LOOKUP_MAP.get(StateConstants.COFFEE).equals(payload[0])) {
                coffeeBreakIcon.setVisible(Boolean.TRUE.toString().equals(payload[1]));
            }
            if (StateConstants.LOOKUP_MAP.get(StateConstants.SHOW).equals(payload[0])) {
                showResults = Boolean.TRUE.toString().equals(payload[1]);
            }
        }
    }

    private void updateEstimations(String[] estimationPayloads) {
        estimations.removeAll();
        for (String estimationPayload : estimationPayloads) {
            if(estimationPayload.isEmpty()) {
                continue;
            }
            String[] payload = estimationPayload.split(StateConstants.KEY_VALUE_DELIMITER);
            estimations.add(new Span((showResults ? payload[1] : "[DONE]") + " - " + payload[0]));
        }
    }

    private void broadcast(Float estimation) {
        BroadcastHelper.broadcast(playerNameField.getValue() + StateConstants.KEY_VALUE_DELIMITER + estimation);
    }

}
