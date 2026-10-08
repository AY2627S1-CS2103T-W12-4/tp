package seedu.address.model.applicant;

import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;

/**
 * Represents an ordered pair of distinct applicant IDs for comparison.
 */
public class ComparisonPair {
    private final ApplicantId firstApplicant;
    private final ApplicantId secondApplicant;

    /**
     * Creates a comparison pair from two distinct applicant IDs.
     *
     * <p>Both IDs must be valid according to
     * {@link ApplicantId#isValidApplicantId(String)}. If either ID is invalid,
     * this method throws {@link ApplicantNotFoundException}. If the IDs are
     * equal, this method throws {@link DuplicateApplicantException}.
     *
     * @param firstApplicant the ID of the first applicant in the pair
     * @param secondApplicant the ID of the second applicant in the pair
     * @return a comparison pair containing the two applicant IDs
     * @throws ApplicantNotFoundException if either applicant ID is invalid
     * @throws DuplicateApplicantException if both IDs identify the same applicant
     */
    public static ComparisonPair init(ApplicantId firstApplicant, ApplicantId secondApplicant)
            throws ApplicantNotFoundException, DuplicateApplicantException {
        if (!(ApplicantId.isValidApplicantId(firstApplicant.value)
                && ApplicantId.isValidApplicantId(secondApplicant.value))) {
            throw new ApplicantNotFoundException();
        } else if (firstApplicant.equals(secondApplicant)) {
            throw new DuplicateApplicantException();
        }
        return new ComparisonPair(firstApplicant, secondApplicant);
    }

    /**
     * Constructs a comparison pair with the specified applicant IDs.
     *
     * @param firstApplicant the ID of the first applicant
     * @param secondApplicant the ID of the second applicant
     */
    private ComparisonPair(ApplicantId firstApplicant, ApplicantId secondApplicant) {
        this.firstApplicant = firstApplicant;
        this.secondApplicant = secondApplicant;
    }

    /**
     * Returns the ID of the first applicant in this pair.
     *
     * @return the first applicant's ID
     */
    public ApplicantId getFirstApplicant() {
        return firstApplicant;
    }

    /**
     * Returns the ID of the second applicant in this pair.
     *
     * @return the second applicant's ID
     */
    public ApplicantId getSecondApplicant() {
        return secondApplicant;
    }
}
