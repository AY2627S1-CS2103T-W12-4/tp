package seedu.recruitdex.ui;

import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.recruitdex.commons.core.GuiSettings;
import seedu.recruitdex.commons.core.LogsCenter;
import seedu.recruitdex.logic.Logic;
import seedu.recruitdex.logic.commands.CommandResult;
import seedu.recruitdex.logic.commands.SwitchTabCommand;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;

    // Independent Ui parts residing in this Ui container
    private ApplicantListPanel applicantListPanel;
    private ComparisonPanel comparisonPanel;
    private RankingPanel rankingPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private Menu helpMenu;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab compareTab;

    @FXML
    private Tab applicantsTab;

    @FXML
    private StackPane applicantListPanelPlaceholder;

    @FXML
    private StackPane comparisonPanelPlaceholder;

    @FXML
    private StackPane rankingPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();
        setUpHelpMenu();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(KeyCombination.valueOf("F1"), this::handleHelp);
    }

    /**
     * Makes clicking "Help" in the menu bar open the command list directly.
     * A menu with no items opens nothing, so its label is what gets clicked.
     */
    private void setUpHelpMenu() {
        Label helpLabel = new Label("Help");
        helpLabel.setOnMouseClicked(event -> handleHelp());
        helpMenu.setGraphic(helpLabel);
    }

    /**
     * Runs {@code action} when {@code keyCombination} is pressed.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(KeyCombination keyCombination, Runnable action) {
        getRoot().getScene().getAccelerators().put(keyCombination, action);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField and
         * ResultDisplay contains a TextArea, some accelerators (e.g., F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                action.run();
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        comparisonPanel = new ComparisonPanel(logic.getCurrentPairProperty(), logic.getApplicantList(),
                logic.getRanking());
        comparisonPanelPlaceholder.getChildren().add(comparisonPanel.getRoot());

        rankingPanel = new RankingPanel(logic.getRanking(), logic.getApplicantList());
        rankingPanelPlaceholder.getChildren().add(rankingPanel.getRoot());

        applicantListPanel = new ApplicantListPanel(logic.getFilteredApplicantList());
        applicantListPanelPlaceholder.getChildren().add(applicantListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Shows the other tab of the main window and names it in the result display.
     */
    private void handleSwitchTab() {
        Tab target = mainTabPane.getSelectionModel().getSelectedItem() == compareTab ? applicantsTab : compareTab;
        mainTabPane.getSelectionModel().select(target);
        resultDisplay.setFeedbackToUser(String.format(SwitchTabCommand.MESSAGE_SHOWING_TAB, target.getText()));
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public ApplicantListPanel getApplicantListPanel() {
        return applicantListPanel;
    }

    public RankingPanel getRankingPanel() {
        return rankingPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.recruitdex.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isSwitchTab()) {
                handleSwitchTab();
            }

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
