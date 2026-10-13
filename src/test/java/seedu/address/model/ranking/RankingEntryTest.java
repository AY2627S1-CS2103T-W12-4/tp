package seedu.address.model.ranking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.ApplicantId;

public class RankingEntryTest {

    private static final ApplicantId APPLICANT_ID_FIRST =
            new ApplicantId("00000000-0000-0000-0000-000000000001");
    private static final ApplicantId APPLICANT_ID_SECOND =
            new ApplicantId("00000000-0000-0000-0000-000000000002");

    @Test
    public void constructor_validValues_preservesValues() {
        OptionalDouble score = OptionalDouble.of(-0.123456789);
        RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, 2, score, 7);

        assertEquals(APPLICANT_ID_FIRST, entry.getApplicantId());
        assertEquals(2, entry.getPosition());
        assertEquals(score, entry.getScore());
        assertEquals(7, entry.getComparisonCount());
    }

    @Test
    public void constructor_emptyScore_accepts() {
        RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.empty(), 3);

        assertEquals(OptionalDouble.empty(), entry.getScore());
        assertEquals(3, entry.getComparisonCount());
    }

    @Test
    public void constructor_zeroCountWithPresentScore_accepts() {
        RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.of(1000), 0);

        assertEquals(OptionalDouble.of(1000), entry.getScore());
        assertEquals(0, entry.getComparisonCount());
    }

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RankingEntry(null, 1, OptionalDouble.empty(), 0));
        assertThrows(NullPointerException.class, () -> new RankingEntry(APPLICANT_ID_FIRST, 1, null, 0));
    }

    @Test
    public void constructor_nonPositivePosition_throwsIllegalArgumentException() {
        for (int position : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new RankingEntry(APPLICANT_ID_FIRST, position, OptionalDouble.empty(), 0));
        }
    }

    @Test
    public void constructor_nonFiniteScore_throwsIllegalArgumentException() {
        for (double score : new double[] {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.of(score), 0));
        }
    }

    @Test
    public void constructor_negativeCount_throwsIllegalArgumentException() {
        for (int count : new int[] {-1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.empty(), count));
        }
    }

    @Test
    public void constructor_finiteBoundaryValues_accepts() {
        double[] scores = {0.0, -0.0, Double.MIN_VALUE, -Double.MIN_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};
        for (double score : scores) {
            RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, Integer.MAX_VALUE,
                    OptionalDouble.of(score), Integer.MAX_VALUE);

            assertEquals(Integer.MAX_VALUE, entry.getPosition());
            assertEquals(OptionalDouble.of(score), entry.getScore());
            assertEquals(Integer.MAX_VALUE, entry.getComparisonCount());
        }
    }

    @Test
    public void equals_sameReference_returnsTrue() {
        RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.of(0), 0);

        assertTrue(entry.equals(entry));
    }

    @Test
    public void equals_equalValues_returnsTrue() {
        for (OptionalDouble score : List.of(OptionalDouble.empty(), OptionalDouble.of(0.25))) {
            RankingEntry first = new RankingEntry(APPLICANT_ID_FIRST, 2, score, 4);
            RankingEntry second = new RankingEntry(new ApplicantId(APPLICANT_ID_FIRST.value), 2, score, 4);
            RankingEntry third = new RankingEntry(new ApplicantId(APPLICANT_ID_FIRST.value), 2, score, 4);

            assertTrue(first.equals(second));
            assertTrue(second.equals(first));
            assertTrue(second.equals(third));
            assertTrue(first.equals(third));
        }
    }

    @Test
    public void equals_nullOrOtherType_returnsFalse() {
        RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.of(0), 0);

        assertFalse(entry.equals(null));
        assertFalse(entry.equals(APPLICANT_ID_FIRST));
    }

    @Test
    public void equals_oneDifferentField_returnsFalse() {
        RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, 2, OptionalDouble.of(0.25), 4);
        List<RankingEntry> differentEntries = List.of(
                new RankingEntry(APPLICANT_ID_SECOND, 2, OptionalDouble.of(0.25), 4),
                new RankingEntry(APPLICANT_ID_FIRST, 3, OptionalDouble.of(0.25), 4),
                new RankingEntry(APPLICANT_ID_FIRST, 2, OptionalDouble.of(0.5), 4),
                new RankingEntry(APPLICANT_ID_FIRST, 2, OptionalDouble.of(0.25), 5));

        for (RankingEntry different : differentEntries) {
            assertFalse(entry.equals(different));
            assertFalse(different.equals(entry));
        }
    }

    @Test
    public void equals_emptyScoreAndZeroScore_returnsFalse() {
        RankingEntry empty = new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.empty(), 0);
        RankingEntry zero = new RankingEntry(APPLICANT_ID_FIRST, 1, OptionalDouble.of(0), 0);

        assertFalse(empty.equals(zero));
        assertFalse(zero.equals(empty));
    }

    @Test
    public void hashCode_equalEntries_supportsHashSet() {
        for (OptionalDouble score : List.of(OptionalDouble.empty(), OptionalDouble.of(0.25))) {
            RankingEntry first = new RankingEntry(APPLICANT_ID_FIRST, 2, score, 4);
            RankingEntry equal = new RankingEntry(new ApplicantId(APPLICANT_ID_FIRST.value), 2, score, 4);
            Set<RankingEntry> entries = new HashSet<>();

            assertEquals(first.hashCode(), equal.hashCode());
            assertTrue(entries.add(first));
            assertFalse(entries.add(equal));
            assertEquals(1, entries.size());
            assertTrue(entries.contains(
                    new RankingEntry(new ApplicantId(APPLICANT_ID_FIRST.value), 2, score, 4)));

            assertTrue(entries.add(new RankingEntry(APPLICANT_ID_FIRST, 3, score, 4)));
            assertEquals(2, entries.size());
        }
    }

    @Test
    public void toString_completeResult_reportsAllFields() {
        for (OptionalDouble score : List.of(OptionalDouble.empty(), OptionalDouble.of(-0.25))) {
            RankingEntry entry = new RankingEntry(APPLICANT_ID_FIRST, 2, score, 7);
            String representation = entry.toString();

            assertTrue(representation.contains(RankingEntry.class.getName()));
            assertTrue(representation.contains("applicantId=" + APPLICANT_ID_FIRST));
            assertTrue(representation.contains("position=2"));
            assertTrue(representation.contains("score=" + score));
            assertTrue(representation.contains("comparisonCount=7"));
        }
    }
}
