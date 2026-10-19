package seedu.recruitdex.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_INTERVIEW_NOTES;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_SOURCE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_YEARS_OF_EXPERIENCE;

import java.util.List;
import java.util.function.Predicate;

import seedu.recruitdex.logic.commands.FindCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;
import seedu.recruitdex.model.applicant.Address;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.Email;
import seedu.recruitdex.model.applicant.InterviewNotes;
import seedu.recruitdex.model.applicant.Name;
import seedu.recruitdex.model.applicant.Phone;
import seedu.recruitdex.model.applicant.Source;
import seedu.recruitdex.model.applicant.TagContainsKeywordsPredicate;
import seedu.recruitdex.model.applicant.YearsOfExperience;

/**
 * Parses input arguments and creates a find command that filters applicants by supplied attributes.
 *
 * <p>Each attribute is specified by its prefix and a value. Values may contain spaces and extend
 * to the next recognized prefix. At least one attribute must be specified, and each prefix may
 * appear at most once. The tag prefix accepts one or more space-separated search terms.</p>
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given arguments in the context of the find command.
     *
     * @param args Arguments supplied after the {@code find} command word.
     * @return A find command that filters by every supplied attribute.
     * @throws ParseException If the arguments contain preamble text, no attributes, duplicate
     *                        prefixes, an empty tag search, or an invalid attribute value.
     */
    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE,
                PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_INTERVIEW_NOTES, PREFIX_YEARS_OF_EXPERIENCE,
                PREFIX_SOURCE, PREFIX_TAG);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw createInvalidFormatException();
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_INTERVIEW_NOTES, PREFIX_YEARS_OF_EXPERIENCE, PREFIX_SOURCE, PREFIX_TAG);

        Predicate<Applicant> predicate = applicant -> true;
        boolean hasAttribute = false;

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
            predicate = predicate.and(applicant ->
                    applicant.getName().toString().toLowerCase().contains(name.toString().toLowerCase()));
            hasAttribute = true;
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
            predicate = predicate.and(applicant ->
                    applicant.getPhone().toString().contains(phone.toString()));
            hasAttribute = true;
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
            predicate = predicate.and(applicant ->
                    applicant.getEmail().toString().toLowerCase().contains(email.toString().toLowerCase()));
            hasAttribute = true;
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
            predicate = predicate.and(applicant ->
                    applicant.getAddress().toString().toLowerCase().contains(address.toString().toLowerCase()));
            hasAttribute = true;
        }
        if (argMultimap.getValue(PREFIX_INTERVIEW_NOTES).isPresent()) {
            InterviewNotes interviewNotes =
                    ParserUtil.parseInterviewNotes(argMultimap.getValue(PREFIX_INTERVIEW_NOTES).get());
            predicate = predicate.and(applicant ->
                    applicant.getInterviewNotes().toString().toLowerCase()
                            .contains(interviewNotes.toString().toLowerCase()));
            hasAttribute = true;
        }
        if (argMultimap.getValue(PREFIX_YEARS_OF_EXPERIENCE).isPresent()) {
            YearsOfExperience yearsOfExperience =
                    ParserUtil.parseYearsOfExperience(argMultimap.getValue(PREFIX_YEARS_OF_EXPERIENCE).get());
            predicate = predicate.and(applicant -> applicant.getYearsOfExperience().equals(yearsOfExperience));
            hasAttribute = true;
        }
        if (argMultimap.getValue(PREFIX_SOURCE).isPresent()) {
            Source source = ParserUtil.parseSource(argMultimap.getValue(PREFIX_SOURCE).get());
            predicate = predicate.and(applicant ->
                    applicant.getSource().toString().toLowerCase().contains(source.toString().toLowerCase()));
            hasAttribute = true;
        }
        if (argMultimap.getValue(PREFIX_TAG).isPresent()) {
            String tagSearchTerms = argMultimap.getValue(PREFIX_TAG).get().trim();
            if (tagSearchTerms.isEmpty()) {
                throw createInvalidFormatException();
            }

            List<String> tagKeywords = List.of(tagSearchTerms.split("\\s+"));
            predicate = predicate.and(new TagContainsKeywordsPredicate(tagKeywords));
            hasAttribute = true;
        }

        if (!hasAttribute) {
            throw createInvalidFormatException();
        }

        return new FindCommand(predicate);
    }

    /**
     * Creates the standard invalid-format exception for the find command.
     *
     * @return A parse exception containing the find command usage message.
     */
    private ParseException createInvalidFormatException() {
        return new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
}
