package seedu.recruitdex.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.recruitdex.logic.parser.Prefix;
import seedu.recruitdex.model.applicant.Applicant;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX = "The applicant index provided is invalid.";
    public static final String MESSAGE_RANKING_UPDATE_FAILED =
            "Could not update the ranking. No changes were made.\n%s";
    public static final String MESSAGE_APPLICANTS_LISTED_OVERVIEW = "%1$d applicant(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code applicant} for display to the user, omitting empty fields.
     */
    public static String format(Applicant applicant) {
        final StringBuilder builder = new StringBuilder();
        builder.append(applicant.getName());
        if (!applicant.getPhone().value.isEmpty()) {
            builder.append("; Phone: ").append(applicant.getPhone());
        }
        if (!applicant.getEmail().value.isEmpty()) {
            builder.append("; Email: ").append(applicant.getEmail());
        }
        if (!applicant.getAddress().value.isEmpty()) {
            builder.append("; Address: ").append(applicant.getAddress());
        }
        if (!applicant.getYearsOfExperience().value.isEmpty()) {
            builder.append("; Experience: ").append(applicant.getYearsOfExperience()).append(" years");
        }
        if (!applicant.getSource().value.isEmpty()) {
            builder.append("; Source: ").append(applicant.getSource());
        }
        if (!applicant.getInterviewNotes().value.isEmpty()) {
            builder.append("; Notes: ").append(applicant.getInterviewNotes());
        }
        if (!applicant.getTags().isEmpty()) {
            builder.append("; Skills: ");
            applicant.getTags().forEach(builder::append);
        }
        return builder.toString();
    }

}
