package seedu.recruitdex.model.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.ranking.RankingEntry;

public class PairSelectorTest {
    private static final long SEED = 2103L;
    // Fixed IDs keep the sorted member order, and therefore seeded selection, identical across runs.
    private static final ApplicantId A = new ApplicantId("00000000-0000-0000-0000-00000000000a");
    private static final ApplicantId B = new ApplicantId("00000000-0000-0000-0000-00000000000b");
    private static final ApplicantId C = new ApplicantId("00000000-0000-0000-0000-00000000000c");
    private static final ApplicantId D = new ApplicantId("00000000-0000-0000-0000-00000000000d");
    private static final ApplicantId E = new ApplicantId("00000000-0000-0000-0000-00000000000e");

    private final PairSelector selector = new PairSelector();
    private final PairSelector seeded = new PairSelector(new Random(SEED));

    @Test
    public void selectPair_fewerThanTwoMembers_returnsEmpty() {
        assertTrue(selector.selectPair(Set.of(), List.of(), List.of()).isEmpty());
        assertTrue(selector.selectPair(Set.of(A), List.of(), List.of()).isEmpty());
    }

    @Test
    public void selectPair_twoMembers_alwaysContainsBothDistinctMembers() {
        Set<ApplicantId> members = Set.of(A, B);
        for (int attempt = 0; attempt < 100; attempt++) {
            ComparisonPair pair = selector.selectPair(members, List.of(), List.of()).orElseThrow();
            assertNotEquals(pair.getFirstApplicantId(), pair.getSecondApplicantId());
            assertEquals(members, Set.of(pair.getFirstApplicantId(), pair.getSecondApplicantId()));
        }
    }

    @Test
    public void selectPair_largerPool_containsOnlyDistinctActiveMembersAndPreservesInputs() {
        Set<ApplicantId> members = new HashSet<>(Set.of(A, B, C));
        Set<ApplicantId> originalMembers = Set.copyOf(members);
        List<ComparisonDecision> history = List.of(
                new ComparisonDecision(A, B, A));
        for (int attempt = 0; attempt < 100; attempt++) {
            ComparisonPair pair = selector.selectPair(members, history, List.of()).orElseThrow();
            assertNotEquals(pair.getFirstApplicantId(), pair.getSecondApplicantId());
            assertTrue(members.contains(pair.getFirstApplicantId()));
            assertTrue(members.contains(pair.getSecondApplicantId()));
        }
        assertEquals(originalMembers, members);
        assertEquals(1, history.size());
    }

    @Test
    public void selectPair_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> selector.selectPair(null, List.of(), List.of()));
        assertThrows(NullPointerException.class, () -> selector.selectPair(Set.of(), null, List.of()));
        assertThrows(NullPointerException.class, () -> selector.selectPair(Set.of(), List.of(), null));
        Set<ApplicantId> withNullMember = new HashSet<>();
        withNullMember.add(null);
        assertThrows(NullPointerException.class, () -> selector.selectPair(withNullMember, List.of(), List.of()));
    }

    @Test
    public void constructor_nullRandom_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PairSelector(null));
    }

    @Test
    public void selectPair_sameSeedAndInputs_returnsSamePair() {
        Set<ApplicantId> forward = new LinkedHashSet<>(List.of(A, B, C, D, E));
        Set<ApplicantId> backward = new LinkedHashSet<>(List.of(E, D, C, B, A));
        PairSelector first = new PairSelector(new Random(SEED));
        PairSelector second = new PairSelector(new Random(SEED));
        for (int attempt = 0; attempt < 50; attempt++) {
            assertEquals(first.selectPair(forward, List.of(), List.of()),
                    second.selectPair(backward, List.of(), List.of()));
        }
    }

    @Test
    public void selectPair_unassessedMember_isSelectedMostOften() {
        List<ComparisonDecision> history = new ArrayList<>();
        for (int cycle = 0; cycle < 3; cycle++) {
            history.add(decision(A, B));
            history.add(decision(B, C));
            history.add(decision(C, A));
        }
        int withD = 0;
        for (int attempt = 0; attempt < 500; attempt++) {
            if (contains(select(Set.of(A, B, C, D), history, List.of()), D)) {
                withD++;
            }
        }
        assertTrue(withD > 350, "Unassessed applicant appeared in " + withD + " of 500 pairs");
    }

    @Test
    public void selectPair_lastAnsweredPairWithThreeMembers_isNotRepeated() {
        List<ComparisonDecision> history = List.of(decision(A, C), decision(A, B));
        for (int attempt = 0; attempt < 500; attempt++) {
            assertNotEquals(Set.of(A, B), members(select(Set.of(A, B, C), history, List.of())));
        }
    }

    @Test
    public void selectPair_twoMembers_repeatsLastAnsweredPair() {
        List<ComparisonDecision> history = List.of(decision(A, B));
        for (int attempt = 0; attempt < 100; attempt++) {
            assertEquals(Set.of(A, B), members(select(Set.of(A, B), history, List.of())));
        }
    }

    @Test
    public void selectPair_sideImbalance_placesLessFrequentFirstApplicantFirst() {
        List<ComparisonDecision> history = List.of(decision(A, B), decision(A, B), decision(A, B));
        for (int attempt = 0; attempt < 100; attempt++) {
            assertEquals(B, select(Set.of(A, B), history, List.of()).getFirstApplicantId());
        }
    }

    @Test
    public void selectPair_inactiveParticipantDecisions_areIgnored() {
        ApplicantId inactive = E;
        List<ComparisonDecision> history = new ArrayList<>();
        for (int count = 0; count < 20; count++) {
            history.add(decision(A, inactive));
        }
        int withA = 0;
        for (int attempt = 0; attempt < 600; attempt++) {
            if (contains(select(Set.of(A, B, C), history, List.of()), A)) {
                withA++;
            }
        }
        // Symmetric members each appear in about two thirds of pairs.
        assertTrue(withA > 300 && withA < 500, "A appeared in " + withA + " of 600 pairs");
    }

    @Test
    public void selectPair_closeScores_preferredOverDistantScores() {
        List<RankingEntry> ranking = List.of(entry(C, 1, 5.0), entry(B, 2, 0.1), entry(A, 3, 0.0));
        int closePairs = 0;
        int distantPairs = 0;
        for (int attempt = 0; attempt < 500; attempt++) {
            Set<ApplicantId> pair = members(select(Set.of(A, B, C), List.of(), ranking));
            if (pair.equals(Set.of(A, B))) {
                closePairs++;
            } else if (pair.equals(Set.of(A, C))) {
                distantPairs++;
            }
        }
        assertTrue(closePairs > distantPairs, closePairs + " close vs " + distantPairs + " distant pairs");
    }

    @Test
    public void selectPair_repeatedPair_penalisedButStillPossible() {
        // Every member has two comparisons. A-B was compared twice and C-D (excluded as the last pair) twice,
        // so {A, B} is expected in about 2.6% of selections and the never-compared {A, C} in about 24%.
        List<ComparisonDecision> history = List.of(decision(A, B), decision(A, B), decision(C, D), decision(C, D));
        int repeated = 0;
        int fresh = 0;
        for (int attempt = 0; attempt < 1000; attempt++) {
            Set<ApplicantId> pair = members(select(Set.of(A, B, C, D), history, List.of()));
            if (pair.equals(Set.of(A, B))) {
                repeated++;
            } else if (pair.equals(Set.of(A, C))) {
                fresh++;
            }
        }
        assertTrue(repeated > 0, "Repeated pair was never selected");
        assertTrue(repeated * 4 < fresh, "Repeated pair selected " + repeated + " times vs " + fresh + " fresh");
    }

    @Test
    public void selectPair_rankingMissingMembers_treatsAsDefaultScore() {
        List<RankingEntry> ranking = List.of(entry(A, 1, 1.0));
        ComparisonPair pair = select(Set.of(A, B, C), List.of(), ranking);
        assertNotEquals(pair.getFirstApplicantId(), pair.getSecondApplicantId());
    }

    @Test
    public void selectPair_thousandMembers_completesQuickly() {
        Random random = new Random(SEED);
        List<ApplicantId> ids = new ArrayList<>();
        for (int count = 0; count < 1000; count++) {
            ids.add(new ApplicantId());
        }
        List<ComparisonDecision> history = new ArrayList<>();
        for (int count = 0; count < 5000; count++) {
            int first = random.nextInt(ids.size());
            int second = (first + 1 + random.nextInt(ids.size() - 1)) % ids.size();
            history.add(decision(ids.get(first), ids.get(second)));
        }
        Set<ApplicantId> members = Set.copyOf(ids);

        long start = System.nanoTime();
        for (int attempt = 0; attempt < 100; attempt++) {
            ComparisonPair pair = select(members, history, List.of());
            assertTrue(members.contains(pair.getFirstApplicantId()));
            assertTrue(members.contains(pair.getSecondApplicantId()));
        }
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
        assertTrue(elapsedMillis < 1000, "100 selections took " + elapsedMillis + " ms");
    }

    private ComparisonPair select(Set<ApplicantId> members, List<ComparisonDecision> history,
            List<RankingEntry> ranking) {
        return seeded.selectPair(members, history, ranking).orElseThrow();
    }

    private static ComparisonDecision decision(ApplicantId first, ApplicantId second) {
        return new ComparisonDecision(first, second, first);
    }

    private static RankingEntry entry(ApplicantId applicantId, int position, double score) {
        return new RankingEntry(applicantId, position, OptionalDouble.of(score), 0);
    }

    private static Set<ApplicantId> members(ComparisonPair pair) {
        return Set.of(pair.getFirstApplicantId(), pair.getSecondApplicantId());
    }

    private static boolean contains(ComparisonPair pair, ApplicantId applicantId) {
        return members(pair).contains(applicantId);
    }
}
