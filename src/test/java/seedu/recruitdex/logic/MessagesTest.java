package seedu.recruitdex.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.testutil.ApplicantBuilder;

public class MessagesTest {

    @Test
    public void format_allFields_showsEveryField() {
        Applicant applicant = new ApplicantBuilder().withName("Alex Yeoh").withPhone("87438807")
                .withEmail("alexyeoh@example.com").withAddress("Blk 30 Geylang Street 29")
                .withYearsOfExperience("6").withSource("LinkedIn").withInterviewNotes("Strong in Java")
                .withTags("Java").build();

        assertEquals("Alex Yeoh; Phone: 87438807; Email: alexyeoh@example.com; Address: Blk 30 Geylang Street 29;"
                + " Experience: 6 years; Source: LinkedIn; Notes: Strong in Java; Skills: [Java]",
                Messages.format(applicant));
    }

    @Test
    public void format_nameOnly_omitsEmptyFields() {
        Applicant applicant = new ApplicantBuilder().withName("Alex Yeoh").withPhone("").withEmail("")
                .withAddress("").build();

        assertEquals("Alex Yeoh", Messages.format(applicant));
    }

    @Test
    public void format_someOptionalFields_showsOnlyThoseFields() {
        Applicant applicant = new ApplicantBuilder().withName("Alex Yeoh").withPhone("").withEmail("")
                .withAddress("").withYearsOfExperience("0").withInterviewNotes("Good fit").build();

        assertEquals("Alex Yeoh; Experience: 0 years; Notes: Good fit", Messages.format(applicant));
    }

    @Test
    public void format_skills_usesSkillsLabelNotTags() {
        Applicant applicant = new ApplicantBuilder().withTags("Kafka").build();

        String formatted = Messages.format(applicant);

        assertTrue(formatted.endsWith("; Skills: [Kafka]"));
        assertFalse(formatted.contains("Tags"));
    }
}
