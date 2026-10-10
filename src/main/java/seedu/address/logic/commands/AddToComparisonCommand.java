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
import seedu.address.model.comparison.exceptions.DuplicateComparisonMemberException;

/**
 * Adds an existing applicant identified by its displayed index to the active comparison list.
 */
public class AddToComparisonCommand extends Command {
    public static final String COMMAND_WORD = "compare-add";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds an existing applicant to the comparison list.\n"
            + "Parameters: INDEX (a positive integer from the displayed applicant list)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Applicant added to comparison list: %1$s";
    public static final String MESSAGE_DUPLICATE_MEMBER = "This applicant is already in the comparison list.";
    public static final String MESSAGE_APPLICANT_NOT_FOUND = "This applicant no longer exists in RecruitDex.";
    public static final String MESSAGE_COMPARISON_UNAVAILABLE = "The comparison list is not available yet.";

    private final Index targetIndex;

    /**
     * Creates a command to add the applicant at {@code targetIndex} in the displayed applicant list.
     */
    public AddToComparisonCommand(Index targetIndex) {
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
            comparisonList.add(applicant.getId());
        } catch (DuplicateComparisonMemberException e) {
            throw new CommandException(MESSAGE_DUPLICATE_MEMBER, e);
        } catch (ApplicantNotFoundException e) {
            throw new CommandException(MESSAGE_APPLICANT_NOT_FOUND, e);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(applicant)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddToComparisonCommand otherCommand)) {
            return false;
        }

        return targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
