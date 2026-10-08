package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.RecruitDexData;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;

/**
 * An immutable snapshot of RecruitDex application data for JSON storage.
 * The legacy root name and applicants field are retained for existing data files.
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
            try {
                recruitDexData.getApplicantRegistry().add(applicant);
            } catch (DuplicateApplicantException e) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_APPLICANT, e);
            }
        }
        return recruitDexData;
    }

}
