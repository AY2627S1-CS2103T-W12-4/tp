package seedu.recruitdex.model.applicant;

import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.recruitdex.commons.util.ToStringBuilder;

/**
 * Tests that an applicant matches every given search condition.
 *
 * <p>Text terms match any part of the attribute, ignoring case, so {@code example.com} matches the email
 * {@code alice@example.com}. Years of experience must match exactly. Each skill term must match part of at
 * least one skill name. A condition that is not given matches every applicant.</p>
 */
public class ApplicantMatchesSearchPredicate implements Predicate<Applicant> {
    private final Optional<String> nameTerm;
    private final Optional<String> phoneTerm;
    private final Optional<String> emailTerm;
    private final Optional<String> addressTerm;
    private final Optional<String> interviewNotesTerm;
    private final Optional<YearsOfExperience> yearsOfExperience;
    private final Optional<String> sourceTerm;
    private final List<String> skillTerms;

    /**
     * Creates a predicate that checks every given condition.
     *
     * @param skillTerms The skill search terms, or an empty list to match any skills.
     */
    public ApplicantMatchesSearchPredicate(Optional<String> nameTerm, Optional<String> phoneTerm,
            Optional<String> emailTerm, Optional<String> addressTerm, Optional<String> interviewNotesTerm,
            Optional<YearsOfExperience> yearsOfExperience, Optional<String> sourceTerm, List<String> skillTerms) {
        requireAllNonNull(nameTerm, phoneTerm, emailTerm, addressTerm, interviewNotesTerm, yearsOfExperience,
                sourceTerm, skillTerms);
        this.nameTerm = nameTerm;
        this.phoneTerm = phoneTerm;
        this.emailTerm = emailTerm;
        this.addressTerm = addressTerm;
        this.interviewNotesTerm = interviewNotesTerm;
        this.yearsOfExperience = yearsOfExperience;
        this.sourceTerm = sourceTerm;
        this.skillTerms = List.copyOf(skillTerms);
    }

    @Override
    public boolean test(Applicant applicant) {
        return containsTerm(applicant.getName().fullName, nameTerm)
                && containsTerm(applicant.getPhone().value, phoneTerm)
                && containsTerm(applicant.getEmail().value, emailTerm)
                && containsTerm(applicant.getAddress().value, addressTerm)
                && containsTerm(applicant.getInterviewNotes().value, interviewNotesTerm)
                && yearsOfExperience.map(applicant.getYearsOfExperience()::equals).orElse(true)
                && containsTerm(applicant.getSource().value, sourceTerm)
                && skillTerms.stream().allMatch(term -> applicant.getTags().stream()
                        .anyMatch(tag -> containsTerm(tag.tagName, Optional.of(term))));
    }

    /**
     * Returns true if {@code term} is absent or is part of {@code value}, ignoring case.
     */
    private static boolean containsTerm(String value, Optional<String> term) {
        return term.map(t -> value.toLowerCase().contains(t.toLowerCase())).orElse(true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ApplicantMatchesSearchPredicate otherPredicate)) {
            return false;
        }

        return nameTerm.equals(otherPredicate.nameTerm)
                && phoneTerm.equals(otherPredicate.phoneTerm)
                && emailTerm.equals(otherPredicate.emailTerm)
                && addressTerm.equals(otherPredicate.addressTerm)
                && interviewNotesTerm.equals(otherPredicate.interviewNotesTerm)
                && yearsOfExperience.equals(otherPredicate.yearsOfExperience)
                && sourceTerm.equals(otherPredicate.sourceTerm)
                && skillTerms.equals(otherPredicate.skillTerms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTerm, phoneTerm, emailTerm, addressTerm, interviewNotesTerm, yearsOfExperience,
                sourceTerm, skillTerms);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", nameTerm)
                .add("phone", phoneTerm)
                .add("email", emailTerm)
                .add("address", addressTerm)
                .add("interviewNotes", interviewNotesTerm)
                .add("yearsOfExperience", yearsOfExperience)
                .add("source", sourceTerm)
                .add("skills", skillTerms)
                .toString();
    }
}
