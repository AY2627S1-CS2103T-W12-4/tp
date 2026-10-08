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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.RecruitDexData;
import seedu.address.model.applicant.Applicant;
import seedu.address.testutil.ApplicantBuilder;

public class JsonRecruitDexDataStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonRecruitDexDataStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAndSaveRecruitDexData_editAndReload_preservesIdsAndOptionalDetails() throws Exception {
        Path filePath = testFolder.resolve("recruitdex.json");
        JsonRecruitDexDataStorage storage = new JsonRecruitDexDataStorage(filePath);
        RecruitDexData original = getTypicalRecruitDexData();
        Applicant edited = new ApplicantBuilder(ALICE).withName("Alice Tan")
                .withInterviewNotes("Strong interview").withYearsOfExperience("5").withSource("Referral").build();
        original.getApplicantRegistry().edit(ALICE.getId(), edited);

        storage.saveRecruitDexData(original);
        RecruitDexData restored = new RecruitDexData(storage.readRecruitDexData().orElseThrow());

        assertEquals(edited, restored.getApplicantRegistry().get(ALICE.getId()));
        assertEquals(original.getApplicantList().stream().map(Applicant::getId).toList(),
                restored.getApplicantList().stream().map(Applicant::getId).toList());
    }

    @Test
    public void readRecruitDexData_duplicateIds_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("duplicateIds.json");
        Applicant duplicate = new ApplicantBuilder(ALICE).withName("Another applicant").build();
        JsonUtil.saveJsonFile(new JsonSerializableRecruitDexData(
                List.of(new JsonAdaptedApplicant(ALICE), new JsonAdaptedApplicant(duplicate))), filePath);

        JsonRecruitDexDataStorage storage = new JsonRecruitDexDataStorage(filePath);
        assertThrows(DataLoadingException.class, storage::readRecruitDexData);
    }

    @Test
    public void readRecruitDexData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readRecruitDexData(null));
    }

    private java.util.Optional<ReadOnlyRecruitDexData> readRecruitDexData(String filePath) throws Exception {
        return new JsonRecruitDexDataStorage(Paths.get(filePath))
                .readRecruitDexData(addToTestDataPathIfNotNull(filePath));
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
        assertThrows(DataLoadingException.class, () ->
                readRecruitDexData("invalidAndValidApplicantRecruitDexData.json"));
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
        original.getApplicantRegistry().add(HOON);
        original.getApplicantRegistry().delete(ALICE.getId());
        jsonRecruitDexDataStorage.saveRecruitDexData(original, filePath);
        readBack = jsonRecruitDexDataStorage.readRecruitDexData(filePath).get();
        assertEquals(original, new RecruitDexData(readBack));

        // Save and read without specifying file path
        original.getApplicantRegistry().add(IDA);
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
