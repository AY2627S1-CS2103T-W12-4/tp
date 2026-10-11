package seedu.address.model.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.CARL;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.comparison.exceptions.DuplicateComparisonMemberException;
import seedu.address.model.ranking.RankingEngine;
import seedu.address.model.ranking.RankingEntry;

public class ComparisonListTest {

    private ApplicantRegistry applicantRegistry;
    private FakeComparisonState comparisonState;
    private FakeRankingEngine rankingEngine;
    private FakePairSelector pairSelector;
    private ComparisonListManager comparisonList;

    @BeforeEach
    public void setUp() {
        applicantRegistry = new ApplicantRegistry();
        applicantRegistry.add(ALICE);
        applicantRegistry.add(BENSON);
        applicantRegistry.add(CARL);

        comparisonState = new FakeComparisonState();
        rankingEngine = new FakeRankingEngine();
        pairSelector = new FakePairSelector();
        comparisonList = new ComparisonListManager(
                applicantRegistry, comparisonState, rankingEngine, pairSelector);
    }

    @Test
    public void add_registeredApplicant_addsMemberAndRecalculatesRanking() {
        ApplicantId applicantId = ALICE.getId();

        comparisonList.add(applicantId);

        assertEquals(Set.of(applicantId), comparisonList.getMemberIds());
        assertEquals(1, rankingEngine.recalculationCount);
        assertEquals(Set.of(applicantId), rankingEngine.lastActiveMembers);
        assertEquals(List.of(), rankingEngine.lastDecisions);
    }

    @Test
    public void add_unregisteredApplicant_throwsApplicantNotFoundException() {
        ApplicantId unregisteredId = new ApplicantId();

        assertThrows(ApplicantNotFoundException.class, () -> comparisonList.add(unregisteredId));

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
                        new ComparisonDecisionManager(
                                ALICE.getId(), BENSON.getId(), ALICE.getId())));
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
        assertThrows(NullPointerException.class, () -> comparisonList.remove(null));

        assertTrue(comparisonList.getMemberIds().isEmpty());
        assertEquals(0, rankingEngine.recalculationCount);
    }

    @Test
    public void remove_memberInCurrentPair_replacesCurrentPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.add(CARL.getId());

        ComparisonPair currentPair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        ComparisonPair replacementPair = new ComparisonPair(BENSON.getId(), CARL.getId());
        comparisonState.setCurrentPair(currentPair);
        pairSelector.nextPair = Optional.of(replacementPair);

        comparisonList.remove(ALICE.getId());

        assertEquals(Optional.of(replacementPair), comparisonState.getCurrentPair());
        assertEquals(1, pairSelector.selectionCount);
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
        assertEquals(0, pairSelector.selectionCount);
    }

    @Test
    public void recordDecision_validWinner_recordsDecisionAndSelectsNextPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());

        ComparisonPair currentPair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        comparisonState.setCurrentPair(currentPair);

        ComparisonPair nextPair = new ComparisonPair(BENSON.getId(), CARL.getId());
        pairSelector.nextPair = Optional.of(nextPair);

        ComparisonDecision decision = comparisonList.recordDecision(ALICE.getId());

        assertEquals(List.of(decision), comparisonList.getDecisionHistory());
        assertEquals(ALICE.getId(), decision.getWinnerId());
        assertEquals(ALICE.getId(), decision.getFirstApplicantId());
        assertEquals(BENSON.getId(), decision.getSecondApplicantId());
        assertEquals(List.of(decision), rankingEngine.lastDecisions);
        assertEquals(Optional.of(nextPair), comparisonState.getCurrentPair());
    }

    @Test
    public void recordDecision_noPendingPair_throwsIllegalStateException() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());

        assertThrows(IllegalStateException.class, () ->
                comparisonList.recordDecision(ALICE.getId()));

        assertTrue(comparisonList.getDecisionHistory().isEmpty());
        assertEquals(2, rankingEngine.recalculationCount);
    }

    @Test
    public void recordDecision_winnerNotInPair_throwsIllegalArgumentException() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonList.add(CARL.getId());
        comparisonState.setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));

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
    public void recordDecision_noNextPair_clearsCurrentPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonState.setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        pairSelector.nextPair = Optional.empty();

        comparisonList.recordDecision(BENSON.getId());

        assertTrue(comparisonState.getCurrentPair().isEmpty());
    }

    @Test
    public void deleteApplicantReferences_removesMembershipAndInvalidatesPair() {
        comparisonList.add(ALICE.getId());
        comparisonList.add(BENSON.getId());
        comparisonState.setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        pairSelector.nextPair = Optional.empty();

        comparisonList.deleteApplicantReferences(ALICE.getId());

        assertEquals(Set.of(BENSON.getId()), comparisonList.getMemberIds());
        assertTrue(comparisonState.getCurrentPair().isEmpty());
        assertEquals(Set.of(BENSON.getId()), rankingEngine.lastActiveMembers);
    }

    @Test
    public void deleteApplicantReferences_nullApplicantId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                comparisonList.deleteApplicantReferences(null));

        assertTrue(comparisonList.getMemberIds().isEmpty());
        assertEquals(0, rankingEngine.recalculationCount);
    }

    private static class FakeComparisonState implements ComparisonState {
        private final ReadOnlyObjectWrapper<Optional<ComparisonPair>> currentPair =
                new ReadOnlyObjectWrapper<>(Optional.empty());

        @Override
        public Optional<ComparisonPair> getCurrentPair() {
            return currentPair.get();
        }

        @Override
        public ReadOnlyObjectProperty<Optional<ComparisonPair>> currentPairProperty() {
            return currentPair.getReadOnlyProperty();
        }

        @Override
        public void setCurrentPair(ComparisonPair pair) {
            currentPair.set(Optional.of(pair));
        }

        @Override
        public void clearCurrentPair() {
            currentPair.set(Optional.empty());
        }
    }

    private static class FakeRankingEngine implements RankingEngine {
        private final ObservableList<RankingEntry> ranking = FXCollections.observableArrayList();
        private int recalculationCount;
        private Set<ApplicantId> lastActiveMembers = Set.of();
        private List<ComparisonDecision> lastDecisions = List.of();

        @Override
        public ObservableList<RankingEntry> getRanking() {
            return FXCollections.unmodifiableObservableList(ranking);
        }

        @Override
        public void recalculate(Set<ApplicantId> activeMembers, List<ComparisonDecision> decisions) {
            recalculationCount++;
            lastActiveMembers = Set.copyOf(activeMembers);
            lastDecisions = List.copyOf(decisions);
        }
    }

    private static class FakePairSelector implements PairSelector {
        private Optional<ComparisonPair> nextPair = Optional.empty();
        private int selectionCount;
        private Set<ApplicantId> lastActiveMembers = Set.of();
        private List<ComparisonDecision> lastDecisions = List.of();

        @Override
        public Optional<ComparisonPair> selectPair(Set<ApplicantId> activeMembers,
                                                   List<ComparisonDecision> decisions, List<RankingEntry> ranking) {
            selectionCount++;
            lastActiveMembers = Set.copyOf(activeMembers);
            lastDecisions = List.copyOf(decisions);
            return nextPair;
        }
    }
}
