package seedu.recruitdex.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.recruitdex.logic.commands.SwitchTabCommand.MESSAGE_SUCCESS;
import static seedu.recruitdex.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.commands.SwitchTabCommand.Tab;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;

public class SwitchTabCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void constructor_nullTab_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SwitchTabCommand(null));
    }

    @Test
    public void execute_compare_success() {
        String expectedMessage = String.format(MESSAGE_SUCCESS, "Compare");
        CommandResult expectedCommandResult = new CommandResult(expectedMessage, Tab.COMPARE);
        assertCommandSuccess(new SwitchTabCommand(Tab.COMPARE), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_applicants_success() {
        String expectedMessage = String.format(MESSAGE_SUCCESS, "All applicants");
        CommandResult expectedCommandResult = new CommandResult(expectedMessage, Tab.APPLICANTS);
        assertCommandSuccess(new SwitchTabCommand(Tab.APPLICANTS), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void equals() {
        SwitchTabCommand switchToCompare = new SwitchTabCommand(Tab.COMPARE);

        // same object -> returns true
        assertTrue(switchToCompare.equals(switchToCompare));

        // same values -> returns true
        assertTrue(switchToCompare.equals(new SwitchTabCommand(Tab.COMPARE)));

        // different tab -> returns false
        assertFalse(switchToCompare.equals(new SwitchTabCommand(Tab.APPLICANTS)));

        // null -> returns false
        assertFalse(switchToCompare.equals(null));

        // different types -> returns false
        assertFalse(switchToCompare.equals(1));
    }

    @Test
    public void toStringMethod() {
        String expected = SwitchTabCommand.class.getCanonicalName() + "{tab=" + Tab.COMPARE + "}";
        assertEquals(expected, new SwitchTabCommand(Tab.COMPARE).toString());
    }
}
