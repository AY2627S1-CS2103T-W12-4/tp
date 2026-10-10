package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.getTypicalRecruitDexData;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_APPLICANT;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.RecruitDexParser;
import seedu.address.model.ModelManager;
import seedu.address.model.RecruitDexData;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.comparison.ComparisonDecision;
import seedu.address.model.comparison.ComparisonList;
import seedu.address.model.userprefs.UserPrefs;

/**
 * Tests removal command behavior without implementing membership, ranking, or decision-history rules.
 */
public class RemoveFromComparisonCommandTest {
    private final ComparisonListStub comparisonList = new ComparisonListStub();
    private final ModelWithComparisonList model = new ModelWithComparisonList(comparisonList);

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemoveFromComparisonCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT).execute(null));
    }

    @Test
    public void execute_validIndexUnfilteredList_delegatesIdAndPreservesRecords() throws Exception {
        List<Applicant> displayedBefore = List.copyOf(model.getFilteredApplicantList());
        RecruitDexData dataBefore = new RecruitDexData(model.getRecruitDexData());
        Index lastIndex = Index.fromOneBased(displayedBefore.size());
        Applicant selectedApplicant = displayedBefore.get(lastIndex.getZeroBased());

        Command command = new RecruitDexParser().parseCommand(
                RemoveFromComparisonCommand.COMMAND_WORD + " " + lastIndex.getOneBased());
        CommandResult result = command.execute(model);

        assertEquals(String.format(RemoveFromComparisonCommand.MESSAGE_SUCCESS, Messages.format(selectedApplicant)),
                result.getFeedbackToUser());
        assertEquals(List.of(selectedApplicant.getId()), comparisonList.removedApplicantIds);
        assertEquals(dataBefore, model.getRecruitDexData());
        assertEquals(displayedBefore, model.getFilteredApplicantList());
    }

    @Test
    public void execute_filterChangedAfterConstruction_resolvesCurrentDisplayedIndex() throws Exception {
        RemoveFromComparisonCommand command = new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT);
        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        RecruitDexData dataBefore = new RecruitDexData(model.getRecruitDexData());

        CommandResult result = command.execute(model);

        assertEquals(String.format(RemoveFromComparisonCommand.MESSAGE_SUCCESS, Messages.format(BENSON)),
                result.getFeedbackToUser());
        assertEquals(List.of(BENSON.getId()), comparisonList.removedApplicantIds);
        assertEquals(dataBefore, model.getRecruitDexData());
        assertEquals(List.of(BENSON), model.getFilteredApplicantList());
    }

    @Test
    public void execute_indexPastUnfilteredList_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredApplicantList().size() + 1);

        assertCommandFailure(new RemoveFromComparisonCommand(invalidIndex), model,
                Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
        assertEquals(0, model.comparisonListAccessCount);
        assertTrue(comparisonList.removedApplicantIds.isEmpty());
    }

    @Test
    public void execute_indexPastFilteredList_throwsCommandException() {
        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        assertTrue(INDEX_SECOND_APPLICANT.getZeroBased() < model.getRecruitDexData().getApplicantList().size());

        assertCommandFailure(new RemoveFromComparisonCommand(INDEX_SECOND_APPLICANT), model,
                Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
        assertEquals(0, model.comparisonListAccessCount);
        assertTrue(comparisonList.removedApplicantIds.isEmpty());
    }

    @Test
    public void execute_emptyDisplayedList_throwsCommandException() {
        model.updateFilteredApplicantList(applicant -> false);

        assertCommandFailure(new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT), model,
                Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
        assertEquals(0, model.comparisonListAccessCount);
    }

    @Test
    public void execute_applicantNotInComparisonList_throwsCommandException() {
        comparisonList.failure = new ApplicantNotFoundException();

        assertCommandFailure(new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT), model,
                RemoveFromComparisonCommand.MESSAGE_NOT_A_MEMBER);
        assertTrue(comparisonList.removedApplicantIds.isEmpty());
        assertEquals(1, comparisonList.removeCallCount);
    }

    @Test
    public void execute_comparisonModelNotConnected_throwsCommandException() {
        ModelManager unwiredModel = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());

        assertCommandFailure(new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT), unwiredModel,
                RemoveFromComparisonCommand.MESSAGE_COMPARISON_UNAVAILABLE);
    }

    @Test
    public void execute_unexpectedUnsupportedRemove_propagatesException() {
        comparisonList.failure = new UnsupportedOperationException("Unexpected model failure");

        assertThrows(UnsupportedOperationException.class, "Unexpected model failure", () ->
                new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT).execute(model));
    }

    @Test
    public void equals() {
        RemoveFromComparisonCommand firstCommand = new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT);
        RemoveFromComparisonCommand secondCommand = new RemoveFromComparisonCommand(INDEX_SECOND_APPLICANT);

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT)));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(new AddToComparisonCommand(INDEX_FIRST_APPLICANT)));
    }

    @Test
    public void toStringMethod() {
        RemoveFromComparisonCommand command = new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT);
        assertEquals(RemoveFromComparisonCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_APPLICANT + "}", command.toString());
    }

    /**
     * Supplies the comparison-list test double alongside the real registry and filtered view.
     */
    private static class ModelWithComparisonList extends ModelManager {
        private final ComparisonList comparisonList;
        private int comparisonListAccessCount;

        ModelWithComparisonList(ComparisonList comparisonList) {
            super(getTypicalRecruitDexData(), new UserPrefs());
            this.comparisonList = comparisonList;
        }

        @Override
        public ComparisonList getComparisonList() {
            comparisonListAccessCount++;
            return comparisonList;
        }
    }

    /**
     * Captures successful removals or simulates a model rejection. Other operations are forbidden in this command.
     */
    private static class ComparisonListStub implements ComparisonList {
        private final List<ApplicantId> removedApplicantIds = new ArrayList<>();
        private RuntimeException failure;
        private int removeCallCount;

        @Override
        public void remove(ApplicantId applicantId) {
            removeCallCount++;
            if (failure != null) {
                throw failure;
            }
            removedApplicantIds.add(applicantId);
        }

        @Override
        public ObservableSet<ApplicantId> getMemberIds() {
            throw new AssertionError("Command must delegate membership validation to the model.");
        }

        @Override
        public ObservableList<ComparisonDecision> getDecisionHistory() {
            throw new AssertionError("Command must not access decision history.");
        }

        @Override
        public void add(ApplicantId applicantId) {
            throw new AssertionError("Command must not add membership.");
        }

        @Override
        public ComparisonDecision recordDecision(ApplicantId winnerId) {
            throw new AssertionError("Command must not record decisions.");
        }

        @Override
        public void deleteApplicantReferences(ApplicantId applicantId) {
            throw new AssertionError("Command must not delete applicant references.");
        }
    }
}
