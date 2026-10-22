package seedu.recruitdex.model.applicant;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.commons.util.AppUtil.checkArgument;

/**
 * Represents an Applicant's address in RecruitDex.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {

    public static final String DEFAULT_VALUE = "";
    public static final String MESSAGE_CONSTRAINTS =
            "Addresses should be empty or start with a non-whitespace character";

    /*
     * The first character of a non-empty address must not be a whitespace.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address, or empty if not provided.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        value = address;
    }

    /**
     * Returns true if a given string is empty or a valid address.
     */
    public static boolean isValidAddress(String test) {
        return test.isEmpty() || test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Address otherAddress)) {
            return false;
        }

        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
