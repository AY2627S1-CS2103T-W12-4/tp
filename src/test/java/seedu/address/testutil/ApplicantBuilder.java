package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

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
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Applicant objects.
 */
public class ApplicantBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private ApplicantId id;
    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private InterviewNotes interviewNotes;
    private YearsOfExperience yearsOfExperience;
    private Source source;
    private Set<Tag> tags;

    /**
     * Creates a {@code ApplicantBuilder} with the default details.
     */
    public ApplicantBuilder() {
        id = new ApplicantId();
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        interviewNotes = new InterviewNotes(InterviewNotes.DEFAULT_VALUE);
        yearsOfExperience = new YearsOfExperience(YearsOfExperience.DEFAULT_VALUE);
        source = new Source(Source.DEFAULT_VALUE);
        tags = new HashSet<>();
    }

    /**
     * Initializes the ApplicantBuilder with the data of {@code applicantToCopy}.
     */
    public ApplicantBuilder(Applicant applicantToCopy) {
        id = applicantToCopy.getId();
        name = applicantToCopy.getName();
        phone = applicantToCopy.getPhone();
        email = applicantToCopy.getEmail();
        address = applicantToCopy.getAddress();
        interviewNotes = applicantToCopy.getInterviewNotes();
        yearsOfExperience = applicantToCopy.getYearsOfExperience();
        source = applicantToCopy.getSource();
        tags = new HashSet<>(applicantToCopy.getTags());
    }

    /**
     * Sets the {@code ApplicantId} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withId(String id) {
        this.id = new ApplicantId(id);
        return this;
    }

    /**
     * Sets the {@code InterviewNotes} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withInterviewNotes(String interviewNotes) {
        this.interviewNotes = new InterviewNotes(interviewNotes);
        return this;
    }

    /**
     * Sets the {@code YearsOfExperience} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withYearsOfExperience(String yearsOfExperience) {
        this.yearsOfExperience = new YearsOfExperience(yearsOfExperience);
        return this;
    }

    /**
     * Sets the {@code Source} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withSource(String source) {
        this.source = new Source(source);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Applicant} that we are building.
     */
    public ApplicantBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    public Applicant build() {
        return new Applicant(id, name, phone, email, address, interviewNotes, yearsOfExperience, source, tags);
    }

}
