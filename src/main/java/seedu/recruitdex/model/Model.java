package seedu.recruitdex.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantRegistry;
import seedu.recruitdex.model.comparison.ComparisonList;
import seedu.recruitdex.model.ranking.RankingEngine;
import seedu.recruitdex.model.userprefs.UserPrefs;

/**
 * The API used to access RecruitDex's model objects, runtime ranking engine, and shared applicant filter.
 * Comparison data is persisted with applicant records, and comparison pairs are selected automatically.
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
     * Returns the live comparison list, retained when application data is reset.
     */
    ComparisonList getComparisonList();

    /**
     * Returns the live engine responsible for derived runtime rankings.
     */
    RankingEngine getRankingEngine();

    /** Returns an unmodifiable observable view of the displayed applicants. */
    ObservableList<Applicant> getFilteredApplicantList();

    /** Returns the current non-null display filter, which can be restored after a failed command. */
    Predicate<? super Applicant> getFilteredApplicantPredicate();

    /**
     * Updates the display filter without changing saved applicant records.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredApplicantList(Predicate<? super Applicant> predicate);
}
