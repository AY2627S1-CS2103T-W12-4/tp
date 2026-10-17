package seedu.recruitdex.ui;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import seedu.recruitdex.commons.core.LogsCenter;
import seedu.recruitdex.logic.commands.CommandInfo;
import seedu.recruitdex.logic.commands.CommandSummary;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2627s1-cs2103t-w12-4.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Full user guide: " + USERGUIDE_URL;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";
    private static final List<String> HEADERS = List.of("Command", "What it does", "Parameters", "Example");
    private static final String NO_PARAMETERS = "-";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private GridPane commandTable;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        fillCommandTable();
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }

    /** Fills the table with a header row, then each group heading followed by its commands. */
    private void fillCommandTable() {
        int row = 0;
        for (int column = 0; column < HEADERS.size(); column++) {
            commandTable.add(createCell(HEADERS.get(column), "help-header"), column, row);
        }
        row++;

        for (Map.Entry<String, List<CommandInfo>> group : CommandSummary.getGroups().entrySet()) {
            Label heading = createCell(group.getKey(), "help-group");
            commandTable.add(heading, 0, row, HEADERS.size(), 1);
            row++;
            for (CommandInfo info : group.getValue()) {
                String rowStyle = row % 2 == 0 ? "help-row-even" : "help-row-odd";
                commandTable.add(createCell(info.commandWord(), "help-command", rowStyle), 0, row);
                commandTable.add(createCell(info.description(), "help-text", rowStyle), 1, row);
                commandTable.add(createCell(displayed(info.parameters()), "help-code", rowStyle), 2, row);
                commandTable.add(createCell(info.example(), "help-code", rowStyle), 3, row);
                row++;
            }
        }
    }

    private static String displayed(String parameters) {
        return parameters.isEmpty() ? NO_PARAMETERS : parameters;
    }

    private static Label createCell(String text, String... styleClasses) {
        Label cell = new Label(text);
        cell.setWrapText(true);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setMaxHeight(Double.MAX_VALUE);
        cell.getStyleClass().addAll(styleClasses);
        return cell;
    }
}
