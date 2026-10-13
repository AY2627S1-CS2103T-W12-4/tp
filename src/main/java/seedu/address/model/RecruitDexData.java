package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.comparison.ComparisonState;

/**
 * Root of the application data saved together in one file.
 * Currently contains applicant records and the pending comparison pair; the rest of the comparison
 * contracts remain unconnected.
 * Display filters and user preferences do not belong to this saved state.
 */
public class RecruitDexData implements ReadOnlyRecruitDexData {

    private final ApplicantRegistry applicantRegistry = new ApplicantRegistry();
    private final ComparisonState comparisonState = new ComparisonState(applicantRegistry);

    public RecruitDexData() {}

    /**
     * Creates an independent data object containing the records in {@code toBeCopied}.
     */
    public RecruitDexData(ReadOnlyRecruitDexData toBeCopied) {
        resetData(toBeCopied);
    }

    /**
     * Replaces saved records after validation, keeping the registry and observable view connected.
     * Invalid replacement data leaves existing records unchanged. A pending comparison pair refers to the
     * old records, so it is cleared once the records are replaced.
     */
    public void resetData(ReadOnlyRecruitDexData newData) {
        requireNonNull(newData);
        applicantRegistry.setApplicants(newData.getApplicantList());
        comparisonState.clearCurrentPair();
    }

    /** Returns the live registry responsible for applicant records. */
    public ApplicantRegistry getApplicantRegistry() {
        return applicantRegistry;
    }

    /** Returns the live state holding the pending comparison pair. */
    public ComparisonState getComparisonState() {
        return comparisonState;
    }

    @Override
    public ObservableList<Applicant> getApplicantList() {
        return applicantRegistry.getApplicantList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("applicants", applicantRegistry)
                .add("comparisonState", comparisonState)
                .toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RecruitDexData otherRecruitDexData)) {
            return false;
        }

        return applicantRegistry.equals(otherRecruitDexData.applicantRegistry)
                && comparisonState.equals(otherRecruitDexData.comparisonState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicantRegistry, comparisonState);
    }
}
