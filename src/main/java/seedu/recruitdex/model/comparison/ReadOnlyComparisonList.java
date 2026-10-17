package seedu.recruitdex.model.comparison;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import seedu.recruitdex.model.applicant.ApplicantId;

/**
 * Read-only access to comparison membership, ordered decisions, and the pending pair.
 */
public interface ReadOnlyComparisonList {
    /** Returns a live, unmodifiable view of active applicant IDs. */
    ObservableSet<ApplicantId> getMemberIds();

    /** Returns a live, unmodifiable history in execution order. */
    ObservableList<ComparisonDecision> getDecisionHistory();

    /** Returns the pending pair, preserving its presented sides. */
    Optional<ComparisonPair> getCurrentPair();
}
