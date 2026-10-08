package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_NOTES_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SOURCE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_YEARS_OF_EXPERIENCE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ApplicantBuilder;

public class ApplicantTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Applicant applicant = new ApplicantBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> applicant.getTags().remove(0));
    }

    @Test
    public void isSameApplicant() {
        // same object -> returns true
        assertTrue(ALICE.isSameApplicant(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameApplicant(null));

        // same name, all other attributes different -> returns true
        Applicant editedAlice = new ApplicantBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameApplicant(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameApplicant(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Applicant editedBob = new ApplicantBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSameApplicant(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new ApplicantBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSameApplicant(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Applicant aliceCopy = new ApplicantBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different applicant -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Applicant editedAlice = new ApplicantBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different interview notes -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withInterviewNotes(VALID_INTERVIEW_NOTES_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different years of experience -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different source -> returns false
        editedAlice = new ApplicantBuilder(ALICE).withSource(VALID_SOURCE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Applicant.class.getCanonicalName() + "{id=" + ALICE.getId() + ", name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress()
                + ", interviewNotes=" + ALICE.getInterviewNotes()
                + ", yearsOfExperience=" + ALICE.getYearsOfExperience()
                + ", source=" + ALICE.getSource() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }

    /**
     * Returns an applicant with ALICE's details, except for the given id and optional fields.
     */
    private static Applicant aliceWith(ApplicantId id, InterviewNotes interviewNotes,
            YearsOfExperience yearsOfExperience, Source source) {
        return new Applicant(id, ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                interviewNotes, yearsOfExperience, source, ALICE.getTags());
    }

    @Test
    public void constructor_nullNewField_throwsNullPointerException() {
        InterviewNotes notes = ALICE.getInterviewNotes();
        YearsOfExperience years = ALICE.getYearsOfExperience();
        Source source = ALICE.getSource();

        assertThrows(NullPointerException.class, () -> aliceWith(null, notes, years, source));
        assertThrows(NullPointerException.class, () -> aliceWith(ALICE.getId(), null, years, source));
        assertThrows(NullPointerException.class, () -> aliceWith(ALICE.getId(), notes, null, source));
        assertThrows(NullPointerException.class, () -> aliceWith(ALICE.getId(), notes, years, null));
    }

    @Test
    public void constructor_withoutNewFields_usesEmptyDefaults() {
        Applicant applicant = new Applicant(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags());
        assertEquals("", applicant.getInterviewNotes().value);
        assertEquals("", applicant.getYearsOfExperience().value);
        assertEquals("", applicant.getSource().value);
    }

    @Test
    public void constructor_withoutId_generatesDifferentIdEachTime() {
        Applicant first = new Applicant(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags());
        Applicant second = new Applicant(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags());
        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    public void getters_returnConstructorValues() {
        ApplicantId id = new ApplicantId();
        Applicant applicant = aliceWith(id, new InterviewNotes("notes"), new YearsOfExperience("3"),
                new Source("Referral"));
        assertEquals(id, applicant.getId());
        assertEquals(new InterviewNotes("notes"), applicant.getInterviewNotes());
        assertEquals(new YearsOfExperience("3"), applicant.getYearsOfExperience());
        assertEquals(new Source("Referral"), applicant.getSource());
    }

    @Test
    public void equalsAndHashCode_differentIdOnly_treatedAsEqual() {
        // the id is an internal identifier, so it is not part of the applicant's data
        Applicant sameDataDifferentId = new ApplicantBuilder(ALICE).withId("123e4567-e89b-12d3-a456-426614174999")
                .build();
        assertNotEquals(ALICE.getId(), sameDataDifferentId.getId());
        assertEquals(ALICE, sameDataDifferentId);
        assertEquals(ALICE.hashCode(), sameDataDifferentId.hashCode());
    }

    @Test
    public void copyViaBuilder_keepsId() {
        assertEquals(ALICE.getId(), new ApplicantBuilder(ALICE).build().getId());
    }

    @Test
    public void isSameApplicant_differentOptionalFields_returnsTrue() {
        Applicant editedAlice = new ApplicantBuilder(ALICE).withInterviewNotes(VALID_INTERVIEW_NOTES_BOB)
                .withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_BOB).withSource(VALID_SOURCE_BOB).build();
        assertTrue(ALICE.isSameApplicant(editedAlice));
    }
}
