package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
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
 * Jackson-friendly version of {@link Applicant}.
 */
class JsonAdaptedApplicant {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Applicant's %s field is missing!";

    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String interviewNotes;
    private final String yearsOfExperience;
    private final String source;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedApplicant} with the given applicant details.
     */
    @JsonCreator
    public JsonAdaptedApplicant(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("email") String email,
            @JsonProperty("address") String address, @JsonProperty("interviewNotes") String interviewNotes,
            @JsonProperty("yearsOfExperience") String yearsOfExperience, @JsonProperty("source") String source,
            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.interviewNotes = interviewNotes;
        this.yearsOfExperience = yearsOfExperience;
        this.source = source;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Applicant} into this class for Jackson use.
     */
    public JsonAdaptedApplicant(Applicant applicant) {
        id = applicant.getId().value;
        name = applicant.getName().fullName;
        phone = applicant.getPhone().value;
        email = applicant.getEmail().value;
        address = applicant.getAddress().value;
        interviewNotes = applicant.getInterviewNotes().value;
        yearsOfExperience = applicant.getYearsOfExperience().value;
        source = applicant.getSource().value;
        tags.addAll(applicant.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted applicant object into the model's {@code Applicant} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted applicant.
     */
    public Applicant toModelType() throws IllegalValueException {
        final List<Tag> applicantTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            applicantTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        // optional fields: files written before these fields existed simply omit them
        final ApplicantId modelId = toModelId();

        final InterviewNotes modelInterviewNotes =
                new InterviewNotes(interviewNotes == null ? InterviewNotes.DEFAULT_VALUE : interviewNotes);

        final String yearsOrDefault = yearsOfExperience == null ? YearsOfExperience.DEFAULT_VALUE : yearsOfExperience;
        if (!YearsOfExperience.isValidYearsOfExperience(yearsOrDefault)) {
            throw new IllegalValueException(YearsOfExperience.MESSAGE_CONSTRAINTS);
        }
        final YearsOfExperience modelYearsOfExperience = new YearsOfExperience(yearsOrDefault);

        final Source modelSource = new Source(this.source == null ? Source.DEFAULT_VALUE : this.source);

        final Set<Tag> modelTags = new HashSet<>(applicantTags);
        return new Applicant(modelId, modelName, modelPhone, modelEmail, modelAddress, modelInterviewNotes,
                modelYearsOfExperience, modelSource, modelTags);
    }

    /**
     * Returns the stored {@code ApplicantId}, or a new one if the file did not store an id.
     */
    private ApplicantId toModelId() throws IllegalValueException {
        if (id == null) {
            return new ApplicantId();
        }
        if (!ApplicantId.isValidApplicantId(id)) {
            throw new IllegalValueException(ApplicantId.MESSAGE_CONSTRAINTS);
        }
        return new ApplicantId(id);
    }

}
