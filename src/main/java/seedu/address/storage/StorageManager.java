package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of RecruitDexData data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonRecruitDexDataStorage recruitDexDataStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given RecruitDex and user prefs storage.
     */
    public StorageManager(JsonRecruitDexDataStorage recruitDexDataStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.recruitDexDataStorage = recruitDexDataStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ RecruitDexData methods ==============================

    @Override
    public Path getRecruitDexDataFilePath() {
        return recruitDexDataStorage.getRecruitDexDataFilePath();
    }

    @Override
    public Optional<ReadOnlyRecruitDexData> readRecruitDexData() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + recruitDexDataStorage.getRecruitDexDataFilePath());
        return recruitDexDataStorage.readRecruitDexData();
    }

    @Override
    public void saveRecruitDexData(ReadOnlyRecruitDexData recruitDexData) throws IOException {
        logger.fine("Attempting to write to data file: " + recruitDexDataStorage.getRecruitDexDataFilePath());
        recruitDexDataStorage.saveRecruitDexData(recruitDexData);
    }

}
