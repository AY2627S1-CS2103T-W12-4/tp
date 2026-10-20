package seedu.recruitdex.logic.parser;

import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruitdex.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.recruitdex.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.commands.SwitchTabCommand;
import seedu.recruitdex.logic.commands.SwitchTabCommand.Tab;

public class SwitchTabCommandParserTest {
    private final SwitchTabCommandParser parser = new SwitchTabCommandParser();

    @Test
    public void parse_validTab_returnsCommand() {
        assertParseSuccess(parser, "compare", new SwitchTabCommand(Tab.COMPARE));
        assertParseSuccess(parser, "applicants", new SwitchTabCommand(Tab.APPLICANTS));
        assertParseSuccess(parser, " \tCOMPARE \t", new SwitchTabCommand(Tab.COMPARE));
        assertParseSuccess(parser, "Applicants", new SwitchTabCommand(Tab.APPLICANTS));
    }

    @Test
    public void parse_invalidArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SwitchTabCommand.MESSAGE_USAGE);
        for (String args : List.of("", " ", "comp", "all", "applicant", "compare applicants",
                "compare extra", "1")) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }
}
