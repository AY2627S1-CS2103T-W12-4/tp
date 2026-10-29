package seedu.recruitdex.logic.parser;

import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruitdex.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.recruitdex.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.recruitdex.logic.parser.ParserUtil.MESSAGE_NON_POSITIVE_INDEX;
import static seedu.recruitdex.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occurs inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_APPLICANT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1.5", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nonPositiveIndex_throwsNonPositiveIndexMessage() {
        assertParseFailure(parser, "0", MESSAGE_NON_POSITIVE_INDEX);
        assertParseFailure(parser, "-1", MESSAGE_NON_POSITIVE_INDEX);
    }
}
