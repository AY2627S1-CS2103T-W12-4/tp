package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.getTypicalRecruitDexData;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.testutil.ApplicantBuilder;

public class RecruitDexDataTest {

    private final RecruitDexData recruitDexData = new RecruitDexData();

    @Test
    public void constructor() {
        assertEquals(List.of(), recruitDexData.getApplicantList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> recruitDexData.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyRecruitDexData_replacesData() {
        RecruitDexData newData = getTypicalRecruitDexData();
        recruitDexData.resetData(newData);
        assertEquals(newData, recruitDexData);
    }

    @Test
    public void resetData_withDuplicateApplicants_throwsDuplicateApplicantException() {
        // Two applicants with the same identity fields
        Applicant editedAlice = new ApplicantBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Applicant> newApplicants = List.of(ALICE, editedAlice);
        RecruitDexDataStub newData = new RecruitDexDataStub(newApplicants);

        assertThrows(DuplicateApplicantException.class, () -> recruitDexData.resetData(newData));
    }

    @Test
    public void hasApplicant_nullApplicant_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> recruitDexData.hasApplicant(null));
    }

    @Test
    public void hasApplicant_applicantNotInRecruitDexData_returnsFalse() {
        assertFalse(recruitDexData.hasApplicant(ALICE));
    }

    @Test
    public void hasApplicant_applicantInRecruitDexData_returnsTrue() {
        recruitDexData.addApplicant(ALICE);
        assertTrue(recruitDexData.hasApplicant(ALICE));
    }

    @Test
    public void hasApplicant_applicantWithSameIdentityFieldsInRecruitDexData_returnsTrue() {
        recruitDexData.addApplicant(ALICE);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(recruitDexData.hasApplicant(editedAlice));
    }

    @Test
    public void getApplicantList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> recruitDexData.getApplicantList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = RecruitDexData.class.getCanonicalName() + "{applicants=" + recruitDexData.getApplicantList() + "}";
        assertEquals(expected, recruitDexData.toString());
    }

    /**
     * A stub ReadOnlyRecruitDexData whose applicants list can violate interface constraints.
     */
    private static class RecruitDexDataStub implements ReadOnlyRecruitDexData {
        private final ObservableList<Applicant> applicants = FXCollections.observableArrayList();

        RecruitDexDataStub(Collection<Applicant> applicants) {
            this.applicants.setAll(applicants);
        }

        @Override
        public ObservableList<Applicant> getApplicantList() {
            return applicants;
        }
    }

}
