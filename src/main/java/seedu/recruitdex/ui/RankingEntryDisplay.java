package seedu.recruitdex.ui;

import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;

import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.ranking.RankingEntry;

/**
 * Holds the text and first-place styling flag for one leaderboard row.
 * Formatting preserves the engine's position and resolves the current applicant name by ID.
 */
record RankingEntryDisplay(String positionText, String nameText, String metadataText,
                           String applicantIndexText, boolean isFirstPosition) {

    /**
     * Formats an entry using its current applicant record, or an unavailable-name fallback.
     * Absent scores and unassessed applicants remain distinct from numerical zero scores.
     *
     * @throws NullPointerException if any argument is null.
     */
    static RankingEntryDisplay create(RankingEntry entry, Map<ApplicantId, Applicant> applicantsById,
                                      List<Applicant> displayedApplicants) {
        requireAllNonNull(entry, applicantsById, displayedApplicants);
        Applicant applicant = applicantsById.get(entry.getApplicantId());
        String nameText = applicant == null ? "Applicant unavailable" : applicant.getName().fullName;
        OptionalDouble score = entry.getScore();
        String scoreText = score.isPresent() ? String.format(Locale.ROOT, "%.6f", score.getAsDouble()) : "—";
        int comparisonCount = entry.getComparisonCount();
        String comparisonText = comparisonCount + (comparisonCount == 1 ? " comparison" : " comparisons");
        if (comparisonCount == 0) {
            comparisonText += " · Unassessed";
        }

        return new RankingEntryDisplay("Rank " + entry.getPosition(), nameText,
                "Score: " + scoreText + " · " + comparisonText,
                getApplicantIndexText(entry.getApplicantId(), displayedApplicants), entry.getPosition() == 1);
    }

    /** Returns the command index from the current applicant results, independently of rank. */
    private static String getApplicantIndexText(ApplicantId applicantId, List<Applicant> displayedApplicants) {
        for (int i = 0; i < displayedApplicants.size(); i++) {
            if (displayedApplicants.get(i).getId().equals(applicantId)) {
                return "Applicant index: " + (i + 1);
            }
        }
        return "Not in current applicant results";
    }
}
