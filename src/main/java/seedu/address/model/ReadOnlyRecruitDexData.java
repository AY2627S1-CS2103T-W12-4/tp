package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.applicant.Applicant;

/**
 * Read-only access to the application data saved together in one file.
 */
public interface ReadOnlyRecruitDexData {

    /**
     * Returns an unmodifiable view of the applicants list.
     * Records have unique IDs and satisfy the registry's duplicate-record policy.
     */
    ObservableList<Applicant> getApplicantList();

}
