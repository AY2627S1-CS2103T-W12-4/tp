package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.comparison.ComparisonList;
import seedu.address.model.userprefs.UserPrefs;

/**
 * The API used by commands to access RecruitDex's model objects and shared applicant filter.
 * Comparison and ranking contracts are intentionally not connected to the running app yet.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true. */
    Predicate<Applicant> PREDICATE_SHOW_ALL_APPLICANTS = unused -> true;

    /** Returns the live user preferences, which are saved separately from application data. */
    UserPrefs getUserPrefs();

    /** Replaces saved application data while retaining the shared applicant view. */
    void setRecruitDexData(ReadOnlyRecruitDexData recruitDexData);

    /** Returns a read-only view of the saved application data. */
    ReadOnlyRecruitDexData getRecruitDexData();

    /** Returns the live registry responsible for applicant record operations. */
    ApplicantRegistry getApplicantRegistry();

    /**
     * Returns the live comparison list responsible for membership and decision operations.
     * @throws UnsupportedOperationException if comparison models have not been connected yet.
     */
    ComparisonList getComparisonList();

    /** Returns an unmodifiable observable view of the displayed applicants. */
    ObservableList<Applicant> getFilteredApplicantList();

    /**
     * Updates the display filter without changing saved applicant records.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredApplicantList(Predicate<Applicant> predicate);
}
