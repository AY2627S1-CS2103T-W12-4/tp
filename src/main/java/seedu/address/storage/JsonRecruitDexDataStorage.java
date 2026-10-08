package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyRecruitDexData;

/**
 * A class to access RecruitDexData data stored as a JSON file on the hard disk.
 */
public class JsonRecruitDexDataStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonRecruitDexDataStorage.class);

    private Path filePath;

    public JsonRecruitDexDataStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getRecruitDexDataFilePath() {
        return filePath;
    }

    /**
     * Returns RecruitDexData data as a {@link ReadOnlyRecruitDexData}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyRecruitDexData> readRecruitDexData() throws DataLoadingException {
        return readRecruitDexData(filePath);
    }

    /**
     * Similar to {@link #readRecruitDexData()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyRecruitDexData> readRecruitDexData(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableRecruitDexData> jsonRecruitDexData = JsonUtil.readJsonFile(
                filePath, JsonSerializableRecruitDexData.class);
        if (!jsonRecruitDexData.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonRecruitDexData.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyRecruitDexData} to the storage.
     * @param recruitDexData cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveRecruitDexData(ReadOnlyRecruitDexData recruitDexData) throws IOException {
        saveRecruitDexData(recruitDexData, filePath);
    }

    /**
     * Similar to {@link #saveRecruitDexData(ReadOnlyRecruitDexData)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveRecruitDexData(ReadOnlyRecruitDexData recruitDexData, Path filePath) throws IOException {
        requireNonNull(recruitDexData);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableRecruitDexData(recruitDexData), filePath);
    }

}
