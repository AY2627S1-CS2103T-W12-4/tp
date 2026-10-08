package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an Applicant's years of work experience in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidYearsOfExperience(String)}
 */
public class YearsOfExperience {

    public static final String MESSAGE_CONSTRAINTS =
            "Years of experience should be a whole number from 0 to 60, or empty if unknown";
    public static final String VALIDATION_REGEX = "|\\d|[1-5]\\d|60";
    public static final String DEFAULT_VALUE = "";
    public final String value;

    /**
     * Constructs a {@code YearsOfExperience}.
     *
     * @param yearsOfExperience A whole number from 0 to 60, or an empty string if unknown.
     */
    public YearsOfExperience(String yearsOfExperience) {
        requireNonNull(yearsOfExperience);
        checkArgument(isValidYearsOfExperience(yearsOfExperience), MESSAGE_CONSTRAINTS);
        value = yearsOfExperience;
    }

    /**
     * Returns true if a given string is a valid number of years of experience.
     */
    public static boolean isValidYearsOfExperience(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof YearsOfExperience otherYearsOfExperience)) {
            return false;
        }

        return value.equals(otherYearsOfExperience.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
