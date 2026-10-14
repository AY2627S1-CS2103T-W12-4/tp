package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showApplicantAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.getTypicalRecruitDexData;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_APPLICANT;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.comparison.ComparisonDecision;
import seedu.address.model.comparison.ComparisonPair;
import seedu.address.model.ranking.RankingEntry;
import seedu.address.model.userprefs.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Applicant applicantToDelete = model.getFilteredApplicantList().get(INDEX_FIRST_APPLICANT.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_APPLICANT);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_APPLICANT_SUCCESS,
                Messages.format(applicantToDelete));

        ModelManager expectedModel = new ModelManager(model.getRecruitDexData(), new UserPrefs());
        expectedModel.getApplicantRegistry().delete(applicantToDelete.getId());

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredApplicantList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showApplicantAtIndex(model, INDEX_FIRST_APPLICANT);

        Applicant applicantToDelete = model.getFilteredApplicantList().get(INDEX_FIRST_APPLICANT.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_APPLICANT);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_APPLICANT_SUCCESS,
                Messages.format(applicantToDelete));

        Model expectedModel = new ModelManager(model.getRecruitDexData(), new UserPrefs());
        expectedModel.getApplicantRegistry().delete(applicantToDelete.getId());
        showNoApplicant(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showApplicantAtIndex(model, INDEX_FIRST_APPLICANT);

        Index outOfBoundIndex = INDEX_SECOND_APPLICANT;
        // ensures that outOfBoundIndex is still in bounds of RecruitDex list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getRecruitDexData().getApplicantList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredComparisonMember_removesAllReferences() throws Exception {
        model.getComparisonList().add(ALICE.getId());
        model.getComparisonList().add(BENSON.getId());
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        model.getComparisonState().setCurrentPair(pair);
        model.getComparisonList().recordDecision(ALICE.getId());
        model.getComparisonState().setCurrentPair(pair);
        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        int recordCountBefore = model.getApplicantRegistry().getApplicantList().size();

        CommandResult result = new DeleteCommand(INDEX_FIRST_APPLICANT).execute(model);

        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_APPLICANT_SUCCESS, Messages.format(BENSON)),
                result.getFeedbackToUser());
        assertFalse(model.getApplicantRegistry().containsId(BENSON.getId()));
        assertEquals(recordCountBefore - 1, model.getApplicantRegistry().getApplicantList().size());
        assertEquals(ALICE, model.getApplicantRegistry().get(ALICE.getId()));
        assertEquals(Set.of(ALICE.getId()), model.getComparisonList().getMemberIds());
        assertTrue(model.getComparisonList().getDecisionHistory().isEmpty());
        assertTrue(model.getComparisonState().getCurrentPair().isEmpty());
        assertEquals(1, model.getRankingEngine().getRanking().size());
        assertEquals(ALICE.getId(), model.getRankingEngine().getRanking().get(0).getApplicantId());
        assertEquals(0, model.getRankingEngine().getRanking().get(0).getComparisonCount());
        assertTrue(model.getFilteredApplicantList().isEmpty());
        model.getApplicantRegistry().add(BENSON);
        assertEquals(List.of(BENSON), model.getFilteredApplicantList());
        assertEquals(Set.of(ALICE.getId()), model.getComparisonList().getMemberIds());
    }

    @Test
    public void execute_invalidFilteredIndex_preservesComparisonState() {
        model.getComparisonList().add(ALICE.getId());
        model.getComparisonList().add(BENSON.getId());
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        model.getComparisonState().setCurrentPair(pair);
        ComparisonDecision decision = model.getComparisonList().recordDecision(ALICE.getId());
        model.getComparisonState().setCurrentPair(pair);
        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        List<Applicant> recordsBefore = List.copyOf(model.getApplicantRegistry().getApplicantList());
        List<RankingEntry> rankingBefore = List.copyOf(model.getRankingEngine().getRanking());

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX, () ->
                new DeleteCommand(INDEX_SECOND_APPLICANT).execute(model));

        assertEquals(recordsBefore, model.getApplicantRegistry().getApplicantList());
        assertEquals(Set.of(ALICE.getId(), BENSON.getId()), model.getComparisonList().getMemberIds());
        assertEquals(List.of(decision), model.getComparisonList().getDecisionHistory());
        assertEquals(Optional.of(pair), model.getComparisonState().getCurrentPair());
        assertEquals(rankingBefore, model.getRankingEngine().getRanking());
        assertEquals(List.of(BENSON), model.getFilteredApplicantList());
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_APPLICANT);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_APPLICANT);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_APPLICANT);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different applicant -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoApplicant(Model model) {
        model.updateFilteredApplicantList(p -> false);

        assertTrue(model.getFilteredApplicantList().isEmpty());
    }
}
