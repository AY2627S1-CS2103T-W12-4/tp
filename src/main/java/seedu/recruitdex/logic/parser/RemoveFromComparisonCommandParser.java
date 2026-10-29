package seedu.recruitdex.logic.parser;

import seedu.recruitdex.commons.core.index.Index;
import seedu.recruitdex.logic.commands.RemoveFromComparisonCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for removing an existing applicant from the comparison list.
 */
public class RemoveFromComparisonCommandParser implements Parser<RemoveFromComparisonCommand> {

    /**
     * Parses a positive displayed applicant index and returns the corresponding command.
     * @throws ParseException if the arguments do not contain exactly one valid index.
     */
    @Override
    public RemoveFromComparisonCommand parse(String args) throws ParseException {
        Index index = ParserUtil.parseIndex(args, RemoveFromComparisonCommand.MESSAGE_USAGE);
        return new RemoveFromComparisonCommand(index);
    }
}
