package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.BENSON;
import static seedu.address.testutil.TypicalApplicants.CARL;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CompareCommand.Side;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.comparison.ComparisonDecision;
import seedu.address.model.comparison.ComparisonPair;
import seedu.address.model.ranking.RankingEntry;

public class CompareCommandTest {
    private ModelManager model;
    private ComparisonPair presentedPair;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getApplicantRegistry().add(CARL);
        model.getComparisonList().add(ALICE.getId());
        model.getComparisonList().add(BENSON.getId());
        // Deliberately reverse registry order: A/B refer to the panel's sides, not displayed indices.
        presentedPair = new ComparisonPair(BENSON.getId(), ALICE.getId());
        model.getComparisonList().getComparisonState().setCurrentPair(presentedPair);
    }

    @Test
    public void execute_sideA_recordsFirstApplicantAndPreservesSearchFilter() throws Exception {
        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(ALICE.getId()));
        var filteredView = model.getFilteredApplicantList();

        CommandResult result = new CompareCommand(Side.A).execute(model);

        assertEquals(String.format(CompareCommand.MESSAGE_SUCCESS, BENSON.getName(), ALICE.getName()),
                result.getFeedbackToUser());
        assertRecordedWinner(BENSON);
        assertSame(filteredView, model.getFilteredApplicantList());
        assertEquals(List.of(ALICE), filteredView);
    }

    @Test
    public void execute_sideB_recordsSecondApplicantAndSelectsNextPair() throws Exception {
        CommandResult result = new CompareCommand(Side.B).execute(model);

        assertEquals(String.format(CompareCommand.MESSAGE_SUCCESS, ALICE.getName(), BENSON.getName()),
                result.getFeedbackToUser());
        assertRecordedWinner(ALICE);
    }

    @Test
    public void execute_noPendingPair_throwsCommandExceptionWithoutChangingData() {
        model.getComparisonList().getComparisonState().clearCurrentPair();
        List<RankingEntry> rankingBefore = List.copyOf(model.getRankingEngine().getRanking());

        assertThrows(CommandException.class, CompareCommand.MESSAGE_NO_PENDING_PAIR, () ->
                new CompareCommand(Side.A).execute(model));

        assertTrue(model.getComparisonList().getDecisionHistory().isEmpty());
        assertEquals(rankingBefore, model.getRankingEngine().getRanking());
        assertTrue(model.getComparisonList().getCurrentPair().isEmpty());
    }

    @Test
    public void execute_inactivePairMember_throwsCommandExceptionWithoutChangingHistoryOrRanking() {
        ComparisonDecision priorDecision = model.getComparisonList().recordDecision(ALICE.getId());
        ComparisonPair invalidPair = new ComparisonPair(ALICE.getId(), CARL.getId());
        model.getComparisonList().getComparisonState().setCurrentPair(invalidPair);
        List<RankingEntry> rankingBefore = List.copyOf(model.getRankingEngine().getRanking());

        assertThrows(CommandException.class, "The pending pair is no longer valid.", () ->
                new CompareCommand(Side.A).execute(model));

        assertEquals(List.of(priorDecision), model.getComparisonList().getDecisionHistory());
        assertEquals(rankingBefore, model.getRankingEngine().getRanking());
        assertEquals(Optional.of(invalidPair), model.getComparisonList().getCurrentPair());
    }

    @Test
    public void constructor_nullSide_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CompareCommand(null));
    }

    @Test
    public void equalsAndToString_distinguishChosenSides() {
        CompareCommand chooseA = new CompareCommand(Side.A);
        assertTrue(chooseA.equals(chooseA));
        assertTrue(chooseA.equals(new CompareCommand(Side.A)));
        assertFalse(chooseA.equals(new CompareCommand(Side.B)));
        assertFalse(chooseA.equals(null));
        assertFalse(chooseA.equals("choose a"));
        assertEquals(CompareCommand.class.getCanonicalName() + "{side=A}", chooseA.toString());
    }

    private void assertRecordedWinner(Applicant winner) {
        List<ComparisonDecision> history = model.getComparisonList().getDecisionHistory();
        assertEquals(1, history.size());
        ComparisonDecision decision = history.get(0);
        assertEquals(presentedPair.getFirstApplicantId(), decision.getFirstApplicantId());
        assertEquals(presentedPair.getSecondApplicantId(), decision.getSecondApplicantId());
        assertEquals(winner.getId(), decision.getWinnerId());
        assertEquals(winner.getId(), model.getRankingEngine().getRanking().get(0).getApplicantId());
        assertTrue(model.getRankingEngine().getRanking().stream().allMatch(entry -> entry.getComparisonCount() == 1));
        ComparisonPair nextPair = model.getComparisonList().getCurrentPair().orElseThrow();
        assertEquals(Set.of(ALICE.getId(), BENSON.getId()),
                Set.of(nextPair.getFirstApplicantId(), nextPair.getSecondApplicantId()));
        assertEquals(Set.of(ALICE.getId(), BENSON.getId()), model.getComparisonList().getMemberIds());
        assertEquals(List.of(ALICE, BENSON, CARL), model.getApplicantRegistry().getApplicantList());
    }
}
