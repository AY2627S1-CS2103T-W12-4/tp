package seedu.address.model.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.ApplicantId;

public class ComparisonPairTest {

    private static final String FIRST_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String SECOND_ID = "123e4567-e89b-12d3-a456-426614174001";

    @Test
    public void constructor_validDistinctIds_preservesPresentationOrder() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);

        ComparisonPair pair = new ComparisonPair(firstApplicantId, secondApplicantId);

        assertEquals(firstApplicantId, pair.getFirstApplicantId());
        assertEquals(secondApplicantId, pair.getSecondApplicantId());

        ComparisonPair reversedPair = new ComparisonPair(secondApplicantId, firstApplicantId);
        assertEquals(secondApplicantId, reversedPair.getFirstApplicantId());
        assertEquals(firstApplicantId, reversedPair.getSecondApplicantId());
    }

    @Test
    public void constructor_equalIdValues_throwsIllegalArgumentException() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicantId = new ApplicantId(FIRST_ID);

        assertThrows(IllegalArgumentException.class, "A comparison pair must contain two distinct applicants.", () ->
                new ComparisonPair(firstApplicantId, secondApplicantId));
    }

    @Test
    public void constructor_sameIdObject_throwsIllegalArgumentException() {
        ApplicantId applicantId = new ApplicantId(FIRST_ID);

        assertThrows(IllegalArgumentException.class, () -> new ComparisonPair(applicantId, applicantId));
    }

    @Test
    public void constructor_nullFirstId_throwsNullPointerException() {
        ApplicantId secondApplicantId = new ApplicantId(SECOND_ID);

        assertThrows(NullPointerException.class, () -> new ComparisonPair(null, secondApplicantId));
    }

    @Test
    public void constructor_nullSecondId_throwsNullPointerException() {
        ApplicantId firstApplicantId = new ApplicantId(FIRST_ID);

        assertThrows(NullPointerException.class, () -> new ComparisonPair(firstApplicantId, null));
    }
}
