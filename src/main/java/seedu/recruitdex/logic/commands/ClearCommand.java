package seedu.recruitdex.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.RecruitDexData;

/**
 * Clears RecruitDex.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes all applicants and the comparison data.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "RecruitDex has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setRecruitDexData(new RecruitDexData());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
