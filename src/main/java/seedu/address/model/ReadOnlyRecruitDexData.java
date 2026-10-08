package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.applicant.Applicant;

/**
 * Unmodifiable view of an RecruitDex
 */
public interface ReadOnlyRecruitDexData {

    /**
     * Returns an unmodifiable view of the applicants list.
     * This list will not contain any duplicate applicants.
     */
    ObservableList<Applicant> getApplicantList();

}
