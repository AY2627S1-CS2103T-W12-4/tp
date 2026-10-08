package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ApplicantIdTest {

    private static final String VALID_ID = "123e4567-e89b-12d3-a456-426614174000";

    private static final String[] INVALID_IDS = {
        "", " ", "not-a-uuid", "123e4567e89b12d3a456426614174000", // no dashes
        "123e4567-e89b-12d3-a456-42661417400", // too short
        "123e4567-e89b-12d3-a456-4266141740000", // too long
        "123e4567-e89b-12d3-a456-42661417400g", // non-hex character
        " 123e4567-e89b-12d3-a456-426614174000", // leading space
        "123e4567-e89b-12d3-a456-426614174000 ", // trailing space
        "1-1-1-1-1" // accepted by UUID.fromString but not canonical
    };

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ApplicantId(null));
    }

    @Test
    public void constructor_invalidId_throwsIllegalArgumentException() {
        for (String invalidId : INVALID_IDS) {
            assertThrows(IllegalArgumentException.class, () -> new ApplicantId(invalidId));
        }
    }

    @Test
    public void constructor_noArgs_generatesValidId() {
        assertTrue(ApplicantId.isValidApplicantId(new ApplicantId().value));
    }

    @Test
    public void constructor_noArgs_generatesDifferentIdsEachTime() {
        assertNotEquals(new ApplicantId(), new ApplicantId());
    }

    @Test
    public void constructor_uppercaseId_normalisedToLowercase() {
        assertEquals(VALID_ID, new ApplicantId(VALID_ID.toUpperCase()).value);
    }

    @Test
    public void isValidApplicantId() {
        // null id
        assertThrows(NullPointerException.class, () -> ApplicantId.isValidApplicantId(null));

        // invalid ids
        for (String invalidId : INVALID_IDS) {
            assertFalse(ApplicantId.isValidApplicantId(invalidId), "should be invalid: '" + invalidId + "'");
        }

        // valid ids
        assertTrue(ApplicantId.isValidApplicantId(VALID_ID));
        assertTrue(ApplicantId.isValidApplicantId(VALID_ID.toUpperCase()));
        assertTrue(ApplicantId.isValidApplicantId("00000000-0000-0000-0000-000000000000")); // nil UUID
    }

    @Test
    public void toString_returnsValue() {
        assertEquals(VALID_ID, new ApplicantId(VALID_ID).toString());
    }

    @Test
    public void equals() {
        ApplicantId id = new ApplicantId(VALID_ID);

        // same values -> returns true
        assertTrue(id.equals(new ApplicantId(VALID_ID)));

        // same values, different case -> returns true
        assertTrue(id.equals(new ApplicantId(VALID_ID.toUpperCase())));

        // same object -> returns true
        assertTrue(id.equals(id));

        // null -> returns false
        assertFalse(id.equals(null));

        // different types -> returns false
        assertFalse(id.equals(5.0f));

        // different values -> returns false
        assertFalse(id.equals(new ApplicantId("123e4567-e89b-12d3-a456-426614174001")));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new ApplicantId(VALID_ID).hashCode(), new ApplicantId(VALID_ID.toUpperCase()).hashCode());
    }
}
