package seedu.address.storage;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.comparison.ComparisonDecision;

/** Jackson-friendly snapshot preserving a judgment's identity, timestamp, and presented sides. */
class JsonAdaptedComparisonDecision {
    private final String decisionId;
    private final String firstApplicantId;
    private final String secondApplicantId;
    private final String winnerId;
    private final String timestamp;

    @JsonCreator
    public JsonAdaptedComparisonDecision(@JsonProperty("decisionId") String decisionId,
            @JsonProperty("firstApplicantId") String firstApplicantId,
            @JsonProperty("secondApplicantId") String secondApplicantId,
            @JsonProperty("winnerId") String winnerId, @JsonProperty("timestamp") String timestamp) {
        this.decisionId = decisionId;
        this.firstApplicantId = firstApplicantId;
        this.secondApplicantId = secondApplicantId;
        this.winnerId = winnerId;
        this.timestamp = timestamp;
    }

    public JsonAdaptedComparisonDecision(ComparisonDecision decision) {
        this(decision.getDecisionId().toString(), decision.getFirstApplicantId().value,
                decision.getSecondApplicantId().value, decision.getWinnerId().value,
                decision.getTimestamp().toString());
    }

    /** Restores a judgment without generating a new ID or timestamp. */
    public ComparisonDecision toModelType() {
        requireAllNonNull(decisionId, timestamp);
        UUID modelDecisionId = UUID.fromString(decisionId);
        checkArgument(modelDecisionId.toString().equalsIgnoreCase(decisionId), "Decision IDs must be full UUIDs.");
        return new ComparisonDecision(modelDecisionId, new ApplicantId(firstApplicantId),
                new ApplicantId(secondApplicantId), new ApplicantId(winnerId), Instant.parse(timestamp));
    }
}
