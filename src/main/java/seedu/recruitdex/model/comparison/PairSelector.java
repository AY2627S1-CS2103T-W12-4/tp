package seedu.recruitdex.model.comparison;

import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.ranking.RankingEntry;

/**
 * Selects a random ordered pair of distinct active applicants independently of the ranking algorithm.
 * Selection does not mutate membership, history, ranking, or the saved pending pair.
 * Every ordered pair is equally likely. Previously compared pairs may be selected again.
 */
public class PairSelector {

    /**
     * Returns a random pair, or empty when fewer than two applicants are active.
     * History and ranking are accepted for future selection strategies; this strategy uses membership only.
     */
    public Optional<ComparisonPair> selectPair(Set<ApplicantId> activeMembers,
            List<ComparisonDecision> decisions, List<RankingEntry> ranking) {
        requireAllNonNull(activeMembers, decisions, ranking);
        List<ApplicantId> members = List.copyOf(activeMembers);
        if (members.size() < 2) {
            return Optional.empty();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int firstIndex = random.nextInt(members.size());
        int secondIndex = random.nextInt(members.size() - 1);
        if (secondIndex >= firstIndex) {
            secondIndex++;
        }
        return Optional.of(new ComparisonPair(members.get(firstIndex), members.get(secondIndex)));
    }
}
