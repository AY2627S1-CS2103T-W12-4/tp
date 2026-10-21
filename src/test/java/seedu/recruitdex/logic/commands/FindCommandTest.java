package seedu.recruitdex.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.logic.Messages.MESSAGE_APPLICANTS_LISTED_OVERVIEW;
import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;
import static seedu.recruitdex.testutil.TypicalApplicants.DANIEL;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalRecruitDexData;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.NameContainsKeywordsPredicate;
import seedu.recruitdex.model.userprefs.UserPrefs;

public class FindCommandTest {
    private final Model model = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindCommand(null));
    }

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand firstCommand = new FindCommand(firstPredicate);

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new FindCommand(firstPredicate)));
        assertFalse(firstCommand.equals(1));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(new FindCommand(secondPredicate)));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FindCommand command = new FindCommand(applicant -> true);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_predicateMatchesNoApplicants_listsZeroApplicants() {
        Predicate<Applicant> predicate = applicant -> false;
        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredApplicantList(predicate);
        assertCommandSuccess(command, model,
                String.format(MESSAGE_APPLICANTS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(List.of(), model.getFilteredApplicantList());
    }

    @Test
    public void execute_predicateMatchesSomeApplicants_listsOnlyMatches() {
        Predicate<Applicant> predicate = applicant ->
                applicant.getName().toString().equals(ALICE.getName().toString())
                        || applicant.getName().toString().equals(BENSON.getName().toString());
        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredApplicantList(predicate);
        assertCommandSuccess(command, model,
                String.format(MESSAGE_APPLICANTS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(ALICE, BENSON), model.getFilteredApplicantList());
    }

    @Test
    public void execute_combinedConditions_listsApplicantsMatchingEveryCondition() {
        Predicate<Applicant> predicate = applicant ->
                applicant.getAddress().toString().toLowerCase().contains("street")
                        && applicant.getTags().stream()
                        .anyMatch(tag -> tag.toString().toLowerCase().contains("friends"));
        FindCommand command = new FindCommand(predicate);

        expectedModel.updateFilteredApplicantList(predicate);
        assertCommandSuccess(command, model,
                String.format(MESSAGE_APPLICANTS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(DANIEL), model.getFilteredApplicantList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate =
                new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand command = new FindCommand(predicate);

        assertEquals(FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}",
                command.toString());
    }
}
