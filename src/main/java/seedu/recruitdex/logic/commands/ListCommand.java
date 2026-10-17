package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.model.Model.PREDICATE_SHOW_ALL_APPLICANTS;

import seedu.recruitdex.model.Model;

/**
 * Lists all applicants in RecruitDex to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows all applicants, undoing any search.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all applicants.";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredApplicantList(PREDICATE_SHOW_ALL_APPLICANTS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
