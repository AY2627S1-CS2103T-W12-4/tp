package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;
import seedu.address.testutil.ApplicantBuilder;

public class ApplicantRegistryTest {

    private final ApplicantRegistry registry = new ApplicantRegistry();

    @Test
    public void add_applicant_returnsIdForStoredApplicant() {
        UUID applicantId = registry.add(ALICE);

        assertNotNull(applicantId);
        assertSame(ALICE, registry.get(applicantId));
    }

    @Test
    public void add_distinctApplicants_returnsDistinctIdsForCorrectApplicants() {
        UUID aliceId = registry.add(ALICE);
        UUID bensonId = registry.add(BENSON);

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
        UUID aliceId = registry.add(ALICE);

        assertThrows(DuplicateApplicantException.class, () -> registry.add(ALICE));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void add_sameNameWithDifferentDetails_throwsDuplicateApplicantException() {
        UUID aliceId = registry.add(ALICE);
        Applicant anotherAlice = new ApplicantBuilder(ALICE).withEmail("other.alice@example.com")
                .withPhone("91234567").build();

        assertThrows(DuplicateApplicantException.class, () -> registry.add(anotherAlice));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void add_rejectedApplicants_preservesExistingRecordsAndAllowsFurtherRegistration() {
        UUID aliceId = registry.add(ALICE);
        UUID bensonId = registry.add(BENSON);

        assertThrows(NullPointerException.class, () -> registry.add(null));
        assertThrows(DuplicateApplicantException.class, () -> registry.add(ALICE));

        Applicant carol = new ApplicantBuilder().withName("Carol Tan").build();
        UUID carolId = registry.add(carol);

        assertSame(ALICE, registry.get(aliceId));
        assertSame(BENSON, registry.get(bensonId));
        assertSame(carol, registry.get(carolId));
    }

    @Test
    public void get_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.get(null));
    }

    @Test
    public void get_unregisteredId_throwsApplicantNotFoundException() {
        UUID unregisteredId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        assertThrows(ApplicantNotFoundException.class, () -> registry.get(unregisteredId));
    }

    @Test
    public void get_repeatedLookup_returnsSameApplicant() {
        UUID aliceId = registry.add(ALICE);

        assertSame(ALICE, registry.get(aliceId));
        assertSame(ALICE, registry.get(aliceId));
    }

    @Test
    public void get_equalIdValue_returnsApplicant() {
        UUID aliceId = registry.add(ALICE);
        UUID copiedId = UUID.fromString(aliceId.toString());

        assertSame(ALICE, registry.get(copiedId));
    }
}
