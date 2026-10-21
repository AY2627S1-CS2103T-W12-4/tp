package seedu.recruitdex.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruitdex.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.commands.SwitchTabCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;

public class SwitchTabCommandParserTest {
    private final SwitchTabCommandParser parser = new SwitchTabCommandParser();

    @Test
    public void parse_noArguments_returnsCommand() throws ParseException {
        assertTrue(parser.parse("") instanceof SwitchTabCommand);
        assertTrue(parser.parse(" \t ") instanceof SwitchTabCommand);
    }

    @Test
    public void parse_anyArgument_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SwitchTabCommand.MESSAGE_USAGE);
        for (String args : List.of("compare", "applicants", " compare ", "1", "x y")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
