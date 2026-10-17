package seedu.recruitdex.logic.parser;

import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_INTERVIEW_NOTES;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_SOURCE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_YEARS_OF_EXPERIENCE;

import java.util.Set;
import java.util.stream.Stream;

import seedu.recruitdex.logic.commands.AddCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;
import seedu.recruitdex.model.applicant.Address;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.applicant.Email;
import seedu.recruitdex.model.applicant.InterviewNotes;
import seedu.recruitdex.model.applicant.Name;
import seedu.recruitdex.model.applicant.Phone;
import seedu.recruitdex.model.applicant.Source;
import seedu.recruitdex.model.applicant.YearsOfExperience;
import seedu.recruitdex.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_INTERVIEW_NOTES, PREFIX_YEARS_OF_EXPERIENCE, PREFIX_SOURCE, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_INTERVIEW_NOTES, PREFIX_YEARS_OF_EXPERIENCE, PREFIX_SOURCE);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        InterviewNotes interviewNotes = ParserUtil.parseInterviewNotes(
                argMultimap.getValue(PREFIX_INTERVIEW_NOTES).orElse(InterviewNotes.DEFAULT_VALUE));
        YearsOfExperience yearsOfExperience = ParserUtil.parseYearsOfExperience(
                argMultimap.getValue(PREFIX_YEARS_OF_EXPERIENCE).orElse(YearsOfExperience.DEFAULT_VALUE));
        Source source = ParserUtil.parseSource(argMultimap.getValue(PREFIX_SOURCE).orElse(Source.DEFAULT_VALUE));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Applicant applicant = new Applicant(new ApplicantId(), name, phone, email, address, interviewNotes,
                yearsOfExperience, source, tagList);

        return new AddCommand(applicant);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
