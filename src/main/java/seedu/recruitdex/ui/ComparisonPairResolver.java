package seedu.recruitdex.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.comparison.ComparisonPair;

/**
 * Looks up the applicants of a {@link ComparisonPair} by ID, so the comparison view always shows the current
 * record of each applicant, including edits made after the pair was chosen.
 */
final class ComparisonPairResolver {

    private ComparisonPairResolver() {}

    /**
     * Returns the two applicants of {@code pair}, first side then second side.
     * A side is empty when there is no pair, or when no record has that ID.
     */
    static List<Optional<Applicant>> resolve(Optional<ComparisonPair> pair, List<Applicant> applicants) {
        requireNonNull(pair);
        requireNonNull(applicants);
        if (pair.isEmpty()) {
            return List.of(Optional.empty(), Optional.empty());
        }
        return List.of(find(pair.get().getFirstApplicantId(), applicants),
                find(pair.get().getSecondApplicantId(), applicants));
    }

    private static Optional<Applicant> find(ApplicantId applicantId, List<Applicant> applicants) {
        return applicants.stream().filter(applicant -> applicant.getId().equals(applicantId)).findFirst();
    }
}
