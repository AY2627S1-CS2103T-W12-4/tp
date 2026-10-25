package seedu.recruitdex.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.recruitdex.logic.commands.CommandResult;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final CommandHistoryList commandHistoryList = new CommandHistoryList();

    private boolean isUpdatingTextFromHistory;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;

        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> {
            setStyleToDefault();
            if (!isUpdatingTextFromHistory) {
                commandHistoryList.resetNavigation();
            }
        });
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryNavigation);
    }

    private void setTextFromHistory(String text) {
        isUpdatingTextFromHistory = true;
        try {
            commandTextField.setText(text);
            commandTextField.positionCaret(text.length());
        } finally {
            isUpdatingTextFromHistory = false;
        }
    }

    /**
     * Handles Up and Down key presses for command history navigation.
     */
    private void handleHistoryNavigation(KeyEvent event) {
        setStyleToDefault();
        if (event.getCode() == KeyCode.UP) {
            String previousCommand = commandHistoryList.previous(commandTextField.getText());
            if (previousCommand != null) {
                this.setTextFromHistory(previousCommand);
                event.consume();
            }
        } else if (event.getCode() == KeyCode.DOWN) {
            String nextCommand = commandHistoryList.next();
            if (nextCommand != null) {
                this.setTextFromHistory(nextCommand);
                event.consume();
            }
        }
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        commandHistoryList.add(commandText);

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.recruitdex.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
}
