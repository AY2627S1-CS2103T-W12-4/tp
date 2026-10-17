package seedu.recruitdex.model.comparison;

import static seedu.recruitdex.commons.util.AppUtil.checkArgument;
import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import seedu.recruitdex.model.applicant.ApplicantId;

/**
 * Represents an immutable record of a completed comparison decision.
 *
 * <p>The participant order preserves the sides on which the applicants were
 * presented. This class validates the structure of a decision; the coordinating
 * comparison model is responsible for ensuring that both applicants are
 * registered and that the decision is valid for the current comparison state.
 */
public class ComparisonDecision {
    private final UUID decisionId;
    private final ApplicantId firstApplicantId;
    private final ApplicantId secondApplicantId;
    private final ApplicantId winnerId;
    private final Instant timestamp;

    /**
     * Constructs a decision for a pair of distinct applicants.
     *
     * <p>A new decision ID and timestamp are assigned when this object is
     * created. Repeating a comparison creates a separate decision with its own
     * ID and timestamp.
     *
     * @param firstApplicantId the ID of the first applicant in the presented pair
     * @param secondApplicantId the ID of the second applicant in the presented pair
     * @param winnerId the ID of the applicant selected as the winner
     * @throws NullPointerException if any applicant ID is null
     * @throws IllegalArgumentException if the participants are the same or the
     *         winner is not one of the participants
     */
    public ComparisonDecision(ApplicantId firstApplicantId,
                              ApplicantId secondApplicantId, ApplicantId winnerId) {
        this(UUID.randomUUID(), firstApplicantId, secondApplicantId, winnerId, Instant.now());
    }

    /**
     * Restores an existing judgment, preserving its ID, timestamp, and presented sides.
     * Validates the same participant and winner constraints as a new judgment.
     */
    public ComparisonDecision(UUID decisionId, ApplicantId firstApplicantId,
            ApplicantId secondApplicantId, ApplicantId winnerId, Instant timestamp) {
        requireAllNonNull(decisionId, firstApplicantId, secondApplicantId, winnerId, timestamp);
        checkArgument(!firstApplicantId.equals(secondApplicantId),
                "A comparison decision must contain two distinct applicants.");
        checkArgument(winnerId.equals(firstApplicantId) || winnerId.equals(secondApplicantId),
                "The winner must be one of the applicants in the comparison.");

        this.decisionId = decisionId;
        this.firstApplicantId = firstApplicantId;
        this.secondApplicantId = secondApplicantId;
        this.winnerId = winnerId;
        this.timestamp = timestamp;
    }

    /** Returns the unique ID of this judgment. */
    public UUID getDecisionId() {
        return decisionId;
    }

    /** Returns the first participant's stable ID, preserving the presented side. */
    public ApplicantId getFirstApplicantId() {
        return firstApplicantId;
    }

    /** Returns the second participant's stable ID, preserving the presented side. */
    public ApplicantId getSecondApplicantId() {
        return secondApplicantId;
    }

    /** Returns the winning participant's stable ID. */
    public ApplicantId getWinnerId() {
        return winnerId;
    }

    /** Returns the time this judgment was recorded. */
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ComparisonDecision otherDecision
                && decisionId.equals(otherDecision.decisionId)
                && firstApplicantId.equals(otherDecision.firstApplicantId)
                && secondApplicantId.equals(otherDecision.secondApplicantId)
                && winnerId.equals(otherDecision.winnerId)
                && timestamp.equals(otherDecision.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(decisionId, firstApplicantId, secondApplicantId, winnerId, timestamp);
    }
}
