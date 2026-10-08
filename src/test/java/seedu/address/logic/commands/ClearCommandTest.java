package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalApplicants.getTypicalRecruitDexData;

import org.junit.jupiter.api.Test;

import seedu.address.model.RecruitDexData;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyRecruitDexData_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyRecruitDexData_success() {
        Model model = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
        expectedModel.setRecruitDexData(new RecruitDexData());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
