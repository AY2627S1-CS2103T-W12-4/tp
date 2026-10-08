package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.userprefs.ReadOnlyUserPrefs;
import seedu.address.model.userprefs.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage {

    /**
     * Returns the file path of the UserPrefs data file.
     */
    Path getUserPrefsFilePath();

    /**
     * Returns UserPrefs data from storage.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if the loading of data from preference file failed.
     */
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    /**
     * Saves the given {@link seedu.address.model.userprefs.ReadOnlyUserPrefs} to the storage.
     * @param userPrefs cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    /**
     * Returns the file path of RecruitDexData data file.
     */
    Path getRecruitDexDataFilePath();

    /**
     * Returns RecruitDexData data as a {@link ReadOnlyRecruitDexData}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyRecruitDexData> readRecruitDexData() throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyRecruitDexData} to the storage.
     * @param recruitDexData cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveRecruitDexData(ReadOnlyRecruitDexData recruitDexData) throws IOException;

}
