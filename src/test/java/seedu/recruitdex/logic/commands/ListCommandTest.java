package seedu.recruitdex.logic.commands;

import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.recruitdex.logic.commands.CommandTestUtil.showApplicantAtIndex;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalRecruitDexData;
import static seedu.recruitdex.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.userprefs.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
        expectedModel = new ModelManager(model.getRecruitDexData(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showApplicantAtIndex(model, INDEX_FIRST_APPLICANT);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
