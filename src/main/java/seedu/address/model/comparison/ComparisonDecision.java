package seedu.address.model.comparison;

import java.time.Instant;
import java.util.UUID;

import seedu.address.model.applicant.ApplicantId;

/**
 * Contract for an immutable completed comparison, separate from an unanswered pair.
 * Participants must be distinct registered applicants and the winner must be one of them.
 * Repeating a comparison creates a new decision ID. History order is execution order,
 * even when timestamps are equal.
 * This contract has no implementation or connection to the running application yet.
 */
public interface ComparisonDecision {

    /** Returns the unique ID of this judgment. */
    UUID getDecisionId();

    /** Returns the first participant's stable ID, preserving the presented side. */
    ApplicantId getFirstApplicant();

    /** Returns the second participant's stable ID, preserving the presented side. */
    ApplicantId getSecondApplicant();

    /** Returns the winning participant's stable ID. */
    ApplicantId getWinnerId();

    /** Returns the time this judgment was recorded. */
    Instant getTimestamp();
}
