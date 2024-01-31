package at.videc.opensource.scrum.view;

import at.videc.opensource.scrum.broadcast.BroadcastMessage;
import at.videc.opensource.scrum.broadcast.CoffeeBreak;
import at.videc.opensource.scrum.broadcast.Estimation;
import at.videc.opensource.scrum.broadcast.constants.Action;
import at.videc.opensource.scrum.broadcast.helper.BroadcastHelper;
import at.videc.opensource.scrum.config.ApplicationProperties;
import at.videc.opensource.scrum.state.ApplicationStateDto;
import at.videc.opensource.scrum.style.StyleConstants;
import at.videc.opensource.scrum.view.base.BaseView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.*;
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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Route("")
@Theme(value = Material.class, variant = Material.DARK)
@CssImport("./styles/planning-poker.css")
@JavaScript("./js/countdown.js")
@Push
public class MainView extends BaseView {

    private ApplicationProperties properties;
    private ApplicationStateDto applicationStateDto;

    private HorizontalLayout header = new HorizontalLayout();
    private HorizontalLayout inputs = new HorizontalLayout();
    private HorizontalLayout buttons = new HorizontalLayout();
    private VerticalLayout estimations = new VerticalLayout();

    private Icon coffeeBreakIcon;
    private TextField playerNameField;
    private List<Button> estimateBtns = new ArrayList<>();
    private Span coffeeBreakDurationSpan;

    private boolean showResults;
    private boolean coffeeBreakRunning;

    private Registration broadcasterRegistration;

    @Autowired
    public MainView(ApplicationProperties properties) {
        this.properties = properties;

        buildHeader();
        buildInputs();
        buildEstimations();
        buildLayout();
    }

    private void buildLayout() {
        add(inputs, header, buttons, new H3("Ergebnisse:"), estimations);
    }

    private void buildEstimations() {
        Arrays.stream(properties.getEstimates()).forEach(value -> {
            String label = String.valueOf(value);
            String sanitizedLabel = label.replace(".", "_");
            Button button = new Button(label);
            button.getStyle().set("background-image", "url(\"img/" + sanitizedLabel + ".png\")");
            button.getClassNames().add("pp-card");
            button.setId("estimate_btn_" + sanitizedLabel);
            button.setWidth(properties.getCardWidth());
            button.setHeight(properties.getCardHeight());
            button.addClickListener(event -> broadcast(value));
            estimateBtns.add(button);
        });

        Button noClueBtn = new Button("?", event -> broadcast(Action.NO_CLUE));
        noClueBtn.getStyle().set("background-image", "url(\"img/no_clue.png\")");
        noClueBtn.getClassNames().add("pp-card");
        noClueBtn.setId("estimate_btn_no_clue");
        noClueBtn.setWidth(properties.getCardWidth());
        noClueBtn.setHeight(properties.getCardHeight());

        Button coffeeBtn = new Button(" ", event -> broadcast(Action.COFFEE));
        coffeeBtn.getStyle().set("background-image", "url(\"img/coffee.png\")");
        coffeeBtn.getClassNames().add("pp-card");
        coffeeBtn.setId("estimate_btn_coffee");
        coffeeBtn.setWidth(properties.getCardWidth());
        coffeeBtn.setHeight(properties.getCardHeight());

        estimateBtns.add(noClueBtn);
        estimateBtns.add(coffeeBtn);

        estimateBtns.forEach(button -> {
            button.setEnabled(false);
            buttons.add(button);
        });
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
            initClientApplicationState();
            broadcast(Action.PARTICIPATE);
        });

        Button showAllBtn = new Button("anzeigen", event -> broadcast(Action.SHOW));
        Button clearAllBtn = new Button("zurücksetzen", event -> broadcast(Action.CLEAR));

        coffeeBreakIcon = new Icon(VaadinIcon.COFFEE);
        coffeeBreakIcon.setId("pp-coffee-icon");
        coffeeBreakIcon.addClassName(StyleConstants.BIG_ICON_CLASS);
        coffeeBreakIcon.setVisible(false);

        LocalTime remainingTime = getRemainingTime();

        coffeeBreakDurationSpan = new Span();
        coffeeBreakDurationSpan.setId("pp-coffee-countdown");
        coffeeBreakDurationSpan.setText(String.format("%02d:%02d", remainingTime.getMinute(), remainingTime.getSecond()));
        coffeeBreakDurationSpan.setVisible(false);

        inputs.add(playerNameField, participateBtn, showAllBtn, clearAllBtn, coffeeBreakIcon, coffeeBreakDurationSpan);
    }

    private void initClientApplicationState() {
        applicationStateDto = new ApplicationStateDto();
        applicationStateDto.setPlayerName(playerNameField.getValue());
    }

    private void buildHeader() {
        header.add(new H1("Planning Poker"));
    }

    private LocalTime getRemainingTime() {
        LocalTime now = LocalTime.now();
        LocalTime targetTime = now.plusSeconds(properties.getCoffeeBreakDuration());
        return targetTime.minusSeconds(now.toSecondOfDay());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        broadcasterRegistration = BroadcastHelper.register(stateDto -> updateView(stateDto, attachEvent.getUI()));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    private void updateView(ApplicationStateDto applicationStateDto, UI ui) {
        String playerName = this.applicationStateDto.getPlayerName();
        this.applicationStateDto = applicationStateDto;
        this.applicationStateDto.setPlayerName(playerName);

        ui.access(() -> {
            // first control payloads
            updateControlValues(applicationStateDto.getControlValues());
            // then estimation payloads
            updateEstimations(applicationStateDto.getEstimationValues());

            if(Boolean.TRUE.equals(applicationStateDto.getControlValues().get(Action.COFFEE)) && !coffeeBreakRunning) {
                // start timer
                ui.getPage().executeJs("window.pp.startCountdown($0,$1,$2)",
                        DateTimeFormatter.ISO_DATE_TIME.format(applicationStateDto.getTargetTime()),
                        coffeeBreakDurationSpan.getId().get(),
                        "Finished");
                coffeeBreakRunning = true;
            } else if(Boolean.FALSE.equals(applicationStateDto.getControlValues().get(Action.COFFEE)) && coffeeBreakRunning){
                // remove timer only when running
                ui.getPage().executeJs("pp.cancelCountdown()");
                coffeeBreakRunning = false;
            }
        });
    }

    private void updateControlValues(Map<Action, Boolean> controlPayloads) {
        for (Map.Entry<Action, Boolean> entry: controlPayloads.entrySet()) {
            switch (entry.getKey()) {
                case COFFEE:
                    boolean haveCoffeBreak = entry.getValue();
                    coffeeBreakIcon.setVisible(haveCoffeBreak);
                    coffeeBreakDurationSpan.setVisible(haveCoffeBreak);
                    break;
                case SHOW:
                    showResults = entry.getValue();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateEstimations(Map<String, Float> estimationPayloads) {
        estimations.removeAll();
        for (Map.Entry<String, Float> entry : estimationPayloads.entrySet()) {
            if(entry.getValue() == null) {
                continue;
            }
            if(entry.getValue() == -1.0f) { // no clue
                estimations.add(new Span((showResults ? "?" : "[DONE]") + " - " + entry.getKey()));
                continue;
            }
            estimations.add(new Span((showResults ? entry.getValue() : "[DONE]") + " - " + entry.getKey()));
        }
    }

    private void broadcast(Float estimation) {
        BroadcastMessage broadcastMessage = new BroadcastMessage(Action.ESTIMATE, new Estimation(playerNameField.getValue(), estimation));
        BroadcastHelper.broadcast(broadcastMessage);
    }

    private void broadcast(Action action) {
        BroadcastMessage broadcastMessage;

        // TODO refactor
        if(action != Action.COFFEE) {
            broadcastMessage = new BroadcastMessage(action, BroadcastMessage.EMPTY_MESSAGE_OBJECT);
        } else {
            broadcastMessage = new BroadcastMessage(action, new CoffeeBreak(properties.getCoffeeBreakDuration()));
        }

        BroadcastHelper.broadcast(broadcastMessage);
    }

}
