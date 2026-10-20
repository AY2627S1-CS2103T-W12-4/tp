package seedu.recruitdex.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.commands.SwitchTabCommand.Tab;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false, null)));
        assertTrue(new CommandResult("feedback", Tab.COMPARE)
                .equals(new CommandResult("feedback", false, false, Tab.COMPARE)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // different tabToShow value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", Tab.COMPARE)));
        assertFalse(new CommandResult("feedback", Tab.COMPARE).equals(new CommandResult("feedback", Tab.APPLICANTS)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // different tabToShow value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", Tab.COMPARE).hashCode());
    }

    @Test
    public void getTabToShow() {
        assertTrue(new CommandResult("feedback").getTabToShow().isEmpty());
        assertEquals(Tab.APPLICANTS, new CommandResult("feedback", Tab.APPLICANTS).getTabToShow().get());
    }

    @Test
    public void constructor_nullTab_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", (Tab) null));
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + ", tabToShow=null}";
        assertEquals(expected, commandResult.toString());
    }
}
