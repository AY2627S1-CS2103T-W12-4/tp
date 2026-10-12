package seedu.address.model.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.ApplicantId;

public class ComparisonDecisionTest {

    private static final String FIRST_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String SECOND_ID = "123e4567-e89b-12d3-a456-426614174001";
    private static final String THIRD_ID = "123e4567-e89b-12d3-a456-426614174002";

    @Test
    public void constructor_validDecisionWithFirstApplicantAsWinner_createsDecision() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);
        Instant timestampBeforeConstruction = Instant.now();

        ComparisonDecision decision = new ComparisonDecision(
                firstApplicantId, secondApplicantId, firstApplicantId);

        Instant timestampAfterConstruction = Instant.now();

        assertNotNull(decision.getDecisionId());
        assertEquals(firstApplicantId, decision.getFirstApplicantId());
        assertEquals(secondApplicantId, decision.getSecondApplicantId());
        assertEquals(firstApplicantId, decision.getWinnerId());
        assertNotNull(decision.getTimestamp());
        assertFalse(decision.getTimestamp().isBefore(timestampBeforeConstruction));
        assertFalse(decision.getTimestamp().isAfter(timestampAfterConstruction));
    }

    @Test
    public void constructor_validDecisionWithSecondApplicantAsWinner_createsDecision() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);

        ComparisonDecision decision = new ComparisonDecision(
                firstApplicantId, secondApplicantId, secondApplicantId);

        assertEquals(firstApplicantId, decision.getFirstApplicantId());
        assertEquals(secondApplicantId, decision.getSecondApplicantId());
        assertEquals(secondApplicantId, decision.getWinnerId());
    }

    @Test
    public void constructor_repeatedComparison_createsDecisionWithNewId() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);

        ComparisonDecision firstDecision = new ComparisonDecision(
                firstApplicantId, secondApplicantId, firstApplicantId);
        ComparisonDecision secondDecision = new ComparisonDecision(
                firstApplicantId, secondApplicantId, firstApplicantId);

        assertNotEquals(firstDecision.getDecisionId(), secondDecision.getDecisionId());
    }

    @Test
    public void constructor_sameApplicantIds_throwsIllegalArgumentException() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(FIRST_ID);

        assertThrows(IllegalArgumentException.class, () ->
                new ComparisonDecision(
                        firstApplicantId, secondApplicantId, firstApplicantId));
    }

    @Test
    public void constructor_winnerNotInPair_throwsIllegalArgumentException() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);
        ApplicantId thirdApplicantId = new ApplicantId(THIRD_ID);

        assertThrows(IllegalArgumentException.class, () ->
                new ComparisonDecision(
                        firstApplicantId, secondApplicantId, thirdApplicantId));
    }

    @Test
    public void constructor_nullFirstApplicantId_throwsNullPointerException() {
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);
        ApplicantId winnerId = new ApplicantId(FIRST_ID);

        assertThrows(NullPointerException.class, () ->
                new ComparisonDecision(null, secondApplicantId, winnerId));
    }

    @Test
    public void constructor_nullSecondApplicantId_throwsNullPointerException() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId winnerId = new ApplicantId(FIRST_ID);

        assertThrows(NullPointerException.class, () ->
                new ComparisonDecision(firstApplicantId, null, winnerId));
    }

    @Test
    public void constructor_nullWinnerId_throwsNullPointerException() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);

        assertThrows(NullPointerException.class, () ->
                new ComparisonDecision(firstApplicantId, secondApplicantId, null));
    }

    @Test
    public void getDecisionId_returnsUuid() {
        ComparisonDecision decision = new ComparisonDecision(
                new ApplicantId(FIRST_ID),
                new ApplicantId(SECOND_ID),
                new ApplicantId(FIRST_ID));

        UUID decisionId = decision.getDecisionId();

        assertNotNull(decisionId);
    }
}
