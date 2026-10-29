package seedu.recruitdex.model.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.testutil.Assert.assertThrows;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;
import static seedu.recruitdex.testutil.TypicalApplicants.CARL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.applicant.ApplicantRegistry;
import seedu.recruitdex.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.recruitdex.model.comparison.exceptions.DuplicateComparisonMemberException;
import seedu.recruitdex.model.ranking.RankingEngine;
import seedu.recruitdex.model.ranking.RankingEntry;

public class ComparisonListTest {

    private ApplicantRegistry applicantRegistry;
    private ComparisonState comparisonState;
    private FakeRankingEngine rankingEngine;
    private ComparisonList comparisonList;

    @BeforeEach
    public void setUp() {
        applicantRegistry = new ApplicantRegistry();
        applicantRegistry.add(ALICE);
        applicantRegistry.add(BENSON);
        applicantRegistry.add(CARL);

        comparisonState = new ComparisonState(applicantRegistry);
        rankingEngine = new FakeRankingEngine();
        comparisonList = new ComparisonList(
                applicantRegistry, comparisonState, rankingEngine);
    }

    @Test
    public void add_registeredApplicant_addsMemberAndRecalculatesRanking() {
        ApplicantId applicantId = ALICE.getId();

        comparisonList.add(applicantId);

        assertEquals(Set.of(applicantId), comparisonList.getMemberIds());
        assertEquals(1, rankingEngine.recalculationCount);
        assertEquals(Set.of(applicantId), rankingEngine.lastActiveMembers);
        assertEquals(List.of(), rankingEngine.lastDecisions);
        assertTrue(comparisonList.getCurrentPair().isEmpty());
    }

    @Test
    public void add_secondMember_selectsInitialPairAndPreservesItOnFurtherAdditions() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        assertCurrentPairContains(Set.of(ALICE.getId(), BENSON.getId()));
        Optional<ComparisonPair> initialPair = comparisonList.getCurrentPair();

        comparisonList.add(CARL.getId());

        assertEquals(initialPair, comparisonList.getCurrentPair());
        assertTrue(comparisonList.getDecisionHistory().isEmpty());
    }

    @Test
    public void add_afterPoolDropsBelowTwoMembers_selectsPairWhenPoolGrowsAgain() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.remove(ALICE.getId());
        assertTrue(comparisonList.getCurrentPair().isEmpty());

        comparisonList.add(CARL.getId());

        assertCurrentPairContains(Set.of(BENSON.getId(), CARL.getId()));
    }

    @Test
    public void add_unregisteredApplicant_throwsApplicantNotFoundException() {
        ApplicantId unregisteredId = new ApplicantId();

        assertThrows(ApplicantNotFoundException.class, () ->
                comparisonList.add(unregisteredId));

        assertTrue(comparisonList.getMemberIds().isEmpty());
        assertEquals(0, rankingEngine.recalculationCount);
    }

    @Test
    public void add_existingMember_throwsDuplicateComparisonMemberException() {
        comparisonList.add(ALICE.getId());

        assertThrows(DuplicateComparisonMemberException.class, () ->
                comparisonList.add(ALICE.getId()));

        assertEquals(Set.of(ALICE.getId()), comparisonList.getMemberIds());
        assertEquals(1, rankingEngine.recalculationCount);
    }

    @Test
    public void add_nullApplicantId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> comparisonList.add(null));

        assertTrue(comparisonList.getMemberIds().isEmpty());
        assertEquals(0, rankingEngine.recalculationCount);
    }

    @Test
    public void getMemberIds_unmodifiableView_rejectsMutation() {
        assertThrows(UnsupportedOperationException.class, () ->
                comparisonList.getMemberIds().add(ALICE.getId()));
    }

    @Test
    public void getDecisionHistory_unmodifiableView_rejectsMutation() {
        assertThrows(UnsupportedOperationException.class, () ->
                comparisonList.getDecisionHistory().add(
                        new ComparisonDecision(
                                ALICE.getId(), BENSON.getId(), ALICE.getId())));
    }

    @Test
    public void getCollections_returnsLiveViews() {
        var memberIds = comparisonList.getMemberIds();
        var decisionHistory = comparisonList.getDecisionHistory();

        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.recordDecision(ALICE.getId());

        assertSame(memberIds, comparisonList.getMemberIds());
        assertSame(decisionHistory, comparisonList.getDecisionHistory());
        assertEquals(Set.of(ALICE.getId(), BENSON.getId()), memberIds);
        assertEquals(1, decisionHistory.size());
    }

    @Test
    public void getComparisonState_returnsLiveReadOnlyState() {
        ReadOnlyComparisonState state = comparisonList.getComparisonState();
        var pairProperty = state.currentPairProperty();
        List<Optional<ComparisonPair>> observedPairs = new ArrayList<>();
        pairProperty.addListener((observable, oldValue, newValue) -> observedPairs.add(newValue));
        assertTrue(state.getCurrentPair().isEmpty());

        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        Optional<ComparisonPair> selectedPair = comparisonList.getCurrentPair();

        assertTrue(selectedPair.isPresent());
        assertSame(state, comparisonList.getComparisonState());
        assertSame(pairProperty, state.currentPairProperty());
        assertEquals(selectedPair, state.getCurrentPair());
        assertEquals(selectedPair, pairProperty.get());
        assertTrue(!(pairProperty instanceof WritableValue<?>));

        comparisonList.remove(ALICE.getId());

        assertTrue(state.getCurrentPair().isEmpty());
        assertTrue(pairProperty.get().isEmpty());
        assertEquals(List.of(selectedPair, Optional.empty()), observedPairs);
    }

    @Test
    public void remove_activeMember_removesMemberAndRecalculatesRanking() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());

        comparisonList.remove(ALICE.getId());

        assertEquals(Set.of(BENSON.getId()), comparisonList.getMemberIds());
        assertEquals(3, rankingEngine.recalculationCount);
        assertEquals(Set.of(BENSON.getId()), rankingEngine.lastActiveMembers);
    }

    @Test
    public void remove_nonMember_leavesStateUnchanged() {
        comparisonList.add(ALICE.getId());

        comparisonList.remove(BENSON.getId());

        assertEquals(Set.of(ALICE.getId()), comparisonList.getMemberIds());
        assertEquals(1, rankingEngine.recalculationCount);
    }

    @Test
    public void remove_nullApplicantId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                comparisonList.remove(null));

        assertTrue(comparisonList.getMemberIds().isEmpty());
        assertEquals(0, rankingEngine.recalculationCount);
    }

    @Test
    public void remove_memberInCurrentPair_replacesCurrentPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.add(CARL.getId());

        comparisonState.setCurrentPair(
                new ComparisonPair(ALICE.getId(), BENSON.getId()));

        comparisonList.remove(ALICE.getId());

        assertCurrentPairContains(Set.of(BENSON.getId(), CARL.getId()));
    }

    @Test
    public void remove_memberNotInCurrentPair_keepsCurrentPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.add(CARL.getId());

        ComparisonPair currentPair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        comparisonState.setCurrentPair(currentPair);

        comparisonList.remove(CARL.getId());

        assertEquals(Optional.of(currentPair), comparisonState.getCurrentPair());
    }

    @Test
    public void recordDecision_validWinner_recordsDecisionAndSelectsNextPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.add(CARL.getId());

        comparisonState.setCurrentPair(
                new ComparisonPair(ALICE.getId(), BENSON.getId()));

        ComparisonDecision decision = comparisonList.recordDecision(ALICE.getId());

        assertEquals(List.of(decision), comparisonList.getDecisionHistory());
        assertEquals(ALICE.getId(), decision.getWinnerId());
        assertEquals(ALICE.getId(), decision.getFirstApplicantId());
        assertEquals(BENSON.getId(), decision.getSecondApplicantId());
        assertEquals(List.of(decision), rankingEngine.lastDecisions);

        ComparisonPair nextPair = comparisonList.getCurrentPair().orElseThrow();
        assertNotEquals(nextPair.getFirstApplicantId(), nextPair.getSecondApplicantId());
        assertTrue(comparisonList.getMemberIds().contains(nextPair.getFirstApplicantId()));
        assertTrue(comparisonList.getMemberIds().contains(nextPair.getSecondApplicantId()));
    }

    @Test
    public void recordDecision_noPendingPair_throwsIllegalStateException() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonState.clearCurrentPair();

        assertThrows(IllegalStateException.class, () ->
                comparisonList.recordDecision(ALICE.getId()));

        assertTrue(comparisonList.getDecisionHistory().isEmpty());
        assertEquals(2, rankingEngine.recalculationCount);
    }

    @Test
    public void remove_memberInCurrentPair_replacesPairBeforeNextDecision() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.add(CARL.getId());
        comparisonState.setCurrentPair(
                new ComparisonPair(ALICE.getId(), BENSON.getId()));

        comparisonList.remove(ALICE.getId());

        assertCurrentPairContains(Set.of(BENSON.getId(), CARL.getId()));

        ComparisonDecision decision = comparisonList.recordDecision(BENSON.getId());

        assertEquals(Set.of(BENSON.getId(), CARL.getId()),
                Set.of(decision.getFirstApplicantId(), decision.getSecondApplicantId()));
        assertEquals(BENSON.getId(), decision.getWinnerId());
        assertEquals(List.of(decision), comparisonList.getDecisionHistory());
    }

    @Test
    public void recordDecision_winnerNotInPair_throwsIllegalArgumentException() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.add(CARL.getId());
        comparisonState.setCurrentPair(
                new ComparisonPair(ALICE.getId(), BENSON.getId()));

        assertThrows(IllegalArgumentException.class, () ->
                comparisonList.recordDecision(CARL.getId()));

        assertTrue(comparisonList.getDecisionHistory().isEmpty());
        assertEquals(3, rankingEngine.recalculationCount);
    }

    @Test
    public void recordDecision_nullWinner_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                comparisonList.recordDecision(null));

        assertTrue(comparisonList.getDecisionHistory().isEmpty());
    }

    @Test
    public void recordDecision_twoActiveMembers_selectsPairFromSameApplicants() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonState.setCurrentPair(
                new ComparisonPair(ALICE.getId(), BENSON.getId()));

        comparisonList.recordDecision(BENSON.getId());

        assertCurrentPairContains(Set.of(ALICE.getId(), BENSON.getId()));
    }

    @Test
    public void recordDecision_rankingEngineReceivesDecisionHistoryInOrder() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonState.setCurrentPair(
                new ComparisonPair(ALICE.getId(), BENSON.getId()));

        ComparisonDecision firstDecision = comparisonList.recordDecision(ALICE.getId());
        ComparisonDecision secondDecision = comparisonList.recordDecision(BENSON.getId());

        assertEquals(List.of(firstDecision, secondDecision), comparisonList.getDecisionHistory());
        assertEquals(List.of(firstDecision, secondDecision), rankingEngine.lastDecisions);
    }

    @Test
    public void deleteApplicantReferences_removesMembershipAndInvalidatesPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonState.setCurrentPair(
                new ComparisonPair(ALICE.getId(), BENSON.getId()));

        comparisonList.deleteApplicantReferences(ALICE.getId());

        assertEquals(Set.of(BENSON.getId()), comparisonList.getMemberIds());
        assertTrue(comparisonList.getCurrentPair().isEmpty());
        assertEquals(Set.of(BENSON.getId()), rankingEngine.lastActiveMembers);
    }

    @Test
    public void deleteApplicantReferences_removesDecisionsInvolvingApplicant() {
        ComparisonDecision involvingAlice =
                new ComparisonDecision(ALICE.getId(), BENSON.getId(), ALICE.getId());
        ComparisonDecision unrelatedToAlice =
                new ComparisonDecision(BENSON.getId(), CARL.getId(), CARL.getId());

        comparisonList.resetData(
                Set.of(ALICE.getId(), BENSON.getId(), CARL.getId()),
                List.of(involvingAlice, unrelatedToAlice),
                Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())));

        comparisonList.deleteApplicantReferences(ALICE.getId());

        assertEquals(List.of(unrelatedToAlice), comparisonList.getDecisionHistory());
        assertEquals(List.of(unrelatedToAlice), rankingEngine.lastDecisions);
        assertEquals(Set.of(BENSON.getId(), CARL.getId()), comparisonList.getMemberIds());
    }

    @Test
    public void deleteApplicantReferences_nullApplicantId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                comparisonList.deleteApplicantReferences(null));

        assertTrue(comparisonList.getMemberIds().isEmpty());
        assertEquals(0, rankingEngine.recalculationCount);
    }

    @Test
    public void resetData_missingPendingPair_selectsPairWithoutAddingDecision() {
        ComparisonDecision decision = new ComparisonDecision(ALICE.getId(), BENSON.getId(), ALICE.getId());

        comparisonList.resetData(Set.of(ALICE.getId(), BENSON.getId()), List.of(decision), Optional.empty());

        assertCurrentPairContains(Set.of(ALICE.getId(), BENSON.getId()));
        assertEquals(List.of(decision), comparisonList.getDecisionHistory());
        assertEquals(1, rankingEngine.recalculationCount);
        assertEquals(List.of(decision), rankingEngine.lastDecisions);
    }

    @Test
    public void resetData_fewerThanTwoMembers_keepsPairEmpty() {
        for (Set<ApplicantId> members : List.of(Set.<ApplicantId>of(), Set.of(ALICE.getId()))) {
            comparisonList.resetData(members, List.of(), Optional.empty());

            assertEquals(members, comparisonList.getMemberIds());
            assertTrue(comparisonList.getCurrentPair().isEmpty());
            assertTrue(comparisonList.getDecisionHistory().isEmpty());
        }
    }

    @Test
    public void resetData_validDataRestoresMembershipHistoryAndPendingPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        ComparisonDecision decision =
                new ComparisonDecision(ALICE.getId(), BENSON.getId(), ALICE.getId());
        ComparisonPair pendingPair = new ComparisonPair(BENSON.getId(), CARL.getId());

        comparisonList.resetData(
                Set.of(BENSON.getId(), CARL.getId()),
                List.of(decision),
                Optional.of(pendingPair));

        assertEquals(Set.of(BENSON.getId(), CARL.getId()), comparisonList.getMemberIds());
        assertEquals(List.of(decision), comparisonList.getDecisionHistory());
        assertEquals(Optional.of(pendingPair), comparisonList.getCurrentPair());
        assertEquals(3, rankingEngine.recalculationCount);
        assertEquals(Set.of(BENSON.getId(), CARL.getId()), rankingEngine.lastActiveMembers);
        assertEquals(List.of(decision), rankingEngine.lastDecisions);
    }

    @Test
    public void resetData_unregisteredMember_throwsApplicantNotFoundExceptionWithoutChangingData() {
        comparisonList.add(ALICE.getId());
        ApplicantId unregisteredId = new ApplicantId();

        assertThrows(ApplicantNotFoundException.class, () ->
                comparisonList.resetData(
                        Set.of(unregisteredId),
                        List.of(),
                        Optional.empty()));

        assertEquals(Set.of(ALICE.getId()), comparisonList.getMemberIds());
        assertTrue(comparisonList.getDecisionHistory().isEmpty());
        assertEquals(1, rankingEngine.recalculationCount);
    }

    @Test
    public void resetData_unregisteredDecisionParticipant_throwsApplicantNotFoundException() {
        ApplicantId unregisteredId = new ApplicantId();
        ComparisonDecision decision =
                new ComparisonDecision(ALICE.getId(), unregisteredId, ALICE.getId());

        assertThrows(ApplicantNotFoundException.class, () ->
                comparisonList.resetData(
                        Set.of(ALICE.getId()),
                        List.of(decision),
                        Optional.empty()));

        assertTrue(comparisonList.getMemberIds().isEmpty());
        assertTrue(comparisonList.getDecisionHistory().isEmpty());
        assertEquals(0, rankingEngine.recalculationCount);
    }

    @Test
    public void resetData_pendingPairWithInactiveApplicant_throwsIllegalArgumentException() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        ComparisonDecision decision = comparisonList.recordDecision(ALICE.getId());
        Optional<ComparisonPair> pairBefore = comparisonList.getCurrentPair();
        ReadOnlyComparisonState state = comparisonList.getComparisonState();
        List<Optional<ComparisonPair>> observedPairs = new ArrayList<>();
        state.currentPairProperty().addListener((observable, oldValue, newValue) -> observedPairs.add(newValue));
        List<ComparisonPair> invalidPairs = List.of(
                new ComparisonPair(ALICE.getId(), BENSON.getId()),
                new ComparisonPair(BENSON.getId(), ALICE.getId()));

        for (ComparisonPair pendingPair : invalidPairs) {
            assertThrows(IllegalArgumentException.class, () ->
                    comparisonList.resetData(
                            Set.of(ALICE.getId(), CARL.getId()),
                            List.of(),
                            Optional.of(pendingPair)));
        }

        assertEquals(Set.of(ALICE.getId(), BENSON.getId()), comparisonList.getMemberIds());
        assertEquals(List.of(decision), comparisonList.getDecisionHistory());
        assertEquals(pairBefore, state.getCurrentPair());
        assertEquals(pairBefore, state.currentPairProperty().get());
        assertTrue(observedPairs.isEmpty());
        assertEquals(3, rankingEngine.recalculationCount);
        assertEquals(Set.of(ALICE.getId(), BENSON.getId()), rankingEngine.lastActiveMembers);
        assertEquals(List.of(decision), rankingEngine.lastDecisions);
    }

    private void assertCurrentPairContains(Set<ApplicantId> expectedMembers) {
        ComparisonPair pair = comparisonList.getCurrentPair().orElseThrow();
        assertEquals(expectedMembers,
                Set.of(pair.getFirstApplicantId(), pair.getSecondApplicantId()));
    }

    private static class FakeRankingEngine extends RankingEngine {
        private final ObservableList<RankingEntry> ranking =
                FXCollections.observableArrayList();
        private int recalculationCount;
        private Set<ApplicantId> lastActiveMembers = Set.of();
        private List<ComparisonDecision> lastDecisions = List.of();

        @Override
        public ObservableList<RankingEntry> getRanking() {
            return FXCollections.unmodifiableObservableList(ranking);
        }

        @Override
        public void recalculate(Set<ApplicantId> activeMembers,
                                List<ComparisonDecision> decisions) {
            recalculationCount++;
            lastActiveMembers = Set.copyOf(activeMembers);
            lastDecisions = List.copyOf(decisions);
        }
    }
}
