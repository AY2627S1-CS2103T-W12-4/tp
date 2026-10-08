package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SourceTest {

    private static final String[] ACCEPTED_VALUES = {
        "", // empty means not recorded
        " ", // whitespace only
        "LinkedIn",
        "a", // single character
        "Strong in Java; weak on testing.", // punctuation
        "line one\nline two", // multiple lines
        "caf\u00e9 \u00fcber \u4e2d\u6587", // non-ASCII
        "a/b y/5 i/x", // text that looks like command prefixes
        "x".repeat(1000) // long text
    };

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Source(null));
    }

    @Test
    public void constructor_anyText_storesValueUnchanged() {
        for (String value : ACCEPTED_VALUES) {
            assertEquals(value, new Source(value).value);
        }
    }

    @Test
    public void defaultValue_isEmpty() {
        assertEquals("", Source.DEFAULT_VALUE);
    }

    @Test
    public void toString_returnsValue() {
        for (String value : ACCEPTED_VALUES) {
            assertEquals(value, new Source(value).toString());
        }
    }

    @Test
    public void equals() {
        Source source = new Source("Strong in Java");

        // same values -> returns true
        assertTrue(source.equals(new Source("Strong in Java")));

        // same object -> returns true
        assertTrue(source.equals(source));

        // null -> returns false
        assertFalse(source.equals(null));

        // different types -> returns false
        assertFalse(source.equals(5.0f));

        // different values -> returns false
        assertFalse(source.equals(new Source("Weak in Java")));

        // different case -> returns false
        assertFalse(source.equals(new Source("strong in java")));

        // empty versus non-empty -> returns false
        assertFalse(source.equals(new Source("")));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new Source("Strong in Java").hashCode(), new Source("Strong in Java").hashCode());
    }
}
