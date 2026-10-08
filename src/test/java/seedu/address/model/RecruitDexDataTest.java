package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.getTypicalRecruitDexData;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.testutil.ApplicantBuilder;

public class RecruitDexDataTest {

    private final RecruitDexData recruitDexData = new RecruitDexData();

    @Test
    public void constructor() {
        assertEquals(List.of(), recruitDexData.getApplicantList());
    }

    @Test
    public void constructor_validData_copiesRegistryAndPreservesIds() {
        RecruitDexData source = getTypicalRecruitDexData();
        RecruitDexData copy = new RecruitDexData(source);

        assertEquals(source, copy);
        assertNotSame(source.getApplicantRegistry(), copy.getApplicantRegistry());
        assertEquals(source.getApplicantList().get(0).getId(), copy.getApplicantList().get(0).getId());
        copy.getApplicantRegistry().delete(ALICE.getId());
        assertSame(ALICE, source.getApplicantRegistry().get(ALICE.getId()));
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> recruitDexData.resetData(null));
    }

    @Test
    public void resetData_validData_retainsRegistryAndObservableView() {
        ApplicantRegistry registry = recruitDexData.getApplicantRegistry();
        ObservableList<Applicant> view = recruitDexData.getApplicantList();
        RecruitDexData newData = getTypicalRecruitDexData();

        recruitDexData.resetData(newData);

        assertEquals(newData, recruitDexData);
        assertSame(registry, recruitDexData.getApplicantRegistry());
        assertSame(view, recruitDexData.getApplicantList());
        assertEquals(newData.getApplicantList(), view);
    }

    @Test
    public void resetData_duplicateIds_rejectsReplacementWithoutChangingRecords() {
        recruitDexData.getApplicantRegistry().add(BENSON);
        Applicant conflictingId = new ApplicantBuilder(ALICE).withName("Another applicant").build();
        ReadOnlyRecruitDexData invalidData = () -> FXCollections.observableArrayList(ALICE, conflictingId);

        assertThrows(DuplicateApplicantException.class, () -> recruitDexData.resetData(invalidData));
        assertEquals(List.of(BENSON), recruitDexData.getApplicantList());
    }

    @Test
    public void resetData_ownData_preservesRecords() {
        recruitDexData.getApplicantRegistry().add(ALICE);
        recruitDexData.resetData(recruitDexData);
        assertEquals(List.of(ALICE), recruitDexData.getApplicantList());
    }

    @Test
    public void getApplicantList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> recruitDexData.getApplicantList().add(ALICE));
    }

    @Test
    public void toStringMethod() {
        String expected = RecruitDexData.class.getCanonicalName()
                + "{applicants=" + recruitDexData.getApplicantList() + "}";
        assertEquals(expected, recruitDexData.toString());
    }
}
