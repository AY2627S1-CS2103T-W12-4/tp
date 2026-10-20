package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.model.Model;

/**
 * Switches the main window to the Compare tab or the All applicants tab.
 * Only the display changes; the model is not modified.
 */
public class SwitchTabCommand extends Command {
    public static final String COMMAND_WORD = "switch";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Switches to the Compare tab or the All applicants tab.\n"
            + "Parameters: TAB (compare or applicants)\n"
            + "Example: " + COMMAND_WORD + " applicants";

    public static final String MESSAGE_SUCCESS = "Showing the %1$s tab.";

    /** The tabs of the main window. */
    public enum Tab {
        COMPARE("Compare"),
        APPLICANTS("All applicants");

        private final String label;

        Tab(String label) {
            this.label = label;
        }

        /** Returns the tab's label as shown in the main window. */
        public String getLabel() {
            return label;
        }
    }

    private final Tab tab;

    public SwitchTabCommand(Tab tab) {
        this.tab = requireNonNull(tab);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(String.format(MESSAGE_SUCCESS, tab.getLabel()), tab);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SwitchTabCommand otherCommand && tab == otherCommand.tab;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tab", tab).toString();
    }
}
