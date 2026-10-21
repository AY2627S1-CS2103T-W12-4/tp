package seedu.recruitdex.logic.commands;

import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.recruitdex.logic.commands.SwitchTabCommand.MESSAGE_SUCCESS;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;

public class SwitchTabCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_switchTab_success() {
        CommandResult expectedCommandResult = new CommandResult(MESSAGE_SUCCESS, false, false, true);
        assertCommandSuccess(new SwitchTabCommand(), model, expectedCommandResult, expectedModel);
    }
}
