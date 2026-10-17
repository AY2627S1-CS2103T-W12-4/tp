package seedu.recruitdex.logic;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.recruitdex.commons.core.GuiSettings;
import seedu.recruitdex.logic.commands.CommandResult;
import seedu.recruitdex.logic.commands.exceptions.CommandException;
import seedu.recruitdex.logic.parser.exceptions.ParseException;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.comparison.ComparisonPair;
import seedu.recruitdex.model.ranking.RankingEntry;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /** Returns an unmodifiable view of the filtered list of applicants */
    ObservableList<Applicant> getFilteredApplicantList();

    /**
     * Returns an unmodifiable view of every applicant record, whatever the search filter shows.
     * Used to look up applicants in the comparison pair and leaderboard by ID.
     */
    ObservableList<Applicant> getApplicantList();

    /** Returns a read-only observable property holding the pending comparison pair, if any. */
    ReadOnlyObjectProperty<Optional<ComparisonPair>> getCurrentPairProperty();

    /**
     * Returns the same live, unmodifiable observable view of the complete current ranking.
     * The applicant search filter does not affect this view.
     */
    ObservableList<RankingEntry> getRanking();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
