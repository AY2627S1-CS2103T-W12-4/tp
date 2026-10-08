package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;

/**
 * Single source of applicant records, preserving registration order and stable IDs.
 * Retains the existing same-name duplicate-record policy separately from ID uniqueness.
 * All mutations validate before changing the read-only observable applicant view.
 */
public class ApplicantRegistry {

    private final ObservableList<Applicant> applicants = FXCollections.observableArrayList();
    private final ObservableList<Applicant> unmodifiableApplicants =
            FXCollections.unmodifiableObservableList(applicants);

    public ApplicantRegistry() {}

    /**
     * Returns whether a record with the given ID is registered.
     */
    public boolean containsId(ApplicantId applicantId) {
        requireNonNull(applicantId);
        return applicants.stream().anyMatch(applicant -> applicant.getId().equals(applicantId));
    }

    /**
     * Checks both ID uniqueness and the existing same-name duplicate-record policy.
     * A name is a duplicate-record check, not the identifier used to edit or delete a record.
     */
    public boolean hasDuplicate(Applicant applicant) {
        requireNonNull(applicant);
        return containsId(applicant.getId()) || applicants.stream().anyMatch(applicant::isSameApplicant);
    }

    /**
     * Registers an applicant under its existing ID and returns that ID.
     * Validation failures leave existing records unchanged.
     * @throws DuplicateApplicantException if the ID or duplicate-record check conflicts with an existing record.
     */
    public ApplicantId add(Applicant applicant) {
        requireNonNull(applicant);
        if (hasDuplicate(applicant)) {
            throw new DuplicateApplicantException();
        }
        applicants.add(applicant);
        return applicant.getId();
    }

    /**
     * Replaces a registered applicant while retaining its ID and registration position.
     * Validation failures leave existing records unchanged.
     * @throws ApplicantNotFoundException if the ID is not registered.
     * @throws IllegalArgumentException if the edited applicant has a different ID.
     * @throws DuplicateApplicantException if another record fails the duplicate-record check.
     */
    public void edit(ApplicantId applicantId, Applicant editedApplicant) {
        requireAllNonNull(applicantId, editedApplicant);
        int index = indexOf(applicantId);
        checkArgument(applicantId.equals(editedApplicant.getId()), "Editing an applicant must not change its ID.");

        boolean duplicatesAnother = applicants.stream()
                .filter(applicant -> !applicant.getId().equals(applicantId))
                .anyMatch(editedApplicant::isSameApplicant);
        if (duplicatesAnother) {
            throw new DuplicateApplicantException();
        }
        applicants.set(index, editedApplicant);
    }

    /**
     * Returns the record registered under the given ID.
     * @throws ApplicantNotFoundException if the ID is not registered.
     */
    public Applicant get(ApplicantId applicantId) {
        requireNonNull(applicantId);
        return applicants.get(indexOf(applicantId));
    }

    /**
     * Permanently deletes the record identified by the given ID.
     * Comparison references will be coordinated here when comparison models are implemented.
     * @throws ApplicantNotFoundException if the ID is not registered.
     */
    public void delete(ApplicantId applicantId) {
        requireNonNull(applicantId);
        applicants.remove(indexOf(applicantId));
    }

    /**
     * Validates and replaces all records, preserving the observable view.
     * Invalid data leaves the registry unchanged. Copying first also permits resetting from this registry.
     */
    public void setApplicants(List<Applicant> replacement) {
        requireNonNull(replacement);
        ApplicantRegistry validated = new ApplicantRegistry();
        for (Applicant applicant : replacement) {
            validated.add(applicant);
        }
        applicants.setAll(validated.applicants);
    }

    /** Returns a live, unmodifiable observable view of the registered records. */
    public ObservableList<Applicant> getApplicantList() {
        return unmodifiableApplicants;
    }

    private int indexOf(ApplicantId applicantId) {
        for (int index = 0; index < applicants.size(); index++) {
            if (applicants.get(index).getId().equals(applicantId)) {
                return index;
            }
        }
        throw new ApplicantNotFoundException();
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof ApplicantRegistry otherRegistry && applicants.equals(otherRegistry.applicants);
    }

    @Override
    public int hashCode() {
        return applicants.hashCode();
    }

    @Override
    public String toString() {
        return applicants.toString();
    }
}
