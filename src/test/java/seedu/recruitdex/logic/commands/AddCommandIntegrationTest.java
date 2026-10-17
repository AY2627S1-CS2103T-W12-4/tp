package seedu.recruitdex.logic.commands;

import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalRecruitDexData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.Messages;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.userprefs.UserPrefs;
import seedu.recruitdex.testutil.ApplicantBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
    }

    @Test
    public void execute_newApplicant_success() {
        Applicant validApplicant = new ApplicantBuilder().build();

        Model expectedModel = new ModelManager(model.getRecruitDexData(), new UserPrefs());
        expectedModel.getApplicantRegistry().add(validApplicant);

        assertCommandSuccess(new AddCommand(validApplicant), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validApplicant)),
                expectedModel);
    }

    @Test
    public void execute_duplicateApplicant_throwsCommandException() {
        Applicant applicantInList = model.getRecruitDexData().getApplicantList().get(0);
        assertCommandFailure(new AddCommand(applicantInList), model,
                AddCommand.MESSAGE_DUPLICATE_APPLICANT);
    }

}
