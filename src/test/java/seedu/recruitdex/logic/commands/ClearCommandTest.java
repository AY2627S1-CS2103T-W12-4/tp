package seedu.recruitdex.logic.commands;

import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalRecruitDexData;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.RecruitDexData;
import seedu.recruitdex.model.userprefs.UserPrefs;

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
