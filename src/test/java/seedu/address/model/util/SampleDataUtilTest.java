package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    private static final Applicant[] SAMPLE_APPLICANTS = SampleDataUtil.getSampleApplicants();

    @Test
    public void getSampleApplicants_returnsApplicants() {
        // building the applicants validates every field, so a bad sample value fails here
        assertTrue(SAMPLE_APPLICANTS.length > 0);
    }

    @Test
    public void getSampleApplicants_namesAreUnique() {
        Set<String> names = Arrays.stream(SAMPLE_APPLICANTS)
                .map(applicant -> applicant.getName().fullName)
                .collect(Collectors.toSet());
        assertEquals(SAMPLE_APPLICANTS.length, names.size());
    }

    @Test
    public void getSampleApplicants_idsAreUnique() {
        Set<String> ids = Arrays.stream(SAMPLE_APPLICANTS)
                .map(applicant -> applicant.getId().value)
                .collect(Collectors.toSet());
        assertEquals(SAMPLE_APPLICANTS.length, ids.size());
    }

    @Test
    public void getSampleApplicants_tagsAreValid() {
        for (Applicant applicant : SAMPLE_APPLICANTS) {
            for (Tag tag : applicant.getTags()) {
                assertTrue(Tag.isValidTagName(tag.tagName), "invalid sample tag: " + tag.tagName);
            }
        }
    }

    @Test
    public void getSampleApplicants_demonstratesEachOptionalField() {
        assertTrue(Arrays.stream(SAMPLE_APPLICANTS)
                .anyMatch(applicant -> !applicant.getInterviewNotes().value.isEmpty()));
        assertTrue(Arrays.stream(SAMPLE_APPLICANTS)
                .anyMatch(applicant -> !applicant.getYearsOfExperience().value.isEmpty()));
        assertTrue(Arrays.stream(SAMPLE_APPLICANTS)
                .anyMatch(applicant -> !applicant.getSource().value.isEmpty()));
    }

    @Test
    public void getSampleApplicants_includesApplicantWithoutInterviewNotes() {
        // the sample data should also show what an applicant with empty optional fields looks like
        assertTrue(Arrays.stream(SAMPLE_APPLICANTS)
                .anyMatch(applicant -> applicant.getInterviewNotes().value.isEmpty()));
    }

    @Test
    public void getSampleRecruitDexData_containsAllSampleApplicants() {
        ReadOnlyRecruitDexData sampleRecruitDexData = SampleDataUtil.getSampleRecruitDexData();
        assertEquals(SAMPLE_APPLICANTS.length, sampleRecruitDexData.getApplicantList().size());
        for (Applicant applicant : SAMPLE_APPLICANTS) {
            assertTrue(sampleRecruitDexData.getApplicantList().contains(applicant));
        }
    }

    @Test
    public void getSampleRecruitDexData_calledTwice_returnsEqualButSeparateBooks() {
        ReadOnlyRecruitDexData first = SampleDataUtil.getSampleRecruitDexData();
        ReadOnlyRecruitDexData second = SampleDataUtil.getSampleRecruitDexData();
        assertEquals(first, second);
        assertNotSame(first, second);
    }

    @Test
    public void getTagSet_noStrings_returnsEmptySet() {
        assertTrue(SampleDataUtil.getTagSet().isEmpty());
    }

    @Test
    public void getTagSet_duplicateStrings_returnsEachTagOnce() {
        Set<Tag> expected = new HashSet<>(Arrays.asList(new Tag("Java"), new Tag("Kafka")));
        assertEquals(expected, SampleDataUtil.getTagSet("Java", "Kafka", "Java"));
    }
}
