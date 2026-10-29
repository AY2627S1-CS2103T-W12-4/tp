package seedu.recruitdex.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;
import static seedu.recruitdex.testutil.TypicalApplicants.CARL;
import static seedu.recruitdex.testutil.TypicalApplicants.DANIEL;
import static seedu.recruitdex.testutil.TypicalApplicants.ELLE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.ReadOnlyRecruitDexData;
import seedu.recruitdex.model.RecruitDexData;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.ApplicantId;
import seedu.recruitdex.model.comparison.ComparisonDecision;
import seedu.recruitdex.model.comparison.ComparisonPair;
import seedu.recruitdex.model.ranking.RankingEntry;
import seedu.recruitdex.storage.JsonRecruitDexDataStorage;
import seedu.recruitdex.storage.JsonUserPrefsStorage;
import seedu.recruitdex.storage.StorageManager;

/** Exercises rejected membership changes with a real ranking convergence failure. */
public class ComparisonCommandRankingFailureTest {
    private static final String RANKING_FAILURE =
            "Bradley-Terry ranking did not converge within the iteration limit.";

    @TempDir
    public Path temporaryFolder;

    @TestFactory
    public Stream<DynamicTest> execute_rankingFailure_preservesModelAndSavedData() {
        return Stream.of("compare-add", "compare-remove", "delete")
                .map(command -> DynamicTest.dynamicTest(command, () -> assertRejectedWithoutChanges(command)));
    }

    private void assertRejectedWithoutChanges(String command) throws Exception {
        ModelManager model = createModel(command);
        Applicant target = command.equals("compare-add") ? DANIEL : ELLE;
        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(target.getId()));
        RecruitDexData dataBefore = new RecruitDexData(model.getRecruitDexData());
        List<RankingEntry> rankingBefore = List.copyOf(model.getRankingEngine().getRanking());
        AtomicInteger saveCount = new AtomicInteger();
        Path dataPath = temporaryFolder.resolve(command + ".json");
        JsonRecruitDexDataStorage dataStorage = new JsonRecruitDexDataStorage(dataPath) {
            @Override
            public void saveRecruitDexData(ReadOnlyRecruitDexData data) throws IOException {
                saveCount.incrementAndGet();
                super.saveRecruitDexData(data);
            }
        };
        dataStorage.saveRecruitDexData(dataBefore);
        byte[] savedBytes = Files.readAllBytes(dataPath);
        saveCount.set(0);
        Logic logic = new LogicManager(model, new StorageManager(dataStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
        AtomicInteger notifications = observeChanges(model);

        CommandException failure = assertThrows(CommandException.class, () -> logic.execute(command + " 1"));

        assertEquals("Could not update the ranking. No changes were made.\n" + RANKING_FAILURE,
                failure.getMessage());
        assertInstanceOf(IllegalStateException.class, failure.getCause());
        assertEquals(dataBefore, model.getRecruitDexData());
        assertEquals(rankingBefore, model.getRankingEngine().getRanking());
        assertEquals(List.of(target), model.getFilteredApplicantList());
        assertEquals(0, notifications.get());
        assertEquals(0, saveCount.get());
        assertArrayEquals(savedBytes, Files.readAllBytes(dataPath));
        assertEquals(dataBefore, dataStorage.readRecruitDexData().orElseThrow());

        // A failed change must leave the data valid for the next command's snapshot.
        assertDoesNotThrow(() -> logic.execute("list"));
        assertEquals(List.of(ALICE, BENSON, CARL, DANIEL, ELLE), model.getFilteredApplicantList());
        assertEquals(dataBefore, model.getRecruitDexData());
        assertEquals(0, saveCount.get());
    }

    private static AtomicInteger observeChanges(ModelManager model) {
        AtomicInteger notifications = new AtomicInteger();
        model.getComparisonList().getMemberIds().addListener(
                (SetChangeListener<ApplicantId>) change -> notifications.incrementAndGet());
        model.getComparisonList().getDecisionHistory().addListener(
                (ListChangeListener<ComparisonDecision>) change -> notifications.incrementAndGet());
        model.getComparisonList().getComparisonState().currentPairProperty().addListener(
                observable -> notifications.incrementAndGet());
        model.getRankingEngine().getRanking().addListener(
                (ListChangeListener<RankingEntry>) change -> notifications.incrementAndGet());
        model.getApplicantRegistry().getApplicantList().addListener(
                (ListChangeListener<Applicant>) change -> notifications.incrementAndGet());
        return notifications;
    }

    private static ModelManager createModel(String command) {
        ModelManager model = new ModelManager();
        for (Applicant applicant : List.of(ALICE, BENSON, CARL, DANIEL, ELLE)) {
            model.getApplicantRegistry().add(applicant);
        }
        List<ComparisonDecision> decisions = createHistory();
        if (command.equals("compare-add")) {
            // Daniel's retained decisions become eligible when he rejoins the pool.
            model.getComparisonList().resetData(Set.of(ALICE.getId(), BENSON.getId(), CARL.getId()), decisions,
                    Optional.of(new ComparisonPair(ALICE.getId(), BENSON.getId())));
        } else {
            // Elle connects the two heavily compared groups, allowing the initial ranking to converge.
            // Removing or deleting her leaves the same slow graph used by RankingEngineTest.
            for (Applicant applicant : List.of(ALICE, BENSON, CARL, DANIEL)) {
                decisions.add(new ComparisonDecision(applicant.getId(), ELLE.getId(), applicant.getId()));
                decisions.add(new ComparisonDecision(ELLE.getId(), applicant.getId(), ELLE.getId()));
            }
            model.getComparisonList().resetData(
                    Set.of(ALICE.getId(), BENSON.getId(), CARL.getId(), DANIEL.getId(), ELLE.getId()), decisions,
                    Optional.of(new ComparisonPair(ELLE.getId(), ALICE.getId())));
        }
        return model;
    }

    private static List<ComparisonDecision> createHistory() {
        List<ComparisonDecision> decisions = new ArrayList<>();
        for (int repetition = 0; repetition < 1000; repetition++) {
            decisions.add(new ComparisonDecision(ALICE.getId(), BENSON.getId(), ALICE.getId()));
            decisions.add(new ComparisonDecision(ALICE.getId(), BENSON.getId(), BENSON.getId()));
            decisions.add(new ComparisonDecision(CARL.getId(), DANIEL.getId(), CARL.getId()));
            decisions.add(new ComparisonDecision(CARL.getId(), DANIEL.getId(), DANIEL.getId()));
        }
        decisions.add(new ComparisonDecision(ALICE.getId(), CARL.getId(), ALICE.getId()));
        return decisions;
    }
}
