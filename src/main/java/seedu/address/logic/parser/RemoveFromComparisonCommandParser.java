package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemoveFromComparisonCommand;
import seedu.address.logic.parser.exceptions.ParseException;

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
        try {
            Index index = ParserUtil.parseIndex(args);
            return new RemoveFromComparisonCommand(index);
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RemoveFromComparisonCommand.MESSAGE_USAGE), e);
        }
    }
}
