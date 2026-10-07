package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;

/**
 * Represents where an Applicant was sourced from, such as LinkedIn or a referral.
 * Guarantees: immutable; the source is free text and may be empty.
 */
public class Source {

    public static final String DEFAULT_VALUE = "";
    public final String value;

    /**
     * Constructs a {@code Source}.
     *
     * @param source Free text, possibly empty.
     */
    public Source(String source) {
        requireNonNull(source);
        value = source;
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
        if (!(other instanceof Source otherSource)) {
            return false;
        }

        return value.equals(otherSource.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
