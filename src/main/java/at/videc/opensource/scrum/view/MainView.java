package at.videc.opensource.scrum.view;

import at.videc.opensource.scrum.broadcast.*;
import at.videc.opensource.scrum.broadcast.constants.Action;
import at.videc.opensource.scrum.broadcast.helper.BroadcastHelper;
import at.videc.opensource.scrum.config.ApplicationProperties;
import at.videc.opensource.scrum.domain.Player;
import at.videc.opensource.scrum.state.ApplicationStateDto;
import at.videc.opensource.scrum.state.control.BooleanContext;
import at.videc.opensource.scrum.state.control.CoffeeBreakContext;
import at.videc.opensource.scrum.state.control.ControlContext;
import at.videc.opensource.scrum.state.control.StoryContext;
import at.videc.opensource.scrum.style.StyleConstants;
import at.videc.opensource.scrum.view.base.BaseView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The main view of the application.<br/>
 * For styling Shadow-DOM componets with CSS see:<br/>
 * see <a href="https://vaadin.com/api/platform/14.10.10/com/vaadin/flow/component/splitlayout/SplitLayout.html">https://vaadin.com/api/platform/14.10.10/com/vaadin/flow/component/splitlayout/SplitLayout.html</a><br/>
 * and <a href="https://github.com/vaadin/vaadin-themable-mixin#readme">https://github.com/vaadin/vaadin-themable-mixin#readme</a><br/>
 * and <a href="https://vaadin.com/docs/v14/flow/styling/importing-style-sheets">https://vaadin.com/docs/v14/flow/styling/importing-style-sheets</a><br/>
 */
@Route("")
@JavaScript("./js/countdown.js")
@CssImport(value = "./styles/planning-poker.css")
@CssImport(value = "./styles/planning-poker-mixin.css", themeFor = "vaadin-split-layout")
public class MainView extends BaseView {

    public static final String PP_COFFEE_ICON_ID = "pp-coffee-icon";
    public static final String PP_COFFEE_COUNTDOWN_ID = "pp-coffee-countdown";
    public static final String PP_COFFEE_COUNTDOWN_FORMAT = "%02d:%02d";
    private final ApplicationProperties properties;
    private ApplicationStateDto applicationStateDto;
    private Registration broadcasterRegistration;

    private final Div header = new Div();
    private final HorizontalLayout inputs = new HorizontalLayout();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Grid<EstimationResult> estimationGrid = new Grid<>();
    private final List<Button> estimateBtns = new ArrayList<>();

    private Icon coffeeBreakIcon;
    private TextField playerNameField;
    private TextField storyUrlField;
    private Span coffeeBreakDurationSpan;
    private Anchor storyAnchor;


    // control values
    private boolean showResults;
    private boolean coffeeBreakRunning;


    @Autowired
    public MainView(ApplicationProperties properties) {
        this.properties = properties;

        buildHeader();
        buildInputs();
        buildEstimations();
        buildEstimationGrid();
        buildLayout();
    }

    private void buildLayout() {
        Div pokerBoard = new Div();
        pokerBoard.setId("pp-poker-board");
        pokerBoard.getClassNames().add(StyleConstants.POKER_BOARD_CLASS);
        pokerBoard.add(inputs, header, buttons);

        SplitLayout splitLayout = new SplitLayout(pokerBoard, estimationGrid);
        splitLayout.setId("pp-split-layout");
        splitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        splitLayout.setSizeFull();

        add(splitLayout);

        setSizeFull();
    }

    private void buildEstimationGrid() {
        estimationGrid.setId("pp-estimation-grid");
        estimationGrid.setItems(Collections.emptySet());
        estimationGrid.addColumn(EstimationResult::getPlayerName).setHeader("Spieler").setFooter(createPlayerFooter());
        estimationGrid.addColumn(EstimationResult::getResult).setHeader("Schätzung").setFooter(createResultFooter()).setSortable(true);
        estimationGrid.getColumns().forEach(column -> column.setAutoWidth(true));
    }

    private String createPlayerFooter() {
        return (applicationStateDto == null ? "0" : applicationStateDto.getEstimationValues().size()) + " Spieler haben geschätzt.";
    }

    private String createResultFooter() {
        return !showResults ? "Warte auf Spieler." : (applicationStateDto == null ? "0.0" : applicationStateDto.getEstimationValues().values().stream().collect(Collectors.averagingDouble(value -> value.doubleValue() == -1.0 ? 0.0 : value.doubleValue())).toString()) + " Storypoints durchschnittlich.";
    }

    private void buildEstimations() {
        Arrays.stream(properties.getEstimates()).forEach(value -> {
            String label = String.valueOf(value);
            estimateBtns.add(buildCardButton(label, event -> broadcast(value)));
        });
        estimateBtns.add(buildCardButton("?", "no_clue", event -> broadcast(Action.NO_CLUE)));
        estimateBtns.add(buildCardButton(" ", "coffee", event -> broadcast(Action.COFFEE)));

        estimateBtns.forEach(button -> {
            button.setEnabled(false);
            buttons.add(button);
        });
    }

    private Button buildCardButton(String label, ComponentEventListener<ClickEvent<Button>> eventListener) {
        String sanitizedLabel = label.replace(".", "_");
        return buildCardButton(label, sanitizedLabel, eventListener);
    }

    private Button buildCardButton(String label, String sanitizedLabel, ComponentEventListener<ClickEvent<Button>> eventListener) {
        Button button = new Button(label);
        button.getStyle().set("background-image", "url(\"img/" + sanitizedLabel + ".png\")");
        button.getClassNames().add(StyleConstants.CARD_BUTTON_CLASS);
        button.setId("pp-estimate_btn_" + sanitizedLabel);
        button.setWidth(properties.getCardWidth());
        button.setHeight(properties.getCardHeight());
        button.addClickListener(eventListener);
        return button;
    }

    private void buildInputs() {
        playerNameField = new TextField();
        playerNameField.setPlaceholder("Vorname");

        storyUrlField = new TextField();
        storyUrlField.setPlaceholder("URL zur Story");
        storyUrlField.setEnabled(false);

        Button participateBtn = new Button("teilnehmen", event -> {
            if (playerNameField.isEmpty()) {
                return;
            }
            playerNameField.setReadOnly(true);
            estimateBtns.forEach(button -> button.setEnabled(true));
            storyUrlField.setEnabled(true);
            initClientApplicationState();
            broadcast(Action.PARTICIPATE);
        });

        Button showAllBtn = new Button("Schätzung anzeigen", event -> broadcast(Action.SHOW));
        Button clearAllBtn = new Button("Alles zurücksetzen", event -> broadcast(Action.CLEAR));
        Button showStoryBtn = new Button("Story " + (storyAnchor.isVisible() ? "ausblenden" : "anzeigen"), event -> broadcast(Action.STORY));

        coffeeBreakIcon = new Icon(VaadinIcon.COFFEE);
        coffeeBreakIcon.setId(PP_COFFEE_ICON_ID);
        coffeeBreakIcon.addClassName(StyleConstants.BIG_ICON_CLASS);
        coffeeBreakIcon.setVisible(false);

        LocalTime remainingTime = getRemainingTime();

        coffeeBreakDurationSpan = new Span();
        coffeeBreakDurationSpan.setId(PP_COFFEE_COUNTDOWN_ID);
        coffeeBreakDurationSpan.setText(String.format(PP_COFFEE_COUNTDOWN_FORMAT, remainingTime.getMinute(), remainingTime.getSecond()));
        coffeeBreakDurationSpan.setVisible(false);

        inputs.add(playerNameField, participateBtn, storyUrlField, showStoryBtn, showAllBtn, clearAllBtn, coffeeBreakIcon, coffeeBreakDurationSpan);
        inputs.setFlexGrow(1.0, storyUrlField);
    }

    private void initClientApplicationState() {
        applicationStateDto = new ApplicationStateDto();
    }

    private void buildHeader() {
        storyAnchor = new Anchor("#", "#");
        storyAnchor.setVisible(false);
        storyAnchor.setTarget("_blank");
        storyAnchor.getClassNames().add("pp-story-anchor");
        header.add(new H2("Planning Poker"), storyAnchor);
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
        this.applicationStateDto = applicationStateDto;

        ui.access(() -> {
            // first control payloads
            updateControlValues(applicationStateDto.getControlValues());
            // then estimation payloads
            updateEstimations(applicationStateDto.getEstimationValues());

            if (applicationStateDto.getControlValues().get(Action.STORY) instanceof StoryContext
                    && ((StoryContext) applicationStateDto.getControlValues().get(Action.STORY)).getCtxObject()
            ) {
                StoryContext storyContext = (StoryContext) applicationStateDto.getControlValues().get(Action.STORY);
                storyAnchor.setHref(storyContext.getStoryUrl());
                storyAnchor.setText("Link zur Beschreibung (" + storyContext.getStoryUrl() + ")");
            }

            if(applicationStateDto.getControlValues().get(Action.COFFEE) instanceof CoffeeBreakContext) {
                CoffeeBreakContext coffeeBreakContext = (CoffeeBreakContext) applicationStateDto.getControlValues().get(Action.COFFEE);
                if (Boolean.TRUE.equals(coffeeBreakContext.getCtxObject()) && !coffeeBreakRunning) {
                    // start timer
                    ui.getPage().executeJs("window.pp.startCountdown($0,$1,$2)",
                            DateTimeFormatter.ISO_DATE_TIME.format(coffeeBreakContext.getTargetTime()),
                            coffeeBreakDurationSpan.getId().get(),
                            "Finished");
                    coffeeBreakRunning = true;
                } else if (Boolean.FALSE.equals(coffeeBreakContext.getCtxObject() && coffeeBreakRunning)) {
                    // remove timer only when running
                    ui.getPage().executeJs("pp.cancelCountdown()");
                    coffeeBreakRunning = false;
                }
            }
        });
    }

    private void updateControlValues(Map<Action, ControlContext<?>> controlPayloads) {
        for (Map.Entry<Action, ControlContext<?>> entry : controlPayloads.entrySet()) {
            switch (entry.getKey()) {
                case COFFEE:
                    boolean haveCoffeBreak = ((CoffeeBreakContext) entry.getValue()).getCtxObject();
                    coffeeBreakIcon.setVisible(haveCoffeBreak);
                    coffeeBreakDurationSpan.setVisible(haveCoffeBreak);
                    break;
                case SHOW:
                    showResults = ((BooleanContext) entry.getValue()).getCtxObject();
                    break;
                case STORY:
                    storyAnchor.setVisible(((StoryContext) entry.getValue()).getCtxObject());
                default:
                    break;
            }
        }
    }

    private void updateEstimations(Map<Player, Float> estimationPayloads) {
        estimationGrid.setItems(query -> {
           return estimationPayloads.entrySet().stream()
                   .map(entry -> new EstimationResult(entry.getKey().getName(), entry.getValue()))
                   .skip(query.getOffset())
                   .limit(query.getLimit());
        });
        // TODO: update footer via event directly from grid when items change
        estimationGrid.getColumns().get(0).setFooter(createPlayerFooter());
        estimationGrid.getColumns().get(1).setFooter(createResultFooter());
    }

    private void broadcast(Float estimation) {
        BroadcastMessage broadcastMessage = new BroadcastMessage(Action.ESTIMATE, new Estimation(playerNameField.getValue(), estimation));
        BroadcastHelper.broadcast(broadcastMessage);
    }

    private void broadcast(Action action) {
        BroadcastMessage broadcastMessage;
        switch (action) {
            case COFFEE:
                broadcastMessage = new BroadcastMessage(action, new CoffeeBreak(properties.getCoffeeBreakDuration()));
                break;
            case NO_CLUE:
                broadcastMessage = new BroadcastMessage(action, new NoClue(playerNameField.getValue()));
                break;
            case STORY:
                broadcastMessage = new BroadcastMessage(action, new Story(storyUrlField.getValue()));
                break;
            default:
                broadcastMessage = new BroadcastMessage(action, BroadcastMessage.EMPTY_MESSAGE_OBJECT);
                break;
        }

        BroadcastHelper.broadcast(broadcastMessage);
    }

    private class EstimationResult {
        private final String playerName;

        private final Float estimation;

        public EstimationResult(String playerName, Float estimation) {
            this.playerName = playerName;
            this.estimation = estimation;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getResult() {
            return showResults ? estimation == -1 ? "?" : estimation.toString() : "[DONE]";
        }
    }

}
