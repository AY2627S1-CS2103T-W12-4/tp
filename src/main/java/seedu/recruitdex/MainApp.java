package seedu.recruitdex;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.recruitdex.commons.core.LogsCenter;
import seedu.recruitdex.commons.exceptions.DataLoadingException;
import seedu.recruitdex.commons.util.StringUtil;
import seedu.recruitdex.logic.Logic;
import seedu.recruitdex.logic.LogicManager;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.ReadOnlyRecruitDexData;
import seedu.recruitdex.model.RecruitDexData;
import seedu.recruitdex.model.userprefs.ReadOnlyUserPrefs;
import seedu.recruitdex.model.userprefs.UserPrefs;
import seedu.recruitdex.model.util.SampleDataUtil;
import seedu.recruitdex.storage.JsonRecruitDexDataStorage;
import seedu.recruitdex.storage.JsonUserPrefsStorage;
import seedu.recruitdex.storage.Storage;
import seedu.recruitdex.storage.StorageManager;
import seedu.recruitdex.ui.Ui;
import seedu.recruitdex.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.3";

    // One sentence per line, because the result display does not wrap long lines.
    public static final String MESSAGE_DATA_FILE_BACKED_UP = "Could not load %s, so RecruitDex started with no"
            + " applicants.\nA copy was saved as %s.\nYour next change will replace %s.";
    public static final String MESSAGE_DATA_FILE_NOT_BACKED_UP = "Could not load %s, so RecruitDex started with no"
            + " applicants.\nCopy the file somewhere safe now: your next change will replace it.";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path RECRUIT_DEX_DATA_FILE_PATH = Paths.get("data", "recruitdex.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Optional<String> startupMessage = Optional.empty();

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing RecruitDex ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonRecruitDexDataStorage recruitDexDataStorage = new JsonRecruitDexDataStorage(RECRUIT_DEX_DATA_FILE_PATH);
        storage = new StorageManager(recruitDexDataStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getRecruitDexDataFilePath(), startupMessage);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s
     * RecruitDex and {@code userPrefs}. <br>
     * The data from the sample RecruitDex will be used instead if {@code storage}'s
     * RecruitDex is not found,
     * or an empty RecruitDex will be used instead if errors occur when reading
     * {@code storage}'s RecruitDex.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getRecruitDexDataFilePath());

        Optional<ReadOnlyRecruitDexData> recruitDexDataOptional;
        ReadOnlyRecruitDexData initialData;
        try {
            recruitDexDataOptional = storage.readRecruitDexData();
            if (recruitDexDataOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getRecruitDexDataFilePath()
                        + " populated with a sample RecruitDexData.");
            }
            initialData = recruitDexDataOptional.orElseGet(SampleDataUtil::getSampleRecruitDexData);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getRecruitDexDataFilePath() + " could not be loaded."
                    + " Will be starting with an empty RecruitDexData.");
            startupMessage = Optional.of(backUpUnloadableDataFile(storage));
            // The unloadable file is not saved over here; the user's first change will replace it.
            return new ModelManager(new RecruitDexData(), userPrefs);
        }

        Model loadedModel = new ModelManager(initialData, userPrefs);
        // Writes a new file with the sample data, and keeps any pending pair chosen while loading.
        try {
            storage.saveRecruitDexData(loadedModel.getRecruitDexData());
        } catch (IOException e) {
            logger.warning("Could not save data file at " + storage.getRecruitDexDataFilePath() + ": " + e);
        }
        return loadedModel;
    }

    /**
     * Backs up the data file that could not be loaded and returns a message telling the user what happened.
     */
    private String backUpUnloadableDataFile(Storage storage) {
        Path dataFilePath = storage.getRecruitDexDataFilePath();
        try {
            Path backupPath = storage.backUpRecruitDexDataFile();
            logger.info("Backed up data file that could not be loaded to " + backupPath);
            return String.format(MESSAGE_DATA_FILE_BACKED_UP, dataFilePath, backupPath, dataFilePath);
        } catch (IOException e) {
            logger.warning("Could not back up data file at " + dataFilePath + ": " + e);
            return String.format(MESSAGE_DATA_FILE_NOT_BACKED_UP, dataFilePath);
        }
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs
     * file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        // Update prefs file in case it was missing to begin with or there are
        // new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting RecruitDex " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping RecruitDex ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
