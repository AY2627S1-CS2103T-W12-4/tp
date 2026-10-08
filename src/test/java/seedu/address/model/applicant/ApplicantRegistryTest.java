package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.testutil.ApplicantBuilder;

public class ApplicantRegistryTest {

    private final ApplicantRegistry registry = new ApplicantRegistry();

    @Test
    public void delete_editedRecord_usesStableIdAndKeepsOtherRecords() {
        registry.add(ALICE);
        registry.add(BENSON);
        registry.edit(ALICE.getId(), new ApplicantBuilder(ALICE).withName("Alice Tan").build());

        registry.delete(new ApplicantId(ALICE.getId().value));

        assertFalse(registry.containsId(ALICE.getId()));
        assertSame(BENSON, registry.get(BENSON.getId()));
        assertEquals(List.of(BENSON), registry.getApplicantList());
    }

    @Test
    public void delete_unknownOrNullId_doesNotChangeRecords() {
        registry.add(ALICE);
        assertThrows(NullPointerException.class, () -> registry.delete(null));
        assertThrows(ApplicantNotFoundException.class, () -> registry.delete(BENSON.getId()));
        assertEquals(List.of(ALICE), registry.getApplicantList());
    }

    @Test
    public void setApplicants_invalidReplacement_preservesRecordsAndObservableView() {
        registry.add(BENSON);
        ObservableList<Applicant> view = registry.getApplicantList();
        Applicant duplicateId = new ApplicantBuilder(ALICE).withName("Different name").build();
        Applicant duplicateName = new ApplicantBuilder(ALICE).withId(BENSON.getId().value).build();

        assertThrows(NullPointerException.class, () -> registry.setApplicants(null));
        assertThrows(NullPointerException.class, () -> registry.setApplicants(Arrays.asList(ALICE, null)));
        assertThrows(DuplicateApplicantException.class, () -> registry.setApplicants(List.of(ALICE, duplicateId)));
        assertThrows(DuplicateApplicantException.class, () -> registry.setApplicants(List.of(ALICE, duplicateName)));
        assertSame(view, registry.getApplicantList());
        assertEquals(List.of(BENSON), view);
    }

    @Test
    public void setApplicants_validReplacement_preservesOrderAndSupportsSelfReplacement() {
        registry.add(ALICE);
        ObservableList<Applicant> view = registry.getApplicantList();

        registry.setApplicants(List.of(BENSON, ALICE));
        registry.setApplicants(view);

        assertSame(view, registry.getApplicantList());
        assertEquals(List.of(BENSON, ALICE), view);
        assertSame(BENSON, registry.get(BENSON.getId()));
        assertSame(ALICE, registry.get(ALICE.getId()));
    }

    @Test
    public void getApplicantList_mutations_areObservableAndCannotBypassRegistry() {
        ObservableList<Applicant> view = registry.getApplicantList();
        int[] changes = {0};
        view.addListener((ListChangeListener<Applicant>) change -> changes[0]++);

        registry.add(ALICE);
        registry.edit(ALICE.getId(), new ApplicantBuilder(ALICE).withName("Alice Tan").build());
        registry.delete(ALICE.getId());

        assertEquals(3, changes[0]);
        assertTrue(view.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> view.add(BENSON));
    }

    @Test
    public void add_applicant_returnsExistingIdForStoredApplicant() {
        ApplicantId applicantId = registry.add(ALICE);

        assertSame(ALICE.getId(), applicantId);
        assertSame(ALICE, registry.get(applicantId));
    }

    @Test
    public void add_distinctApplicants_returnsDistinctIdsForCorrectApplicants() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId bensonId = registry.add(BENSON);

        assertSame(ALICE.getId(), aliceId);
        assertSame(BENSON.getId(), bensonId);
        assertNotEquals(aliceId, bensonId);
        assertSame(ALICE, registry.get(aliceId));
        assertSame(BENSON, registry.get(bensonId));
    }

    @Test
    public void add_nullApplicant_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.add(null));
    }

    @Test
    public void add_duplicateApplicant_throwsDuplicateApplicantException() {
        ApplicantId aliceId = registry.add(ALICE);

        assertThrows(DuplicateApplicantException.class, () -> registry.add(ALICE));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void add_sameNameWithDifferentDetails_throwsDuplicateApplicantException() {
        ApplicantId aliceId = registry.add(ALICE);
        Applicant anotherAlice = new ApplicantBuilder(ALICE).withId("00000000-0000-0000-0000-000000000002")
                .withEmail("other.alice@example.com")
                .withPhone("91234567").build();

        assertThrows(DuplicateApplicantException.class, () -> registry.add(anotherAlice));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void add_rejectedApplicants_preservesExistingRecordsAndAllowsFurtherRegistration() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId bensonId = registry.add(BENSON);

        assertThrows(NullPointerException.class, () -> registry.add(null));
        assertThrows(DuplicateApplicantException.class, () -> registry.add(ALICE));

        Applicant carol = new ApplicantBuilder().withName("Carol Tan").build();
        ApplicantId carolId = registry.add(carol);

        assertSame(ALICE, registry.get(aliceId));
        assertSame(BENSON, registry.get(bensonId));
        assertSame(carol, registry.get(carolId));
    }

    @Test
    public void add_sameIdWithDifferentName_throwsDuplicateApplicantException() {
        ApplicantId aliceId = registry.add(ALICE);
        Applicant anotherApplicant = new ApplicantBuilder(BENSON).withId(aliceId.value).build();

        assertThrows(DuplicateApplicantException.class, () -> registry.add(anotherApplicant));
        assertSame(ALICE, registry.get(aliceId));

        ApplicantId bensonId = registry.add(BENSON);
        assertSame(BENSON, registry.get(bensonId));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void add_sameApplicantToDifferentRegistries_preservesId() {
        ApplicantRegistry anotherRegistry = new ApplicantRegistry();

        ApplicantId applicantId = registry.add(ALICE);
        ApplicantId anotherRegistryId = anotherRegistry.add(ALICE);

        assertSame(ALICE.getId(), applicantId);
        assertSame(applicantId, anotherRegistryId);
        assertSame(ALICE, registry.get(applicantId));
        assertSame(ALICE, anotherRegistry.get(applicantId));
    }

    @Test
    public void edit_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.edit(null, ALICE));
    }

    @Test
    public void edit_nullEditedApplicant_throwsNullPointerException() {
        ApplicantId aliceId = registry.add(ALICE);

        assertThrows(NullPointerException.class, () -> registry.edit(aliceId, null));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void edit_unregisteredId_throwsApplicantNotFoundException() {
        ApplicantId aliceId = registry.add(ALICE);
        Applicant unregisteredApplicant = new ApplicantBuilder().withName("Carol Tan").build();

        assertThrows(ApplicantNotFoundException.class, () ->
                registry.edit(unregisteredApplicant.getId(), unregisteredApplicant));
        assertThrows(ApplicantNotFoundException.class, () -> registry.get(unregisteredApplicant.getId()));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void edit_sameApplicant_success() {
        ApplicantId aliceId = registry.add(ALICE);

        registry.edit(aliceId, ALICE);

        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void edit_sameNameWithDifferentDetails_replacesApplicant() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId bensonId = registry.add(BENSON);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withEmail("other.alice@example.com")
                .withPhone("91234567").withInterviewNotes("Available next week")
                .withYearsOfExperience("5").withSource("Referral").build();

        registry.edit(aliceId, editedAlice);

        assertSame(editedAlice, registry.get(aliceId));
        assertSame(aliceId, registry.get(aliceId).getId());
        assertSame(BENSON, registry.get(bensonId));
    }

    @Test
    public void edit_newName_preservesIdAndAllowsOriginalNameToBeRegistered() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId bensonId = registry.add(BENSON);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withName("Alice Tan").build();
        Applicant anotherAlice = new ApplicantBuilder(ALICE).withId("00000000-0000-0000-0000-000000000002")
                .build();

        registry.edit(aliceId, editedAlice);
        ApplicantId anotherAliceId = registry.add(anotherAlice);

        assertSame(editedAlice, registry.get(aliceId));
        assertSame(aliceId, registry.get(aliceId).getId());
        assertSame(anotherAlice, registry.get(anotherAliceId));
        assertSame(BENSON, registry.get(bensonId));
        assertThrows(DuplicateApplicantException.class, () -> registry.add(editedAlice));
    }

    @Test
    public void edit_duplicateName_throwsDuplicateApplicantException() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId bensonId = registry.add(BENSON);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withName(BENSON.getName().fullName).build();

        assertThrows(DuplicateApplicantException.class, () -> registry.edit(aliceId, editedAlice));
        assertSame(ALICE, registry.get(aliceId));
        assertSame(BENSON, registry.get(bensonId));

        Applicant validEdit = new ApplicantBuilder(ALICE).withName("Alice Tan").build();
        registry.edit(aliceId, validEdit);
        assertSame(validEdit, registry.get(aliceId));
    }

    @Test
    public void edit_differentId_throwsIllegalArgumentException() {
        ApplicantId aliceId = registry.add(ALICE);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withId("00000000-0000-0000-0000-000000000002")
                .build();

        assertThrows(IllegalArgumentException.class, () -> registry.edit(aliceId, editedAlice));
        assertSame(ALICE, registry.get(aliceId));
        assertThrows(ApplicantNotFoundException.class, () -> registry.get(editedAlice.getId()));
    }

    @Test
    public void edit_anotherRegisteredId_throwsIllegalArgumentException() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId bensonId = registry.add(BENSON);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withId(bensonId.value).build();

        assertThrows(IllegalArgumentException.class, () -> registry.edit(aliceId, editedAlice));
        assertSame(ALICE, registry.get(aliceId));
        assertSame(BENSON, registry.get(bensonId));
    }

    @Test
    public void edit_equalIdValue_replacesApplicant() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId copiedId = new ApplicantId(aliceId.value);
        Applicant editedAlice = new ApplicantBuilder(ALICE).withId(aliceId.value).withName("Alice Tan").build();

        registry.edit(copiedId, editedAlice);

        assertSame(editedAlice, registry.get(aliceId));
        assertSame(editedAlice, registry.get(copiedId));
    }

    @Test
    public void get_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.get(null));
    }

    @Test
    public void get_unregisteredId_throwsApplicantNotFoundException() {
        ApplicantId unregisteredId = new ApplicantId("00000000-0000-0000-0000-000000000001");

        assertThrows(ApplicantNotFoundException.class, () -> registry.get(unregisteredId));
    }

    @Test
    public void get_repeatedLookup_returnsSameApplicant() {
        ApplicantId aliceId = registry.add(ALICE);

        assertSame(ALICE, registry.get(aliceId));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void get_equalIdValue_returnsApplicant() {
        ApplicantId aliceId = registry.add(ALICE);
        ApplicantId copiedId = new ApplicantId(aliceId.value);

        assertSame(ALICE, registry.get(copiedId));
    }
}
