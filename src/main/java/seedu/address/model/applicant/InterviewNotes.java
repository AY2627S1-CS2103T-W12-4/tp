package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;

/**
 * Represents an Applicant's interview notes in the address book.
 * Guarantees: immutable; notes are free text and may be empty.
 */
public class InterviewNotes {

    public static final String DEFAULT_VALUE = "";
    public final String value;

    /**
     * Constructs an {@code InterviewNotes}.
     *
     * @param interviewNotes Free text, possibly empty.
     */
    public InterviewNotes(String interviewNotes) {
        requireNonNull(interviewNotes);
        value = interviewNotes;
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
        if (!(other instanceof InterviewNotes otherInterviewNotes)) {
            return false;
        }

        return value.equals(otherInterviewNotes.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
