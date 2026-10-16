package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CommandInfoTest {

    @Test
    public void fromUsage_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CommandInfo.fromUsage(null));
    }

    @Test
    public void fromUsage_withParametersOnTheirOwnLine_splitsIntoFourParts() {
        CommandInfo info = CommandInfo.fromUsage("delete: Deletes an applicant.\n"
                + "Parameters: INDEX (must be a positive integer)\n"
                + "Example: delete 1");

        assertEquals("delete", info.commandWord());
        assertEquals("Deletes an applicant.", info.description());
        assertEquals("INDEX (must be a positive integer)", info.parameters());
        assertEquals("delete 1", info.example());
    }

    @Test
    public void fromUsage_withParametersOnTheSameLine_splitsIntoFourParts() {
        CommandInfo info = CommandInfo.fromUsage("add: Adds an applicant. Parameters: n/NAME p/PHONE\n"
                + "Example: add n/Amy p/911");

        assertEquals("Adds an applicant.", info.description());
        assertEquals("n/NAME p/PHONE", info.parameters());
        assertEquals("add n/Amy p/911", info.example());
    }

    @Test
    public void fromUsage_withoutParameters_hasEmptyParameters() {
        CommandInfo info = CommandInfo.fromUsage("list: Shows all applicants.\nExample: list");

        assertEquals("list", info.commandWord());
        assertEquals("Shows all applicants.", info.description());
        assertEquals("", info.parameters());
        assertEquals("list", info.example());
    }

    @Test
    public void fromUsage_descriptionOverSeveralLines_isJoinedIntoOne() {
        CommandInfo info = CommandInfo.fromUsage("edit: Edits the details\n  of an applicant.   Existing values\n"
                + "will be overwritten.\nParameters: INDEX [n/NAME]\n  [p/PHONE]\nExample: edit 1 n/Amy");

        assertEquals("Edits the details of an applicant. Existing values will be overwritten.", info.description());
        assertEquals("INDEX [n/NAME] [p/PHONE]", info.parameters());
    }

    @Test
    public void fromUsage_commandWordWithHyphen_isKept() {
        CommandInfo info = CommandInfo.fromUsage("compare-add: Adds an applicant.\n"
                + "Parameters: INDEX\nExample: compare-add 1");

        assertEquals("compare-add", info.commandWord());
        assertEquals("compare-add 1", info.example());
    }

    @Test
    public void fromUsage_exampleTextContainingSlashesAndColons_isKeptWhole() {
        CommandInfo info = CommandInfo.fromUsage("find: Finds applicants.\nExample: find s/LinkedIn: strong");
        assertEquals("find s/LinkedIn: strong", info.example());
    }

    @Test
    public void fromUsage_usageMentioningExampleAndParametersInTheDescription_usesTheMarkersInOrder() {
        CommandInfo info = CommandInfo.fromUsage("find: Finds names.\nParameters: KEYWORD\nExample: find alex");
        assertEquals("KEYWORD", info.parameters());
        assertEquals("find alex", info.example());
    }

    @Test
    public void fromUsage_withoutColonAfterWord_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> CommandInfo.fromUsage("list Shows all.\nExample: list"));
    }

    @Test
    public void fromUsage_withoutExample_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> CommandInfo.fromUsage("list: Shows all applicants."));
    }

    @Test
    public void fromUsage_exampleBeforeTheCommandWord_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> CommandInfo.fromUsage("Example: list"));
        assertThrows(IllegalArgumentException.class, () -> CommandInfo.fromUsage(""));
    }

    @Test
    public void fromUsage_everyRealUsage_parsesWithoutError() {
        for (String usage : CommandSummary.getUsages()) {
            CommandInfo info = CommandInfo.fromUsage(usage);
            assertEquals(usage.substring(0, usage.indexOf(':')), info.commandWord());
        }
    }

    @Test
    public void equals_comparesAllFourParts() {
        CommandInfo info = new CommandInfo("a", "b", "c", "d");
        assertEquals(info, new CommandInfo("a", "b", "c", "d"));
        assertEquals(info.hashCode(), new CommandInfo("a", "b", "c", "d").hashCode());
    }
}
