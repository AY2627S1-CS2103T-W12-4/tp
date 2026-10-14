package seedu.address.model.comparison;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.ranking.RankingEntry;

/**
 * Placeholder for selecting useful comparisons independently of the ranking algorithm.
 * Selection does not mutate membership, history, ranking, or the saved pending pair.
 * The selection strategy remains to be implemented. For now, no next pair is selected,
 * even when two or more applicants are active.
 */
public class PairSelector {

    /**
     * Returns no pair until a selection strategy is implemented.
     * Inputs describe the current pool, ordered history, and derived ranking.
     */
    public Optional<ComparisonPair> selectPair(Set<ApplicantId> activeMembers,
            List<ComparisonDecision> decisions, List<RankingEntry> ranking) {
        requireAllNonNull(activeMembers, decisions, ranking);
        return Optional.empty();
    }
}
