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
import java.util.Optional;
import java.util.stream.Stream;

import seedu.recruitdex.logic.commands.FindCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;
import seedu.recruitdex.model.applicant.ApplicantMatchesSearchPredicate;
import seedu.recruitdex.model.applicant.YearsOfExperience;

/**
 * Parses input arguments and creates a find command that filters applicants by supplied attributes.
 *
 * <p>Each attribute is specified by its prefix and a search term. Terms may contain spaces and extend
 * to the next recognized prefix. Terms are plain text rather than full attribute values, so part of an
 * email or phone number can be searched. At least one attribute must be specified, each prefix may
 * appear at most once, and no term may be blank. The tag prefix accepts one or more space-separated
 * search terms.</p>
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final String PHONE_TERM_REGEX = "\\d+";

    /**
     * Parses the given arguments in the context of the find command.
     *
     * @param args Arguments supplied after the {@code find} command word.
     * @return A find command that filters by every supplied attribute.
     * @throws ParseException If the arguments contain preamble text, no attributes, duplicate
     *                        prefixes, a blank term, or an invalid phone or years of experience term.
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

        Optional<String> nameTerm = parseTerm(argMultimap, PREFIX_NAME);
        Optional<String> phoneTerm = parseTerm(argMultimap, PREFIX_PHONE);
        Optional<String> emailTerm = parseTerm(argMultimap, PREFIX_EMAIL);
        Optional<String> addressTerm = parseTerm(argMultimap, PREFIX_ADDRESS);
        Optional<String> interviewNotesTerm = parseTerm(argMultimap, PREFIX_INTERVIEW_NOTES);
        Optional<String> yearsOfExperienceTerm = parseTerm(argMultimap, PREFIX_YEARS_OF_EXPERIENCE);
        Optional<String> sourceTerm = parseTerm(argMultimap, PREFIX_SOURCE);
        Optional<String> skillTerms = parseTerm(argMultimap, PREFIX_TAG);

        boolean hasAttribute = Stream.of(nameTerm, phoneTerm, emailTerm, addressTerm, interviewNotesTerm,
                yearsOfExperienceTerm, sourceTerm, skillTerms).anyMatch(Optional::isPresent);
        if (!hasAttribute) {
            throw createInvalidFormatException();
        }

        if (phoneTerm.isPresent() && !phoneTerm.get().matches(PHONE_TERM_REGEX)) {
            throw new ParseException(FindCommand.MESSAGE_INVALID_PHONE_TERM);
        }

        Optional<YearsOfExperience> yearsOfExperience = Optional.empty();
        if (yearsOfExperienceTerm.isPresent()) {
            yearsOfExperience = Optional.of(ParserUtil.parseYearsOfExperience(yearsOfExperienceTerm.get()));
        }

        List<String> skillTermList = skillTerms.map(terms -> List.of(terms.split("\\s+"))).orElse(List.of());

        return new FindCommand(new ApplicantMatchesSearchPredicate(nameTerm, phoneTerm, emailTerm, addressTerm,
                interviewNotesTerm, yearsOfExperience, sourceTerm, skillTermList));
    }

    /**
     * Returns the trimmed term given for {@code prefix}, or an empty {@code Optional} if the prefix is absent.
     *
     * @throws ParseException If the prefix is present but its term is blank.
     */
    private Optional<String> parseTerm(ArgumentMultimap argMultimap, Prefix prefix) throws ParseException {
        Optional<String> term = argMultimap.getValue(prefix).map(String::trim);
        if (term.isPresent() && term.get().isEmpty()) {
            throw createInvalidFormatException();
        }
        return term;
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
