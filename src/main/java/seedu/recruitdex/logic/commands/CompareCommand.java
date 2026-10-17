package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.comparison.ComparisonList;
import seedu.recruitdex.model.comparison.ComparisonPair;

/**
 * Records a judgment for the displayed comparison pair by choosing side A or B.
 * ComparisonList validates the judgment, updates the ranking, and selects the next pair.
 */
public class CompareCommand extends Command {
    public static final String COMMAND_WORD = "choose";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Chooses the stronger applicant in the current comparison pair.\n"
            + "Parameters: SIDE (a or b, as labelled in the comparison panel)\n"
            + "Example: " + COMMAND_WORD + " a";

    public static final String MESSAGE_SUCCESS = "Comparison recorded: %1$s chosen over %2$s.";
    public static final String MESSAGE_NO_PENDING_PAIR = "There is no pending comparison pair.";

    /** The two sides displayed in the comparison panel, independent of applicant-list indices. */
    public enum Side {
        A, B
    }

    private final Side side;

    public CompareCommand(Side side) {
        this.side = requireNonNull(side);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ComparisonList comparisons = model.getComparisonList();
        ComparisonPair pair = comparisons.getCurrentPair()
                .orElseThrow(() -> new CommandException(MESSAGE_NO_PENDING_PAIR));
        ApplicantId winnerId = side == Side.A ? pair.getFirstApplicantId() : pair.getSecondApplicantId();
        ApplicantId loserId = side == Side.A ? pair.getSecondApplicantId() : pair.getFirstApplicantId();

        try {
            comparisons.recordDecision(winnerId);
        } catch (IllegalStateException e) {
            throw new CommandException(e.getMessage(), e);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                model.getApplicantRegistry().get(winnerId).getName(),
                model.getApplicantRegistry().get(loserId).getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof CompareCommand otherCommand && side == otherCommand.side;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("side", side).toString();
    }
}
