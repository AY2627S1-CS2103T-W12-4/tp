package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.recruitdex.commons.core.index.Index;
import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.logic.Messages;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.comparison.ComparisonList;

/**
 * Removes an applicant identified by its displayed index from the active comparison list.
 * The model retains the applicant record and decision history and coordinates ranking and pair updates.
 * Removing a non-member is reported as a command error without changing model state.
 */
public class RemoveFromComparisonCommand extends Command {
    public static final String COMMAND_WORD = "cremove";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes an applicant from the comparison list, keeping the applicant record.\n"
            + "Parameters: INDEX (a positive integer from the current applicant results, not the rank)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Applicant removed from comparison list: %1$s";
    public static final String MESSAGE_NOT_A_MEMBER = "This applicant is not in the comparison list.";

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
        ComparisonList comparisonList = model.getComparisonList();

        if (!comparisonList.getMemberIds().contains(applicant.getId())) {
            throw new CommandException(MESSAGE_NOT_A_MEMBER);
        }
        try {
            comparisonList.remove(applicant.getId());
        } catch (IllegalStateException e) {
            throw new CommandException(String.format(Messages.MESSAGE_RANKING_UPDATE_FAILED, e.getMessage()), e);
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
