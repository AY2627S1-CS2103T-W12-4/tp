package seedu.address.model.ranking;

import java.util.OptionalDouble;

import seedu.address.model.applicant.ApplicantId;

/**
 * Contract for an immutable derived ranking result, referencing a record by ID.
 * Positions are positive, comparison counts are non-negative, and tied standing
 * must not imply evidence of superiority merely because entries have a display order.
 * Ranking entries are runtime results and are not saved as applicant records.
 * This contract has no implementation or connection to the running application yet.
 */
public interface RankingEntry {

    /** Returns the ranked applicant's stable ID. */
    ApplicantId getApplicantId();

    /** Returns the applicant's one-based ranking position, allowing tied positions. */
    int getPosition();

    /** Returns the score if the eventual algorithm provides one. */
    OptionalDouble getScore();

    /** Returns the number of relevant completed comparisons involving this applicant. */
    int getComparisonCount();
}
