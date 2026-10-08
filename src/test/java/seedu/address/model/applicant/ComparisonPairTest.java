package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.exceptions.DuplicateApplicantException;

public class ComparisonPairTest {

    private static final String FIRST_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String SECOND_ID = "123e4567-e89b-12d3-a456-426614174001";

    @Test
    public void init_validDistinctIds_returnsComparisonPair() {
        ApplicantId firstApplicant = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicant = new ApplicantId(SECOND_ID);

        ComparisonPair pair = ComparisonPair.init(firstApplicant, secondApplicant);

        assertEquals(firstApplicant, pair.getFirstApplicant());
        assertEquals(secondApplicant, pair.getSecondApplicant());
    }

    @Test
    public void init_sameIds_throwsDuplicateApplicantException() {
        ApplicantId firstApplicant = new ApplicantId(FIRST_ID);
        ApplicantId secondApplicant = new ApplicantId(FIRST_ID);

        assertThrows(DuplicateApplicantException.class, () ->
                ComparisonPair.init(firstApplicant, secondApplicant));
    }
}
