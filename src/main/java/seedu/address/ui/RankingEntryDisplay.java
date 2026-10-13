package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;

import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.ranking.RankingEntry;

/**
 * Holds the text and first-place styling flag for one leaderboard row.
 * Formatting preserves the engine's position and resolves the current applicant name by ID.
 */
record RankingEntryDisplay(String positionText, String nameText, String metadataText, boolean isFirstPosition) {

    /**
     * Formats an entry using its current applicant record, or an unavailable-name fallback.
     * Absent scores and unassessed applicants remain distinct from numerical zero scores.
     *
     * @throws NullPointerException if {@code entry} or {@code applicantsById} is null.
     */
    static RankingEntryDisplay create(RankingEntry entry, Map<ApplicantId, Applicant> applicantsById) {
        requireAllNonNull(entry, applicantsById);
        Applicant applicant = applicantsById.get(entry.getApplicantId());
        String nameText = applicant == null ? "Applicant unavailable" : applicant.getName().fullName;
        OptionalDouble score = entry.getScore();
        String scoreText = score.isPresent() ? String.format(Locale.ROOT, "%.6f", score.getAsDouble()) : "—";
        int comparisonCount = entry.getComparisonCount();
        String comparisonText = comparisonCount + (comparisonCount == 1 ? " comparison" : " comparisons");
        if (comparisonCount == 0) {
            comparisonText += " · Unassessed";
        }

        return new RankingEntryDisplay(entry.getPosition() + ".", nameText,
                "Score: " + scoreText + " · " + comparisonText, entry.getPosition() == 1);
    }
}
