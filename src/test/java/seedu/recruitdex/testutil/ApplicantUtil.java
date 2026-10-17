package seedu.recruitdex.testutil;

import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_INTERVIEW_NOTES;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_SOURCE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_YEARS_OF_EXPERIENCE;

import java.util.Set;

import seedu.recruitdex.logic.commands.AddCommand;
import seedu.recruitdex.logic.commands.EditCommand.EditApplicantDescriptor;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.tag.Tag;

/**
 * A utility class for Applicant.
 */
public class ApplicantUtil {

    /**
     * Returns an add command string for adding the {@code applicant}.
     */
    public static String getAddCommand(Applicant applicant) {
        return AddCommand.COMMAND_WORD + " " + getApplicantDetails(applicant);
    }

    /**
     * Returns the part of command string for the given {@code applicant}'s details.
     */
    public static String getApplicantDetails(Applicant applicant) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + applicant.getName().fullName + " ");
        sb.append(PREFIX_PHONE + applicant.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + applicant.getEmail().value + " ");
        sb.append(PREFIX_ADDRESS + applicant.getAddress().value + " ");
        sb.append(PREFIX_INTERVIEW_NOTES + applicant.getInterviewNotes().value + " ");
        sb.append(PREFIX_YEARS_OF_EXPERIENCE + applicant.getYearsOfExperience().value + " ");
        sb.append(PREFIX_SOURCE + applicant.getSource().value + " ");
        applicant.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditApplicantDescriptor}'s details.
     */
    public static String getEditApplicantDescriptorDetails(EditApplicantDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(address.value).append(" "));
        descriptor.getInterviewNotes().ifPresent(
                notes -> sb.append(PREFIX_INTERVIEW_NOTES).append(notes.value).append(" "));
        descriptor.getYearsOfExperience().ifPresent(
                years -> sb.append(PREFIX_YEARS_OF_EXPERIENCE).append(years.value).append(" "));
        descriptor.getSource().ifPresent(source -> sb.append(PREFIX_SOURCE).append(source.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
