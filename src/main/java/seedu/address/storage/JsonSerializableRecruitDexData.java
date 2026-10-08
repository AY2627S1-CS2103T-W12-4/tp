package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.RecruitDexData;
import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.applicant.Applicant;

/**
 * An Immutable RecruitDexData that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableRecruitDexData {

    public static final String MESSAGE_DUPLICATE_APPLICANT = "Applicants list contains duplicate applicant(s).";

    private final List<JsonAdaptedApplicant> applicants = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableRecruitDexData} with the given applicants.
     */
    @JsonCreator
    public JsonSerializableRecruitDexData(@JsonProperty("applicants") List<JsonAdaptedApplicant> applicants) {
        this.applicants.addAll(applicants);
    }

    /**
     * Converts a given {@code ReadOnlyRecruitDexData} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableRecruitDexData}.
     */
    public JsonSerializableRecruitDexData(ReadOnlyRecruitDexData source) {
        applicants.addAll(source.getApplicantList().stream().map(JsonAdaptedApplicant::new)
                .collect(Collectors.toList()));
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
            if (recruitDexData.hasApplicant(applicant)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_APPLICANT);
            }
            recruitDexData.addApplicant(applicant);
        }
        return recruitDexData;
    }

}
