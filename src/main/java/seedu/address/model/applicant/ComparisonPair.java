package seedu.address.model.applicant;

import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;

public class ComparisonPair {
    ApplicantId firstApplicant;
    ApplicantId secondApplicant;

    public static ComparisonPair init(ApplicantId firstApplicant, ApplicantId secondApplicant)
            throws ApplicantNotFoundException{
        if (!(ApplicantId.isValidApplicantId(firstApplicant.value) &&
                ApplicantId.isValidApplicantId(secondApplicant.value))) {
            throw new ApplicantNotFoundException();
        }
        return new ComparisonPair(firstApplicant, secondApplicant);
    }

    private ComparisonPair(ApplicantId firstApplicant, ApplicantId secondApplicant) {
        this.firstApplicant = firstApplicant;
        this.secondApplicant = secondApplicant;
    }

    public ApplicantId getFirstApplicant() {
        return firstApplicant;
    }

    public ApplicantId getSecondApplicant() {
        return secondApplicant;
    }
}
