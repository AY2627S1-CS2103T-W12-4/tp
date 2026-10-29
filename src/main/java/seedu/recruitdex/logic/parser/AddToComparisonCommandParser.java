package seedu.recruitdex.logic.parser;

import seedu.recruitdex.commons.core.index.Index;
import seedu.recruitdex.logic.commands.AddToComparisonCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;

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
        Index index = ParserUtil.parseIndex(args, AddToComparisonCommand.MESSAGE_USAGE);
        return new AddToComparisonCommand(index);
    }
}
