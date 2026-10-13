package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.CARL;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.comparison.ComparisonPair;
import seedu.address.testutil.ApplicantBuilder;

public class ComparisonPairResolverTest {

    private static final List<Applicant> APPLICANTS = List.of(ALICE, BENSON, CARL);

    @Test
    public void resolve_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ComparisonPairResolver.resolve(null, APPLICANTS));
        assertThrows(NullPointerException.class, () -> ComparisonPairResolver.resolve(Optional.empty(), null));
    }

    @Test
    public void resolve_noPair_returnsTwoEmptySides() {
        assertEquals(List.of(Optional.empty(), Optional.empty()),
                ComparisonPairResolver.resolve(Optional.empty(), APPLICANTS));
    }

    @Test
    public void resolve_noPairAndNoApplicants_returnsTwoEmptySides() {
        assertEquals(List.of(Optional.empty(), Optional.empty()),
                ComparisonPairResolver.resolve(Optional.empty(), List.of()));
    }

    @Test
    public void resolve_pairOfKnownApplicants_returnsThemInPresentedOrder() {
        Optional<ComparisonPair> pair = Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId()));

        List<Optional<Applicant>> sides = ComparisonPairResolver.resolve(pair, APPLICANTS);

        assertSame(ALICE, sides.get(0).get());
        assertSame(BENSON, sides.get(1).get());
    }

    @Test
    public void resolve_swappedPair_swapsTheSides() {
        Optional<ComparisonPair> pair = Optional.of(new ComparisonPair(BENSON.getId(), ALICE.getId()));

        List<Optional<Applicant>> sides = ComparisonPairResolver.resolve(pair, APPLICANTS);

        assertSame(BENSON, sides.get(0).get());
        assertSame(ALICE, sides.get(1).get());
    }

    @Test
    public void resolve_applicantsInAnyListOrder_findsByIdNotPosition() {
        Optional<ComparisonPair> pair = Optional.of(new ComparisonPair(CARL.getId(), ALICE.getId()));

        List<Optional<Applicant>> sides = ComparisonPairResolver.resolve(pair, List.of(ALICE, BENSON, CARL));
        List<Optional<Applicant>> reversedSides =
                ComparisonPairResolver.resolve(pair, List.of(CARL, BENSON, ALICE));

        assertEquals(sides, reversedSides);
        assertSame(CARL, sides.get(0).get());
        assertSame(ALICE, sides.get(1).get());
    }

    @Test
    public void resolve_editedApplicant_returnsTheCurrentRecord() {
        Optional<ComparisonPair> pair = Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        Applicant editedAlice = new ApplicantBuilder(ALICE).withPhone("99999999").build();

        // an edit keeps the ID, so the pair now resolves to the edited record
        List<Optional<Applicant>> sides = ComparisonPairResolver.resolve(pair, List.of(editedAlice, BENSON));

        assertSame(editedAlice, sides.get(0).get());
        assertEquals("99999999", sides.get(0).get().getPhone().value);
    }

    @Test
    public void resolve_missingRecord_leavesThatSideEmpty() {
        ApplicantId unknownId = new ApplicantId("123e4567-e89b-12d3-a456-426614174999");

        List<Optional<Applicant>> firstMissing = ComparisonPairResolver.resolve(
                Optional.of(new ComparisonPair(unknownId, BENSON.getId())), APPLICANTS);
        assertEquals(Optional.empty(), firstMissing.get(0));
        assertSame(BENSON, firstMissing.get(1).get());

        List<Optional<Applicant>> secondMissing = ComparisonPairResolver.resolve(
                Optional.of(new ComparisonPair(ALICE.getId(), unknownId)), APPLICANTS);
        assertSame(ALICE, secondMissing.get(0).get());
        assertEquals(Optional.empty(), secondMissing.get(1));
    }

    @Test
    public void resolve_deletedApplicant_leavesThatSideEmpty() {
        Optional<ComparisonPair> pair = Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId()));

        List<Optional<Applicant>> sides = ComparisonPairResolver.resolve(pair, List.of(ALICE, CARL));

        assertSame(ALICE, sides.get(0).get());
        assertEquals(Optional.empty(), sides.get(1));
    }

    @Test
    public void resolve_noRecordsAtAll_returnsTwoEmptySides() {
        Optional<ComparisonPair> pair = Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        assertEquals(List.of(Optional.empty(), Optional.empty()), ComparisonPairResolver.resolve(pair, List.of()));
    }

    @Test
    public void resolve_alwaysReturnsExactlyTwoSides() {
        assertEquals(2, ComparisonPairResolver.resolve(Optional.empty(), APPLICANTS).size());
        assertEquals(2, ComparisonPairResolver.resolve(
                Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())), APPLICANTS).size());
    }
}
