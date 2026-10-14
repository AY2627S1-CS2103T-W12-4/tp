package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ComparisonCommand;
import seedu.address.logic.commands.ComparisonCommand.Side;

public class ComparisonCommandParserTest {
    private final ComparisonCommandParser parser = new ComparisonCommandParser();

    @Test
    public void parse_validSide_returnsCommand() {
        assertParseSuccess(parser, "a", new ComparisonCommand(Side.A));
        assertParseSuccess(parser, "b", new ComparisonCommand(Side.B));
        assertParseSuccess(parser, " \tA \t", new ComparisonCommand(Side.A));
        assertParseSuccess(parser, " \tB \t", new ComparisonCommand(Side.B));
    }

    @Test
    public void parse_invalidArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ComparisonCommand.MESSAGE_USAGE);
        for (String args : new String[] {"", " ", "c", "1", "ab", "a b", "a extra", "a/", "a\nb"}) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
