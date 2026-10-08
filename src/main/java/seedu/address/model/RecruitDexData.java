package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.UniqueApplicantList;

/**
 * Wraps all data at RecruitDex level.
 * Duplicates are not allowed (by .isSameApplicant comparison).
 */
public class RecruitDexData implements ReadOnlyRecruitDexData {

    private final UniqueApplicantList applicants = new UniqueApplicantList();

    public RecruitDexData() {}

    /**
     * Creates a RecruitDexData using the Applicants in the {@code toBeCopied}
     */
    public RecruitDexData(ReadOnlyRecruitDexData toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the applicant list with {@code applicants}.
     * {@code applicants} must not contain duplicate applicants.
     */
    public void setApplicants(List<Applicant> applicants) {
        this.applicants.setApplicants(applicants);
    }

    /**
     * Resets the existing data of this {@code RecruitDexData} with {@code newData}.
     */
    public void resetData(ReadOnlyRecruitDexData newData) {
        requireNonNull(newData);

        setApplicants(newData.getApplicantList());
    }

    //// applicant-level operations

    /**
     * Returns true if an applicant with the same identity as {@code applicant} exists in RecruitDex.
     */
    public boolean hasApplicant(Applicant applicant) {
        requireNonNull(applicant);
        return applicants.contains(applicant);
    }

    /**
     * Adds an applicant to RecruitDex.
     * The applicant must not already exist in RecruitDex.
     */
    public void addApplicant(Applicant p) {
        applicants.add(p);
    }

    /**
     * Replaces the given applicant {@code target} in the list with {@code editedApplicant}.
     * {@code target} must exist in RecruitDex.
     * The applicant identity of {@code editedApplicant} must not be the same as another existing applicant
     * in RecruitDex.
     */
    public void setApplicant(Applicant target, Applicant editedApplicant) {
        requireNonNull(editedApplicant);

        applicants.setApplicant(target, editedApplicant);
    }

    /**
     * Removes {@code key} from this {@code RecruitDexData}.
     * {@code key} must exist in RecruitDex.
     */
    public void removeApplicant(Applicant key) {
        applicants.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("applicants", applicants)
                .toString();
    }

    @Override
    public ObservableList<Applicant> getApplicantList() {
        return applicants.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RecruitDexData otherRecruitDexData)) {
            return false;
        }

        return applicants.equals(otherRecruitDexData.applicants);
    }

    @Override
    public int hashCode() {
        return applicants.hashCode();
    }
}
