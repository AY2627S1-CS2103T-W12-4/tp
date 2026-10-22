package seedu.recruitdex.model.comparison;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;

/** Read-only access to the pending comparison pair and its observable property. */
public interface ReadOnlyComparisonState {
    /** Returns the pending pair, or empty when no pair is available. */
    Optional<ComparisonPair> getCurrentPair();

    /** Returns a live, read-only observable property containing the pending pair. */
    ReadOnlyObjectProperty<Optional<ComparisonPair>> currentPairProperty();
}
