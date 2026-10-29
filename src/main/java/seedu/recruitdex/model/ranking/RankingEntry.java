package seedu.recruitdex.model.ranking;

import static seedu.recruitdex.commons.util.AppUtil.checkArgument;
import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.OptionalDouble;

import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.model.applicant.ApplicantId;

/**
 * Represents an immutable derived ranking result, referencing an applicant record by ID.
 * Positions are positive and may be shared by tied applicants, independently of display order.
 * Scores are optional and finite when present; comparison counts are non-negative.
 * The ranking engine determines positions, scores, and relevant comparison counts.
 * Entries are runtime results rebuilt from model data and are not saved as applicant records.
 */
public final class RankingEntry {

    private final ApplicantId applicantId;
    private final int position;
    private final OptionalDouble score;
    private final int comparisonCount;

    /**
     * Constructs a complete ranking result without calculating or rounding its values.
     * An empty score means no numerical score is supplied. A zero comparison count is valid,
     * including when the ranking engine supplies an initial score.
     *
     * @param applicantId the ranked applicant's stable ID.
     * @param position the positive, one-based position, which may be shared by tied applicants.
     * @param score the optional numerical score, which must be finite when present.
     * @param comparisonCount the non-negative number of relevant completed comparisons.
     * @throws NullPointerException if {@code applicantId} or {@code score} is null.
     * @throws IllegalArgumentException if the position is not positive, a present score is not finite,
     *     or the comparison count is negative.
     */
    public RankingEntry(ApplicantId applicantId, int position, OptionalDouble score, int comparisonCount) {
        requireAllNonNull(applicantId, score);
        checkArgument(position > 0, "Ranking position must be positive.");
        checkArgument(!score.isPresent() || Double.isFinite(score.getAsDouble()),
                "Ranking score must be finite when present.");
        checkArgument(comparisonCount >= 0, "Comparison count must be non-negative.");

        this.applicantId = applicantId;
        this.position = position;
        this.score = score;
        this.comparisonCount = comparisonCount;
    }

    /**
     * Returns the ranked applicant's stable ID.
     */
    public ApplicantId getApplicantId() {
        return applicantId;
    }

    /**
     * Returns the applicant's one-based ranking position, allowing tied positions.
     * For unassessed entries this is only an internal position after assessed entries;
     * callers must check {@link #isAssessed()} before displaying a rank.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns whether this applicant has evidence contributing to the current ranking.
     * Unassessed entries' initial scores and positions must not be presented as an assessment.
     */
    public boolean isAssessed() {
        return comparisonCount > 0;
    }

    /**
     * Returns the score, or empty when no numerical score is supplied.
     */
    public OptionalDouble getScore() {
        return score;
    }

    /**
     * Returns the number of relevant completed comparisons involving this applicant.
     */
    public int getComparisonCount() {
        return comparisonCount;
    }

    /**
     * Returns whether {@code other} has the same applicant ID, position, optional score,
     * and comparison count.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RankingEntry otherEntry)) {
            return false;
        }

        return applicantId.equals(otherEntry.applicantId)
                && position == otherEntry.position
                && score.equals(otherEntry.score)
                && comparisonCount == otherEntry.comparisonCount;
    }

    /**
     * Returns a hash code based on all four fields of this ranking result.
     */
    @Override
    public int hashCode() {
        return Objects.hash(applicantId, position, score, comparisonCount);
    }

    /**
     * Returns a diagnostic representation containing all four fields of this ranking result.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("applicantId", applicantId)
                .add("position", position)
                .add("score", score)
                .add("comparisonCount", comparisonCount)
                .toString();
    }
}
