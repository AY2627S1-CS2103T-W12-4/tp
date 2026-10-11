package seedu.address.model.comparison;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.Instant;
import java.util.UUID;

import seedu.address.model.applicant.ApplicantId;

/**
 * Represents an immutable record of a completed comparison decision.
 *
 * <p>The participant order preserves the sides on which the applicants were
 * presented. This class validates the structure of a decision; the coordinating
 * comparison model is responsible for ensuring that both applicants are
 * registered and that the decision is valid for the current comparison state.
 */
public class ComparisonDecisionManager implements ComparisonDecision {
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
    public ComparisonDecisionManager(ApplicantId firstApplicantId,
                                     ApplicantId secondApplicantId, ApplicantId winnerId) {
        requireAllNonNull(firstApplicantId, secondApplicantId, winnerId);
        checkArgument(!firstApplicantId.equals(secondApplicantId),
                "A comparison decision must contain two distinct applicants.");
        checkArgument(winnerId.equals(firstApplicantId) || winnerId.equals(secondApplicantId),
                "The winner must be one of the applicants in the comparison.");

        this.decisionId = UUID.randomUUID();
        this.firstApplicantId = firstApplicantId;
        this.secondApplicantId = secondApplicantId;
        this.winnerId = winnerId;
        this.timestamp = Instant.now();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getDecisionId() {
        return decisionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicantId getFirstApplicantId() {
        return firstApplicantId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicantId getSecondApplicantId() {
        return secondApplicantId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicantId getWinnerId() {
        return winnerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
