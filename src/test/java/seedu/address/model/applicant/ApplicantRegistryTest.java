package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.testutil.ApplicantBuilder;

public class ApplicantRegistryTest {

    private final ApplicantRegistry registry = new ApplicantRegistry();

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
