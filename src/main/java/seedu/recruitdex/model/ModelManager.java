package seedu.recruitdex.model;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.recruitdex.commons.core.LogsCenter;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantRegistry;
import seedu.recruitdex.model.comparison.ComparisonList;
import seedu.recruitdex.model.ranking.RankingEngine;
import seedu.recruitdex.model.userprefs.ReadOnlyUserPrefs;
import seedu.recruitdex.model.userprefs.UserPrefs;

/**
 * Holds saved RecruitDex data, preferences, and runtime filtering and ranking.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final RecruitDexData recruitDexData;
    private final UserPrefs userPrefs;
    private final RankingEngine rankingEngine;
    private final FilteredList<Applicant> filteredApplicants;
    private final ObservableList<Applicant> unmodifiableFilteredApplicants;

    /**
     * Initializes a ModelManager with copies of the given application data and preferences.
     */
    public ModelManager(ReadOnlyRecruitDexData recruitDexData, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(recruitDexData, userPrefs);

        logger.fine("Initializing with RecruitDex data: " + recruitDexData + " and user prefs " + userPrefs);

        rankingEngine = new RankingEngine();
        this.recruitDexData = new RecruitDexData(recruitDexData, rankingEngine);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredApplicants = new FilteredList<>(getApplicantRegistry().getApplicantList());
        unmodifiableFilteredApplicants = FXCollections.unmodifiableObservableList(filteredApplicants);
    }

    public ModelManager() {
        this(new RecruitDexData(), new UserPrefs());
    }

    @Override
    public UserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public void setRecruitDexData(ReadOnlyRecruitDexData recruitDexData) {
        // RecruitDexData retains its components and restores comparisons and their derived ranking.
        this.recruitDexData.resetData(recruitDexData);
    }

    @Override
    public ReadOnlyRecruitDexData getRecruitDexData() {
        return recruitDexData;
    }

    @Override
    public ApplicantRegistry getApplicantRegistry() {
        return recruitDexData.getApplicantRegistry();
    }

    @Override
    public ComparisonList getComparisonList() {
        return recruitDexData.getComparisonList();
    }

    @Override
    public RankingEngine getRankingEngine() {
        return rankingEngine;
    }

    @Override
    public ObservableList<Applicant> getFilteredApplicantList() {
        return unmodifiableFilteredApplicants;
    }

    @Override
    public void updateFilteredApplicantList(Predicate<Applicant> predicate) {
        requireNonNull(predicate);
        filteredApplicants.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return recruitDexData.equals(otherModelManager.recruitDexData)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredApplicants.equals(otherModelManager.filteredApplicants);
    }
}
