package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddToComparisonCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for adding an existing applicant to the comparison list.
 */
public class AddToComparisonCommandParser implements Parser<AddToComparisonCommand> {

    /**
     * Parses a positive displayed applicant index and returns the corresponding command.
     * @throws ParseException if the arguments do not contain exactly one valid index.
     */
    @Override
    public AddToComparisonCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new AddToComparisonCommand(index);
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddToComparisonCommand.MESSAGE_USAGE), e);
        }
    }
}
