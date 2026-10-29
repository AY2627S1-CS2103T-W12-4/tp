package seedu.recruitdex.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.testutil.Assert.assertThrows;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.ranking.RankingEntry;
import seedu.recruitdex.testutil.ApplicantBuilder;

public class RankingEntryDisplayTest {

    private static final List<Applicant> DISPLAYED_APPLICANTS = List.of(ALICE, BENSON);
    private static final Map<ApplicantId, Applicant> APPLICANTS_BY_ID =
            Map.of(ALICE.getId(), ALICE, BENSON.getId(), BENSON);

    @Test
    public void create_nullArguments_throwsNullPointerException() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.empty(), 0);

        assertThrows(NullPointerException.class, () ->
                RankingEntryDisplay.create(null, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS));
        assertThrows(NullPointerException.class, () -> RankingEntryDisplay.create(entry, null, DISPLAYED_APPLICANTS));
        assertThrows(NullPointerException.class, () -> RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, null));
    }

    @Test
    public void create_completeEntry_formatsAllFields() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 3, OptionalDouble.of(-0.405465), 7);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);

        assertEquals("Rank 3", display.positionText());
        assertEquals(ALICE.getName().fullName, display.nameText());
        assertEquals("Score: -0.405465 · 7 comparisons", display.metadataText());
        assertFalse(display.isFirstPosition());
    }

    @Test
    public void create_sharedFirstPosition_preservesTie() {
        RankingEntry first = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0.287682), 1);
        RankingEntry second = new RankingEntry(BENSON.getId(), 1, OptionalDouble.of(0.287682), 5);

        RankingEntryDisplay firstDisplay = RankingEntryDisplay.create(first, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);
        RankingEntryDisplay secondDisplay =
                RankingEntryDisplay.create(second, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);

        assertEquals("Rank 1", firstDisplay.positionText());
        assertEquals(firstDisplay.positionText(), secondDisplay.positionText());
        assertTrue(firstDisplay.isFirstPosition());
        assertTrue(secondDisplay.isFirstPosition());
        assertEquals("Applicant index: 1", firstDisplay.applicantIndexText());
        assertEquals("Applicant index: 2", secondDisplay.applicantIndexText());
        assertEquals("Score: 0.287682 · 1 comparison", firstDisplay.metadataText());
        assertEquals("Score: 0.287682 · 5 comparisons", secondDisplay.metadataText());
    }

    @Test
    public void create_equivalentIdAndDifferentRecordOrder_resolvesById() {
        Map<ApplicantId, Applicant> applicantsById = new LinkedHashMap<>();
        applicantsById.put(BENSON.getId(), BENSON);
        applicantsById.put(ALICE.getId(), ALICE);
        RankingEntry entry = new RankingEntry(new ApplicantId(ALICE.getId().value), 2,
                OptionalDouble.of(0), 2);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, applicantsById, DISPLAYED_APPLICANTS);

        assertEquals(ALICE.getName().fullName, display.nameText());
        assertEquals("Rank 2", display.positionText());
    }

    @Test
    public void create_editedApplicant_displaysCurrentName() {
        Map<ApplicantId, Applicant> applicantsById = new HashMap<>(APPLICANTS_BY_ID);
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0), 2);
        RankingEntryDisplay originalDisplay = RankingEntryDisplay.create(entry, applicantsById, DISPLAYED_APPLICANTS);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withName("Alice Tan").build();
        applicantsById.put(editedAlice.getId(), editedAlice);
        RankingEntryDisplay updatedDisplay = RankingEntryDisplay.create(entry, applicantsById, DISPLAYED_APPLICANTS);

        assertEquals("Alice Tan", updatedDisplay.nameText());
        assertEquals(ALICE.getName().fullName, originalDisplay.nameText());
        assertEquals(originalDisplay.positionText(), updatedDisplay.positionText());
        assertEquals(originalDisplay.metadataText(), updatedDisplay.metadataText());
    }

    @Test
    public void create_missingApplicant_preservesResultWithFallback() {
        ApplicantId missingId = new ApplicantId("00000000-0000-0000-0000-000000000999");
        RankingEntry entry = new RankingEntry(missingId, 4, OptionalDouble.of(-0.000001), 2);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);

        assertEquals("Applicant unavailable", display.nameText());
        assertEquals("Rank 4", display.positionText());
        assertEquals("Score: -0.000001 · 2 comparisons", display.metadataText());
        assertFalse(display.isFirstPosition());
    }

    @Test
    public void create_emptyScore_usesDash() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 2, OptionalDouble.empty(), 3);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);

        assertEquals("Score: — · 3 comparisons", display.metadataText());
    }

    @Test
    public void create_zeroScoreWithComparisons_isAssessed() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0), 2);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);

        assertEquals("Score: 0.000000 · 2 comparisons", display.metadataText());
        assertEquals("Rank 1", display.positionText());
        assertTrue(display.isFirstPosition());
    }

    @Test
    public void create_noComparisons_marksUnassessed() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0), 0);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);

        assertEquals("Unassessed · 0 comparisons", display.metadataText());
        assertEquals("", display.positionText());
        assertFalse(display.isFirstPosition());
    }

    @Test
    public void create_emptyUnassessedScore_preservesAbsence() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.empty(), 0);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, Map.of(), DISPLAYED_APPLICANTS);

        assertEquals("Applicant unavailable", display.nameText());
        assertEquals("Unassessed · 0 comparisons", display.metadataText());
        assertEquals("", display.positionText());
    }

    @Test
    public void create_decimalCommaLocale_usesDot() {
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        RankingEntry entry = new RankingEntry(ALICE.getId(), 2, OptionalDouble.of(-0.405465), 1);

        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.GERMANY);
            RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, DISPLAYED_APPLICANTS);

            assertEquals("Score: -0.405465 · 1 comparison", display.metadataText());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }

    @Test
    public void create_filteredResults_updatesIndexAndPreservesHiddenApplicantName() {
        FilteredList<Applicant> results = new FilteredList<>(FXCollections.observableArrayList(BENSON, ALICE));
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.empty(), 0);

        assertEquals("Applicant index: 2",
                RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, results).applicantIndexText());
        results.setPredicate(applicant -> applicant.getId().equals(ALICE.getId()));
        assertEquals("Applicant index: 1",
                RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, results).applicantIndexText());
        results.setPredicate(applicant -> false);
        RankingEntryDisplay hiddenDisplay = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, results);
        assertEquals("Not in current applicant results", hiddenDisplay.applicantIndexText());
        assertEquals(ALICE.getName().fullName, hiddenDisplay.nameText());
        assertEquals("", hiddenDisplay.positionText());
        results.setPredicate(null);
        assertEquals("Applicant index: 2",
                RankingEntryDisplay.create(entry, APPLICANTS_BY_ID, results).applicantIndexText());
    }

}
