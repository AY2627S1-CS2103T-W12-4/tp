package seedu.recruitdex.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CommandHistoryList}.
 */
public class CommandHistoryListTest {

    @Test
    public void add_nullOrEmptyCommand_throwsIllegalArgumentException() {
        CommandHistoryList history = new CommandHistoryList();

        assertThrows(IllegalArgumentException.class, () -> history.add(null));
        assertThrows(IllegalArgumentException.class, () -> history.add(""));
    }

    @Test
    public void previous_movesTowardOldestAndStopsAtOldest() {
        CommandHistoryList history = new CommandHistoryList();
        history.add("older");
        history.add("newer");

        assertEquals("newer", history.previous("draft"));
        assertEquals("older", history.previous("newer"));
        assertEquals("older", history.previous("older"));
    }

    @Test
    public void next_movesTowardNewestThenRestoresDraft() {
        CommandHistoryList history = new CommandHistoryList();
        history.add("older");
        history.add("newer");

        assertEquals("newer", history.previous("draft"));
        assertEquals("older", history.previous("newer"));
        assertEquals("newer", history.next());
        assertEquals("draft", history.next());
        assertEquals(null, history.next());
    }

    @Test
    public void previous_singleEntryThenNext_restoresDraft() {
        CommandHistoryList history = new CommandHistoryList();
        history.add("only command");

        assertEquals("only command", history.previous("unfinished draft"));
        assertEquals("unfinished draft", history.next());
    }

    @Test
    public void previous_preservesEmptyDraft() {
        CommandHistoryList history = new CommandHistoryList();
        history.add("command");

        assertEquals("command", history.previous(""));
        assertEquals("", history.next());
    }

    @Test
    public void resetNavigation_stopsBrowsingAndNextReturnsNull() {
        CommandHistoryList history = new CommandHistoryList();
        history.add("older");
        history.add("newer");

        assertEquals("newer", history.previous("draft"));
        history.resetNavigation();

        assertEquals(null, history.next());
        assertEquals("newer", history.previous("new draft"));
    }

    @Test
    public void add_whileBrowsing_resetsNavigationAndNewCommandBecomesNewest() {
        CommandHistoryList history = new CommandHistoryList();
        history.add("older");
        history.add("newer");
        history.previous("draft");

        history.add("latest");

        assertEquals("latest", history.previous("another draft"));
        assertEquals("newer", history.previous("latest"));
    }

    @Test
    public void previous_nullCurrentText_preservesEmptyDraft() {
        CommandHistoryList history = new CommandHistoryList();
        history.add("command");

        assertEquals("command", history.previous(null));
        assertEquals("", history.next());
    }
}
