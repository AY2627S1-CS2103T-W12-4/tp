package seedu.recruitdex.model.comparison;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.applicant.ApplicantRegistry;
import seedu.recruitdex.model.applicant.exceptions.ApplicantNotFoundException;

/**
 * Holds the pending, unanswered {@link ComparisonPair}, preserving the order of the two presented sides.
 * A pending pair is not a decision, so it has no effect on the ranking.
 * Both applicants of a pair must have a record in the registry. Whether they are active members of the
 * comparison list is checked by {@link ComparisonList}, which coordinates membership with this state.
 * Applicant details are resolved from the registry by ID, so record edits appear without changing the pair.
 */
public class ComparisonState {

    private final ApplicantRegistry applicantRegistry;
    private final ReadOnlyObjectWrapper<Optional<ComparisonPair>> currentPair =
            new ReadOnlyObjectWrapper<>(Optional.empty());

    /**
     * Creates a state with no pending pair, validating pairs against the records in {@code applicantRegistry}.
     */
    public ComparisonState(ApplicantRegistry applicantRegistry) {
        this.applicantRegistry = requireNonNull(applicantRegistry);
    }

    /** Returns the pending pair, or empty when no pair is available. */
    public Optional<ComparisonPair> getCurrentPair() {
        return currentPair.get();
    }

    /** Returns a live, read-only observable property containing the same pending pair. */
    public ReadOnlyObjectProperty<Optional<ComparisonPair>> currentPairProperty() {
        return currentPair.getReadOnlyProperty();
    }

    /**
     * Sets the pending pair without recording a decision or changing the ranking.
     * A rejected pair leaves the current state unchanged.
     *
     * @throws ApplicantNotFoundException if either applicant of {@code pair} has no record in the registry.
     */
    public void setCurrentPair(ComparisonPair pair) {
        requireNonNull(pair);
        requireRecord(pair.getFirstApplicantId());
        requireRecord(pair.getSecondApplicantId());
        currentPair.set(Optional.of(pair));
    }

    /** Clears the pending pair without recording a decision. */
    public void clearCurrentPair() {
        currentPair.set(Optional.empty());
    }

    private void requireRecord(ApplicantId applicantId) {
        if (!applicantRegistry.containsId(applicantId)) {
            throw new ApplicantNotFoundException();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ComparisonState otherComparisonState)) {
            return false;
        }

        return getCurrentPair().equals(otherComparisonState.getCurrentPair());
    }

    @Override
    public int hashCode() {
        return getCurrentPair().hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("currentPair", getCurrentPair())
                .toString();
    }
}
