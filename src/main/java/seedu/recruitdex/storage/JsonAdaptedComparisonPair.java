package seedu.recruitdex.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.comparison.ComparisonPair;

/** Jackson-friendly snapshot of the ordered sides of a pending comparison. */
class JsonAdaptedComparisonPair {
    private final String firstApplicantId;
    private final String secondApplicantId;

    @JsonCreator
    public JsonAdaptedComparisonPair(@JsonProperty("firstApplicantId") String firstApplicantId,
            @JsonProperty("secondApplicantId") String secondApplicantId) {
        this.firstApplicantId = firstApplicantId;
        this.secondApplicantId = secondApplicantId;
    }

    public JsonAdaptedComparisonPair(ComparisonPair pair) {
        this(pair.getFirstApplicantId().value, pair.getSecondApplicantId().value);
    }

    /** Restores and validates the pair's IDs and distinct sides. */
    public ComparisonPair toModelType() {
        return new ComparisonPair(new ApplicantId(firstApplicantId), new ApplicantId(secondApplicantId));
    }
}
