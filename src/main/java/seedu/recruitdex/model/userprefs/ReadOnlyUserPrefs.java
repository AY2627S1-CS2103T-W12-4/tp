package seedu.recruitdex.model.userprefs;

import seedu.recruitdex.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

}
