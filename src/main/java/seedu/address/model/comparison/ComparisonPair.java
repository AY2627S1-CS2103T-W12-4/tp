package seedu.address.model.comparison;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.applicant.ApplicantId;

/**
 * Represents an ordered pair of distinct applicant IDs for comparison.
 */
public class ComparisonPair {
    private final ApplicantId firstApplicantId;
    private final ApplicantId secondApplicantId;

    /**
     * Constructs an ordered comparison pair from two distinct, non-null applicant IDs.
     * ApplicantId already validates the ID format. The model coordinating comparisons
     * is responsible for checking that both applicants exist and are active members.
     *
     * @param firstApplicantId the ID of the first applicant in the pair
     * @param secondApplicantId the ID of the second applicant in the pair
     * @throws NullPointerException if either applicant ID is null
     * @throws IllegalArgumentException if both IDs identify the same applicant
     */
    public ComparisonPair(ApplicantId firstApplicantId, ApplicantId secondApplicantId) {
        requireAllNonNull(firstApplicantId, secondApplicantId);
        checkArgument(!firstApplicantId.equals(secondApplicantId),
                "A comparison pair must contain two distinct applicants.");
        this.firstApplicantId = firstApplicantId;
        this.secondApplicantId = secondApplicantId;
    }

    /**
     * Returns the ID of the first applicant in this pair.
     *
     * @return the first applicant's ID
     */
    public ApplicantId getFirstApplicantId() {
        return firstApplicantId;
    }

    /**
     * Returns the ID of the second applicant in this pair.
     *
     * @return the second applicant's ID
     */
    public ApplicantId getSecondApplicantId() {
        return secondApplicantId;
    }

    /**
     * Returns true if both pairs hold the same applicants on the same sides.
     * The order matters, because it decides which applicant is presented first.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ComparisonPair otherPair)) {
            return false;
        }

        return firstApplicantId.equals(otherPair.firstApplicantId)
                && secondApplicantId.equals(otherPair.secondApplicantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstApplicantId, secondApplicantId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("firstApplicantId", firstApplicantId)
                .add("secondApplicantId", secondApplicantId)
                .toString();
    }
}
