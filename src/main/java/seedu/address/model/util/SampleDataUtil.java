package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.RecruitDexData;
import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.applicant.Address;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.InterviewNotes;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;
import seedu.address.model.applicant.Source;
import seedu.address.model.applicant.YearsOfExperience;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code RecruitDexData} with sample data.
 */
public class SampleDataUtil {
    public static Applicant[] getSampleApplicants() {
        return new Applicant[] {
            new Applicant(new ApplicantId(), new Name("Alex Yeoh"), new Phone("87438807"),
                new Email("alexyeoh@example.com"), new Address("Blk 30 Geylang Street 29, #06-40"),
                new InterviewNotes("Strong system design; solid on concurrency. Weaker on testing."),
                new YearsOfExperience("6"), new Source("LinkedIn"), getTagSet("Java", "Kafka")),
            new Applicant(new ApplicantId(), new Name("Bernice Yu"), new Phone("99272758"),
                new Email("berniceyu@example.com"), new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                new InterviewNotes("Great communicator; strong UI instincts."),
                new YearsOfExperience("4"), new Source("Referral"), getTagSet("React", "TypeScript")),
            new Applicant(new ApplicantId(), new Name("Charlotte Oliveiro"), new Phone("93210283"),
                new Email("charlotte@example.com"), new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                new InterviewNotes("Excellent coding round; good testing habits."),
                new YearsOfExperience("3"), new Source("Company website"), getTagSet("Java", "Docker")),
            new Applicant(new ApplicantId(), new Name("David Li"), new Phone("91031282"),
                new Email("lidavid@example.com"), new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                new InterviewNotes(""), new YearsOfExperience("1"), new Source("Career fair"),
                getTagSet("Python")),
            new Applicant(new ApplicantId(), new Name("Irfan Ibrahim"), new Phone("92492021"),
                new Email("irfan@example.com"), new Address("Blk 47 Tampines Street 20, #17-35"),
                new InterviewNotes(""), new YearsOfExperience("0"), new Source("LinkedIn"),
                getTagSet("Rust")),
            new Applicant(new ApplicantId(), new Name("Roy Balakrishnan"), new Phone("92624417"),
                new Email("royb@example.com"), new Address("Blk 45 Aljunied Street 85, #11-31"),
                new InterviewNotes("Senior profile; expects a lead role."),
                new YearsOfExperience("10"), new Source("Referral"), getTagSet("Go", "AWS"))
        };
    }

    public static ReadOnlyRecruitDexData getSampleRecruitDexData() {
        RecruitDexData sampleAb = new RecruitDexData();
        for (Applicant sampleApplicant : getSampleApplicants()) {
            sampleAb.addApplicant(sampleApplicant);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
