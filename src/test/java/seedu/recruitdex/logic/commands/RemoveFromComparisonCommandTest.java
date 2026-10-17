package seedu.recruitdex.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.recruitdex.testutil.Assert.assertThrows;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;
import static seedu.recruitdex.testutil.TypicalApplicants.CARL;
import static seedu.recruitdex.testutil.TypicalApplicants.HOON;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalApplicants;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalRecruitDexData;
import static seedu.recruitdex.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;
import static seedu.recruitdex.testutil.TypicalIndexes.INDEX_SECOND_APPLICANT;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.recruitdex.commons.core.index.Index;
import seedu.recruitdex.logic.Messages;
import seedu.recruitdex.logic.parser.RecruitDexParser;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.RecruitDexData;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.applicant.ApplicantRegistry;
import seedu.recruitdex.model.comparison.ComparisonDecision;
import seedu.recruitdex.model.comparison.ComparisonList;
import seedu.recruitdex.model.comparison.ComparisonPair;
import seedu.recruitdex.model.comparison.ComparisonState;
import seedu.recruitdex.model.ranking.RankingEngine;
import seedu.recruitdex.model.ranking.RankingEntry;
import seedu.recruitdex.model.userprefs.UserPrefs;

/**
 * Tests command delegation in isolation and integration with the concrete comparison list and state.
 * Uses comparison-list stubs for delegation checks and the stored list and real engine for integration checks.
 */
public class RemoveFromComparisonCommandTest {
    private final ComparisonListStub comparisonList = new ComparisonListStub();
    private final ModelWithComparisonListStub model = new ModelWithComparisonListStub(comparisonList);

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
    public void execute_defaultModelManager_nonMemberLeavesStateUnchanged() throws Exception {
        ModelManager connectedModel = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
        ComparisonList realComparisonList = connectedModel.getComparisonList();
        assertSame(connectedModel.getRecruitDexData().getComparisonList(), realComparisonList);
        realComparisonList.add(ALICE.getId());
        realComparisonList.add(CARL.getId());
        connectedModel.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        RecruitDexData dataBefore = new RecruitDexData(connectedModel.getRecruitDexData());
        ObservableList<RankingEntry> ranking = connectedModel.getRankingEngine().getRanking();
        List<RankingEntry> rankingBefore = List.copyOf(ranking);

        CommandResult result = new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT).execute(connectedModel);

        assertEquals(RemoveFromComparisonCommand.MESSAGE_NOT_A_MEMBER, result.getFeedbackToUser());
        assertEquals(dataBefore, connectedModel.getRecruitDexData());
        assertSame(ranking, connectedModel.getRankingEngine().getRanking());
        assertEquals(rankingBefore, ranking);
        assertEquals(List.of(BENSON), connectedModel.getFilteredApplicantList());
    }

    @Test
    public void execute_defaultModelManager_removesMembershipAndUpdatesRanking() throws Exception {
        ModelManager liveModel = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
        assertSame(liveModel.getRecruitDexData().getComparisonList(), liveModel.getComparisonList());
        liveModel.getComparisonList().add(ALICE.getId());
        liveModel.getComparisonList().add(BENSON.getId());
        List<Applicant> recordsBefore = List.copyOf(liveModel.getApplicantRegistry().getApplicantList());

        CommandResult result = new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT).execute(liveModel);

        assertEquals(String.format(RemoveFromComparisonCommand.MESSAGE_SUCCESS, Messages.format(ALICE)),
                result.getFeedbackToUser());
        assertEquals(Set.of(BENSON.getId()), liveModel.getComparisonList().getMemberIds());
        assertEquals(1, liveModel.getRankingEngine().getRanking().size());
        assertEquals(BENSON.getId(), liveModel.getRankingEngine().getRanking().get(0).getApplicantId());
        assertEquals(recordsBefore, liveModel.getApplicantRegistry().getApplicantList());
        assertEquals(recordsBefore, liveModel.getFilteredApplicantList());
    }

    @Test
    public void execute_unexpectedUnsupportedRemove_propagatesException() {
        comparisonList.failure = new UnsupportedOperationException("Unexpected model failure");

        assertThrows(UnsupportedOperationException.class, "Unexpected model failure", () ->
                new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT).execute(model));
    }

    @Test
    public void execute_defaultModelManager_removesFilteredApplicantAndRefreshesRanking() throws Exception {
        ModelManager connectedModel = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
        ComparisonList realComparisonList = connectedModel.getComparisonList();
        assertSame(connectedModel.getRecruitDexData().getComparisonList(), realComparisonList);
        realComparisonList.add(ALICE.getId());
        realComparisonList.add(BENSON.getId());
        connectedModel.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        List<Applicant> recordsBefore = List.copyOf(connectedModel.getApplicantRegistry().getApplicantList());
        ObservableList<RankingEntry> ranking = connectedModel.getRankingEngine().getRanking();

        CommandResult result = new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT).execute(connectedModel);

        assertEquals(String.format(RemoveFromComparisonCommand.MESSAGE_SUCCESS, Messages.format(BENSON)),
                result.getFeedbackToUser());
        assertEquals(Set.of(ALICE.getId()), realComparisonList.getMemberIds());
        assertSame(ranking, connectedModel.getRankingEngine().getRanking());
        assertEquals(1, ranking.size());
        assertEquals(ALICE.getId(), ranking.get(0).getApplicantId());
        assertEquals(0, ranking.get(0).getComparisonCount());
        assertTrue(realComparisonList.getDecisionHistory().isEmpty());
        assertTrue(connectedModel.getComparisonList().getCurrentPair().isEmpty());
        assertEquals(recordsBefore, connectedModel.getApplicantRegistry().getApplicantList());
        assertEquals(List.of(BENSON), connectedModel.getFilteredApplicantList());
    }

    @Test
    public void execute_nonMember_stillDelegatesRemovalToModel() throws Exception {
        model.getApplicantRegistry().add(HOON);
        RecruitDexData dataBefore = new RecruitDexData(model.getRecruitDexData());
        Index index = Index.fromOneBased(model.getFilteredApplicantList().size());
        assertFalse(comparisonList.getMemberIds().contains(HOON.getId()));

        CommandResult result = new RemoveFromComparisonCommand(index).execute(model);

        assertEquals(RemoveFromComparisonCommand.MESSAGE_NOT_A_MEMBER, result.getFeedbackToUser());
        assertEquals(List.of(HOON.getId()), comparisonList.removedApplicantIds);
        assertEquals(dataBefore, model.getRecruitDexData());
    }

    @Test
    public void execute_defaultModelManager_retainsHistoryAndReplacesAffectedPair() throws Exception {
        ModelManager connectedModel = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());
        ComparisonList realComparisonList = connectedModel.getComparisonList();
        assertSame(connectedModel.getRecruitDexData().getComparisonList(), realComparisonList);
        realComparisonList.add(ALICE.getId());
        realComparisonList.add(BENSON.getId());
        realComparisonList.add(CARL.getId());
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        connectedModel.getComparisonList().getComparisonState().setCurrentPair(pair);
        ComparisonDecision decision = realComparisonList.recordDecision(ALICE.getId());
        connectedModel.getComparisonList().getComparisonState().setCurrentPair(pair);
        connectedModel.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        List<Applicant> recordsBefore = List.copyOf(connectedModel.getApplicantRegistry().getApplicantList());

        new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT).execute(connectedModel);

        assertEquals(Set.of(ALICE.getId(), CARL.getId()), realComparisonList.getMemberIds());
        assertEquals(List.of(decision), realComparisonList.getDecisionHistory());
        ComparisonPair replacementPair = realComparisonList.getCurrentPair().orElseThrow();
        assertEquals(Set.of(ALICE.getId(), CARL.getId()),
                Set.of(replacementPair.getFirstApplicantId(), replacementPair.getSecondApplicantId()));
        ObservableList<RankingEntry> ranking = connectedModel.getRankingEngine().getRanking();
        assertEquals(2, ranking.size());
        assertTrue(ranking.stream().allMatch(entry -> entry.getComparisonCount() == 0));
        assertTrue(ranking.stream().noneMatch(entry -> entry.getApplicantId().equals(BENSON.getId())));
        assertEquals(recordsBefore, connectedModel.getApplicantRegistry().getApplicantList());
        assertEquals(List.of(BENSON), connectedModel.getFilteredApplicantList());

        new AddToComparisonCommand(INDEX_FIRST_APPLICANT).execute(connectedModel);

        assertEquals(List.of(decision), realComparisonList.getDecisionHistory());
        assertEquals(3, ranking.size());
        assertEquals(ALICE.getId(), ranking.get(0).getApplicantId());
        assertEquals(1, ranking.get(0).getComparisonCount());
        assertEquals(1, ranking.stream().filter(entry -> entry.getApplicantId().equals(BENSON.getId()))
                .findFirst().orElseThrow().getComparisonCount());
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
     * Supplies a comparison-list stub only for isolated command delegation and failure checks.
     */
    private static class ModelWithComparisonListStub extends ModelManager {
        private final ComparisonListStub comparisonList;
        private int comparisonListAccessCount;

        ModelWithComparisonListStub(ComparisonListStub comparisonList) {
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
    private static class ComparisonListStub extends ComparisonList {
        private final List<ApplicantId> removedApplicantIds = new ArrayList<>();
        private RuntimeException failure;

        ComparisonListStub() {
            this(new ApplicantRegistry());
        }

        private ComparisonListStub(ApplicantRegistry registry) {
            super(registry, new ComparisonState(registry), new RankingEngine());
            for (Applicant applicant : getTypicalApplicants()) {
                registry.add(applicant);
                super.add(applicant.getId());
            }
        }

        @Override
        public void remove(ApplicantId applicantId) {
            if (failure != null) {
                throw failure;
            }
            removedApplicantIds.add(applicantId);
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
