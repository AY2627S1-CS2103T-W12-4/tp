package seedu.recruitdex.model.comparison;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.ranking.RankingEntry;

/**
 * Selects an ordered pair of distinct active applicants, independently of the ranking algorithm.
 * Applicants with fewer eligible comparisons are preferred, then partners with close scores.
 * Repeated pairs are discouraged but never forbidden, and the pair answered last is not presented again
 * unless it is the only possible pair. The applicant shown first less often is placed first.
 * Only decisions whose two participants are active are considered.
 * Selection does not mutate membership, history, ranking, or the saved pending pair.
 */
public class PairSelector {

    /** How strongly the anchor is pulled towards applicants with few comparisons. */
    private static final double ANCHOR_COVERAGE_EXPONENT = 2.0;
    /** How quickly the weight of a repeatedly compared pair fades. */
    private static final double REPEAT_PENALTY_EXPONENT = 2.0;
    /** Keeps every allowed candidate selectable. */
    private static final double MIN_WEIGHT = 1e-9;

    private final Random random;

    /** Creates a selector using an unseeded source of randomness. */
    public PairSelector() {
        this(new Random());
    }

    /** Creates a selector using {@code random}, allowing reproducible selection in tests. */
    public PairSelector(Random random) {
        this.random = requireNonNull(random);
    }

    /**
     * Returns a pair of distinct active members, or empty when fewer than two applicants are active.
     * Decisions involving inactive applicants are ignored. Members missing from {@code ranking} have score 0.
     */
    public Optional<ComparisonPair> selectPair(Set<ApplicantId> activeMembers,
            List<ComparisonDecision> decisions, List<RankingEntry> ranking) {
        requireAllNonNull(activeMembers, decisions, ranking);
        List<ApplicantId> members = new ArrayList<>(List.copyOf(activeMembers));
        List<ComparisonDecision> decisionSnapshot = List.copyOf(decisions);
        List<RankingEntry> rankingSnapshot = List.copyOf(ranking);
        if (members.size() < 2) {
            return Optional.empty();
        }
        members.sort(Comparator.comparing(applicantId -> applicantId.value));

        SelectionStats stats = SelectionStats.from(members, decisionSnapshot, rankingSnapshot);
        if (members.size() == 2) {
            return Optional.of(orderBySide(members.get(0), members.get(1), stats));
        }

        ApplicantId anchor = sampleAnchor(members, stats);
        ApplicantId partner = samplePartner(anchor, members, stats);
        return Optional.of(orderBySide(anchor, partner, stats));
    }

    private ApplicantId sampleAnchor(List<ApplicantId> members, SelectionStats stats) {
        double[] weights = new double[members.size()];
        for (int index = 0; index < members.size(); index++) {
            int comparisonCount = stats.getComparisonCount(members.get(index));
            weights[index] = 1 / Math.pow(1 + comparisonCount, ANCHOR_COVERAGE_EXPONENT);
        }
        return members.get(sampleIndex(weights));
    }

    private ApplicantId samplePartner(ApplicantId anchor, List<ApplicantId> members, SelectionStats stats) {
        List<ApplicantId> candidates = new ArrayList<>(members);
        candidates.remove(anchor);
        double[] weights = new double[candidates.size()];
        for (int index = 0; index < candidates.size(); index++) {
            weights[index] = partnerWeight(anchor, candidates.get(index), stats);
        }
        return candidates.get(sampleIndex(weights));
    }

    private double partnerWeight(ApplicantId anchor, ApplicantId candidate, SelectionStats stats) {
        if (stats.lastAnsweredPair().equals(Optional.of(PairKey.of(anchor, candidate)))) {
            return 0;
        }
        double gap = stats.getScore(anchor) - stats.getScore(candidate);
        double winProbability = 1 / (1 + Math.exp(-gap));
        double closeness = 4 * winProbability * (1 - winProbability);
        double coverage = 1.0 / (1 + stats.getComparisonCount(candidate));
        double repeat = 1 / Math.pow(1 + stats.getPairCount(anchor, candidate), REPEAT_PENALTY_EXPONENT);
        return Math.max(MIN_WEIGHT, closeness * coverage * repeat);
    }

    /** Places the applicant with the lower first-side imbalance first, breaking ties randomly. */
    private ComparisonPair orderBySide(ApplicantId first, ApplicantId second, SelectionStats stats) {
        int difference = Integer.compare(stats.getSideImbalance(first), stats.getSideImbalance(second));
        boolean shouldSwap = difference > 0 || (difference == 0 && random.nextBoolean());
        return shouldSwap ? new ComparisonPair(second, first) : new ComparisonPair(first, second);
    }

    /** Samples an index with probability proportional to its non-negative weight. */
    private int sampleIndex(double[] weights) {
        double total = 0;
        for (double weight : weights) {
            total += weight;
        }
        assert total > 0 : "At least one candidate must have a positive weight.";

        double target = random.nextDouble() * total;
        int lastPositive = 0;
        for (int index = 0; index < weights.length; index++) {
            if (weights[index] <= 0) {
                continue;
            }
            lastPositive = index;
            target -= weights[index];
            if (target < 0) {
                return index;
            }
        }
        return lastPositive;
    }

    /**
     * Holds eligible-history statistics for one selection, discarded afterwards.
     */
    private record SelectionStats(Map<ApplicantId, Double> scores, Map<ApplicantId, Integer> comparisonCounts,
            Map<ApplicantId, Integer> sideImbalances, Map<PairKey, Integer> pairCounts,
            Optional<PairKey> lastAnsweredPair) {

        static SelectionStats from(List<ApplicantId> members, List<ComparisonDecision> decisions,
                List<RankingEntry> ranking) {
            Set<ApplicantId> active = Set.copyOf(members);
            Map<ApplicantId, Double> scores = new HashMap<>();
            for (RankingEntry entry : ranking) {
                entry.getScore().ifPresent(score -> scores.put(entry.getApplicantId(), score));
            }

            Map<ApplicantId, Integer> counts = new HashMap<>();
            Map<ApplicantId, Integer> imbalances = new HashMap<>();
            Map<PairKey, Integer> pairCounts = new HashMap<>();
            PairKey lastPair = null;
            for (ComparisonDecision decision : decisions) {
                ApplicantId first = requireNonNull(decision.getFirstApplicantId());
                ApplicantId second = requireNonNull(decision.getSecondApplicantId());
                if (!active.contains(first) || !active.contains(second)) {
                    continue;
                }
                counts.merge(first, 1, Integer::sum);
                counts.merge(second, 1, Integer::sum);
                imbalances.merge(first, 1, Integer::sum);
                imbalances.merge(second, -1, Integer::sum);
                lastPair = PairKey.of(first, second);
                pairCounts.merge(lastPair, 1, Integer::sum);
            }
            return new SelectionStats(scores, counts, imbalances, pairCounts, Optional.ofNullable(lastPair));
        }

        double getScore(ApplicantId applicantId) {
            return scores.getOrDefault(applicantId, 0.0);
        }

        int getComparisonCount(ApplicantId applicantId) {
            return comparisonCounts.getOrDefault(applicantId, 0);
        }

        int getSideImbalance(ApplicantId applicantId) {
            return sideImbalances.getOrDefault(applicantId, 0);
        }

        int getPairCount(ApplicantId first, ApplicantId second) {
            return pairCounts.getOrDefault(PairKey.of(first, second), 0);
        }
    }

    /**
     * Identifies an unordered applicant pair, with IDs stored in ascending value order.
     */
    private record PairKey(ApplicantId lower, ApplicantId higher) {
        static PairKey of(ApplicantId first, ApplicantId second) {
            return first.value.compareTo(second.value) <= 0
                    ? new PairKey(first, second)
                    : new PairKey(second, first);
        }
    }
}
