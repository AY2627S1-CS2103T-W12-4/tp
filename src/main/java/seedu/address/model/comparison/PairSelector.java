package seedu.address.model.comparison;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.applicant.ComparisonPair;
import seedu.address.model.ranking.RankingEntry;

/**
 * Contract for selecting useful comparisons independently of the ranking algorithm.
 * Selection does not mutate membership, history, ranking, or the saved pending pair.
 * The strategy remains to be chosen; this contract has no implementation or connection
 * to the running application yet.
 */
public interface PairSelector {

    /**
     * Chooses two distinct active applicant IDs, preserving their presentation order.
     * Returns empty when fewer than two applicants are active.
     * Inputs describe the current pool, ordered history, and derived ranking.
     */
    Optional<ComparisonPair> selectPair(Set<ApplicantId> activeMembers,
            List<ComparisonDecision> decisions, List<RankingEntry> ranking);
}
