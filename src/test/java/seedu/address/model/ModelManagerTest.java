package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_APPLICANTS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.applicant.NameContainsKeywordsPredicate;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.model.comparison.ComparisonDecision;
import seedu.address.model.comparison.ComparisonPair;
import seedu.address.model.ranking.RankingEngine;
import seedu.address.model.ranking.RankingEntry;
import seedu.address.model.userprefs.UserPrefs;
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

    @Test
    public void getComparisonState_isTheSameObjectAsTheDataState() {
        RecruitDexData data = (RecruitDexData) modelManager.getRecruitDexData();
        assertSame(modelManager.getComparisonState(), data.getComparisonState());
    }

    @Test
    public void getComparisonState_validatesAgainstTheModelRegistry() {
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        assertThrows(ApplicantNotFoundException.class, () -> modelManager.getComparisonState().setCurrentPair(pair));

        modelManager.getApplicantRegistry().add(ALICE);
        modelManager.getApplicantRegistry().add(BENSON);
        modelManager.getComparisonState().setCurrentPair(pair);

        assertEquals(Optional.of(pair), modelManager.getComparisonState().getCurrentPair());
    }

    @Test
    public void getComparisonState_ignoresTheSearchFilter() {
        modelManager.getApplicantRegistry().add(ALICE);
        modelManager.getApplicantRegistry().add(BENSON);
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        modelManager.getComparisonState().setCurrentPair(pair);

        modelManager.updateFilteredApplicantList(new NameContainsKeywordsPredicate(List.of("Alice")));

        assertEquals(Optional.of(pair), modelManager.getComparisonState().getCurrentPair());
    }

    @Test
    public void setRecruitDexData_clearsPendingPair() {
        modelManager.getApplicantRegistry().add(ALICE);
        modelManager.getApplicantRegistry().add(BENSON);
        modelManager.getComparisonState().setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));

        modelManager.setRecruitDexData(new RecruitDexData());

        assertEquals(Optional.empty(), modelManager.getComparisonState().getCurrentPair());
    }

    @Test
    public void getRankingEngine_repeatedAccess_returnsSameEngine() {
        RankingEngine engine = modelManager.getRankingEngine();

        assertSame(engine, modelManager.getRankingEngine());
        assertTrue(engine.getRanking().isEmpty());
    }

    @Test
    public void getRankingEngine_separateModels_haveIndependentRankings() {
        ModelManager otherModel = new ModelManager();
        modelManager.getApplicantRegistry().add(ALICE);

        modelManager.getRankingEngine().recalculate(Set.of(ALICE.getId()), List.of());

        assertNotSame(modelManager.getRankingEngine(), otherModel.getRankingEngine());
        assertEquals(1, modelManager.getRankingEngine().getRanking().size());
        assertTrue(otherModel.getRankingEngine().getRanking().isEmpty());
    }

    @Test
    public void constructor_existingRecords_doesNotAssumeComparisonMembership() {
        RecruitDexData data = new RecruitDexDataBuilder().withApplicant(ALICE).withApplicant(BENSON).build();

        ModelManager model = new ModelManager(data, new UserPrefs());

        assertEquals(2, model.getApplicantRegistry().getApplicantList().size());
        assertTrue(model.getRankingEngine().getRanking().isEmpty());
    }

    @Test
    public void setRecruitDexData_validReplacement_clearsExistingRankingView() {
        modelManager.getApplicantRegistry().add(ALICE);
        RankingEngine engine = modelManager.getRankingEngine();
        ObservableList<RankingEntry> rankingEntries = engine.getRanking();
        engine.recalculate(Set.of(ALICE.getId()), List.of());
        RecruitDexData replacement = new RecruitDexDataBuilder().withApplicant(BENSON).build();

        modelManager.setRecruitDexData(replacement);

        assertSame(engine, modelManager.getRankingEngine());
        assertSame(rankingEntries, engine.getRanking());
        assertTrue(rankingEntries.isEmpty());
        assertEquals(List.of(BENSON), modelManager.getApplicantRegistry().getApplicantList());

        engine.recalculate(Set.of(BENSON.getId()), List.of());
        assertEquals(BENSON.getId(), rankingEntries.get(0).getApplicantId());
    }

    @Test
    public void setRecruitDexData_invalidReplacement_preservesRanking() {
        modelManager.getApplicantRegistry().add(ALICE);
        RankingEngine engine = modelManager.getRankingEngine();
        engine.recalculate(Set.of(ALICE.getId()), List.of());
        ObservableList<RankingEntry> rankingEntries = engine.getRanking();
        List<RankingEntry> originalEntries = List.copyOf(rankingEntries);
        ReadOnlyRecruitDexData invalidData = () -> FXCollections.observableArrayList(ALICE, ALICE);

        assertThrows(DuplicateApplicantException.class, () -> modelManager.setRecruitDexData(invalidData));

        assertSame(engine, modelManager.getRankingEngine());
        assertSame(rankingEntries, engine.getRanking());
        assertEquals(originalEntries, rankingEntries);
        assertEquals(List.of(ALICE), modelManager.getApplicantRegistry().getApplicantList());
    }

    @Test
    public void getRankingEngine_searchFilter_doesNotChangeRanking() {
        modelManager.getApplicantRegistry().add(ALICE);
        modelManager.getApplicantRegistry().add(BENSON);
        modelManager.getRankingEngine().recalculate(Set.of(ALICE.getId(), BENSON.getId()),
                List.of(new ComparisonDecision(ALICE.getId(), BENSON.getId(), ALICE.getId())));
        List<RankingEntry> originalEntries = List.copyOf(modelManager.getRankingEngine().getRanking());

        modelManager.updateFilteredApplicantList(applicant -> false);

        assertTrue(modelManager.getFilteredApplicantList().isEmpty());
        assertEquals(originalEntries, modelManager.getRankingEngine().getRanking());
    }

    @Test
    public void equals_differentRuntimeRankings_ignoresEngineIdentityAndResults() {
        modelManager.getApplicantRegistry().add(ALICE);
        ModelManager otherModel = new ModelManager(modelManager.getRecruitDexData(), new UserPrefs());

        modelManager.getRankingEngine().recalculate(Set.of(ALICE.getId()), List.of());

        assertEquals(modelManager, otherModel);
    }
}
