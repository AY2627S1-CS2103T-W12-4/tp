package seedu.recruitdex.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_APPLICANT_DISPLAYED_INDEX;
import static seedu.recruitdex.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.recruitdex.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
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
import java.util.OptionalDouble;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.recruitdex.logic.commands.AddCommand;
import seedu.recruitdex.logic.commands.AddToComparisonCommand;
import seedu.recruitdex.logic.commands.CommandResult;
import seedu.recruitdex.logic.commands.CompareCommand;
import seedu.recruitdex.logic.commands.DeleteCommand;
import seedu.recruitdex.logic.commands.ExitCommand;
import seedu.recruitdex.logic.commands.HelpCommand;
import seedu.recruitdex.logic.commands.ListCommand;
import seedu.recruitdex.logic.commands.RemoveFromComparisonCommand;
import seedu.recruitdex.logic.commands.SwitchTabCommand;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.logic.parser.exceptions.ParseException;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.ReadOnlyRecruitDexData;
import seedu.recruitdex.model.RecruitDexData;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.NameContainsKeywordsPredicate;
import seedu.recruitdex.model.comparison.ComparisonDecision;
import seedu.recruitdex.model.comparison.ComparisonPair;
import seedu.recruitdex.model.ranking.RankingEntry;
import seedu.recruitdex.model.tag.Tag;
import seedu.recruitdex.model.userprefs.UserPrefs;
import seedu.recruitdex.storage.JsonRecruitDexDataStorage;
import seedu.recruitdex.storage.JsonUserPrefsStorage;
import seedu.recruitdex.storage.StorageManager;
import seedu.recruitdex.testutil.ApplicantBuilder;

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
    public void execute_addAndEditPunctuatedSkills_persistAfterReopening() throws Exception {
        logic.execute("add -n Alice -t C++ -t node.js");
        JsonRecruitDexDataStorage savedRecords =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        Applicant added = savedRecords.readRecruitDexData().orElseThrow().getApplicantList().get(0);
        assertEquals("Alice", added.getName().fullName);
        assertEquals(Set.of(new Tag("C++"), new Tag("node.js")), added.getTags());

        logic.execute("edit 1 -t C# -t .NET");
        Applicant edited = savedRecords.readRecruitDexData().orElseThrow().getApplicantList().get(0);
        assertEquals(added.getId(), edited.getId());
        assertEquals(Set.of(new Tag("C#"), new Tag(".NET")), edited.getTags());
    }

    @Test
    public void execute_addNameOnly_persistsEmptyOptionalDetails() throws Exception {
        CommandResult result = logic.execute("add -nJane Doe");
        assertEquals("New applicant added: Jane Doe", result.getFeedbackToUser());

        Applicant added = model.getApplicantRegistry().getApplicantList().get(0);
        assertEquals("Jane Doe", added.getName().fullName);
        assertEquals("", added.getPhone().value);
        assertEquals("", added.getEmail().value);
        assertEquals("", added.getAddress().value);
        assertEquals("", added.getInterviewNotes().value);
        assertEquals("", added.getYearsOfExperience().value);
        assertEquals("", added.getSource().value);
        assertTrue(added.getTags().isEmpty());

        JsonRecruitDexDataStorage savedRecords =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        Applicant reopened = savedRecords.readRecruitDexData().orElseThrow().getApplicantList().get(0);
        assertEquals(added, reopened);
        assertEquals(added.getId(), reopened.getId());

        logic.execute("edit 1 -p91234567 -ejane@example.com -aBlk 1 Clementi");
        Applicant edited = model.getApplicantRegistry().getApplicantList().get(0);
        assertEquals(added.getId(), edited.getId());
        assertEquals("91234567", edited.getPhone().value);
        assertEquals("jane@example.com", edited.getEmail().value);
        assertEquals("Blk 1 Clementi", edited.getAddress().value);
    }

    @Test
    public void execute_compareAdd_updatesRankingAndPersistsMembership() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        logic.execute("find -n Benson");

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
        logic.execute("cadd 1");
        assertTrue(pairProperty.get().isEmpty());

        logic.execute("cadd 2");

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
        logic.execute("cadd 1");
        logic.execute("cadd 2");
        logic.execute("find -n Alice");
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
        logic.execute("cadd 1");
        JsonRecruitDexDataStorage saved =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ReadOnlyRecruitDexData savedBefore = saved.readRecruitDexData().orElseThrow();

        assertCommandException("choose a", CompareCommand.MESSAGE_NO_PENDING_PAIR);

        assertEquals(savedBefore, saved.readRecruitDexData().orElseThrow());
        assertTrue(model.getComparisonList().getDecisionHistory().isEmpty());
    }

    @Test
    public void execute_savesCurrentHistoryAndPendingPairAfterDataChange() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getComparisonList().add(ALICE.getId());
        model.getComparisonList().add(BENSON.getId());
        ComparisonPair pair = new ComparisonPair(BENSON.getId(), ALICE.getId());
        model.getComparisonList().resetData(
                Set.copyOf(model.getComparisonList().getMemberIds()),
                List.copyOf(model.getComparisonList().getDecisionHistory()),
                Optional.of(pair));
        ComparisonDecision decision = model.getComparisonList().recordDecision(ALICE.getId());
        model.getComparisonList().resetData(
                Set.copyOf(model.getComparisonList().getMemberIds()),
                List.copyOf(model.getComparisonList().getDecisionHistory()),
                Optional.of(pair));

        logic.execute("add -nCarol Lee");

        JsonRecruitDexDataStorage saved =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ModelManager reopened = new ModelManager(saved.readRecruitDexData().orElseThrow(), new UserPrefs());
        assertEquals(model.getRecruitDexData(), reopened.getRecruitDexData());
        assertEquals(List.of(decision), reopened.getComparisonList().getDecisionHistory());
        assertEquals(Optional.of(pair), reopened.getComparisonList().getCurrentPair());
        assertEquals(model.getRankingEngine().getRanking(), reopened.getRankingEngine().getRanking());
        assertEquals(model.getApplicantRegistry().getApplicantList(), reopened.getFilteredApplicantList());

        logic.execute("clear");
        reopened.setRecruitDexData(saved.readRecruitDexData().orElseThrow());
        assertTrue(reopened.getApplicantRegistry().getApplicantList().isEmpty());
        assertTrue(reopened.getComparisonList().getMemberIds().isEmpty());
        assertTrue(reopened.getComparisonList().getDecisionHistory().isEmpty());
        assertTrue(reopened.getComparisonList().getCurrentPair().isEmpty());
        assertTrue(reopened.getRankingEngine().getRanking().isEmpty());
    }

    @Test
    public void execute_compareRemove_persistsRemovalAndClearedPairAcrossRestart() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 1");
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 2");
        logic.execute("choose a");
        List<ComparisonDecision> historyBefore = List.copyOf(model.getComparisonList().getDecisionHistory());
        logic.execute("find -n Benson");
        ObservableList<RankingEntry> ranking = logic.getRanking();

        CommandResult result = logic.execute(RemoveFromComparisonCommand.COMMAND_WORD + " 1");

        assertEquals(String.format(RemoveFromComparisonCommand.MESSAGE_SUCCESS, Messages.format(BENSON)),
                result.getFeedbackToUser());
        assertEquals(Set.of(ALICE.getId()), model.getComparisonList().getMemberIds());
        assertSame(ranking, logic.getRanking());
        assertEquals(1, ranking.size());
        assertEquals(ALICE.getId(), ranking.get(0).getApplicantId());
        assertEquals(0, ranking.get(0).getComparisonCount());
        assertTrue(logic.getCurrentPairProperty().get().isEmpty());
        assertEquals(List.of(BENSON), logic.getFilteredApplicantList());
        assertEquals(List.of(ALICE, BENSON), model.getApplicantRegistry().getApplicantList());
        JsonRecruitDexDataStorage savedRecords =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ModelManager reopened = new ModelManager(savedRecords.readRecruitDexData().orElseThrow(), new UserPrefs());
        assertEquals(model.getRecruitDexData(), reopened.getRecruitDexData());
        assertEquals(List.of(ALICE, BENSON), reopened.getApplicantRegistry().getApplicantList());
        assertEquals(Set.of(ALICE.getId()), reopened.getComparisonList().getMemberIds());
        assertEquals(historyBefore, reopened.getComparisonList().getDecisionHistory());
        assertTrue(reopened.getComparisonList().getCurrentPair().isEmpty());
        assertEquals(ranking, reopened.getRankingEngine().getRanking());
        assertEquals(List.of(ALICE, BENSON), reopened.getFilteredApplicantList());

        assertThrows(CommandException.class, RemoveFromComparisonCommand.MESSAGE_NOT_A_MEMBER, () ->
                logic.execute(RemoveFromComparisonCommand.COMMAND_WORD + " 1"));
        assertEquals(Set.of(ALICE.getId()), model.getComparisonList().getMemberIds());
        assertEquals(1, ranking.size());
        assertEquals(ALICE.getId(), ranking.get(0).getApplicantId());
        assertEquals(List.of(BENSON), logic.getFilteredApplicantList());
        assertEquals(reopened.getRecruitDexData(), savedRecords.readRecruitDexData().orElseThrow());
    }

    @Test
    public void execute_comparisonMembershipErrors_throwsBeforeSaving() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 1");
        ReadOnlyRecruitDexData dataBefore = new RecruitDexData(model.getRecruitDexData());
        JsonRecruitDexDataStorage savedRecords =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        failingStorageLogic(DUMMY_IO_EXCEPTION);

        assertCommandException(AddToComparisonCommand.COMMAND_WORD + " 1",
                AddToComparisonCommand.MESSAGE_DUPLICATE_MEMBER);
        assertCommandException(RemoveFromComparisonCommand.COMMAND_WORD + " 2",
                RemoveFromComparisonCommand.MESSAGE_NOT_A_MEMBER);

        assertEquals(dataBefore, model.getRecruitDexData());
        assertEquals(dataBefore, savedRecords.readRecruitDexData().orElseThrow());
        assertEquals(List.of(ALICE, BENSON), logic.getFilteredApplicantList());
    }

    @Test
    public void execute_compareRemove_persistsReplacementPairAndRetainedHistoryAcrossRestart() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getApplicantRegistry().add(CARL);
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 1");
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 2");
        logic.execute("choose a");
        ComparisonDecision decision = model.getComparisonList().getDecisionHistory().get(0);
        logic.execute(AddToComparisonCommand.COMMAND_WORD + " 3");
        logic.execute("find -n Benson");

        logic.execute(RemoveFromComparisonCommand.COMMAND_WORD + " 1");

        ComparisonPair replacementPair = logic.getCurrentPairProperty().get().orElseThrow();
        assertEquals(Set.of(ALICE.getId(), CARL.getId()),
                Set.of(replacementPair.getFirstApplicantId(), replacementPair.getSecondApplicantId()));
        JsonRecruitDexDataStorage savedRecords =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("recruitDexData.json"));
        ModelManager reopened = new ModelManager(savedRecords.readRecruitDexData().orElseThrow(), new UserPrefs());
        assertEquals(model.getRecruitDexData(), reopened.getRecruitDexData());
        assertEquals(List.of(ALICE, BENSON, CARL), reopened.getApplicantRegistry().getApplicantList());
        assertEquals(Set.of(ALICE.getId(), CARL.getId()), reopened.getComparisonList().getMemberIds());
        assertEquals(List.of(decision), reopened.getComparisonList().getDecisionHistory());
        assertEquals(Optional.of(replacementPair), reopened.getComparisonList().getCurrentPair());
        assertEquals(logic.getRanking(), reopened.getRankingEngine().getRanking());
        assertEquals(2, reopened.getRankingEngine().getRanking().size());
        assertTrue(reopened.getRankingEngine().getRanking().stream()
                .allMatch(entry -> entry.getComparisonCount() == 0));

        Logic reopenedLogic = new LogicManager(reopened, new StorageManager(savedRecords,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))));

        reopenedLogic.execute(AddToComparisonCommand.COMMAND_WORD + " 2");

        assertEquals(Set.of(ALICE.getId(), BENSON.getId(), CARL.getId()), reopened.getComparisonList().getMemberIds());
        assertEquals(List.of(decision), reopened.getComparisonList().getDecisionHistory());
        assertEquals(Optional.of(replacementPair), reopened.getComparisonList().getCurrentPair());
        assertEquals(3, reopenedLogic.getRanking().size());
        assertEquals(decision.getWinnerId(), reopenedLogic.getRanking().get(0).getApplicantId());
        assertEquals(1, reopenedLogic.getRanking().get(0).getComparisonCount());
        assertEquals(2, reopenedLogic.getRanking().stream().mapToInt(RankingEntry::getComparisonCount).sum());
        assertEquals(reopened.getRecruitDexData(), savedRecords.readRecruitDexData().orElseThrow());
    }

    @Test
    public void execute_helpWithFailingStorage_returnsHelpResult() throws Exception {
        assertUiCommandSucceedsWithFailingStorage(HelpCommand.COMMAND_WORD,
                new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false));
    }

    @Test
    public void execute_switchTabWithFailingStorage_returnsSwitchTabResult() throws Exception {
        assertUiCommandSucceedsWithFailingStorage(SwitchTabCommand.COMMAND_WORD,
                new CommandResult(SwitchTabCommand.MESSAGE_SUCCESS, false, false, true));
    }

    @Test
    public void execute_exitWithFailingStorage_returnsExitResult() throws Exception {
        assertUiCommandSucceedsWithFailingStorage(ExitCommand.COMMAND_WORD,
                new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true));
    }

    @Test
    public void execute_listWithFailingStorage_showsAllApplicants() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        logic.execute("find -n Benson");
        failingStorageLogic(DUMMY_AD_EXCEPTION);

        assertEquals(new CommandResult(ListCommand.MESSAGE_SUCCESS), logic.execute(ListCommand.COMMAND_WORD));
        assertEquals(List.of(ALICE, BENSON), logic.getFilteredApplicantList());
    }

    @Test
    public void execute_findWithFailingStorage_filtersApplicants() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        failingStorageLogic(DUMMY_IO_EXCEPTION);

        logic.execute("find -n Benson");
        assertEquals(List.of(BENSON), logic.getFilteredApplicantList());
    }

    @Test
    public void execute_commandsLeavingDataUnchanged_doNotReplaceDataFile() throws Exception {
        model.getApplicantRegistry().add(ALICE);
        Path dataFile = temporaryFolder.resolve("recruitDexData.json");
        Files.writeString(dataFile, "unloadable data");

        for (String command : List.of("list", "find -n Alice", "help", "sw", "exit")) {
            logic.execute(command);
        }
        assertEquals("unloadable data", Files.readString(dataFile));

        logic.execute("add -nCarol Lee");
        assertNotEquals("unloadable data", Files.readString(dataFile));
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
        // the save fails, so the add is undone and the model is as it was before the command
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void execute_storageThrowsIoException_restoresDataThatWasThereBefore() {
        model.getApplicantRegistry().add(ALICE);
        model.getComparisonList().add(ALICE.getId());
        ModelManager expectedModel = new ModelManager(model.getRecruitDexData(), new UserPrefs());
        failingStorageLogic(DUMMY_IO_EXCEPTION);

        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertThrows(CommandException.class, () -> logic.execute(addCommand));

        assertEquals(expectedModel.getRecruitDexData(), model.getRecruitDexData());
        assertEquals(List.of(ALICE), model.getApplicantRegistry().getApplicantList());
        assertTrue(model.getComparisonList().getMemberIds().contains(ALICE.getId()));
    }

    @Test
    public void execute_storageThrowsAccessDenied_restoresDataThatWasThereBefore() {
        model.getApplicantRegistry().add(ALICE);
        failingStorageLogic(DUMMY_AD_EXCEPTION);

        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertThrows(CommandException.class, () -> logic.execute(addCommand));

        assertEquals(List.of(ALICE), model.getApplicantRegistry().getApplicantList());
    }

    @Test
    public void execute_storageThrowsIoException_failedDeleteIsUndone() {
        model.getApplicantRegistry().add(ALICE);
        model.getComparisonList().add(ALICE.getId());
        failingStorageLogic(DUMMY_IO_EXCEPTION);

        assertThrows(CommandException.class, () -> logic.execute(DeleteCommand.COMMAND_WORD + " 1"));

        // both the record and its membership of the comparison list are back
        assertEquals(List.of(ALICE), model.getApplicantRegistry().getApplicantList());
        assertTrue(model.getComparisonList().getMemberIds().contains(ALICE.getId()));
    }

    @Test
    public void execute_failedAddWithIoException_preservesSearchFilter() throws Exception {
        assertFailedCommandPreservesSearchFilter("add -nCarol Lee", DUMMY_IO_EXCEPTION);
    }

    @Test
    public void execute_failedAddWithAccessDenied_preservesSearchFilter() throws Exception {
        assertFailedCommandPreservesSearchFilter("add -nCarol Lee", DUMMY_AD_EXCEPTION);
    }

    @Test
    public void execute_failedEditWithIoException_preservesSearchFilter() throws Exception {
        assertFailedCommandPreservesSearchFilter("edit 1 -nOther Name", DUMMY_IO_EXCEPTION);
    }

    @Test
    public void execute_failedEditWithAccessDenied_preservesSearchFilter() throws Exception {
        assertFailedCommandPreservesSearchFilter("edit 1 -nOther Name", DUMMY_AD_EXCEPTION);
    }

    /** Checks both the restored search results and whether the original filter still matches future edits. */
    private void assertFailedCommandPreservesSearchFilter(String command, IOException exception) throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getComparisonList().add(ALICE.getId());
        model.getComparisonList().add(BENSON.getId());
        model.getComparisonList().recordDecision(ALICE.getId());
        logic.execute("find -n Benson");
        ObservableList<Applicant> displayed = logic.getFilteredApplicantList();
        ReadOnlyRecruitDexData dataBeforeCommand = new RecruitDexData(model.getRecruitDexData());
        List<RankingEntry> rankingBeforeCommand = List.copyOf(logic.getRanking());
        failingStorageLogic(exception);

        assertThrows(CommandException.class, () -> logic.execute(command));

        assertSame(displayed, logic.getFilteredApplicantList());
        assertEquals(List.of(BENSON), displayed);
        assertEquals(dataBeforeCommand, model.getRecruitDexData());
        assertEquals(rankingBeforeCommand, logic.getRanking());

        Applicant matchingAlice = new ApplicantBuilder(ALICE).withName("Benson Pauline").build();
        model.getApplicantRegistry().edit(ALICE.getId(), matchingAlice);
        assertEquals(List.of(matchingAlice, BENSON), displayed);
    }

    @Test
    public void execute_storageWorks_keepsTheChange() throws Exception {
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        logic.execute(addCommand);
        assertEquals(1, model.getApplicantRegistry().getApplicantList().size());
    }

    /** Checks that a UI action survives save failures without changing recruitment data or the search results. */
    private void assertUiCommandSucceedsWithFailingStorage(String command, CommandResult expected) throws Exception {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getComparisonList().add(ALICE.getId());
        model.getComparisonList().add(BENSON.getId());
        model.getComparisonList().recordDecision(ALICE.getId());
        logic.execute("find -n Benson");
        ReadOnlyRecruitDexData dataBeforeCommand = new RecruitDexData(model.getRecruitDexData());
        List<RankingEntry> rankingBeforeCommand = List.copyOf(logic.getRanking());

        for (IOException exception : List.of(DUMMY_IO_EXCEPTION, DUMMY_AD_EXCEPTION)) {
            failingStorageLogic(exception);

            assertEquals(expected, logic.execute(command));
            assertEquals(dataBeforeCommand, model.getRecruitDexData());
            assertEquals(rankingBeforeCommand, logic.getRanking());
            assertEquals(List.of(BENSON), logic.getFilteredApplicantList());
        }
    }

    /** Points {@code logic} at a storage whose saves always throw {@code exception}. */
    private void failingStorageLogic(IOException exception) {
        JsonRecruitDexDataStorage recruitDexDataStorage =
                new JsonRecruitDexDataStorage(temporaryFolder.resolve("failing.json")) {
                    @Override
                    public void saveRecruitDexData(ReadOnlyRecruitDexData recruitDexData) throws IOException {
                        throw exception;
                    }
                };
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"));
        logic = new LogicManager(model, new StorageManager(recruitDexDataStorage, userPrefsStorage));
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
        model.getComparisonList().add(ALICE.getId());
        assertEquals(Optional.empty(), logic.getCurrentPairProperty().get());

        model.getComparisonList().add(BENSON.getId());
        ComparisonPair pair = model.getComparisonList().getCurrentPair().orElseThrow();
        assertEquals(Optional.of(pair), logic.getCurrentPairProperty().get());

        model.getComparisonList().remove(BENSON.getId());
        assertEquals(Optional.empty(), logic.getCurrentPairProperty().get());
    }

    @Test
    public void getCurrentPairProperty_notifiesListeners() {
        model.getApplicantRegistry().add(ALICE);
        model.getApplicantRegistry().add(BENSON);
        model.getComparisonList().add(ALICE.getId());
        List<Optional<ComparisonPair>> seen = new ArrayList<>();
        logic.getCurrentPairProperty().addListener((observable, oldValue, newValue) -> seen.add(newValue));

        model.getComparisonList().add(BENSON.getId());

        assertEquals(List.of(model.getComparisonList().getCurrentPair()), seen);
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
