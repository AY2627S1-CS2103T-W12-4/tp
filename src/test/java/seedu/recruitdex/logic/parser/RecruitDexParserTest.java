package seedu.recruitdex.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruitdex.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.recruitdex.testutil.Assert.assertThrows;
import static seedu.recruitdex.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.commands.AddCommand;
import seedu.recruitdex.logic.commands.AddToComparisonCommand;
import seedu.recruitdex.logic.commands.ClearCommand;
import seedu.recruitdex.logic.commands.CompareCommand;
import seedu.recruitdex.logic.commands.CompareCommand.Side;
import seedu.recruitdex.logic.commands.DeleteCommand;
import seedu.recruitdex.logic.commands.EditCommand;
import seedu.recruitdex.logic.commands.EditCommand.EditApplicantDescriptor;
import seedu.recruitdex.logic.commands.ExitCommand;
import seedu.recruitdex.logic.commands.FindCommand;
import seedu.recruitdex.logic.commands.HelpCommand;
import seedu.recruitdex.logic.commands.ListCommand;
import seedu.recruitdex.logic.commands.RemoveFromComparisonCommand;
import seedu.recruitdex.logic.commands.SwitchTabCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.testutil.ApplicantBuilder;
import seedu.recruitdex.testutil.ApplicantUtil;
import seedu.recruitdex.testutil.EditApplicantDescriptorBuilder;

public class RecruitDexParserTest {

    private final RecruitDexParser parser = new RecruitDexParser();

    @Test
    public void parseCommand_add() throws Exception {
        Applicant applicant = new ApplicantBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(ApplicantUtil.getAddCommand(applicant));
        assertEquals(new AddCommand(applicant), command);
    }

    @Test
    public void parseCommand_addToComparison() throws Exception {
        assertEquals(new AddToComparisonCommand(INDEX_FIRST_APPLICANT), parser.parseCommand(
                AddToComparisonCommand.COMMAND_WORD + " " + INDEX_FIRST_APPLICANT.getOneBased()));
    }

    @Test
    public void parseCommand_choose() throws Exception {
        assertEquals(new CompareCommand(Side.A), parser.parseCommand("choose a"));
        assertEquals(new CompareCommand(Side.B), parser.parseCommand("choose B"));
    }

    @Test
    public void parseCommand_chooseMissingOrInvalidSide_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompareCommand.MESSAGE_USAGE);
        for (String input : new String[] {"choose", "choose c", "choose a b"}) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_switchTab() throws Exception {
        assertTrue(parser.parseCommand(SwitchTabCommand.COMMAND_WORD) instanceof SwitchTabCommand);
        assertTrue(parser.parseCommand(SwitchTabCommand.COMMAND_WORD + "  ") instanceof SwitchTabCommand);
    }

    @Test
    public void parseCommand_switchTabWithArgument_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SwitchTabCommand.MESSAGE_USAGE);
        for (String input : new String[] {"sw compare", "sw applicants", "sw 1"}) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_addToComparisonMissingIndex_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddToComparisonCommand.MESSAGE_USAGE), () ->
                parser.parseCommand(AddToComparisonCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_removeFromComparison() throws Exception {
        assertEquals(new RemoveFromComparisonCommand(INDEX_FIRST_APPLICANT), parser.parseCommand(
                RemoveFromComparisonCommand.COMMAND_WORD + " " + INDEX_FIRST_APPLICANT.getOneBased()));
    }

    @Test
    public void parseCommand_removeFromComparisonMissingIndex_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveFromComparisonCommand.MESSAGE_USAGE), () ->
                parser.parseCommand(RemoveFromComparisonCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_APPLICANT.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_APPLICANT), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Applicant applicant = new ApplicantBuilder().build();
        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder(applicant).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_APPLICANT.getOneBased() + " "
                + ApplicantUtil.getEditApplicantDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_APPLICANT, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_findByTags() throws Exception {
        assertTrue(parser.parseCommand("find -t foo bar baz") instanceof FindCommand);
    }

    @Test
    public void parseCommand_findWithoutAttributes_throwsParseException() {
        String expectedMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);

        assertThrows(ParseException.class, expectedMessage, () ->
                parser.parseCommand(FindCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
