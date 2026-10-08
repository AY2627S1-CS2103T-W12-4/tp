package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.RecruitDexData;
import seedu.address.model.applicant.Applicant;
import seedu.address.testutil.ApplicantBuilder;
import seedu.address.testutil.TypicalApplicants;

public class JsonSerializableRecruitDexDataTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableRecruitDexDataTest");
    private static final Path TYPICAL_APPLICANTS_FILE =
            TEST_DATA_FOLDER.resolve("typicalApplicantsRecruitDexData.json");
    private static final Path INVALID_APPLICANT_FILE = TEST_DATA_FOLDER.resolve("invalidApplicantRecruitDexData.json");
    private static final Path DUPLICATE_APPLICANT_FILE =
            TEST_DATA_FOLDER.resolve("duplicateApplicantRecruitDexData.json");

    @Test
    public void toModelType_duplicateIdsWithDifferentNames_throwsIllegalValueException() {
        Applicant first = new ApplicantBuilder().withName("First applicant").build();
        Applicant second = new ApplicantBuilder(first).withName("Second applicant").build();
        JsonSerializableRecruitDexData data = new JsonSerializableRecruitDexData(
                List.of(new JsonAdaptedApplicant(first), new JsonAdaptedApplicant(second)));

        assertThrows(IllegalValueException.class, JsonSerializableRecruitDexData.MESSAGE_DUPLICATE_APPLICANT,
                data::toModelType);
    }

    @Test
    public void toModelType_typicalApplicantsFile_success() throws Exception {
        JsonSerializableRecruitDexData dataFromFile = JsonUtil.readJsonFile(TYPICAL_APPLICANTS_FILE,
                JsonSerializableRecruitDexData.class).get();
        RecruitDexData recruitDexDataFromFile = dataFromFile.toModelType();
        RecruitDexData typicalApplicantsRecruitDexData = TypicalApplicants.getTypicalRecruitDexData();
        assertEquals(recruitDexDataFromFile, typicalApplicantsRecruitDexData);
    }

    @Test
    public void toModelType_invalidApplicantFile_throwsIllegalValueException() throws Exception {
        JsonSerializableRecruitDexData dataFromFile = JsonUtil.readJsonFile(INVALID_APPLICANT_FILE,
                JsonSerializableRecruitDexData.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateApplicants_throwsIllegalValueException() throws Exception {
        JsonSerializableRecruitDexData dataFromFile = JsonUtil.readJsonFile(DUPLICATE_APPLICANT_FILE,
                JsonSerializableRecruitDexData.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableRecruitDexData.MESSAGE_DUPLICATE_APPLICANT,
                dataFromFile::toModelType);
    }

}
