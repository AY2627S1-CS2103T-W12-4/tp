package seedu.address.model.comparison.exceptions;

/**
 * Signals that an applicant is already a member of the active comparison list.
 */
public class DuplicateComparisonMemberException extends RuntimeException {
    public DuplicateComparisonMemberException() {
        super("Applicant is already in the comparison list");
    }
}
