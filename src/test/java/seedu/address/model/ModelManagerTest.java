package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_APPLICANTS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.applicant.NameContainsKeywordsPredicate;
import seedu.address.testutil.ApplicantBuilder;
import seedu.address.testutil.RecruitDexDataBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void registryChanges_filteredView_updatesWithoutModelForwarding() {
        ObservableList<Applicant> displayed = modelManager.getFilteredApplicantList();
        modelManager.updateFilteredApplicantList(applicant -> applicant.getName().equals(ALICE.getName()));
        modelManager.getApplicantRegistry().add(ALICE);
        modelManager.getApplicantRegistry().add(BENSON);
        assertEquals(List.of(ALICE), displayed);

        Applicant editedAlice = new ApplicantBuilder(ALICE).withName("Alice Tan").build();
        modelManager.getApplicantRegistry().edit(ALICE.getId(), editedAlice);
        assertTrue(displayed.isEmpty());
        assertSame(editedAlice, modelManager.getApplicantRegistry().get(ALICE.getId()));

        modelManager.updateFilteredApplicantList(PREDICATE_SHOW_ALL_APPLICANTS);
        modelManager.getApplicantRegistry().delete(ALICE.getId());
        assertEquals(List.of(BENSON), displayed);
    }

    @Test
    public void setRecruitDexData_keepsExistingFilteredViewAndPredicateConnected() {
        ApplicantRegistry registry = modelManager.getApplicantRegistry();
        ObservableList<Applicant> displayed = modelManager.getFilteredApplicantList();
        modelManager.updateFilteredApplicantList(applicant -> applicant.getId().equals(ALICE.getId()));

        RecruitDexData replacement = new RecruitDexDataBuilder().withApplicant(ALICE).withApplicant(BENSON).build();
        modelManager.setRecruitDexData(replacement);
        assertSame(registry, modelManager.getApplicantRegistry());
        assertSame(displayed, modelManager.getFilteredApplicantList());
        assertEquals(List.of(ALICE), displayed);

        modelManager.setRecruitDexData(new RecruitDexData());
        assertTrue(displayed.isEmpty());
        registry.add(ALICE);
        assertEquals(List.of(ALICE), displayed);
        assertEquals(List.of(ALICE, BENSON), replacement.getApplicantList());
    }

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getUserPrefs().getGuiSettings());
        assertEquals(new RecruitDexData(), new RecruitDexData(modelManager.getRecruitDexData()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new RecruitDexData(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.getUserPrefs().setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.getUserPrefs().setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getUserPrefs().getGuiSettings());
    }

    @Test
    public void hasApplicant_nullApplicant_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.getApplicantRegistry().hasDuplicate(null));
    }

    @Test
    public void hasApplicant_applicantNotInRecruitDexData_returnsFalse() {
        assertFalse(modelManager.getApplicantRegistry().hasDuplicate(ALICE));
    }

    @Test
    public void hasApplicant_applicantInRecruitDexData_returnsTrue() {
        modelManager.getApplicantRegistry().add(ALICE);
        assertTrue(modelManager.getApplicantRegistry().hasDuplicate(ALICE));
    }

    @Test
    public void getFilteredApplicantList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredApplicantList().remove(0));
    }

    @Test
    public void equals() {
        RecruitDexData recruitDexData = new RecruitDexDataBuilder().withApplicant(ALICE).withApplicant(BENSON).build();
        RecruitDexData differentRecruitDexData = new RecruitDexData();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(recruitDexData, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(recruitDexData, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different recruitDexData -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentRecruitDexData, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredApplicantList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(recruitDexData, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredApplicantList(PREDICATE_SHOW_ALL_APPLICANTS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(recruitDexData, differentUserPrefs)));
    }
}
