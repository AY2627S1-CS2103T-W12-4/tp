package seedu.recruitdex.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.testutil.ApplicantBuilder;

public class ApplicantMatchesSearchPredicateTest {
    private static final Applicant APPLICANT = new ApplicantBuilder().withName("Alice Pauline")
            .withPhone("94351253").withEmail("alice@example.com").withAddress("123, Jurong West Ave 6")
            .withInterviewNotes("Strong in Java").withYearsOfExperience("5").withSource("LinkedIn")
            .withTags("Java", "Kafka").build();

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ApplicantMatchesSearchPredicate(null, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                List.of()));
        assertThrows(NullPointerException.class, () -> new ApplicantMatchesSearchPredicate(Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), null));
    }

    @Test
    public void test_noConditions_returnsTrue() {
        assertTrue(skills().test(APPLICANT));
    }

    @Test
    public void test_textTerms_matchPartsIgnoringCase() {
        assertTrue(name("PAUL").test(APPLICANT));
        assertTrue(phone("12").test(APPLICANT));
        assertTrue(email("example.com").test(APPLICANT));
        assertTrue(email("@EXAMPLE").test(APPLICANT));
        assertTrue(email("alice").test(APPLICANT));

        assertFalse(name("Bob").test(APPLICANT));
        assertFalse(phone("99").test(APPLICANT));
        assertFalse(email("gmail").test(APPLICANT));
    }

    @Test
    public void test_yearsOfExperience_matchesExactly() {
        assertTrue(yearsOfExperience("5").test(APPLICANT));
        assertFalse(yearsOfExperience("50").test(APPLICANT));
        assertFalse(yearsOfExperience("0").test(APPLICANT));
    }

    @Test
    public void test_skillTerms_eachMatchPartOfASkillName() {
        assertTrue(skills("java").test(APPLICANT));
        assertTrue(skills("jav", "KAF").test(APPLICANT));

        assertFalse(skills("java", "python").test(APPLICANT));
        assertFalse(skills("]").test(APPLICANT));
        assertFalse(skills("[java]").test(APPLICANT));
    }

    @Test
    public void test_allConditions_requiresEveryConditionToMatch() {
        ApplicantMatchesSearchPredicate matching = new ApplicantMatchesSearchPredicate(Optional.of("alice"),
                Optional.of("9435"), Optional.of("example"), Optional.of("jurong"), Optional.of("java"),
                Optional.of(new YearsOfExperience("5")), Optional.of("linked"), List.of("kaf"));
        ApplicantMatchesSearchPredicate wrongSource = new ApplicantMatchesSearchPredicate(Optional.of("alice"),
                Optional.of("9435"), Optional.of("example"), Optional.of("jurong"), Optional.of("java"),
                Optional.of(new YearsOfExperience("5")), Optional.of("referral"), List.of("kaf"));

        assertTrue(matching.test(APPLICANT));
        assertFalse(wrongSource.test(APPLICANT));
    }

    @Test
    public void equals() {
        ApplicantMatchesSearchPredicate predicate = email("example");

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(email("example")));
        assertEquals(predicate.hashCode(), email("example").hashCode());

        assertFalse(predicate.equals(1));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(email("gmail")));
        assertFalse(predicate.equals(name("example")));
        assertFalse(skills("java").equals(skills("java", "kafka")));
    }

    @Test
    public void toStringMethod() {
        String expected = ApplicantMatchesSearchPredicate.class.getCanonicalName()
                + "{name=Optional.empty, phone=Optional.empty, email=Optional[example], address=Optional.empty, "
                + "interviewNotes=Optional.empty, yearsOfExperience=Optional.empty, source=Optional.empty, "
                + "skills=[]}";
        assertEquals(expected, email("example").toString());
    }

    private static ApplicantMatchesSearchPredicate name(String term) {
        return new ApplicantMatchesSearchPredicate(Optional.of(term), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), List.of());
    }

    private static ApplicantMatchesSearchPredicate phone(String term) {
        return new ApplicantMatchesSearchPredicate(Optional.empty(), Optional.of(term), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), List.of());
    }

    private static ApplicantMatchesSearchPredicate email(String term) {
        return new ApplicantMatchesSearchPredicate(Optional.empty(), Optional.empty(), Optional.of(term),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), List.of());
    }

    private static ApplicantMatchesSearchPredicate yearsOfExperience(String years) {
        return new ApplicantMatchesSearchPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(new YearsOfExperience(years)), Optional.empty(),
                List.of());
    }

    private static ApplicantMatchesSearchPredicate skills(String... terms) {
        return new ApplicantMatchesSearchPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), List.of(terms));
    }
}
