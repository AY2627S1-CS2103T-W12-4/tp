package seedu.address.model.comparison;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Contract for the saved unanswered pair, preserving the order of the two presented sides.
 * Every pair must contain two distinct active applicants with records in the registry.
 * Fewer than two active applicants means no pair is available.
 * Applicant details are resolved from the registry so record edits appear without changing IDs.
 * This contract has no implementation or connection to the running application yet.
 */
public interface ComparisonState {

    /** Returns the pending pair, or empty when no pair is available. */
    Optional<ComparisonPair> getCurrentPair();

    /** Returns a live, read-only observable property containing the same pending pair. */
    ReadOnlyObjectProperty<Optional<ComparisonPair>> currentPairProperty();

    /**
     * Validates and sets a pending pair without recording a decision or changing the ranking.
     * Pair changes must be saved with the application data, even when no decision was recorded.
     * Loading restores the saved sides without selecting a replacement pair.
     * An invalid pair is rejected without changing the current state.
     */
    void setCurrentPair(ComparisonPair pair);

    /** Clears the pending pair without recording a decision. */
    void clearCurrentPair();
}
