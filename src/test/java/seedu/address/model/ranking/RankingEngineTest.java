package seedu.address.model.ranking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.comparison.ComparisonDecision;

public class RankingEngineTest {

    private static final ApplicantId APPLICANT_ID_A = new ApplicantId("00000000-0000-0000-0000-000000000001");
    private static final ApplicantId APPLICANT_ID_B = new ApplicantId("00000000-0000-0000-0000-000000000002");
    private static final ApplicantId APPLICANT_ID_C = new ApplicantId("00000000-0000-0000-0000-000000000003");
    private static final ApplicantId APPLICANT_ID_D = new ApplicantId("00000000-0000-0000-0000-000000000004");
    private static final ApplicantId APPLICANT_ID_E = new ApplicantId("00000000-0000-0000-0000-000000000005");
    private static final UUID DECISION_ID = UUID.fromString("00000000-0000-0000-0000-000000000100");

    private final RankingEngine engine = new RankingEngine();

    @Test
    public void constructor_initialState_hasEmptyRanking() {
        assertTrue(engine.getRanking().isEmpty());
    }

    @Test
    public void getRanking_existingView_tracksRecalculation() {
        ObservableList<RankingEntry> rankingEntries = engine.getRanking();

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));
        assertSame(rankingEntries, engine.getRanking());
        assertEquals(List.of(createEntry(APPLICANT_ID_A, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_B, 2, -0.405465, 1)), rankingEntries);

        engine.recalculate(Set.of(APPLICANT_ID_B), List.of());
        assertSame(rankingEntries, engine.getRanking());
        assertEquals(List.of(createEntry(APPLICANT_ID_B, 1, 0, 0)), rankingEntries);
    }

    @Test
    public void getRanking_mutations_throwsUnsupportedOperationException() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of());
        ObservableList<RankingEntry> rankingEntries = engine.getRanking();
        List<RankingEntry> originalEntries = List.copyOf(rankingEntries);

        assertThrows(UnsupportedOperationException.class, () ->
                rankingEntries.add(createEntry(APPLICANT_ID_C, 1, 0, 0)));
        assertThrows(UnsupportedOperationException.class, () ->
                rankingEntries.set(0, createEntry(APPLICANT_ID_C, 1, 0, 0)));
        assertThrows(UnsupportedOperationException.class, () -> rankingEntries.remove(0));
        assertThrows(UnsupportedOperationException.class, rankingEntries::clear);
        assertEquals(originalEntries, rankingEntries);
    }

    @Test
    public void recalculate_success_notifiesCompletedResult() {
        ObservableList<RankingEntry> rankingEntries = engine.getRanking();
        List<List<RankingEntry>> notifications = new ArrayList<>();
        rankingEntries.addListener((ListChangeListener<RankingEntry>) change ->
                notifications.add(List.copyOf(rankingEntries)));

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));

        List<RankingEntry> expectedEntries = List.of(
                createEntry(APPLICANT_ID_A, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_C, 2, 0, 0),
                createEntry(APPLICANT_ID_B, 3, -0.405465, 1));
        assertEquals(List.of(expectedEntries), notifications);
    }

    @Test
    public void recalculate_emptyMembership_clearsPreviousRanking() {
        ComparisonDecision judgment = createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A);
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of(judgment));

        engine.recalculate(Set.of(), List.of(judgment));

        assertTrue(engine.getRanking().isEmpty());
    }

    @Test
    public void recalculate_singleMember_returnsNeutralEntry() {
        engine.recalculate(Set.of(APPLICANT_ID_A), List.of());

        assertRanking(createEntry(APPLICANT_ID_A, 1, 0, 0));
    }

    @Test
    public void recalculate_noHistory_returnsTiedIdOrder() {
        engine.recalculate(new LinkedHashSet<>(List.of(APPLICANT_ID_C, APPLICANT_ID_B, APPLICANT_ID_A)),
                List.of());

        assertRanking(createEntry(APPLICANT_ID_A, 1, 0, 0),
                createEntry(APPLICANT_ID_B, 1, 0, 0),
                createEntry(APPLICANT_ID_C, 1, 0, 0));
    }

    @Test
    public void recalculate_singleDecision_matchesAnalyticResult() {
        // For one pair, strengths are 2 * (wins + 1) / (total comparisons + 2).
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));
        assertRanking(createEntry(APPLICANT_ID_A, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_B, 2, -0.405465, 1));

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B)));
        assertRanking(createEntry(APPLICANT_ID_B, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_A, 2, -0.405465, 1));
    }

    @Test
    public void recalculate_repeatedComparisons_countsEveryJudgment() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(
                        createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                        createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));

        assertRanking(createEntry(APPLICANT_ID_A, 1, 0.405465, 2),
                createEntry(APPLICANT_ID_B, 2, -0.693147, 2));
    }

    @Test
    public void recalculate_mixedOutcomes_usesWinsAndLosses() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(
                        createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                        createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B),
                        createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));

        assertRanking(createEntry(APPLICANT_ID_A, 1, 0.182322, 3),
                createEntry(APPLICANT_ID_B, 2, -0.223144, 3));
    }

    @Test
    public void recalculate_transitiveChain_matchesIndependentReference() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C),
                List.of(
                        createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                        createDecision(APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_B)));

        // Reference log strengths from an independent Newton fit: 0.343116336, -0.023608268, -0.487761876.
        assertRanking(createEntry(APPLICANT_ID_A, 1, 0.343116, 1),
                createEntry(APPLICANT_ID_B, 2, -0.023608, 2),
                createEntry(APPLICANT_ID_C, 3, -0.487762, 1));
    }

    @Test
    public void recalculate_balancedCycle_returnsTiedResults() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C),
                List.of(
                        createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                        createDecision(APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_B),
                        createDecision(APPLICANT_ID_C, APPLICANT_ID_A, APPLICANT_ID_C)));

        assertRanking(createEntry(APPLICANT_ID_A, 1, 0, 2),
                createEntry(APPLICANT_ID_B, 1, 0, 2),
                createEntry(APPLICANT_ID_C, 1, 0, 2));
    }

    @Test
    public void recalculate_tiedGroups_usesCompetitionPositions() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_D),
                List.of(
                        createDecision(APPLICANT_ID_C, APPLICANT_ID_A, APPLICANT_ID_C),
                        createDecision(APPLICANT_ID_D, APPLICANT_ID_B, APPLICANT_ID_D)));

        assertRanking(createEntry(APPLICANT_ID_C, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_D, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_A, 3, -0.405465, 1), createEntry(APPLICANT_ID_B, 3, -0.405465, 1));
    }

    @Test
    public void recalculate_equalScoresDifferentCounts_usesIdOrder() {
        List<ComparisonDecision> decisions = new ArrayList<>(List.of(
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B)));
        for (int repetition = 0; repetition < 5; repetition++) {
            decisions.add(createDecision(APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_C));
            decisions.add(createDecision(APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_D));
        }

        engine.recalculate(
                Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_E),
                decisions);

        assertRanking(createEntry(APPLICANT_ID_A, 1, 0, 2),
                createEntry(APPLICANT_ID_B, 1, 0, 2),
                createEntry(APPLICANT_ID_C, 1, 0, 10),
                createEntry(APPLICANT_ID_D, 1, 0, 10), createEntry(APPLICANT_ID_E, 1, 0, 0));
    }

    @Test
    public void recalculate_disconnectedGroups_matchesSeparateFits() {
        ApplicantId isolated = new ApplicantId("00000000-0000-0000-0000-000000000006");
        List<ComparisonDecision> firstDecisions = List.of(
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A));
        List<ComparisonDecision> secondDecisions = List.of(
                createDecision(APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_C),
                createDecision(APPLICANT_ID_D, APPLICANT_ID_E, APPLICANT_ID_D));
        RankingEngine firstEngine = new RankingEngine();
        RankingEngine secondEngine = new RankingEngine();
        firstEngine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), firstDecisions);
        secondEngine.recalculate(Set.of(APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_E), secondDecisions);
        List<ComparisonDecision> combinedDecisions = new ArrayList<>(firstDecisions);
        combinedDecisions.addAll(secondDecisions);

        engine.recalculate(
                Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_D,
                        APPLICANT_ID_E, isolated),
                combinedDecisions);

        for (RankingEntry expectedEntry : firstEngine.getRanking()) {
            assertScoreAndCount(expectedEntry);
        }
        for (RankingEntry expectedEntry : secondEngine.getRanking()) {
            assertScoreAndCount(expectedEntry);
        }
        assertEquals(OptionalDouble.of(0), findEntry(isolated).getScore());
        assertEquals(0, findEntry(isolated).getComparisonCount());
        assertEquals(6, engine.getRanking().size());
    }

    @Test
    public void recalculate_reversedPresentedSides_preservesResult() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));
        List<RankingEntry> originalEntries = List.copyOf(engine.getRanking());

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_B, APPLICANT_ID_A, APPLICANT_ID_A)));

        assertEquals(originalEntries, engine.getRanking());
    }

    @Test
    public void recalculate_shuffledInputs_returnsIdenticalEntries() {
        List<ComparisonDecision> decisions = new ArrayList<>(List.of(
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B),
                createDecision(APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_B),
                createDecision(APPLICANT_ID_A, APPLICANT_ID_C, APPLICANT_ID_A)));
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C), decisions);
        List<RankingEntry> originalEntries = List.copyOf(engine.getRanking());
        Collections.reverse(decisions);
        engine.recalculate(new LinkedHashSet<>(List.of(APPLICANT_ID_C, APPLICANT_ID_B, APPLICANT_ID_A)),
                decisions);

        assertEquals(originalEntries, engine.getRanking());
    }

    @Test
    public void recalculate_inactiveParticipants_excludesEntireDecision() {
        List<ComparisonDecision> inactiveDecisions = List.of(
                createDecision(APPLICANT_ID_C, APPLICANT_ID_A, APPLICANT_ID_C),
                createDecision(APPLICANT_ID_A, APPLICANT_ID_C, APPLICANT_ID_A),
                createDecision(APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_C));
        for (ComparisonDecision inactive : inactiveDecisions) {
            engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of(inactive));

            assertRanking(createEntry(APPLICANT_ID_A, 1, 0, 0), createEntry(APPLICANT_ID_B, 1, 0, 0));
        }
    }

    @Test
    public void recalculate_removeAndReactivate_rebuildsResults() {
        List<ComparisonDecision> decisions = List.of(
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                createDecision(APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_B));
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C), decisions);
        List<RankingEntry> originalEntries = List.copyOf(engine.getRanking());

        engine.recalculate(Set.of(APPLICANT_ID_B, APPLICANT_ID_C), decisions);
        assertRanking(createEntry(APPLICANT_ID_B, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_C, 2, -0.405465, 1));

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C), decisions);
        assertEquals(originalEntries, engine.getRanking());
    }

    @Test
    public void recalculate_repeatedCalls_doesNotAccumulateState() {
        List<ComparisonDecision> decisions = List.of(
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A));
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), decisions);
        List<RankingEntry> originalEntries = List.copyOf(engine.getRanking());

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), decisions);
        assertEquals(originalEntries, engine.getRanking());

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B)));
        assertRanking(createEntry(APPLICANT_ID_B, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_A, 2, -0.405465, 1));
    }

    @Test
    public void recalculate_equivalentIdObjects_resolvesByValue() {
        ComparisonDecision judgment = createDecision(
                new ApplicantId(APPLICANT_ID_A.value), new ApplicantId(APPLICANT_ID_B.value),
                new ApplicantId(APPLICANT_ID_B.value));

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of(judgment));

        assertRanking(createEntry(APPLICANT_ID_B, 1, 0.287682, 1),
                createEntry(APPLICANT_ID_A, 2, -0.405465, 1));
    }

    @Test
    public void recalculate_inputCollections_remainIndependent() {
        Set<ApplicantId> members = new HashSet<>(List.of(APPLICANT_ID_A, APPLICANT_ID_B));
        List<ComparisonDecision> decisions = new ArrayList<>(List.of(
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));
        Set<ApplicantId> originalMembers = Set.copyOf(members);
        List<ComparisonDecision> originalDecisions = List.copyOf(decisions);

        engine.recalculate(members, decisions);
        List<RankingEntry> originalEntries = List.copyOf(engine.getRanking());
        assertEquals(originalMembers, members);
        assertEquals(originalDecisions, decisions);

        members.clear();
        decisions.clear();
        assertEquals(originalEntries, engine.getRanking());
    }

    @Test
    public void recalculate_previousEntries_remainUnchanged() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));
        RankingEntry originalEntry = findEntry(APPLICANT_ID_A);

        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B)));

        assertEquals(createEntry(APPLICANT_ID_A, 1, 0.287682, 1), originalEntry);
        assertEquals(createEntry(APPLICANT_ID_A, 2, -0.405465, 1), findEntry(APPLICANT_ID_A));
    }

    @Test
    public void recalculate_nullInputs_preservesRanking() {
        assertRejectedWithoutPublication(NullPointerException.class, () ->
                engine.recalculate(null, List.of()));
        assertRejectedWithoutPublication(NullPointerException.class, () ->
                engine.recalculate(Set.of(APPLICANT_ID_A), null));
        Set<ApplicantId> membersWithNull = new HashSet<>(Arrays.asList(APPLICANT_ID_A, null));
        assertRejectedWithoutPublication(NullPointerException.class, () ->
                engine.recalculate(membersWithNull, List.of()));
        List<ComparisonDecision> decisionsWithNull = Arrays.asList(
                createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A), null);
        assertRejectedWithoutPublication(NullPointerException.class, () ->
                engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), decisionsWithNull));
    }

    @Test
    public void recalculate_nullDecisionFields_preservesRanking() {
        List<ComparisonDecision> malformedDecisions = List.of(
                new StubDecision(null, APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A),
                new StubDecision(DECISION_ID, null, APPLICANT_ID_B, APPLICANT_ID_A),
                new StubDecision(DECISION_ID, APPLICANT_ID_A, null, APPLICANT_ID_A),
                new StubDecision(DECISION_ID, APPLICANT_ID_A, APPLICANT_ID_B, null));
        for (ComparisonDecision judgment : malformedDecisions) {
            assertRejectedWithoutPublication(NullPointerException.class, () ->
                    engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of(judgment)));
        }
    }

    @Test
    public void recalculate_malformedDecision_preservesRanking() {
        for (ComparisonDecision judgment : List.of(
                new StubDecision(DECISION_ID, APPLICANT_ID_A, APPLICANT_ID_A, APPLICANT_ID_A),
                new StubDecision(DECISION_ID, APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C))) {
            assertRejectedWithoutPublication(IllegalArgumentException.class, () ->
                    engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C),
                            List.of(judgment)));
        }
    }

    @Test
    public void recalculate_duplicateDecisionId_preservesRanking() {
        ComparisonDecision judgment = createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A);
        ComparisonDecision sameId = new StubDecision(
                judgment.getDecisionId(), APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B);

        assertRejectedWithoutPublication(IllegalArgumentException.class, () ->
                engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of(judgment, judgment)));
        assertRejectedWithoutPublication(IllegalArgumentException.class, () ->
                engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of(judgment, sameId)));
    }

    @Test
    public void recalculate_inactiveMalformedHistory_stillRejects() {
        ComparisonDecision invalidWinner = new StubDecision(
                DECISION_ID, APPLICANT_ID_D, APPLICANT_ID_E, APPLICANT_ID_A);
        assertRejectedWithoutPublication(IllegalArgumentException.class, () ->
                engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), List.of(invalidWinner)));

        List<ComparisonDecision> duplicateInactiveDecisions = List.of(
                new StubDecision(DECISION_ID, APPLICANT_ID_D, APPLICANT_ID_E, APPLICANT_ID_D),
                new StubDecision(DECISION_ID, APPLICANT_ID_D, APPLICANT_ID_E, APPLICANT_ID_E));
        assertRejectedWithoutPublication(IllegalArgumentException.class, () ->
                engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B), duplicateInactiveDecisions));
    }

    @Test
    public void recalculate_slowConvergence_returnsCompleteResult() {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_D),
                createConnectedHistory(200));

        // Independent Newton reference; this graph needs residual checks after small parameter changes.
        assertRanking(createEntry(APPLICANT_ID_A, 1, 0.183155, 401),
                createEntry(APPLICANT_ID_B, 2, 0.181169, 400),
                createEntry(APPLICANT_ID_D, 3, -0.221910, 400),
                createEntry(APPLICANT_ID_C, 4, -0.223901, 401));
    }

    @Test
    public void recalculate_iterationLimit_preservesRanking() {
        List<ComparisonDecision> decisions = createConnectedHistory(1000);
        assertRejectedWithoutPublication(IllegalStateException.class, () ->
                engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_D),
                        decisions),
                "Bradley-Terry ranking did not converge within the iteration limit.");
    }

    @Test
    public void requirePositiveFinite_invalidValues_throws() throws ReflectiveOperationException {
        Method method = getPrivateMethod("requirePositiveFinite", double.class);
        double[] invalidValues = {0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (double value : invalidValues) {
            assertThrows(IllegalStateException.class, () -> invokePrivate(method, value));
        }
        for (double value : new double[] {Double.MIN_VALUE, 1, Double.MAX_VALUE}) {
            invokePrivate(method, value);
        }
    }

    @Test
    public void hasConverged_nonFiniteRelativeChange_throws() throws ReflectiveOperationException {
        Object data = createEmptyComparisonData(List.of(APPLICANT_ID_A));
        Method method = getPrivateMethod("hasConverged", data.getClass(), double[].class, double[].class);

        // Positive finite values can still overflow when their relative difference is calculated.
        assertThrows(IllegalStateException.class, () ->
                invokePrivate(method, data, new double[] {Double.MIN_VALUE}, new double[] {1}));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createEntries_nearZeroScores_roundsAndGroupsCorrectly() throws ReflectiveOperationException {
        Object data = createEmptyComparisonData(
                List.of(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_C, APPLICANT_ID_D));
        Method method = getPrivateMethod("createEntries", data.getClass(), double[].class);
        double[] strengths = {StrictMath.exp(0.00000049), StrictMath.exp(0.00000051),
                StrictMath.exp(-0.00000049), StrictMath.exp(-0.00000051)};

        List<RankingEntry> entries = (List<RankingEntry>) invokePrivate(method, data, strengths);

        assertEquals(List.of(createEntry(APPLICANT_ID_B, 1, 0.000001, 0),
                createEntry(APPLICANT_ID_A, 2, 0, 0),
                createEntry(APPLICANT_ID_C, 2, 0, 0), createEntry(APPLICANT_ID_D, 4, -0.000001, 0)), entries);
        assertEquals(Double.doubleToLongBits(0.0),
                Double.doubleToLongBits(entries.get(2).getScore().orElseThrow()));
    }

    private static ComparisonDecision createDecision(ApplicantId first, ApplicantId second,
            ApplicantId winner) {
        return new ComparisonDecision(first, second, winner);
    }

    private static RankingEntry createEntry(ApplicantId applicantId, int position, double score, int count) {
        return new RankingEntry(applicantId, position, OptionalDouble.of(score), count);
    }

    private void assertRanking(RankingEntry... expectedEntries) {
        assertEquals(List.of(expectedEntries), engine.getRanking());
    }

    private RankingEntry findEntry(ApplicantId applicantId) {
        return engine.getRanking().stream().filter(entry -> entry.getApplicantId().equals(applicantId))
                .findFirst().orElseThrow();
    }

    private void assertScoreAndCount(RankingEntry expectedEntry) {
        RankingEntry actual = findEntry(expectedEntry.getApplicantId());
        assertEquals(expectedEntry.getScore(), actual.getScore());
        assertEquals(expectedEntry.getComparisonCount(), actual.getComparisonCount());
    }

    private void assertRejectedWithoutPublication(Class<? extends Throwable> exceptionType,
            Executable operation) {
        assertRejectedWithoutPublication(exceptionType, operation, null);
    }

    private void assertRejectedWithoutPublication(Class<? extends Throwable> exceptionType,
            Executable operation, String message) {
        engine.recalculate(Set.of(APPLICANT_ID_A, APPLICANT_ID_B),
                List.of(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A)));
        ObservableList<RankingEntry> rankingEntries = engine.getRanking();
        List<RankingEntry> originalEntries = List.copyOf(rankingEntries);
        AtomicInteger notifications = new AtomicInteger();
        ListChangeListener<RankingEntry> listener = change -> notifications.incrementAndGet();
        rankingEntries.addListener(listener);

        try {
            if (message == null) {
                assertThrows(exceptionType, operation);
            } else {
                assertThrows(exceptionType, message, operation);
            }
            assertSame(rankingEntries, engine.getRanking());
            assertEquals(originalEntries, rankingEntries);
            assertEquals(0, notifications.get());
        } finally {
            rankingEntries.removeListener(listener);
        }
    }

    private static List<ComparisonDecision> createConnectedHistory(int repetitions) {
        List<ComparisonDecision> decisions = new ArrayList<>();
        for (int repetition = 0; repetition < repetitions; repetition++) {
            decisions.add(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A));
            decisions.add(createDecision(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_B));
            decisions.add(createDecision(APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_C));
            decisions.add(createDecision(APPLICANT_ID_C, APPLICANT_ID_D, APPLICANT_ID_D));
        }
        decisions.add(createDecision(APPLICANT_ID_A, APPLICANT_ID_C, APPLICANT_ID_A));
        return decisions;
    }

    private static Method getPrivateMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        Method method = RankingEngine.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    private static Object createEmptyComparisonData(List<ApplicantId> applicantIds)
            throws ReflectiveOperationException {
        Class<?> dataType = Class.forName(RankingEngine.class.getName() + "$ComparisonData");
        Constructor<?> constructor = dataType.getDeclaredConstructor(
                List.class, List.class, int[].class, int[].class);
        constructor.setAccessible(true);
        return constructor.newInstance(applicantIds, List.of(),
                new int[applicantIds.size()], new int[applicantIds.size()]);
    }

    private static Object invokePrivate(Method method, Object... arguments) {
        try {
            return method.invoke(null, arguments);
        } catch (InvocationTargetException exception) {
            if (exception.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            if (exception.getCause() instanceof Error cause) {
                throw cause;
            }
            throw new AssertionError(exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static class StubDecision extends ComparisonDecision {
        private final UUID decisionId;
        private final ApplicantId firstApplicantId;
        private final ApplicantId secondApplicantId;
        private final ApplicantId winnerId;

        private StubDecision(UUID decisionId, ApplicantId first, ApplicantId second, ApplicantId winner) {
            super(APPLICANT_ID_A, APPLICANT_ID_B, APPLICANT_ID_A);
            this.decisionId = decisionId;
            this.firstApplicantId = first;
            this.secondApplicantId = second;
            this.winnerId = winner;
        }

        @Override
        public UUID getDecisionId() {
            return decisionId;
        }

        @Override
        public ApplicantId getFirstApplicantId() {
            return firstApplicantId;
        }

        @Override
        public ApplicantId getSecondApplicantId() {
            return secondApplicantId;
        }

        @Override
        public ApplicantId getWinnerId() {
            return winnerId;
        }
    }
}
