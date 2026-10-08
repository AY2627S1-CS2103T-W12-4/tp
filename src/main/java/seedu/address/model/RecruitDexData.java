package seedu.address.model;

import static java.util.Objects.requireNonNull;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantRegistry;

/**
 * Root of the application data saved together in one file.
 * Currently contains applicant records; future comparison contracts remain unconnected.
 * Display filters and user preferences do not belong to this saved state.
 */
public class RecruitDexData implements ReadOnlyRecruitDexData {

    private final ApplicantRegistry applicantRegistry = new ApplicantRegistry();

    public RecruitDexData() {}

    /**
     * Creates an independent data object containing the records in {@code toBeCopied}.
     */
    public RecruitDexData(ReadOnlyRecruitDexData toBeCopied) {
        resetData(toBeCopied);
    }

    /**
     * Replaces saved records after validation, keeping the registry and observable view connected.
     * Invalid replacement data leaves existing records unchanged.
     */
    public void resetData(ReadOnlyRecruitDexData newData) {
        requireNonNull(newData);
        applicantRegistry.setApplicants(newData.getApplicantList());
    }

    /** Returns the live registry responsible for applicant records. */
    public ApplicantRegistry getApplicantRegistry() {
        return applicantRegistry;
    }

    @Override
    public ObservableList<Applicant> getApplicantList() {
        return applicantRegistry.getApplicantList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("applicants", applicantRegistry)
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

        return applicantRegistry.equals(otherRecruitDexData.applicantRegistry);
    }

    @Override
    public int hashCode() {
        return applicantRegistry.hashCode();
    }
}
