package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.UUID;

/**
 * Represents an Applicant's unique identifier in RecruitDex.
 * Guarantees: immutable; is valid as declared in {@link #isValidApplicantId(String)}
 */
public class ApplicantId {

    public static final String MESSAGE_CONSTRAINTS =
            "Applicant IDs should be UUIDs in the form 123e4567-e89b-12d3-a456-426614174000";
    public static final String VALIDATION_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    public final String value;

    /**
     * Constructs a new random {@code ApplicantId}.
     */
    public ApplicantId() {
        value = UUID.randomUUID().toString();
    }

    /**
     * Constructs an {@code ApplicantId} from an existing identifier.
     *
     * @param applicantId A valid UUID string.
     */
    public ApplicantId(String applicantId) {
        requireNonNull(applicantId);
        checkArgument(isValidApplicantId(applicantId), MESSAGE_CONSTRAINTS);
        value = applicantId.toLowerCase();
    }

    /**
     * Returns true if a given string is a valid applicant ID.
     */
    public static boolean isValidApplicantId(String test) {
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
        if (!(other instanceof ApplicantId otherApplicantId)) {
            return false;
        }

        return value.equals(otherApplicantId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
