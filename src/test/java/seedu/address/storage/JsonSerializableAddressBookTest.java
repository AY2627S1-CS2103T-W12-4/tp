package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalApplicants;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_APPLICANTS_FILE = TEST_DATA_FOLDER.resolve("typicalApplicantsAddressBook.json");
    private static final Path INVALID_APPLICANT_FILE = TEST_DATA_FOLDER.resolve("invalidApplicantAddressBook.json");
    private static final Path DUPLICATE_APPLICANT_FILE = TEST_DATA_FOLDER.resolve("duplicateApplicantAddressBook.json");

    @Test
    public void toModelType_typicalApplicantsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_APPLICANTS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalApplicantsAddressBook = TypicalApplicants.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalApplicantsAddressBook);
    }

    @Test
    public void toModelType_invalidApplicantFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_APPLICANT_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateApplicants_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_APPLICANT_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_APPLICANT,
                dataFromFile::toModelType);
    }

}
