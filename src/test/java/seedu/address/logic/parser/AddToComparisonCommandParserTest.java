package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddToComparisonCommand;

public class AddToComparisonCommandParserTest {
    private final AddToComparisonCommandParser parser = new AddToComparisonCommandParser();

    @Test
    public void parse_validIndex_returnsCommand() {
        assertParseSuccess(parser, "1", new AddToComparisonCommand(INDEX_FIRST_APPLICANT));
        assertParseSuccess(parser, " \t1 \t", new AddToComparisonCommand(INDEX_FIRST_APPLICANT));
    }

    @Test
    public void parse_invalidArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddToComparisonCommand.MESSAGE_USAGE);
        for (String args : new String[] {"", " ", "0", "-1", "a", "1 2", "1 extra", "1.5", "2147483648"}) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
