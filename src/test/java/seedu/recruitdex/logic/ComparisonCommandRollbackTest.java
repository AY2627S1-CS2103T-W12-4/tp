package seedu.recruitdex.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.recruitdex.testutil.Assert.assertThrows;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;
import static seedu.recruitdex.testutil.TypicalApplicants.CARL;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.recruitdex.logic.commands.AddToComparisonCommand;
import seedu.recruitdex.logic.commands.RemoveFromComparisonCommand;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.ReadOnlyRecruitDexData;
import seedu.recruitdex.model.RecruitDexData;
import seedu.recruitdex.model.comparison.ComparisonDecision;
import seedu.recruitdex.model.comparison.ComparisonList;
import seedu.recruitdex.model.comparison.ComparisonPair;
import seedu.recruitdex.model.ranking.RankingEntry;
import seedu.recruitdex.storage.JsonRecruitDexDataStorage;
import seedu.recruitdex.storage.JsonUserPrefsStorage;
import seedu.recruitdex.storage.StorageManager;

/**
 * Exercises membership commands through LogicManager when their model changes cannot be saved.
 */
public class ComparisonCommandRollbackTest {
    @TempDir
    public Path temporaryFolder;

    @TestFactory
    public Stream<DynamicTest> execute_saveFailure_restoresComparisonState() {
        return Stream.of(new IOException("Save failed"), new AccessDeniedException("recruitDexData.json"))
                .flatMap(failure -> Stream.of(MembershipChange.values())
                        .map(change -> DynamicTest.dynamicTest(change + ": " + failure.getClass().getSimpleName(), () ->
                                assertRollback(change, failure))));
    }

    private void assertRollback(MembershipChange change, IOException failure) throws Exception {
        Path dataPath = Files.createTempDirectory(temporaryFolder, "rollback-").resolve("recruitDexData.json");
        ModelManager model = new ModelManager();
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getApplicantRegistry().add(CARL);
        ComparisonList comparisons = model.getComparisonList();
        ComparisonDecision decision = new ComparisonDecision(BENSON.getId(), ALICE.getId(), ALICE.getId());
        ComparisonPair originalPair = new ComparisonPair(BENSON.getId(), ALICE.getId());
        switch (change) {
            case ADD_INITIAL_PAIR:
                comparisons.resetData(Set.of(ALICE.getId()), List.of(), Optional.empty());
                break;
            case READD_WITH_HISTORY:
                comparisons.resetData(Set.of(ALICE.getId(), CARL.getId()), List.of(decision),
                        Optional.of(new ComparisonPair(CARL.getId(), ALICE.getId())));
                break;
            case REMOVE_CLEAR_PAIR:
                comparisons.resetData(Set.of(ALICE.getId(), BENSON.getId()), List.of(decision),
                        Optional.of(originalPair));
                break;
            case REMOVE_REPLACE_PAIR:
                comparisons.resetData(Set.of(ALICE.getId(), BENSON.getId(), CARL.getId()), List.of(decision),
                        Optional.of(originalPair));
                break;
            default:
                throw new AssertionError("Unhandled membership change: " + change);
        }
        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(BENSON.getId()));
        RecruitDexData dataBefore = new RecruitDexData(model.getRecruitDexData());
        List<RankingEntry> rankingBefore = List.copyOf(model.getRankingEngine().getRanking());
        var members = comparisons.getMemberIds();
        var history = comparisons.getDecisionHistory();
        var applicants = model.getApplicantRegistry().getApplicantList();
        var displayedApplicants = model.getFilteredApplicantList();
        ObservableList<RankingEntry> ranking = model.getRankingEngine().getRanking();
        JsonRecruitDexDataStorage savedRecords = new JsonRecruitDexDataStorage(dataPath);
        savedRecords.saveRecruitDexData(dataBefore);
        byte[] savedBytes = Files.readAllBytes(dataPath);
        List<RecruitDexData> attemptedSaves = new ArrayList<>();
        JsonRecruitDexDataStorage failingStorage = new JsonRecruitDexDataStorage(dataPath) {
            @Override
            public void saveRecruitDexData(ReadOnlyRecruitDexData data) throws IOException {
                attemptedSaves.add(new RecruitDexData(data));
                assertNotEquals(rankingBefore, ranking);
                throw failure;
            }
        };
        Logic logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(dataPath.resolveSibling("userPrefs.json"))));
        var pairProperty = logic.getCurrentPairProperty();
        String commandWord = switch (change) {
            case ADD_INITIAL_PAIR, READD_WITH_HISTORY -> AddToComparisonCommand.COMMAND_WORD;
            case REMOVE_CLEAR_PAIR, REMOVE_REPLACE_PAIR -> RemoveFromComparisonCommand.COMMAND_WORD;
        };
        String expectedMessage = failure instanceof AccessDeniedException
                ? String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, failure.getMessage())
                : String.format(LogicManager.FILE_OPS_ERROR_FORMAT, failure.getMessage());

        assertThrows(CommandException.class, expectedMessage, () -> logic.execute(commandWord + " 1"));

        assertEquals(1, attemptedSaves.size());
        assertChangedState(change, dataBefore, attemptedSaves.get(0));
        assertEquals(dataBefore, model.getRecruitDexData());
        assertEquals(rankingBefore, ranking);
        assertSame(comparisons, model.getComparisonList());
        assertSame(members, comparisons.getMemberIds());
        assertEquals(dataBefore.getComparisonList().getMemberIds(), members);
        assertSame(history, comparisons.getDecisionHistory());
        assertEquals(dataBefore.getComparisonList().getDecisionHistory(), history);
        assertSame(pairProperty, logic.getCurrentPairProperty());
        assertEquals(dataBefore.getComparisonList().getCurrentPair(), pairProperty.get());
        assertSame(applicants, model.getApplicantRegistry().getApplicantList());
        assertEquals(List.of(ALICE, BENSON, CARL), applicants);
        assertSame(displayedApplicants, logic.getFilteredApplicantList());
        assertEquals(List.of(BENSON), displayedApplicants);
        assertSame(ranking, logic.getRanking());
        assertArrayEquals(savedBytes, Files.readAllBytes(dataPath));
        assertEquals(dataBefore, savedRecords.readRecruitDexData().orElseThrow());
    }

    /** Verifies that saving failed after the command applied its membership and pair changes. */
    private void assertChangedState(MembershipChange change, RecruitDexData before, RecruitDexData attempted) {
        assertEquals(before.getApplicantList(), attempted.getApplicantList());
        assertEquals(before.getComparisonList().getDecisionHistory(),
                attempted.getComparisonList().getDecisionHistory());
        assertNotEquals(before.getComparisonList().getMemberIds(), attempted.getComparisonList().getMemberIds());
        Optional<ComparisonPair> pairAtSave = attempted.getComparisonList().getCurrentPair();
        switch (change) {
            case ADD_INITIAL_PAIR:
                assertEquals(Set.of(ALICE.getId(), BENSON.getId()), attempted.getComparisonList().getMemberIds());
                ComparisonPair initialPair = pairAtSave.orElseThrow();
                assertEquals(Set.of(ALICE.getId(), BENSON.getId()),
                        Set.of(initialPair.getFirstApplicantId(), initialPair.getSecondApplicantId()));
                break;
            case READD_WITH_HISTORY:
                assertEquals(Set.of(ALICE.getId(), BENSON.getId(), CARL.getId()),
                        attempted.getComparisonList().getMemberIds());
                assertEquals(before.getComparisonList().getCurrentPair(), pairAtSave);
                break;
            case REMOVE_CLEAR_PAIR:
                assertEquals(Set.of(ALICE.getId()), attempted.getComparisonList().getMemberIds());
                assertEquals(Optional.empty(), pairAtSave);
                break;
            case REMOVE_REPLACE_PAIR:
                assertEquals(Set.of(ALICE.getId(), CARL.getId()), attempted.getComparisonList().getMemberIds());
                ComparisonPair replacementPair = pairAtSave.orElseThrow();
                assertEquals(Set.of(ALICE.getId(), CARL.getId()),
                        Set.of(replacementPair.getFirstApplicantId(), replacementPair.getSecondApplicantId()));
                break;
            default:
                throw new AssertionError("Unhandled membership change: " + change);
        }
    }

    private enum MembershipChange {
        ADD_INITIAL_PAIR,
        READD_WITH_HISTORY,
        REMOVE_CLEAR_PAIR,
        REMOVE_REPLACE_PAIR
    }
}
