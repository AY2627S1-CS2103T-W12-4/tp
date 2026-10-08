package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_NOTES_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_NOTES_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SOURCE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SOURCE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_YEARS_OF_EXPERIENCE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_YEARS_OF_EXPERIENCE_BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditApplicantDescriptor;
import seedu.address.testutil.EditApplicantDescriptorBuilder;

public class EditApplicantDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditApplicantDescriptor descriptorWithSameValues = new EditApplicantDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditApplicantDescriptor editedAmy =
                new EditApplicantDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditApplicantDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditApplicantDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditApplicantDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditApplicantDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different interview notes -> returns false
        editedAmy = new EditApplicantDescriptorBuilder(DESC_AMY).withInterviewNotes(VALID_INTERVIEW_NOTES_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different years of experience -> returns false
        editedAmy = new EditApplicantDescriptorBuilder(DESC_AMY)
                .withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different source -> returns false
        editedAmy = new EditApplicantDescriptorBuilder(DESC_AMY).withSource(VALID_SOURCE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditApplicantDescriptor editApplicantDescriptor = new EditApplicantDescriptor();
        String expected = EditApplicantDescriptor.class.getCanonicalName() + "{name="
                + editApplicantDescriptor.getName().orElse(null) + ", phone="
                + editApplicantDescriptor.getPhone().orElse(null) + ", email="
                + editApplicantDescriptor.getEmail().orElse(null) + ", address="
                + editApplicantDescriptor.getAddress().orElse(null) + ", interviewNotes="
                + editApplicantDescriptor.getInterviewNotes().orElse(null) + ", yearsOfExperience="
                + editApplicantDescriptor.getYearsOfExperience().orElse(null) + ", source="
                + editApplicantDescriptor.getSource().orElse(null) + ", tags="
                + editApplicantDescriptor.getTags().orElse(null) + "}";
        assertEquals(expected, editApplicantDescriptor.toString());
    }

    @Test
    public void isAnyFieldEdited() {
        // no fields -> false
        assertFalse(new EditApplicantDescriptor().isAnyFieldEdited());

        // each new field on its own -> true
        assertTrue(new EditApplicantDescriptorBuilder().withInterviewNotes("").build().isAnyFieldEdited());
        assertTrue(new EditApplicantDescriptorBuilder().withYearsOfExperience("").build().isAnyFieldEdited());
        assertTrue(new EditApplicantDescriptorBuilder().withSource("").build().isAnyFieldEdited());
    }

    @Test
    public void copyConstructor_copiesNewFields() {
        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder()
                .withInterviewNotes(VALID_INTERVIEW_NOTES_AMY).withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_AMY)
                .withSource(VALID_SOURCE_AMY).build();
        EditApplicantDescriptor copy = new EditApplicantDescriptor(descriptor);
        assertEquals(descriptor.getInterviewNotes(), copy.getInterviewNotes());
        assertEquals(descriptor.getYearsOfExperience(), copy.getYearsOfExperience());
        assertEquals(descriptor.getSource(), copy.getSource());
    }
}
