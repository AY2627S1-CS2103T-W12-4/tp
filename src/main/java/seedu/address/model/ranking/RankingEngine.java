package seedu.address.model.ranking;

import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.comparison.ComparisonDecision;

/**
 * Contract for deriving the full ranking from active membership and eligible decisions.
 * Includes every active applicant, including those with no completed comparisons.
 * Implementations must define initialization, repeated comparisons, tie standing and display,
 * and deterministic recalculation; the algorithm remains to be chosen.
 * Calculated rankings are runtime state, rebuilt after loading rather than saved.
 * This contract has no implementation or connection to the running application yet.
 */
public interface RankingEngine {

    /** Returns a live, unmodifiable observable view of the complete current ranking. */
    ObservableList<RankingEntry> getRanking();

    /**
     * Rebuilds ranking results using decision execution order, considering only decisions
     * whose two participants are active. Reactivating both participants makes retained history eligible again.
     * The model operation requesting recalculation must coordinate it atomically with a decision
     * or membership change; a rejected operation must leave the existing ranking unchanged.
     */
    void recalculate(Set<ApplicantId> activeMembers, List<ComparisonDecision> decisions);
}
