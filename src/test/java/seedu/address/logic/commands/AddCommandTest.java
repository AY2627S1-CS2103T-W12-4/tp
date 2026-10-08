package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.applicant.Applicant;
import seedu.address.testutil.ApplicantBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullApplicant_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_applicantAcceptedByRegistry_addSuccessful() throws Exception {
        Model model = new ModelManager();
        Applicant validApplicant = new ApplicantBuilder().build();

        CommandResult commandResult = new AddCommand(validApplicant).execute(model);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validApplicant)),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validApplicant), model.getApplicantRegistry().getApplicantList());
        assertEquals(validApplicant.getId(), model.getFilteredApplicantList().get(0).getId());
    }

    @Test
    public void execute_duplicateApplicant_throwsCommandException() {
        Model model = new ModelManager();
        model.getApplicantRegistry().add(ALICE);
        Applicant duplicate = new ApplicantBuilder(ALICE).withId("00000000-0000-0000-0000-000000000002").build();

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_APPLICANT, () ->
                new AddCommand(duplicate).execute(model));
        assertEquals(List.of(ALICE), model.getApplicantRegistry().getApplicantList());
    }

    @Test
    public void execute_duplicateId_throwsCommandException() {
        Model model = new ModelManager();
        model.getApplicantRegistry().add(ALICE);
        Applicant duplicate = new ApplicantBuilder(ALICE).withName("Another applicant").build();

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_APPLICANT, () ->
                new AddCommand(duplicate).execute(model));
        assertEquals(List.of(ALICE), model.getApplicantRegistry().getApplicantList());
    }

    @Test
    public void execute_filteredList_showsAllApplicantsAfterAdding() throws Exception {
        Model model = new ModelManager();
        model.getApplicantRegistry().add(ALICE);
        model.updateFilteredApplicantList(applicant -> false);
        Applicant toAdd = new ApplicantBuilder().build();

        new AddCommand(toAdd).execute(model);

        assertEquals(List.of(ALICE, toAdd), model.getFilteredApplicantList());
    }

    @Test
    public void equals() {
        Applicant alice = new ApplicantBuilder().withName("Alice").build();
        Applicant bob = new ApplicantBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        assertTrue(addAliceCommand.equals(addAliceCommand));
        assertTrue(addAliceCommand.equals(new AddCommand(alice)));
        assertFalse(addAliceCommand.equals(1));
        assertFalse(addAliceCommand.equals(null));
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }
}
