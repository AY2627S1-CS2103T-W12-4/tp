package seedu.recruitdex.storage;

import static seedu.recruitdex.commons.util.AppUtil.checkArgument;
import static seedu.recruitdex.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.recruitdex.commons.exceptions.IllegalValueException;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.recruitdex.model.comparison.ComparisonDecision;
import seedu.recruitdex.model.comparison.ComparisonList;
import seedu.recruitdex.model.comparison.ComparisonPair;
import seedu.recruitdex.model.comparison.ReadOnlyComparisonList;

/** Immutable storage snapshot of membership, ordered history, and the pending pair. */
class JsonAdaptedComparisonList {
    private final List<String> memberIds;
    private final List<JsonAdaptedComparisonDecision> decisionHistory;
    private final JsonAdaptedComparisonPair currentPair;

    @JsonCreator
    public JsonAdaptedComparisonList(@JsonProperty("memberIds") List<String> memberIds,
            @JsonProperty("decisionHistory") List<JsonAdaptedComparisonDecision> decisionHistory,
            @JsonProperty("currentPair") JsonAdaptedComparisonPair currentPair) {
        this.memberIds = memberIds == null ? null : new ArrayList<>(memberIds);
        this.decisionHistory = decisionHistory == null ? null : new ArrayList<>(decisionHistory);
        this.currentPair = currentPair;
    }

    public JsonAdaptedComparisonList(ReadOnlyComparisonList source) {
        memberIds = source.getMemberIds().stream().map(id -> id.value).sorted().toList();
        decisionHistory = source.getDecisionHistory().stream().map(JsonAdaptedComparisonDecision::new).toList();
        currentPair = source.getCurrentPair().map(JsonAdaptedComparisonPair::new).orElse(null);
    }

    /** Restores validated comparisons into an existing list and rebuilds its ranking. */
    public void restoreTo(ComparisonList target) throws IllegalValueException {
        try {
            requireAllNonNull(memberIds, decisionHistory);
            Set<ApplicantId> members = new HashSet<>();
            for (String memberId : memberIds) {
                checkArgument(members.add(new ApplicantId(memberId)), "Comparison member IDs must be unique.");
            }
            List<ComparisonDecision> decisions = decisionHistory.stream()
                    .map(JsonAdaptedComparisonDecision::toModelType).toList();
            Optional<ComparisonPair> pair = Optional.ofNullable(currentPair)
                    .map(JsonAdaptedComparisonPair::toModelType);
            target.resetData(members, decisions, pair);
        } catch (IllegalArgumentException | NullPointerException | ApplicantNotFoundException
                | DateTimeException | IllegalStateException e) {
            throw new IllegalValueException("Invalid comparison data: " + e.getMessage(), e);
        }
    }
}
