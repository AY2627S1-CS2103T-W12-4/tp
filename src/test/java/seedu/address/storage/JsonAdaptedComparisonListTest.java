package seedu.address.storage;

import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.CARL;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.comparison.ComparisonDecision;

public class JsonAdaptedComparisonListTest {
    private static final String TIMESTAMP = "2026-10-14T12:00:00Z";
    private static final String DECISION_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final List<String> MEMBERS = List.of(ALICE.getId().value, BENSON.getId().value);

    @TempDir
    public Path testFolder;

    @Test
    public void read_invalidMembers_throwsDataLoadingException() throws Exception {
        for (List<String> members : List.of(List.of("invalid-id"), List.of(CARL.getId().value),
                List.of(ALICE.getId().value, ALICE.getId().value), Arrays.asList((String) null))) {
            assertInvalid(new JsonAdaptedComparisonList(members, List.of(), null));
        }
        assertInvalid(new JsonAdaptedComparisonList(null, List.of(), null));
    }

    @Test
    public void read_invalidPendingPair_throwsDataLoadingException() throws Exception {
        for (JsonAdaptedComparisonPair pair : List.of(
                new JsonAdaptedComparisonPair(ALICE.getId().value, ALICE.getId().value),
                new JsonAdaptedComparisonPair(ALICE.getId().value, CARL.getId().value),
                new JsonAdaptedComparisonPair("invalid-id", BENSON.getId().value),
                new JsonAdaptedComparisonPair(ALICE.getId().value, null))) {
            assertInvalid(new JsonAdaptedComparisonList(MEMBERS, List.of(), pair));
        }
        assertInvalid(new JsonAdaptedComparisonList(List.of(ALICE.getId().value), List.of(),
                new JsonAdaptedComparisonPair(ALICE.getId().value, BENSON.getId().value)));
    }

    @Test
    public void read_invalidDecisionFields_throwsDataLoadingException() throws Exception {
        for (String decisionId : Arrays.asList(null, "invalid-id", "1-1-1-1-1")) {
            assertInvalidDecision(new JsonAdaptedComparisonDecision(decisionId, ALICE.getId().value,
                    BENSON.getId().value, ALICE.getId().value, TIMESTAMP));
        }
        for (String timestamp : Arrays.asList(null, "invalid-date")) {
            assertInvalidDecision(new JsonAdaptedComparisonDecision(DECISION_ID, ALICE.getId().value,
                    BENSON.getId().value, ALICE.getId().value, timestamp));
        }
        for (String participantId : Arrays.asList(null, "invalid-id", CARL.getId().value, ALICE.getId().value)) {
            assertInvalidDecision(new JsonAdaptedComparisonDecision(DECISION_ID, ALICE.getId().value,
                    participantId, ALICE.getId().value, TIMESTAMP));
        }
        for (String winnerId : Arrays.asList(null, "invalid-id", CARL.getId().value)) {
            assertInvalidDecision(new JsonAdaptedComparisonDecision(DECISION_ID, ALICE.getId().value,
                    BENSON.getId().value, winnerId, TIMESTAMP));
        }
    }

    @Test
    public void read_duplicateDecisionIdsAndMissingHistory_throwsDataLoadingException() throws Exception {
        ComparisonDecision decision = new ComparisonDecision(ALICE.getId(), BENSON.getId(), ALICE.getId());
        JsonAdaptedComparisonDecision adapted = new JsonAdaptedComparisonDecision(decision);
        assertInvalid(new JsonAdaptedComparisonList(MEMBERS, List.of(adapted, adapted), null));
        assertInvalid(new JsonAdaptedComparisonList(MEMBERS, null, null));
        assertInvalid(new JsonAdaptedComparisonList(MEMBERS,
                Arrays.asList((JsonAdaptedComparisonDecision) null), null));

        // Even inactive historical participants must still have an applicant record.
        assertInvalid(new JsonAdaptedComparisonList(List.of(), List.of(new JsonAdaptedComparisonDecision(
                UUID.randomUUID().toString(), ALICE.getId().value, CARL.getId().value,
                ALICE.getId().value, TIMESTAMP)), null));
    }

    private void assertInvalidDecision(JsonAdaptedComparisonDecision decision) throws Exception {
        assertInvalid(new JsonAdaptedComparisonList(MEMBERS, List.of(decision), null));
    }

    private void assertInvalid(JsonAdaptedComparisonList comparisons) throws Exception {
        Path file = testFolder.resolve("invalidComparisons.json");
        JsonSerializableRecruitDexData snapshot = new JsonSerializableRecruitDexData(
                List.of(new JsonAdaptedApplicant(ALICE), new JsonAdaptedApplicant(BENSON)), comparisons);
        JsonUtil.saveJsonFile(snapshot, file);
        JsonRecruitDexDataStorage storage = new JsonRecruitDexDataStorage(file);
        assertThrows(DataLoadingException.class, storage::readRecruitDexData);
    }
}
