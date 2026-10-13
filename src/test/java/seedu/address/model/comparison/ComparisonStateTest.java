package seedu.address.model.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.CARL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.value.WritableValue;
import seedu.address.model.applicant.ApplicantId;
import seedu.address.model.applicant.ApplicantRegistry;
import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.testutil.ApplicantBuilder;

public class ComparisonStateTest {

    private static final ApplicantId UNREGISTERED_ID = new ApplicantId("123e4567-e89b-12d3-a456-426614174999");

    private ApplicantRegistry registry;
    private ComparisonState state;
    private ComparisonPair aliceBensonPair;
    private ComparisonPair bensonAlicePair;
    private ComparisonPair aliceCarlPair;

    @BeforeEach
    public void setUp() {
        registry = new ApplicantRegistry();
        registry.add(ALICE);
        registry.add(BENSON);
        registry.add(CARL);
        state = new ComparisonState(registry);
        aliceBensonPair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        bensonAlicePair = new ComparisonPair(BENSON.getId(), ALICE.getId());
        aliceCarlPair = new ComparisonPair(ALICE.getId(), CARL.getId());
    }

    @Test
    public void constructor_nullRegistry_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ComparisonState(null));
    }

    @Test
    public void constructor_newState_hasNoPair() {
        assertEquals(Optional.empty(), state.getCurrentPair());
        assertEquals(Optional.empty(), state.currentPairProperty().get());
    }

    @Test
    public void setCurrentPair_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> state.setCurrentPair(null));
    }

    @Test
    public void setCurrentPair_registeredApplicants_setsPair() {
        state.setCurrentPair(aliceBensonPair);
        assertEquals(Optional.of(aliceBensonPair), state.getCurrentPair());
    }

    @Test
    public void setCurrentPair_preservesPresentedSides() {
        state.setCurrentPair(bensonAlicePair);

        ComparisonPair stored = state.getCurrentPair().get();
        assertEquals(BENSON.getId(), stored.getFirstApplicantId());
        assertEquals(ALICE.getId(), stored.getSecondApplicantId());
    }

    @Test
    public void setCurrentPair_existingPair_replacesIt() {
        state.setCurrentPair(aliceBensonPair);
        state.setCurrentPair(aliceCarlPair);
        assertEquals(Optional.of(aliceCarlPair), state.getCurrentPair());
    }

    @Test
    public void setCurrentPair_samePairTwice_keepsPair() {
        state.setCurrentPair(aliceBensonPair);
        state.setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        assertEquals(Optional.of(aliceBensonPair), state.getCurrentPair());
    }

    @Test
    public void setCurrentPair_unregisteredApplicant_throwsAndKeepsCurrentState() {
        ComparisonPair firstUnregistered = new ComparisonPair(UNREGISTERED_ID, ALICE.getId());
        ComparisonPair secondUnregistered = new ComparisonPair(ALICE.getId(), UNREGISTERED_ID);
        ComparisonPair bothUnregistered = new ComparisonPair(UNREGISTERED_ID,
                new ApplicantId("123e4567-e89b-12d3-a456-426614174998"));

        // rejected when there is no pair yet, and when a pair is pending
        for (ComparisonPair invalidPair : List.of(firstUnregistered, secondUnregistered, bothUnregistered)) {
            assertThrows(ApplicantNotFoundException.class, () -> state.setCurrentPair(invalidPair));
            assertEquals(Optional.empty(), state.getCurrentPair());
        }

        state.setCurrentPair(aliceBensonPair);
        for (ComparisonPair invalidPair : List.of(firstUnregistered, secondUnregistered, bothUnregistered)) {
            assertThrows(ApplicantNotFoundException.class, () -> state.setCurrentPair(invalidPair));
            assertEquals(Optional.of(aliceBensonPair), state.getCurrentPair());
        }
    }

    @Test
    public void setCurrentPair_applicantDeletedFromRegistry_throws() {
        registry.delete(BENSON.getId());
        assertThrows(ApplicantNotFoundException.class, () -> state.setCurrentPair(aliceBensonPair));
        assertEquals(Optional.empty(), state.getCurrentPair());
    }

    @Test
    public void setCurrentPair_applicantAddedToRegistryLater_isAccepted() {
        ApplicantRegistry smallRegistry = new ApplicantRegistry();
        smallRegistry.add(ALICE);
        ComparisonState smallState = new ComparisonState(smallRegistry);
        assertThrows(ApplicantNotFoundException.class, () -> smallState.setCurrentPair(aliceBensonPair));

        smallRegistry.add(BENSON);
        smallState.setCurrentPair(aliceBensonPair);
        assertEquals(Optional.of(aliceBensonPair), smallState.getCurrentPair());
    }

    @Test
    public void currentPair_afterRecordIsEdited_isUnchanged() {
        state.setCurrentPair(aliceBensonPair);

        registry.edit(ALICE.getId(), new ApplicantBuilder(ALICE).withPhone("99999999").build());

        // the pair refers to applicants by ID, so edits to their records do not touch it
        assertEquals(Optional.of(aliceBensonPair), state.getCurrentPair());
    }

    @Test
    public void clearCurrentPair_pendingPair_removesIt() {
        state.setCurrentPair(aliceBensonPair);
        state.clearCurrentPair();
        assertEquals(Optional.empty(), state.getCurrentPair());
    }

    @Test
    public void clearCurrentPair_noPair_staysEmpty() {
        state.clearCurrentPair();
        assertEquals(Optional.empty(), state.getCurrentPair());
    }

    @Test
    public void currentPairProperty_tracksCurrentPair() {
        state.setCurrentPair(aliceBensonPair);
        assertEquals(Optional.of(aliceBensonPair), state.currentPairProperty().get());

        state.setCurrentPair(aliceCarlPair);
        assertEquals(Optional.of(aliceCarlPair), state.currentPairProperty().get());

        state.clearCurrentPair();
        assertEquals(Optional.empty(), state.currentPairProperty().get());
    }

    @Test
    public void currentPairProperty_isReadOnly() {
        assertFalse(state.currentPairProperty() instanceof WritableValue);
    }

    @Test
    public void currentPairProperty_isTheSameObjectEachTime() {
        assertTrue(state.currentPairProperty() == state.currentPairProperty());
    }

    @Test
    public void currentPairProperty_notifiesListenersOfEveryChange() {
        List<Optional<ComparisonPair>> seen = new ArrayList<>();
        state.currentPairProperty().addListener((observable, oldValue, newValue) -> seen.add(newValue));

        state.setCurrentPair(aliceBensonPair);
        state.setCurrentPair(aliceCarlPair);
        state.clearCurrentPair();

        assertEquals(List.of(Optional.of(aliceBensonPair), Optional.of(aliceCarlPair), Optional.empty()), seen);
    }

    @Test
    public void currentPairProperty_reportsOldAndNewValues() {
        state.setCurrentPair(aliceBensonPair);
        List<String> changes = new ArrayList<>();
        state.currentPairProperty().addListener((observable, oldValue, newValue) ->
                changes.add(oldValue.isPresent() + "->" + newValue.isPresent()));

        state.clearCurrentPair();
        state.setCurrentPair(aliceCarlPair);

        assertEquals(List.of("true->false", "false->true"), changes);
    }

    @Test
    public void currentPairProperty_noChange_doesNotNotify() {
        List<Optional<ComparisonPair>> seen = new ArrayList<>();
        state.currentPairProperty().addListener((observable, oldValue, newValue) -> seen.add(newValue));

        state.clearCurrentPair(); // already empty

        state.setCurrentPair(aliceBensonPair);
        seen.clear();
        state.setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId())); // the same pair again

        assertTrue(seen.isEmpty());
    }

    @Test
    public void currentPairProperty_rejectedPair_doesNotNotify() {
        state.setCurrentPair(aliceBensonPair);
        List<Optional<ComparisonPair>> seen = new ArrayList<>();
        state.currentPairProperty().addListener((observable, oldValue, newValue) -> seen.add(newValue));

        ComparisonPair invalidPair = new ComparisonPair(ALICE.getId(), UNREGISTERED_ID);
        assertThrows(ApplicantNotFoundException.class, () -> state.setCurrentPair(invalidPair));

        assertTrue(seen.isEmpty());
    }

    @Test
    public void equals() {
        ComparisonState other = new ComparisonState(new ApplicantRegistry());

        // two states with no pair -> returns true
        assertTrue(state.equals(other));

        // same object -> returns true
        assertTrue(state.equals(state));

        // null -> returns false
        assertFalse(state.equals(null));

        // different type -> returns false
        assertFalse(state.equals(5));

        // pair set versus no pair -> returns false
        state.setCurrentPair(aliceBensonPair);
        assertFalse(state.equals(other));

        // the same pair, even with a different registry behind it -> returns true
        ComparisonState otherWithPair = new ComparisonState(registry);
        otherWithPair.setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        assertTrue(state.equals(otherWithPair));

        // a different pair -> returns false
        otherWithPair.setCurrentPair(aliceCarlPair);
        assertFalse(state.equals(otherWithPair));

        // the same applicants on swapped sides -> returns false
        otherWithPair.setCurrentPair(bensonAlicePair);
        assertFalse(state.equals(otherWithPair));
    }

    @Test
    public void hashCode_equalStates_haveEqualHashCodes() {
        ComparisonState other = new ComparisonState(registry);
        assertEquals(state.hashCode(), other.hashCode());

        state.setCurrentPair(aliceBensonPair);
        other.setCurrentPair(new ComparisonPair(ALICE.getId(), BENSON.getId()));
        assertEquals(state.hashCode(), other.hashCode());
    }

    @Test
    public void toString_showsCurrentPair() {
        assertTrue(state.toString().contains("Optional.empty"));

        state.setCurrentPair(aliceBensonPair);
        assertTrue(state.toString().contains(ALICE.getId().toString()));
        assertTrue(state.toString().contains(BENSON.getId().toString()));
    }
}
