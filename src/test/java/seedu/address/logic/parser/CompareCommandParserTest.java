package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CompareCommand;
import seedu.address.logic.commands.CompareCommand.Side;

public class CompareCommandParserTest {
    private final CompareCommandParser parser = new CompareCommandParser();

    @Test
    public void parse_validSide_returnsCommand() {
        assertParseSuccess(parser, "a", new CompareCommand(Side.A));
        assertParseSuccess(parser, "b", new CompareCommand(Side.B));
        assertParseSuccess(parser, " \tA \t", new CompareCommand(Side.A));
        assertParseSuccess(parser, " \tB \t", new CompareCommand(Side.B));
    }

    @Test
    public void parse_invalidArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompareCommand.MESSAGE_USAGE);
        for (String args : new String[] {"", " ", "c", "1", "ab", "a b", "a extra", "a/", "a\nb"}) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
