package seedu.address.model.comparison;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.comparison.exceptions.DuplicateComparisonMemberException;
import seedu.address.model.ranking.RankingEngine;

/**
 * Manages active comparison membership and the ordered history of decisions.
 *
 * <p>This class coordinates membership changes and completed decisions with the
 * applicant registry, pending comparison state, ranking engine, and pair selector.
 * It stores applicant IDs and decision records, rather than copies of applicant
 * records. The observable collections exposed by this class are unmodifiable views.
 */
public class ComparisonList {
    private final ApplicantRegistry applicantRegistry;
    private final ComparisonState comparisonState;
    private final RankingEngine rankingEngine;
    private final PairSelector pairSelector;

    private final ObservableSet<ApplicantId> memberIds = FXCollections.observableSet();
    private final ObservableSet<ApplicantId> unmodifiableMemberIds =
            FXCollections.unmodifiableObservableSet(memberIds);
    private final ObservableList<ComparisonDecision> decisionHistory =
            FXCollections.observableArrayList();
    private final ObservableList<ComparisonDecision> unmodifiableDecisionHistory =
            FXCollections.unmodifiableObservableList(decisionHistory);

    /**
     * Constructs a comparison list with the components required to coordinate comparisons.
     *
     * @param applicantRegistry the registry used to verify applicant records
     * @param comparisonState the state containing the pending pair
     * @param rankingEngine the engine used to recalculate the ranking
     * @param pairSelector the selector used to choose a subsequent pair
     * @throws NullPointerException if any collaborator is null
     */
    public ComparisonList(ApplicantRegistry applicantRegistry,
                          ComparisonState comparisonState,
                          RankingEngine rankingEngine,
                          PairSelector pairSelector) {
        this.applicantRegistry = requireNonNull(applicantRegistry);
        this.comparisonState = requireNonNull(comparisonState);
        this.rankingEngine = requireNonNull(rankingEngine);
        this.pairSelector = requireNonNull(pairSelector);
    }

    /** Returns a live, unmodifiable observable set of unique active applicant IDs. */
    public ObservableSet<ApplicantId> getMemberIds() {
        return unmodifiableMemberIds;
    }

    /** Returns a live, unmodifiable observable history in decision execution order. */
    public ObservableList<ComparisonDecision> getDecisionHistory() {
        return unmodifiableDecisionHistory;
    }

    /**
     * Adds an existing applicant to the pool, without adding or copying a record.
     * Refreshes the ranking, making retained decisions eligible when both participants are active.
     * Rejected membership changes leave all model state unchanged.
     * @throws NullPointerException if {@code applicantId} is null.
     * @throws ApplicantNotFoundException if the applicant has no record in the registry.
     * @throws DuplicateComparisonMemberException if the applicant is already an active member.
     */
    public void add(ApplicantId applicantId) {
        requireNonNull(applicantId);

        if (!applicantRegistry.containsId(applicantId)) {
            throw new ApplicantNotFoundException();
        }
        if (memberIds.contains(applicantId)) {
            throw new DuplicateComparisonMemberException();
        }

        memberIds.add(applicantId);
        recalculateRanking(decisionHistory);
    }

    /**
     * Removes membership while retaining the applicant record and decision history.
     * Refreshes the ranking, excluding decisions involving inactive applicants,
     * and clears or replaces a current pair that is no longer valid.
     *
     * <p>Removing an ID that is not currently a member has no effect.
     */
    public void remove(ApplicantId applicantId) {
        requireNonNull(applicantId);

        if (!memberIds.remove(applicantId)) {
            return;
        }

        recalculateRanking(decisionHistory);
        replaceCurrentPairIfInvalid();
    }

    /**
     * Records a new judgment for the current pair and refreshes the ranking as one successful operation.
     * Both participants must still be active and the winner must belong to the current pair.
     * A rejected judgment changes neither history nor ranking. A completed pair is no longer pending.
     *
     * <p>After recording a decision, this method selects and saves a subsequent pair.
     * If no pair is available, it clears the pending pair.
     *
     * @throws NullPointerException if {@code winnerId} is null
     * @throws IllegalStateException if there is no pending pair, or if either participant
     *         is no longer an active member or registered applicant
     * @throws IllegalArgumentException if {@code winnerId} is not one of the pending pair's
     *         applicants
     */
    public ComparisonDecision recordDecision(ApplicantId winnerId) {
        requireNonNull(winnerId);

        ComparisonPair currentPair = comparisonState.getCurrentPair()
                .orElseThrow(() -> new IllegalStateException("There is no pending comparison pair."));
        ApplicantId firstApplicantId = currentPair.getFirstApplicantId();
        ApplicantId secondApplicantId = currentPair.getSecondApplicantId();

        if (!memberIds.contains(firstApplicantId) || !memberIds.contains(secondApplicantId)
                || !applicantRegistry.containsId(firstApplicantId)
                || !applicantRegistry.containsId(secondApplicantId)) {
            throw new IllegalStateException("The pending pair is no longer valid.");
        }
        if (!winnerId.equals(firstApplicantId) && !winnerId.equals(secondApplicantId)) {
            throw new IllegalArgumentException("The winner must be one of the applicants in the pending pair.");
        }

        ComparisonDecision decision =
                new ComparisonDecision(firstApplicantId, secondApplicantId, winnerId);
        List<ComparisonDecision> updatedHistory = new ArrayList<>(decisionHistory);
        updatedHistory.add(decision);

        recalculateRanking(updatedHistory);
        decisionHistory.add(decision);
        setNextPairOrClear();

        return decision;
    }

    /**
     * Removes membership and all decisions involving an applicant being permanently deleted.
     * Refreshes the ranking and invalidates an affected pending pair. The registry coordinates
     * this operation with record deletion; commands only request deletion through the registry.
     *
     * <p>This method removes the applicant from active membership and removes
     * decisions involving that applicant. The applicant record itself is retained
     * for the registry to delete as part of its coordinated operation.
     *
     * @throws NullPointerException if {@code applicantId} is null
     */
    public void deleteApplicantReferences(ApplicantId applicantId) {
        requireNonNull(applicantId);

        memberIds.remove(applicantId);
        decisionHistory.removeIf(decision ->
                decision.getFirstApplicantId().equals(applicantId)
                        || decision.getSecondApplicantId().equals(applicantId));

        recalculateRanking(decisionHistory);
        replaceCurrentPairIfInvalid();
    }

    /**
     * Recalculates the ranking from the given decision history and current membership.
     *
     * @param decisions the decisions to use when rebuilding the ranking
     */
    private void recalculateRanking(List<ComparisonDecision> decisions) {
        Set<ApplicantId> activeMembers = Set.copyOf(memberIds);
        rankingEngine.recalculate(activeMembers, List.copyOf(decisions));
    }

    /**
     * Replaces the pending pair if one of its applicants is no longer eligible.
     */
    private void replaceCurrentPairIfInvalid() {
        Optional<ComparisonPair> currentPair = comparisonState.getCurrentPair();
        if (currentPair.isEmpty()) {
            return;
        }

        ComparisonPair pair = currentPair.get();
        boolean firstApplicantIsActive = memberIds.contains(pair.getFirstApplicantId())
                && applicantRegistry.containsId(pair.getFirstApplicantId());
        boolean secondApplicantIsActive = memberIds.contains(pair.getSecondApplicantId())
                && applicantRegistry.containsId(pair.getSecondApplicantId());

        if (!firstApplicantIsActive || !secondApplicantIsActive) {
            setNextPairOrClear();
        }
    }

    /**
     * Selects and saves a subsequent pair, or clears the pending pair if none is available.
     */
    private void setNextPairOrClear() {
        Optional<ComparisonPair> nextPair = pairSelector.selectPair(
                Set.copyOf(memberIds),
                List.copyOf(decisionHistory),
                List.copyOf(rankingEngine.getRanking()));

        if (nextPair.isPresent()) {
            comparisonState.setCurrentPair(nextPair.get());
        } else {
            comparisonState.clearCurrentPair();
        }
    }
}
