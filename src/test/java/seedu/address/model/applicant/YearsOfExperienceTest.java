package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class YearsOfExperienceTest {

    private static final String[] VALID_VALUES = {"", "0", "1", "9", "10", "11", "25", "49", "50", "59", "60"};

    private static final String[] INVALID_VALUES = {
        " ", // spaces only
        "-1", // negative
        "61", // just above the maximum
        "99", "100", "600", // well above the maximum
        "07", "007", "060", // leading zeros
        "1.5", "5.0", // decimals
        "five", "abc", "5a", "a5", // non-numeric
        " 5", "5 ", "1 0", // whitespace
        "+5", // sign
        "1e1", // exponent
        "\u0665" // non-ASCII digit
    };

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new YearsOfExperience(null));
    }

    @Test
    public void constructor_invalidValue_throwsIllegalArgumentException() {
        for (String invalidValue : INVALID_VALUES) {
            assertThrows(IllegalArgumentException.class, () -> new YearsOfExperience(invalidValue));
        }
    }

    @Test
    public void constructor_validValue_storesValueUnchanged() {
        for (String validValue : VALID_VALUES) {
            assertEquals(validValue, new YearsOfExperience(validValue).value);
        }
    }

    @Test
    public void isValidYearsOfExperience() {
        // null value
        assertThrows(NullPointerException.class, () -> YearsOfExperience.isValidYearsOfExperience(null));

        // invalid values
        for (String invalidValue : INVALID_VALUES) {
            assertFalse(YearsOfExperience.isValidYearsOfExperience(invalidValue),
                    "should be invalid: '" + invalidValue + "'");
        }

        // valid values
        for (String validValue : VALID_VALUES) {
            assertTrue(YearsOfExperience.isValidYearsOfExperience(validValue),
                    "should be valid: '" + validValue + "'");
        }
    }

    @Test
    public void defaultValue_isValidAndEmpty() {
        assertEquals("", YearsOfExperience.DEFAULT_VALUE);
        assertTrue(YearsOfExperience.isValidYearsOfExperience(YearsOfExperience.DEFAULT_VALUE));
    }

    @Test
    public void toString_returnsValue() {
        assertEquals("7", new YearsOfExperience("7").toString());
    }

    @Test
    public void equals() {
        YearsOfExperience years = new YearsOfExperience("5");

        // same values -> returns true
        assertTrue(years.equals(new YearsOfExperience("5")));

        // same object -> returns true
        assertTrue(years.equals(years));

        // null -> returns false
        assertFalse(years.equals(null));

        // different types -> returns false
        assertFalse(years.equals(5.0f));

        // different values -> returns false
        assertFalse(years.equals(new YearsOfExperience("6")));

        // empty versus non-empty -> returns false
        assertFalse(years.equals(new YearsOfExperience("")));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new YearsOfExperience("5").hashCode(), new YearsOfExperience("5").hashCode());
    }
}
