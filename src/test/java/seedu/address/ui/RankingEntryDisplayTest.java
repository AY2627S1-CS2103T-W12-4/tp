package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.ranking.RankingEntry;
import seedu.address.testutil.ApplicantBuilder;

public class RankingEntryDisplayTest {

    private static final Map<ApplicantId, Applicant> APPLICANTS_BY_ID =
            Map.of(ALICE.getId(), ALICE, BENSON.getId(), BENSON);

    @Test
    public void create_nullArguments_throwsNullPointerException() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.empty(), 0);

        assertThrows(NullPointerException.class, () -> RankingEntryDisplay.create(null, APPLICANTS_BY_ID));
        assertThrows(NullPointerException.class, () -> RankingEntryDisplay.create(entry, null));
    }

    @Test
    public void create_completeEntry_formatsAllFields() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 3, OptionalDouble.of(-0.405465), 7);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID);

        assertEquals("3.", display.positionText());
        assertEquals(ALICE.getName().fullName, display.nameText());
        assertEquals("Score: -0.405465 · 7 comparisons", display.metadataText());
        assertFalse(display.isFirstPosition());
    }

    @Test
    public void create_sharedFirstPosition_preservesTie() {
        RankingEntry first = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0.287682), 1);
        RankingEntry second = new RankingEntry(BENSON.getId(), 1, OptionalDouble.of(0.287682), 5);

        RankingEntryDisplay firstDisplay = RankingEntryDisplay.create(first, APPLICANTS_BY_ID);
        RankingEntryDisplay secondDisplay = RankingEntryDisplay.create(second, APPLICANTS_BY_ID);

        assertEquals("1.", firstDisplay.positionText());
        assertEquals(firstDisplay.positionText(), secondDisplay.positionText());
        assertTrue(firstDisplay.isFirstPosition());
        assertTrue(secondDisplay.isFirstPosition());
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

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, applicantsById);

        assertEquals(ALICE.getName().fullName, display.nameText());
        assertEquals("2.", display.positionText());
    }

    @Test
    public void create_editedApplicant_displaysCurrentName() {
        Map<ApplicantId, Applicant> applicantsById = new HashMap<>(APPLICANTS_BY_ID);
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0), 2);
        RankingEntryDisplay originalDisplay = RankingEntryDisplay.create(entry, applicantsById);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withName("Alice Tan").build();
        applicantsById.put(editedAlice.getId(), editedAlice);
        RankingEntryDisplay updatedDisplay = RankingEntryDisplay.create(entry, applicantsById);

        assertEquals("Alice Tan", updatedDisplay.nameText());
        assertEquals(ALICE.getName().fullName, originalDisplay.nameText());
        assertEquals(originalDisplay.positionText(), updatedDisplay.positionText());
        assertEquals(originalDisplay.metadataText(), updatedDisplay.metadataText());
    }

    @Test
    public void create_missingApplicant_preservesResultWithFallback() {
        ApplicantId missingId = new ApplicantId("00000000-0000-0000-0000-000000000999");
        RankingEntry entry = new RankingEntry(missingId, 4, OptionalDouble.of(-0.000001), 2);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID);

        assertEquals("Applicant unavailable", display.nameText());
        assertEquals("4.", display.positionText());
        assertEquals("Score: -0.000001 · 2 comparisons", display.metadataText());
        assertFalse(display.isFirstPosition());
    }

    @Test
    public void create_emptyScore_usesDash() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 2, OptionalDouble.empty(), 3);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID);

        assertEquals("Score: — · 3 comparisons", display.metadataText());
    }

    @Test
    public void create_zeroScoreWithComparisons_isAssessed() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0), 2);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID);

        assertEquals("Score: 0.000000 · 2 comparisons", display.metadataText());
    }

    @Test
    public void create_noComparisons_marksUnassessed() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0), 0);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID);

        assertEquals("Score: 0.000000 · 0 comparisons · Unassessed", display.metadataText());
        assertTrue(display.isFirstPosition());
    }

    @Test
    public void create_emptyUnassessedScore_preservesAbsence() {
        RankingEntry entry = new RankingEntry(ALICE.getId(), 1, OptionalDouble.empty(), 0);

        RankingEntryDisplay display = RankingEntryDisplay.create(entry, Map.of());

        assertEquals("Applicant unavailable", display.nameText());
        assertEquals("Score: — · 0 comparisons · Unassessed", display.metadataText());
    }

    @Test
    public void create_decimalCommaLocale_usesDot() {
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        RankingEntry entry = new RankingEntry(ALICE.getId(), 2, OptionalDouble.of(-0.405465), 1);

        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.GERMANY);
            RankingEntryDisplay display = RankingEntryDisplay.create(entry, APPLICANTS_BY_ID);

            assertEquals("Score: -0.405465 · 1 comparison", display.metadataText());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }
}
