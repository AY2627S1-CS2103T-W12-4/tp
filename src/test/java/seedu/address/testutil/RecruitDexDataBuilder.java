package seedu.address.testutil;

import seedu.address.model.RecruitDexData;
import seedu.address.model.applicant.Applicant;

/**
 * A utility class to help with building RecruitDexData objects.
 * Example usage: <br>
 *     {@code RecruitDexData ab = new RecruitDexDataBuilder().withApplicant("John", "Doe").build();}
 */
public class RecruitDexDataBuilder {

    private RecruitDexData recruitDexData;

    public RecruitDexDataBuilder() {
        recruitDexData = new RecruitDexData();
    }

    public RecruitDexDataBuilder(RecruitDexData recruitDexData) {
        this.recruitDexData = recruitDexData;
    }

    /**
     * Adds a new {@code Applicant} to the {@code RecruitDexData} that we are building.
     */
    public RecruitDexDataBuilder withApplicant(Applicant applicant) {
        recruitDexData.addApplicant(applicant);
        return this;
    }

    public RecruitDexData build() {
        return recruitDexData;
    }
}
