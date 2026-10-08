package seedu.address.model.comparison;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import seedu.address.model.applicant.ApplicantId;

/**
 * Contract for one active pool of applicant IDs and its ordered decision history.
 * Records, membership, and decisions are separate: joining requires an existing record,
 * while removing membership keeps the record and history. Search filters never change membership.
 * Implementations own coordination with the registry, comparison state, and ranking engine;
 * commands must not perform that coordination themselves.
 * This contract has no implementation or connection to the running application yet.
 */
public interface ComparisonList {

    /** Returns a live, unmodifiable observable set of unique active applicant IDs. */
    ObservableSet<ApplicantId> getMemberIds();

    /** Returns a live, unmodifiable observable history in decision execution order. */
    ObservableList<ComparisonDecision> getDecisionHistory();

    /**
     * Adds an existing applicant to the pool, without adding or copying a record.
     * Refreshes the ranking, making retained decisions eligible when both participants are active.
     * Rejected membership changes leave all model state unchanged.
     */
    void add(ApplicantId applicantId);

    /**
     * Removes membership while retaining the applicant record and decision history.
     * Refreshes the ranking, excluding decisions involving inactive applicants,
     * and clears or replaces a current pair that is no longer valid.
     */
    void remove(ApplicantId applicantId);

    /**
     * Records a new judgment for the current pair and refreshes the ranking as one successful operation.
     * Both participants must still be active and the winner must belong to the current pair.
     * A rejected judgment changes neither history nor ranking. A completed pair is no longer pending.
     */
    ComparisonDecision recordDecision(ApplicantId winnerId);

    /**
     * Removes membership and all decisions involving an applicant being permanently deleted.
     * Refreshes the ranking and invalidates an affected pending pair. The registry coordinates
     * this operation with record deletion; commands only request deletion through the registry.
     * This is the proposed removal policy in Models.md, to be settled before implementation.
     */
    void deleteApplicantReferences(ApplicantId applicantId);
}
