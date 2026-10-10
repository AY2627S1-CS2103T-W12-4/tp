package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.comparison.ComparisonList;

/**
 * Removes an applicant identified by its displayed index from the active comparison list.
 * The model retains the applicant record and decision history and coordinates ranking and pair updates.
 * An {@link ApplicantNotFoundException} from removal is reported as missing comparison membership.
 */
public class RemoveFromComparisonCommand extends Command {
    public static final String COMMAND_WORD = "compare-remove";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes an applicant from the comparison list, keeping the applicant record.\n"
            + "Parameters: INDEX (a positive integer from the displayed applicant list)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Applicant removed from comparison list: %1$s";
    public static final String MESSAGE_NOT_A_MEMBER = "This applicant is not in the comparison list.";
    public static final String MESSAGE_COMPARISON_UNAVAILABLE = "The comparison list is not available yet.";

    private final Index targetIndex;

    /**
     * Creates a command to remove the applicant at {@code targetIndex} in the displayed applicant list.
     */
    public RemoveFromComparisonCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Applicant> displayedApplicants = model.getFilteredApplicantList();
        if (targetIndex.getZeroBased() >= displayedApplicants.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
        }

        Applicant applicant = displayedApplicants.get(targetIndex.getZeroBased());
        ComparisonList comparisonList;
        try {
            comparisonList = model.getComparisonList();
        } catch (UnsupportedOperationException e) {
            throw new CommandException(MESSAGE_COMPARISON_UNAVAILABLE, e);
        }

        try {
            comparisonList.remove(applicant.getId());
        } catch (ApplicantNotFoundException e) {
            throw new CommandException(MESSAGE_NOT_A_MEMBER, e);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(applicant)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RemoveFromComparisonCommand otherCommand)) {
            return false;
        }

        return targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
