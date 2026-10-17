package seedu.recruitdex.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.recruitdex.commons.exceptions.IllegalValueException;
import seedu.recruitdex.model.ReadOnlyRecruitDexData;
import seedu.recruitdex.model.RecruitDexData;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.exceptions.DuplicateApplicantException;

/**
 * An immutable snapshot of RecruitDex application data for JSON storage.
 * The root name is used when JSON root wrapping is enabled.
 */
@JsonRootName(value = "recruitdex")
class JsonSerializableRecruitDexData {

    public static final String MESSAGE_DUPLICATE_APPLICANT = "Applicants list contains duplicate applicant(s).";

    private final List<JsonAdaptedApplicant> applicants = new ArrayList<>();
    private final JsonAdaptedComparisonList comparisonList;

    /**
     * Constructs a {@code JsonSerializableRecruitDexData} with the given applicants.
     */
    public JsonSerializableRecruitDexData(List<JsonAdaptedApplicant> applicants) {
        this(applicants, null);
    }

    /** Missing comparison data is accepted for files saved before comparison persistence existed. */
    @JsonCreator
    public JsonSerializableRecruitDexData(@JsonProperty("applicants") List<JsonAdaptedApplicant> applicants,
            @JsonProperty("comparisonList") JsonAdaptedComparisonList comparisonList) {
        this.applicants.addAll(applicants);
        this.comparisonList = comparisonList;
    }

    /**
     * Converts a given {@code ReadOnlyRecruitDexData} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableRecruitDexData}.
     */
    public JsonSerializableRecruitDexData(ReadOnlyRecruitDexData source) {
        applicants.addAll(source.getApplicantList().stream().map(JsonAdaptedApplicant::new)
                .collect(Collectors.toList()));
        comparisonList = new JsonAdaptedComparisonList(source.getComparisonList());
    }

    /**
     * Converts this RecruitDex into the model's {@code RecruitDexData} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public RecruitDexData toModelType() throws IllegalValueException {
        RecruitDexData recruitDexData = new RecruitDexData();
        for (JsonAdaptedApplicant jsonAdaptedApplicant : applicants) {
            Applicant applicant = jsonAdaptedApplicant.toModelType();
            try {
                recruitDexData.getApplicantRegistry().add(applicant);
            } catch (DuplicateApplicantException e) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_APPLICANT, e);
            }
        }
        if (comparisonList != null) {
            comparisonList.restoreTo(recruitDexData.getComparisonList());
        }
        return recruitDexData;
    }

}
