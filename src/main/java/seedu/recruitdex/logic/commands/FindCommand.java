package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_INTERVIEW_NOTES;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_SOURCE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_YEARS_OF_EXPERIENCE;

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
            + ": Finds applicants matching every given attribute and displays them as a list with index numbers. "
            + "Give at least one attribute. Each term matches part of the attribute, ignoring case, "
            + "except years of experience, which must match exactly. "
            + "Each skill term must match part of at least one skill.\n"
            + "Parameters: "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_INTERVIEW_NOTES + "INTERVIEW_NOTES] "
            + "[" + PREFIX_YEARS_OF_EXPERIENCE + "YEARS_OF_EXPERIENCE] "
            + "[" + PREFIX_SOURCE + "SOURCE] "
            + "[" + PREFIX_TAG + "SKILL_TERMS]\n"
            + "Example: " + COMMAND_WORD + " -n Alice -e example.com -t java kaf";

    public static final String MESSAGE_INVALID_PHONE_TERM = "Phone search terms should contain only digits.";

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
