package seedu.address.model.applicant;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents an Applicant in RecruitDex.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Applicant {

    // Identity fields
    private final ApplicantId id;
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final InterviewNotes interviewNotes;
    private final YearsOfExperience yearsOfExperience;
    private final Source source;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates an applicant with a new {@code ApplicantId} and empty optional details.
     * Every field must be present and not null.
     */
    public Applicant(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(new ApplicantId(), name, phone, email, address,
                new InterviewNotes(InterviewNotes.DEFAULT_VALUE),
                new YearsOfExperience(YearsOfExperience.DEFAULT_VALUE),
                new Source(Source.DEFAULT_VALUE), tags);
    }

    /**
     * Every field must be present and not null.
     */
    public Applicant(ApplicantId id, Name name, Phone phone, Email email, Address address,
            InterviewNotes interviewNotes, YearsOfExperience yearsOfExperience, Source source, Set<Tag> tags) {
        requireAllNonNull(id, name, phone, email, address, interviewNotes, yearsOfExperience, source, tags);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.interviewNotes = interviewNotes;
        this.yearsOfExperience = yearsOfExperience;
        this.source = source;
        this.tags.addAll(tags);
    }

    public ApplicantId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public InterviewNotes getInterviewNotes() {
        return interviewNotes;
    }

    public YearsOfExperience getYearsOfExperience() {
        return yearsOfExperience;
    }

    public Source getSource() {
        return source;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both applicants have the same name.
     * This defines a weaker notion of equality between two applicants.
     */
    public boolean isSameApplicant(Applicant otherApplicant) {
        if (otherApplicant == this) {
            return true;
        }

        return otherApplicant != null
                && otherApplicant.getName().equals(getName());
    }

    /**
     * Returns true if both applicants have the same data fields.
     * This defines a stronger notion of equality between two applicants.
     * The {@code ApplicantId} is an internal identifier rather than applicant data, so it is not compared.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Applicant otherApplicant)) {
            return false;
        }

        return name.equals(otherApplicant.name)
                && phone.equals(otherApplicant.phone)
                && email.equals(otherApplicant.email)
                && address.equals(otherApplicant.address)
                && interviewNotes.equals(otherApplicant.interviewNotes)
                && yearsOfExperience.equals(otherApplicant.yearsOfExperience)
                && source.equals(otherApplicant.source)
                && tags.equals(otherApplicant.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, interviewNotes, yearsOfExperience, source, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("interviewNotes", interviewNotes)
                .add("yearsOfExperience", yearsOfExperience)
                .add("source", source)
                .add("tags", tags)
                .toString();
    }

}
