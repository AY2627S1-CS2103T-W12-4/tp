package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.logic.Messages;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.applicant.Applicant;

/**
 * Finds and lists applicants that match every attribute condition in the supplied predicate.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds applicants matching one or more attributes and displays them as a list with index numbers.\n"
            + "Parameters: -n NAME -p PHONE -e EMAIL -a ADDRESS -i INTERVIEW_NOTES "
            + "-y YEARS_OF_EXPERIENCE -s SOURCE -t SKILL_TERMS\n"
            + "Each skill search term must partially match at least one tag (case-insensitive).\n"
            + "Example: " + COMMAND_WORD + " -n Alice Tan -p 91234567 -e alice@example.com -t java kaf";

    private final Predicate<Applicant> predicate;

    /**
     * Creates a find command that filters applicants using the specified predicate.
     *
     * @param predicate The conditions an applicant must satisfy to be listed.
     */
    public FindCommand(Predicate<Applicant> predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    /**
     * Updates the model's applicant filter and returns the number of matching applicants.
     *
     * @param model The model containing the applicants to filter.
     * @return A command result reporting the number of matching applicants.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredApplicantList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_APPLICANTS_LISTED_OVERVIEW, model.getFilteredApplicantList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
