package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.storage.JsonAdaptedApplicant.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.applicant.Address;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.InterviewNotes;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;
import seedu.address.model.applicant.Source;
import seedu.address.model.applicant.YearsOfExperience;

public class JsonAdaptedApplicantTest {
    private static final String INVALID_ID = "not-a-uuid";
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_YEARS_OF_EXPERIENCE = "61";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_ID = BENSON.getId().toString();
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_INTERVIEW_NOTES = BENSON.getInterviewNotes().toString();
    private static final String VALID_YEARS_OF_EXPERIENCE = BENSON.getYearsOfExperience().toString();
    private static final String VALID_SOURCE = BENSON.getSource().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    /**
     * Returns a {@code JsonAdaptedApplicant} with BENSON's details, except for the given required fields.
     */
    private static JsonAdaptedApplicant adaptedWithRequired(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags) {
        return new JsonAdaptedApplicant(VALID_ID, name, phone, email, address, VALID_INTERVIEW_NOTES,
                VALID_YEARS_OF_EXPERIENCE, VALID_SOURCE, tags);
    }

    /**
     * Returns a {@code JsonAdaptedApplicant} with BENSON's details, except for the given optional fields.
     */
    private static JsonAdaptedApplicant adaptedWithOptional(String id, String interviewNotes,
            String yearsOfExperience, String source) {
        return new JsonAdaptedApplicant(id, VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, interviewNotes,
                yearsOfExperience, source, VALID_TAGS);
    }

    @Test
    public void toModelType_validApplicantDetails_returnsApplicant() throws Exception {
        JsonAdaptedApplicant applicant = new JsonAdaptedApplicant(BENSON);
        assertEquals(BENSON, applicant.toModelType());
    }

    @Test
    public void toModelType_validApplicantDetails_preservesId() throws Exception {
        Applicant converted = new JsonAdaptedApplicant(BENSON).toModelType();
        assertEquals(BENSON.getId(), converted.getId());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, applicant::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedApplicant applicant =
                adaptedWithRequired(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, invalidTags);
        assertThrows(IllegalValueException.class, applicant::toModelType);
    }

    @Test
    public void toModelType_invalidId_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant = adaptedWithOptional(INVALID_ID, VALID_INTERVIEW_NOTES,
                VALID_YEARS_OF_EXPERIENCE, VALID_SOURCE);
        assertThrows(IllegalValueException.class, ApplicantId.MESSAGE_CONSTRAINTS, applicant::toModelType);
    }

    @Test
    public void toModelType_invalidYearsOfExperience_throwsIllegalValueException() {
        JsonAdaptedApplicant applicant = adaptedWithOptional(VALID_ID, VALID_INTERVIEW_NOTES,
                INVALID_YEARS_OF_EXPERIENCE, VALID_SOURCE);
        assertThrows(IllegalValueException.class, YearsOfExperience.MESSAGE_CONSTRAINTS, applicant::toModelType);
    }

    @Test
    public void toModelType_nullId_generatesNewId() throws Exception {
        Applicant converted = adaptedWithOptional(null, VALID_INTERVIEW_NOTES, VALID_YEARS_OF_EXPERIENCE,
                VALID_SOURCE).toModelType();
        assertNotNull(converted.getId());
    }

    @Test
    public void toModelType_nullOptionalFields_usesEmptyDefaults() throws Exception {
        Applicant converted = adaptedWithOptional(VALID_ID, null, null, null).toModelType();
        assertEquals(new InterviewNotes(InterviewNotes.DEFAULT_VALUE), converted.getInterviewNotes());
        assertEquals(new YearsOfExperience(YearsOfExperience.DEFAULT_VALUE), converted.getYearsOfExperience());
        assertEquals(new Source(Source.DEFAULT_VALUE), converted.getSource());
    }

    @Test
    public void toModelType_emptyOptionalFields_returnsApplicantWithEmptyFields() throws Exception {
        Applicant converted = adaptedWithOptional(VALID_ID, "", "", "").toModelType();
        assertEquals("", converted.getInterviewNotes().value);
        assertEquals("", converted.getYearsOfExperience().value);
        assertEquals("", converted.getSource().value);
    }

}
