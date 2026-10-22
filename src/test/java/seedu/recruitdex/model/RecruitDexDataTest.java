package seedu.recruitdex.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.recruitdex.testutil.Assert.assertThrows;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalRecruitDexData;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantRegistry;
import seedu.recruitdex.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.recruitdex.model.applicant.exceptions.DuplicateApplicantException;
import seedu.recruitdex.model.comparison.ComparisonPair;
import seedu.recruitdex.testutil.ApplicantBuilder;

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
        ReadOnlyRecruitDexData invalidData = new RecruitDexData() {
            @Override
            public ObservableList<Applicant> getApplicantList() {
                return FXCollections.observableArrayList(ALICE, conflictingId);
            }
        };

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
                + ", comparisonList=" + recruitDexData.getComparisonList() + "}";
        assertEquals(expected, recruitDexData.toString());
    }

    @Test
    public void getComparisonState_startsWithNoPair() {
        assertEquals(Optional.empty(), recruitDexData.getComparisonList().getCurrentPair());
    }

    @Test
    public void comparisonResetData_validatesAgainstTheDataRegistry() {
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        assertThrows(ApplicantNotFoundException.class, () ->
                recruitDexData.getComparisonList().resetData(
                        Set.of(ALICE.getId(), BENSON.getId()), List.of(), Optional.of(pair)));

        recruitDexData.getApplicantRegistry().add(ALICE);
        recruitDexData.getApplicantRegistry().add(BENSON);
        recruitDexData.getComparisonList().resetData(
                Set.of(ALICE.getId(), BENSON.getId()), List.of(), Optional.of(pair));

        assertEquals(Optional.of(pair), recruitDexData.getComparisonList().getCurrentPair());
    }

    @Test
    public void getComparisonState_returnsTheSameLiveObjectEachTime() {
        assertSame(recruitDexData.getComparisonList().getComparisonState(),
                recruitDexData.getComparisonList().getComparisonState());
    }

    @Test
    public void resetData_clearsPendingPair() {
        RecruitDexData data = getTypicalRecruitDexData();
        data.getComparisonList().add(ALICE.getId());
        data.getComparisonList().add(BENSON.getId());
        data.getComparisonList().resetData(
                Set.copyOf(data.getComparisonList().getMemberIds()),
                List.copyOf(data.getComparisonList().getDecisionHistory()),
                Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())));

        data.resetData(getTypicalRecruitDexData());

        assertEquals(Optional.empty(), data.getComparisonList().getCurrentPair());
    }

    @Test
    public void resetData_invalidData_keepsPendingPair() {
        RecruitDexData data = getTypicalRecruitDexData();
        data.getComparisonList().add(ALICE.getId());
        data.getComparisonList().add(BENSON.getId());
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        data.getComparisonList().resetData(
                Set.copyOf(data.getComparisonList().getMemberIds()),
                List.copyOf(data.getComparisonList().getDecisionHistory()),
                Optional.of(pair));
        List<Applicant> duplicates = List.of(ALICE, ALICE);
        ReadOnlyRecruitDexData invalid = new RecruitDexData() {
            @Override
            public ObservableList<Applicant> getApplicantList() {
                return FXCollections.observableArrayList(duplicates);
            }
        };

        assertThrows(DuplicateApplicantException.class, () -> data.resetData(invalid));

        assertEquals(Optional.of(pair), data.getComparisonList().getCurrentPair());
    }

    @Test
    public void copyConstructor_copiesPendingPairIntoIndependentState() {
        RecruitDexData source = getTypicalRecruitDexData();
        source.getComparisonList().add(ALICE.getId());
        source.getComparisonList().add(BENSON.getId());
        source.getComparisonList().resetData(
                Set.copyOf(source.getComparisonList().getMemberIds()),
                List.copyOf(source.getComparisonList().getDecisionHistory()),
                Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())));

        RecruitDexData copy = new RecruitDexData(source);

        assertEquals(source.getComparisonList().getCurrentPair(), copy.getComparisonList().getCurrentPair());
        assertNotSame(source.getComparisonList().getComparisonState(), copy.getComparisonList().getComparisonState());
        copy.getComparisonList().remove(ALICE.getId());
        assertEquals(Set.of(ALICE.getId(), BENSON.getId()), source.getComparisonList().getMemberIds());
        assertEquals(Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())),
                source.getComparisonList().getCurrentPair());
    }

    @Test
    public void registryDelete_standaloneData_cleansOwnedComparisonReferences() {
        RecruitDexData data = getTypicalRecruitDexData();
        data.getComparisonList().add(ALICE.getId());
        data.getComparisonList().add(BENSON.getId());
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        data.getComparisonList().resetData(
                Set.copyOf(data.getComparisonList().getMemberIds()),
                List.copyOf(data.getComparisonList().getDecisionHistory()),
                Optional.of(pair));
        data.getComparisonList().recordDecision(ALICE.getId());
        data.getComparisonList().resetData(
                Set.copyOf(data.getComparisonList().getMemberIds()),
                List.copyOf(data.getComparisonList().getDecisionHistory()),
                Optional.of(pair));

        data.getApplicantRegistry().delete(ALICE.getId());

        assertEquals(Set.of(BENSON.getId()), data.getComparisonList().getMemberIds());
        assertEquals(List.of(), data.getComparisonList().getDecisionHistory());
        assertEquals(Optional.empty(), data.getComparisonList().getCurrentPair());
    }

    @Test
    public void equalsAndHashCode_takePendingPairIntoAccount() {
        RecruitDexData first = getTypicalRecruitDexData();
        RecruitDexData second = getTypicalRecruitDexData();
        first.getComparisonList().add(ALICE.getId());
        first.getComparisonList().add(BENSON.getId());
        second.getComparisonList().add(ALICE.getId());
        second.getComparisonList().add(BENSON.getId());
        first.getComparisonList().resetData(
                Set.copyOf(first.getComparisonList().getMemberIds()),
                List.copyOf(first.getComparisonList().getDecisionHistory()),
                Optional.empty());
        second.getComparisonList().resetData(
                Set.copyOf(second.getComparisonList().getMemberIds()),
                List.copyOf(second.getComparisonList().getDecisionHistory()),
                Optional.empty());
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        first.getComparisonList().resetData(
                Set.copyOf(first.getComparisonList().getMemberIds()),
                List.copyOf(first.getComparisonList().getDecisionHistory()),
                Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())));
        assertNotEquals(first, second);

        second.getComparisonList().resetData(
                Set.copyOf(second.getComparisonList().getMemberIds()),
                List.copyOf(second.getComparisonList().getDecisionHistory()),
                Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())));
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
