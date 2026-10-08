package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.applicant.Applicant;

/**
 * Represents the in-memory model of RecruitDex data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final RecruitDexData recruitDexData;
    private final UserPrefs userPrefs;
    private final FilteredList<Applicant> filteredApplicants;

    /**
     * Initializes a ModelManager with the given recruitDexData and userPrefs.
     */
    public ModelManager(ReadOnlyRecruitDexData recruitDexData, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(recruitDexData, userPrefs);

        logger.fine("Initializing with RecruitDex: " + recruitDexData + " and user prefs " + userPrefs);

        this.recruitDexData = new RecruitDexData(recruitDexData);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredApplicants = new FilteredList<>(this.recruitDexData.getApplicantList());
    }

    public ModelManager() {
        this(new RecruitDexData(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== RecruitDexData ================================================================================

    @Override
    public void setRecruitDexData(ReadOnlyRecruitDexData recruitDexData) {
        this.recruitDexData.resetData(recruitDexData);
    }

    @Override
    public ReadOnlyRecruitDexData getRecruitDexData() {
        return recruitDexData;
    }

    @Override
    public boolean hasApplicant(Applicant applicant) {
        requireNonNull(applicant);
        return recruitDexData.hasApplicant(applicant);
    }

    @Override
    public void deleteApplicant(Applicant target) {
        recruitDexData.removeApplicant(target);
    }

    @Override
    public void addApplicant(Applicant applicant) {
        recruitDexData.addApplicant(applicant);
        updateFilteredApplicantList(PREDICATE_SHOW_ALL_APPLICANTS);
    }

    @Override
    public void setApplicant(Applicant target, Applicant editedApplicant) {
        requireAllNonNull(target, editedApplicant);

        recruitDexData.setApplicant(target, editedApplicant);
    }

    //=========== Filtered Applicant List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Applicant} backed by the internal list of
     * {@code recruitDexData}
     */
    @Override
    public ObservableList<Applicant> getFilteredApplicantList() {
        return filteredApplicants;
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

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return recruitDexData.equals(otherModelManager.recruitDexData)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredApplicants.equals(otherModelManager.filteredApplicants);
    }

}
