package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTERVIEW_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SOURCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEARS_OF_EXPERIENCE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_APPLICANTS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.applicant.Address;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.InterviewNotes;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;
import seedu.address.model.applicant.Source;
import seedu.address.model.applicant.YearsOfExperience;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing applicant in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the applicant identified "
            + "by the index number used in the displayed applicant list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_INTERVIEW_NOTES + "INTERVIEW_NOTES] "
            + "[" + PREFIX_YEARS_OF_EXPERIENCE + "YEARS_OF_EXPERIENCE] "
            + "[" + PREFIX_SOURCE + "SOURCE] "
            + "[" + PREFIX_TAG + "SKILL]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_APPLICANT_SUCCESS = "Edited applicant: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_APPLICANT = "This applicant already exists in the address book.";

    private final Index index;
    private final EditApplicantDescriptor editApplicantDescriptor;

    /**
     * @param index of the applicant in the filtered applicant list to edit
     * @param editApplicantDescriptor details to edit the applicant with
     */
    public EditCommand(Index index, EditApplicantDescriptor editApplicantDescriptor) {
        requireNonNull(index);
        requireNonNull(editApplicantDescriptor);

        this.index = index;
        this.editApplicantDescriptor = new EditApplicantDescriptor(editApplicantDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Applicant> lastShownList = model.getFilteredApplicantList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
        }

        Applicant applicantToEdit = lastShownList.get(index.getZeroBased());
        Applicant editedApplicant = createEditedApplicant(applicantToEdit, editApplicantDescriptor);

        if (!applicantToEdit.isSameApplicant(editedApplicant) && model.hasApplicant(editedApplicant)) {
            throw new CommandException(MESSAGE_DUPLICATE_APPLICANT);
        }

        model.setApplicant(applicantToEdit, editedApplicant);
        model.updateFilteredApplicantList(PREDICATE_SHOW_ALL_APPLICANTS);
        return new CommandResult(String.format(MESSAGE_EDIT_APPLICANT_SUCCESS, Messages.format(editedApplicant)));
    }

    /**
     * Creates and returns a {@code Applicant} with the details of {@code applicantToEdit}
     * edited with {@code editApplicantDescriptor}.
     */
    private static Applicant createEditedApplicant(Applicant applicantToEdit,
            EditApplicantDescriptor editApplicantDescriptor) {
        assert applicantToEdit != null;

        Name updatedName = editApplicantDescriptor.getName().orElse(applicantToEdit.getName());
        Phone updatedPhone = editApplicantDescriptor.getPhone().orElse(applicantToEdit.getPhone());
        Email updatedEmail = editApplicantDescriptor.getEmail().orElse(applicantToEdit.getEmail());
        Address updatedAddress = editApplicantDescriptor.getAddress().orElse(applicantToEdit.getAddress());
        InterviewNotes updatedInterviewNotes = editApplicantDescriptor.getInterviewNotes()
                .orElse(applicantToEdit.getInterviewNotes());
        YearsOfExperience updatedYearsOfExperience = editApplicantDescriptor.getYearsOfExperience()
                .orElse(applicantToEdit.getYearsOfExperience());
        Source updatedSource = editApplicantDescriptor.getSource().orElse(applicantToEdit.getSource());
        Set<Tag> updatedTags = editApplicantDescriptor.getTags().orElse(applicantToEdit.getTags());

        return new Applicant(applicantToEdit.getId(), updatedName, updatedPhone, updatedEmail, updatedAddress,
                updatedInterviewNotes, updatedYearsOfExperience, updatedSource, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editApplicantDescriptor.equals(otherEditCommand.editApplicantDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editApplicantDescriptor", editApplicantDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the applicant with. Each non-empty field value will replace the
     * corresponding field value of the applicant.
     */
    public static class EditApplicantDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private InterviewNotes interviewNotes;
        private YearsOfExperience yearsOfExperience;
        private Source source;
        private Set<Tag> tags;

        public EditApplicantDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditApplicantDescriptor(EditApplicantDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setInterviewNotes(toCopy.interviewNotes);
            setYearsOfExperience(toCopy.yearsOfExperience);
            setSource(toCopy.source);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, interviewNotes, yearsOfExperience,
                    source, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setInterviewNotes(InterviewNotes interviewNotes) {
            this.interviewNotes = interviewNotes;
        }

        public Optional<InterviewNotes> getInterviewNotes() {
            return Optional.ofNullable(interviewNotes);
        }

        public void setYearsOfExperience(YearsOfExperience yearsOfExperience) {
            this.yearsOfExperience = yearsOfExperience;
        }

        public Optional<YearsOfExperience> getYearsOfExperience() {
            return Optional.ofNullable(yearsOfExperience);
        }

        public void setSource(Source source) {
            this.source = source;
        }

        public Optional<Source> getSource() {
            return Optional.ofNullable(source);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditApplicantDescriptor otherEditApplicantDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditApplicantDescriptor.name)
                    && Objects.equals(phone, otherEditApplicantDescriptor.phone)
                    && Objects.equals(email, otherEditApplicantDescriptor.email)
                    && Objects.equals(address, otherEditApplicantDescriptor.address)
                    && Objects.equals(interviewNotes, otherEditApplicantDescriptor.interviewNotes)
                    && Objects.equals(yearsOfExperience, otherEditApplicantDescriptor.yearsOfExperience)
                    && Objects.equals(source, otherEditApplicantDescriptor.source)
                    && Objects.equals(tags, otherEditApplicantDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
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
}
