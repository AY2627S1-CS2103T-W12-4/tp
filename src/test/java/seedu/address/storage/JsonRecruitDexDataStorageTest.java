package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.HOON;
import static seedu.address.testutil.TypicalApplicants.IDA;
import static seedu.address.testutil.TypicalApplicants.getTypicalRecruitDexData;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.RecruitDexData;
import seedu.address.model.ReadOnlyRecruitDexData;

public class JsonRecruitDexDataStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonRecruitDexDataStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readRecruitDexData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readRecruitDexData(null));
    }

    private java.util.Optional<ReadOnlyRecruitDexData> readRecruitDexData(String filePath) throws Exception {
        return new JsonRecruitDexDataStorage(Paths.get(filePath)).readRecruitDexData(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readRecruitDexData("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readRecruitDexData("notJsonFormatRecruitDexData.json"));
    }

    @Test
    public void readRecruitDexData_invalidApplicantRecruitDexData_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readRecruitDexData("invalidApplicantRecruitDexData.json"));
    }

    @Test
    public void readRecruitDexData_invalidAndValidApplicantRecruitDexData_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readRecruitDexData("invalidAndValidApplicantRecruitDexData.json"));
    }

    @Test
    public void readAndSaveRecruitDexData_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempRecruitDexData.json");
        RecruitDexData original = getTypicalRecruitDexData();
        JsonRecruitDexDataStorage jsonRecruitDexDataStorage = new JsonRecruitDexDataStorage(filePath);

        // Save in new file and read back
        jsonRecruitDexDataStorage.saveRecruitDexData(original, filePath);
        ReadOnlyRecruitDexData readBack = jsonRecruitDexDataStorage.readRecruitDexData(filePath).get();
        assertEquals(original, new RecruitDexData(readBack));

        // Modify data, overwrite existing file, and read back
        original.addApplicant(HOON);
        original.removeApplicant(ALICE);
        jsonRecruitDexDataStorage.saveRecruitDexData(original, filePath);
        readBack = jsonRecruitDexDataStorage.readRecruitDexData(filePath).get();
        assertEquals(original, new RecruitDexData(readBack));

        // Save and read without specifying file path
        original.addApplicant(IDA);
        jsonRecruitDexDataStorage.saveRecruitDexData(original); // file path not specified
        readBack = jsonRecruitDexDataStorage.readRecruitDexData().get(); // file path not specified
        assertEquals(original, new RecruitDexData(readBack));

    }

    @Test
    public void saveRecruitDexData_nullRecruitDexData_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveRecruitDexData(null, "SomeFile.json"));
    }

    /**
     * Saves {@code recruitDexData} at the specified {@code filePath}.
     */
    private void saveRecruitDexData(ReadOnlyRecruitDexData recruitDexData, String filePath) {
        try {
            new JsonRecruitDexDataStorage(Paths.get(filePath))
                    .saveRecruitDexData(recruitDexData, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveRecruitDexData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveRecruitDexData(new RecruitDexData(), null));
    }
}
