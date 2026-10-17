package seedu.recruitdex.model;

import javafx.collections.ObservableList;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.comparison.ReadOnlyComparisonList;

/**
 * Read-only access to the application data saved together in one file.
 */
public interface ReadOnlyRecruitDexData {

    /**
     * Returns an unmodifiable view of the applicants list.
     * Records have unique IDs and satisfy the registry's duplicate-record policy.
     */
    ObservableList<Applicant> getApplicantList();

    /** Returns read-only access to all comparison data saved with the applicant records. */
    ReadOnlyComparisonList getComparisonList();

}
