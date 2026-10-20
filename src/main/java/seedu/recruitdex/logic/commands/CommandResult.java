package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.logic.commands.SwitchTabCommand.Tab;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The tab of the main window to show, or null to leave the tab unchanged. */
    private final Tab tabToShow;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     * {@code tabToShow} may be null to leave the tab unchanged.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit, Tab tabToShow) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.tabToShow = tabToShow;
    }

    /**
     * Constructs a {@code CommandResult} with the specified fields, leaving the tab unchanged.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null);
    }

    /**
     * Constructs a {@code CommandResult} that shows {@code tabToShow},
     * with other fields set to their default value.
     */
    public CommandResult(String feedbackToUser, Tab tabToShow) {
        this(feedbackToUser, false, false, requireNonNull(tabToShow));
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    /** Returns the tab of the main window to show, if the command asks for one. */
    public Optional<Tab> getTabToShow() {
        return Optional.ofNullable(tabToShow);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && Objects.equals(tabToShow, otherCommandResult.tabToShow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, tabToShow);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("tabToShow", tabToShow)
                .toString();
    }

}
