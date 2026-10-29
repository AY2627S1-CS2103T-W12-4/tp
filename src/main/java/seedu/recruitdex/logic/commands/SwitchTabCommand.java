package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.recruitdex.model.Model;

/**
 * Switches the main window between the Compare tab and the Applicants tab.
 * Only the display changes; the model is not modified.
 */
public class SwitchTabCommand extends Command {
    public static final String COMMAND_WORD = "sw";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Switches between the Compare tab and the Applicants tab.\n"
            + "Example: " + COMMAND_WORD;

    /** Shown if the UI does not replace it with the name of the tab it switched to. */
    public static final String MESSAGE_SUCCESS = "Switched to the other tab.";

    /** Shown by the UI after switching, with the label of the tab now showing. */
    public static final String MESSAGE_SHOWING_TAB = "Showing the %1$s tab.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS, false, false, true);
    }
}
