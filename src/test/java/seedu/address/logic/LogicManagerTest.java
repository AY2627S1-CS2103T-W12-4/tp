package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalApplicants.ALICE;
import static seedu.address.testutil.TypicalApplicants.AMY;
import static seedu.address.testutil.TypicalApplicants.BENSON;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddToComparisonCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.CompareCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RemoveFromComparisonCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyRecruitDexData;
import seedu.address.model.applicant.Applicant;
import seedu.address.model.applicant.NameContainsKeywordsPredicate;
import seedu.address.model.comparison.ComparisonDecision;
import seedu.address.model.comparison.ComparisonPair;
import seedu.address.model.ranking.RankingEntry;
import seedu.address.model.userprefs.UserPrefs;
import seedu.address.storage.JsonRecruitDexDataStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.ApplicantBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonRecruitDexDataStorage recruitDexDataStorage =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(recruitDexDataStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_compareAdd_updatesRankingAndPersistsMembership() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        logic.execute("find Benson");

        CommandResult result = logic.execute(AddToComparisonCommand.COMMAND_WORD + " 1");

        assertEquals(String.format(AddToComparisonCommand.MESSAGE_SUCCESS, Messages.format(BENSON)),
                result.getFeedbackToUser());
        assertEquals(Set.of(BENSON.getId()), model.getComparisonList().getMemberIds());
        assertEquals(1, model.getRankingEngine().getRanking().size());
        assertEquals(BENSON.getId(), model.getRankingEngine().getRanking().get(0).getApplicantId());
        assertEquals(List.of(BENSON), logic.getFilteredApplicantList());
        JsonRecruitDexDataStorage savedRecords =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ModelManager reopenedModel =
                new ModelManager(savedRecords.readRecruitDexData().orElseThrow(), new UserPrefs());
        assertEquals(List.of(ALICE, BENSON), reopenedModel.getApplicantRegistry().getApplicantList());
        assertEquals(Set.of(BENSON.getId()), reopenedModel.getComparisonList().getMemberIds());
        assertEquals(model.getRankingEngine().getRanking(), reopenedModel.getRankingEngine().getRanking());
    }

    @Test
    public void execute_secondComparisonMember_populatesObservablePairAndPersistsPresentedSides() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        var pairProperty = logic.getCurrentPairProperty();
        logic.execute("compare-add 1");
        assertTrue(pairProperty.get().isEmpty());

        logic.execute("compare-add 2");

        ComparisonPair selectedPair = pairProperty.get().orElseThrow();
        assertEquals(Set.of(ALICE.getId(), BENSON.getId()),
                Set.of(selectedPair.getFirstApplicantId(), selectedPair.getSecondApplicantId()));
        JsonRecruitDexDataStorage saved =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ModelManager reopened = new ModelManager(saved.readRecruitDexData().orElseThrow(), new UserPrefs());
        assertEquals(Optional.of(selectedPair), reopened.getComparisonList().getCurrentPair());
        assertTrue(reopened.getComparisonList().getDecisionHistory().isEmpty());
        assertEquals(model.getRankingEngine().getRanking(), reopened.getRankingEngine().getRanking());
    }

    @Test
    public void execute_choose_recordsDisplayedSideAndPersistsHistoryRankingAndNextPair() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        logic.execute("compare-add 1");
        logic.execute("compare-add 2");
        logic.execute("find Alice");
        ComparisonPair presentedPair = logic.getCurrentPairProperty().get().orElseThrow();
        Applicant winner = model.getApplicantRegistry().get(presentedPair.getFirstApplicantId());
        Applicant loser = model.getApplicantRegistry().get(presentedPair.getSecondApplicantId());
        ObservableList<RankingEntry> ranking = logic.getRanking();

        CommandResult result = logic.execute("choose a");

        assertEquals(String.format(CompareCommand.MESSAGE_SUCCESS, winner.getName(), loser.getName()),
                result.getFeedbackToUser());
        ComparisonDecision firstDecision = model.getComparisonList().getDecisionHistory().get(0);
        assertEquals(presentedPair.getFirstApplicantId(), firstDecision.getFirstApplicantId());
        assertEquals(presentedPair.getSecondApplicantId(), firstDecision.getSecondApplicantId());
        assertEquals(winner.getId(), firstDecision.getWinnerId());
        assertSame(ranking, logic.getRanking());
        assertEquals(winner.getId(), ranking.get(0).getApplicantId());
        assertEquals(List.of(ALICE), logic.getFilteredApplicantList());

        JsonRecruitDexDataStorage saved =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ModelManager reopened = new ModelManager(saved.readRecruitDexData().orElseThrow(), new UserPrefs());
        assertEquals(model.getRecruitDexData(), reopened.getRecruitDexData());
        assertEquals(List.of(firstDecision), reopened.getComparisonList().getDecisionHistory());
        assertEquals(logic.getCurrentPairProperty().get(), reopened.getComparisonList().getCurrentPair());
        assertEquals(ranking, reopened.getRankingEngine().getRanking());

        ComparisonPair nextPair = logic.getCurrentPairProperty().get().orElseThrow();
        logic.execute("choose B");
        ComparisonDecision secondDecision = model.getComparisonList().getDecisionHistory().get(1);
        assertEquals(nextPair.getSecondApplicantId(), secondDecision.getWinnerId());
        assertNotEquals(firstDecision.getDecisionId(), secondDecision.getDecisionId());
        reopened.setRecruitDexData(saved.readRecruitDexData().orElseThrow());
        assertEquals(List.of(firstDecision, secondDecision), reopened.getComparisonList().getDecisionHistory());
        assertEquals(model.getRecruitDexData(), reopened.getRecruitDexData());
        assertEquals(ranking, reopened.getRankingEngine().getRanking());
    }

    @Test
    public void execute_chooseWithoutPair_rejectsCommandAndPreservesSavedData() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        logic.execute("compare-add 1");
        JsonRecruitDexDataStorage saved =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ReadOnlyRecruitDexData savedBefore = saved.readRecruitDexData().orElseThrow();

        assertCommandException("choose a", CompareCommand.MESSAGE_NO_PENDING_PAIR);

        assertEquals(savedBefore, saved.readRecruitDexData().orElseThrow());
        assertTrue(model.getComparisonList().getDecisionHistory().isEmpty());
    }

    @Test
    public void execute_savesCurrentHistoryAndPendingPairAfterEveryCommand() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getComparisonList().add(ALICE.getId());
        model.getComparisonList().add(BENSON.getId());
        ComparisonPair pair = new ComparisonPair(BENSON.getId(), ALICE.getId());
        model.getComparisonList().getComparisonState().setCurrentPair(pair);
        ComparisonDecision decision = model.getComparisonList().recordDecision(ALICE.getId());
        model.getComparisonList().getComparisonState().setCurrentPair(pair);

        logic.execute("find Alice");

        JsonRecruitDexDataStorage saved =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ModelManager reopened = new ModelManager(saved.readRecruitDexData().orElseThrow(), new UserPrefs());
        assertEquals(model.getRecruitDexData(), reopened.getRecruitDexData());
        assertEquals(List.of(decision), reopened.getComparisonList().getDecisionHistory());
        assertEquals(Optional.of(pair), reopened.getComparisonList().getCurrentPair());
        assertEquals(model.getRankingEngine().getRanking(), reopened.getRankingEngine().getRanking());
        assertEquals(List.of(ALICE, BENSON), reopened.getFilteredApplicantList());

        logic.execute("clear");
        reopened.setRecruitDexData(saved.readRecruitDexData().orElseThrow());
        assertTrue(reopened.getApplicantRegistry().getApplicantList().isEmpty());
        assertTrue(reopened.getComparisonList().getMemberIds().isEmpty());
        assertTrue(reopened.getComparisonList().getDecisionHistory().isEmpty());
        assertTrue(reopened.getComparisonList().getCurrentPair().isEmpty());
        assertTrue(reopened.getRankingEngine().getRanking().isEmpty());
    }

    @Test
    public void execute_compareRemove_updatesRankingAndPreservesRecordsAndFilter() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 1");
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 2");
        logic.execute("find Benson");
        ObservableList<RankingEntry> ranking = logic.getRanking();

        CommandResult result = logic.execute(RemoveFromComparisonCommand.COMMAND_WORD + " 1");

        assertEquals(String.format(RemoveFromComparisonCommand.MESSAGE_SUCCESS, Messages.format(BENSON)),
                result.getFeedbackToUser());
        assertEquals(Set.of(ALICE.getId()), model.getComparisonList().getMemberIds());
        assertSame(ranking, logic.getRanking());
        assertEquals(1, ranking.size());
        assertEquals(ALICE.getId(), ranking.get(0).getApplicantId());
        assertEquals(List.of(BENSON), logic.getFilteredApplicantList());
        assertEquals(List.of(ALICE, BENSON), model.getApplicantRegistry().getApplicantList());
        JsonRecruitDexDataStorage savedRecords =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        assertEquals(List.of(ALICE, BENSON), savedRecords.readRecruitDexData().orElseThrow().getApplicantList());

        CommandResult repeatedResult = logic.execute(RemoveFromComparisonCommand.COMMAND_WORD + " 1");

        assertEquals(RemoveFromComparisonCommand.MESSAGE_NOT_A_MEMBER, repeatedResult.getFeedbackToUser());
        assertEquals(Set.of(ALICE.getId()), model.getComparisonList().getMemberIds());
        assertEquals(1, ranking.size());
        assertEquals(ALICE.getId(), ranking.get(0).getApplicantId());
        assertEquals(List.of(BENSON), logic.getFilteredApplicantList());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredApplicantList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredApplicantList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getRecruitDexData(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonRecruitDexDataStorage that throws the IOException e when saving
        JsonRecruitDexDataStorage recruitDexDataStorage = new JsonRecruitDexDataStorage(prefPath) {
            @Override
            public void saveRecruitDexData(ReadOnlyRecruitDexData recruitDexData) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(recruitDexDataStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveRecruitDexData method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Applicant expectedApplicant = new ApplicantBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.getApplicantRegistry().add(expectedApplicant);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void getApplicantList_modifyList_throwsUnsupportedOperationException() {
        model.getApplicantRegistry().add(ALICE);
        assertThrows(UnsupportedOperationException.class, () -> logic.getApplicantList().remove(0));
    }

    @Test
    public void getApplicantList_includesApplicantsHiddenByTheSearchFilter() {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);

        model.updateFilteredApplicantList(new NameContainsKeywordsPredicate(List.of("Alice")));

        assertEquals(1, logic.getFilteredApplicantList().size());
        assertEquals(List.of(ALICE, BENSON), logic.getApplicantList());
    }

    @Test
    public void getApplicantList_isLive() {
        assertEquals(List.of(), logic.getApplicantList());
        model.getApplicantRegistry().add(ALICE);
        assertEquals(List.of(ALICE), logic.getApplicantList());
    }

    @Test
    public void getCurrentPairProperty_followsTheModelState() {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        ComparisonPair pair = new ComparisonPair(ALICE.getId(), BENSON.getId());
        assertEquals(Optional.empty(), logic.getCurrentPairProperty().get());

        model.getComparisonList().getComparisonState().setCurrentPair(pair);
        assertEquals(Optional.of(pair), logic.getCurrentPairProperty().get());

        model.getComparisonList().getComparisonState().clearCurrentPair();
        assertEquals(Optional.empty(), logic.getCurrentPairProperty().get());
    }

    @Test
    public void getCurrentPairProperty_notifiesListeners() {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        List<Optional<ComparisonPair>> seen = new ArrayList<>();
        logic.getCurrentPairProperty().addListener((observable, oldValue, newValue) -> seen.add(newValue));
        ComparisonPair pair = new ComparisonPair(BENSON.getId(), ALICE.getId());

        model.getComparisonList().getComparisonState().setCurrentPair(pair);

        assertEquals(List.of(Optional.of(pair)), seen);
    }

    @Test
    public void getRanking_recalculation_updatesSameLiveView() {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        ObservableList<RankingEntry> rankingEntries = logic.getRanking();
        assertSame(model.getRankingEngine().getRanking(), rankingEntries);
        assertTrue(rankingEntries.isEmpty());

        model.getRankingEngine().recalculate(Set.of(ALICE.getId()), List.of());
        assertEquals(List.of(new RankingEntry(ALICE.getId(), 1, OptionalDouble.of(0), 0)), rankingEntries);

        model.getRankingEngine().recalculate(Set.of(BENSON.getId()), List.of());
        assertSame(rankingEntries, logic.getRanking());
        assertEquals(List.of(new RankingEntry(BENSON.getId(), 1, OptionalDouble.of(0), 0)), rankingEntries);
    }

    @Test
    public void getRanking_modifyView_throwsUnsupportedOperationException() {
        model.getApplicantRegistry().add(ALICE);
        model.getRankingEngine().recalculate(Set.of(ALICE.getId()), List.of());
        List<RankingEntry> originalEntries = List.copyOf(logic.getRanking());

        assertThrows(UnsupportedOperationException.class, () -> logic.getRanking().clear());

        assertEquals(originalEntries, logic.getRanking());
    }

    @Test
    public void getRanking_searchFilter_preservesCompleteRanking() {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getRankingEngine().recalculate(Set.of(ALICE.getId(), BENSON.getId()),
                List.of(new ComparisonDecision(ALICE.getId(), BENSON.getId(), ALICE.getId())));
        List<RankingEntry> originalEntries = List.copyOf(logic.getRanking());

        model.updateFilteredApplicantList(applicant -> applicant.getId().equals(ALICE.getId()));

        assertEquals(List.of(ALICE), logic.getFilteredApplicantList());
        assertEquals(List.of(ALICE, BENSON), logic.getApplicantList());
        assertEquals(originalEntries, logic.getRanking());
    }

    @Test
    public void execute_clearCommand_clearsRetainedRankingView() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getRankingEngine().recalculate(Set.of(ALICE.getId()), List.of());
        ObservableList<RankingEntry> rankingEntries = logic.getRanking();

        logic.execute("clear");

        assertSame(rankingEntries, logic.getRanking());
        assertTrue(rankingEntries.isEmpty());
    }
}
