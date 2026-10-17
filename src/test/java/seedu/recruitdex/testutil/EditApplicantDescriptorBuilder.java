package seedu.recruitdex.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.recruitdex.logic.commands.EditCommand.EditApplicantDescriptor;
import seedu.recruitdex.model.applicant.Address;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.Email;
import seedu.recruitdex.model.applicant.InterviewNotes;
import seedu.recruitdex.model.applicant.Name;
import seedu.recruitdex.model.applicant.Phone;
import seedu.recruitdex.model.applicant.Source;
import seedu.recruitdex.model.applicant.YearsOfExperience;
import seedu.recruitdex.model.tag.Tag;

/**
 * A utility class to help with building EditApplicantDescriptor objects.
 */
public class EditApplicantDescriptorBuilder {

    private EditApplicantDescriptor descriptor;

    public EditApplicantDescriptorBuilder() {
        descriptor = new EditApplicantDescriptor();
    }

    public EditApplicantDescriptorBuilder(EditApplicantDescriptor descriptor) {
        this.descriptor = new EditApplicantDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditApplicantDescriptor} with fields containing {@code applicant}'s details
     */
    public EditApplicantDescriptorBuilder(Applicant applicant) {
        descriptor = new EditApplicantDescriptor();
        descriptor.setName(applicant.getName());
        descriptor.setPhone(applicant.getPhone());
        descriptor.setEmail(applicant.getEmail());
        descriptor.setAddress(applicant.getAddress());
        descriptor.setInterviewNotes(applicant.getInterviewNotes());
        descriptor.setYearsOfExperience(applicant.getYearsOfExperience());
        descriptor.setSource(applicant.getSource());
        descriptor.setTags(applicant.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditApplicantDescriptor} that we are building.
     */
    public EditApplicantDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditApplicantDescriptor} that we are building.
     */
    public EditApplicantDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditApplicantDescriptor} that we are building.
     */
    public EditApplicantDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditApplicantDescriptor} that we are building.
     */
    public EditApplicantDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Sets the {@code InterviewNotes} of the {@code EditApplicantDescriptor} that we are building.
     */
    public EditApplicantDescriptorBuilder withInterviewNotes(String interviewNotes) {
        descriptor.setInterviewNotes(new InterviewNotes(interviewNotes));
        return this;
    }

    /**
     * Sets the {@code YearsOfExperience} of the {@code EditApplicantDescriptor} that we are building.
     */
    public EditApplicantDescriptorBuilder withYearsOfExperience(String yearsOfExperience) {
        descriptor.setYearsOfExperience(new YearsOfExperience(yearsOfExperience));
        return this;
    }

    /**
     * Sets the {@code Source} of the {@code EditApplicantDescriptor} that we are building.
     */
    public EditApplicantDescriptorBuilder withSource(String source) {
        descriptor.setSource(new Source(source));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code EditApplicantDescriptor}
     * that we are building.
     */
    public EditApplicantDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    public EditApplicantDescriptor build() {
        return descriptor;
    }
}
