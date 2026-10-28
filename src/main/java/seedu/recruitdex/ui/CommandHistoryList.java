package seedu.recruitdex.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores submitted command inputs and supports navigating through them.
 *
 * <p>The list is kept in submission order, so the newest command is at the end.
 * Navigation also preserves the text that was in the command field when the
 * user first moved back into history.</p>
 */
public class CommandHistoryList {

    private final List<String> commands = new ArrayList<>();

    /**
     * Index of the command currently being shown, or {@code -1} when the user
     * is not navigating history.
     */
    private int historyIndex = -1;

    /** Text that was in the command field when history navigation began. */
    private String draft = "";

    /**
     * Records a non-empty command.
     *
     * @param command command text to record
     * @throws IllegalArgumentException if {@code command} is null or empty
     */
    public void add(String command) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Command must not be null or empty.");
        }
        commands.add(command);
        resetNavigation();
    }

    /**
     * Moves to the previous command, starting from the newest command.
     *
     * @param currentText current contents of the command field
     * @return the command to display, or {@code null} if there is no history
     */
    public String previous(String currentText) {
        if (commands.isEmpty()) {
            return null;
        }

        if (historyIndex == -1) {
            draft = currentText == null ? "" : currentText;
            historyIndex = commands.size() - 1;
        } else if (historyIndex > 0) {
            historyIndex--;
        }

        return commands.get(historyIndex);
    }

    /**
     * Moves to the next command. Moving past the newest command restores the
     * draft that was present when navigation began.
     *
     * @return the next command or saved draft, or {@code null} if not browsing
     *         history
     */
    public String next() {
        if (historyIndex == -1) {
            return null;
        }

        if (historyIndex < commands.size() - 1) {
            historyIndex++;
            return commands.get(historyIndex);
        }

        String savedDraft = draft;
        resetNavigation();
        return savedDraft;
    }

    /**
     * Stops history navigation and discards the saved draft.
     *
     * <p>Call this after a command is submitted or when the user edits the
     * field independently of history navigation.</p>
     */
    public void resetNavigation() {
        historyIndex = -1;
        draft = "";
    }
}
