package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.getTypicalRecruitDexData;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.model.comparison.ComparisonPair;
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
                + "{applicants=" + recruitDexData.getApplicantList()
                + ", comparisonState=" + recruitDexData.getComparisonState() + "}";
        assertEquals(expected, recruitDexData.toString());
    }

    @Test
    public void getComparisonState_startsWithNoPair() {
        assertEquals(Optional.empty(), recruitDexData.getComparisonState().getCurrentPair());
    }

    @Test
    public void getComparisonState_validatesAgainstTheDataRegistry() {
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        assertThrows(ApplicantNotFoundException.class, () -> recruitDexData.getComparisonState().setCurrentPair(pair));

        recruitDexData.getApplicantRegistry().add(ALICE);
        recruitDexData.getApplicantRegistry().add(BENSON);
        recruitDexData.getComparisonState().setCurrentPair(pair);

        assertEquals(Optional.of(pair), recruitDexData.getComparisonState().getCurrentPair());
    }

    @Test
    public void getComparisonState_returnsTheSameLiveObjectEachTime() {
        assertSame(recruitDexData.getComparisonState(), recruitDexData.getComparisonState());
    }

    @Test
    public void resetData_clearsPendingPair() {
        RecruitDexData data = getTypicalRecruitDexData();
        data.getComparisonState().setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));

        data.resetData(getTypicalRecruitDexData());

        assertEquals(Optional.empty(), data.getComparisonState().getCurrentPair());
    }

    @Test
    public void resetData_invalidData_keepsPendingPair() {
        RecruitDexData data = getTypicalRecruitDexData();
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        data.getComparisonState().setCurrentPair(pair);
        List<Applicant> duplicates = List.of(ALICE, ALICE);
        ReadOnlyRecruitDexData invalid = new ReadOnlyRecruitDexData() {
            @Override
            public ObservableList<Applicant> getApplicantList() {
                return FXCollections.observableArrayList(duplicates);
            }
        };

        assertThrows(DuplicateApplicantException.class, () -> data.resetData(invalid));

        assertEquals(Optional.of(pair), data.getComparisonState().getCurrentPair());
    }

    @Test
    public void copyConstructor_doesNotCopyPendingPair() {
        RecruitDexData source = getTypicalRecruitDexData();
        source.getComparisonState().setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));

        RecruitDexData copy = new RecruitDexData(source);

        assertEquals(Optional.empty(), copy.getComparisonState().getCurrentPair());
        assertNotSame(source.getComparisonState(), copy.getComparisonState());
    }

    @Test
    public void equalsAndHashCode_takePendingPairIntoAccount() {
        RecruitDexData first = getTypicalRecruitDexData();
        RecruitDexData second = getTypicalRecruitDexData();
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        first.getComparisonState().setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        assertNotEquals(first, second);

        second.getComparisonState().setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
