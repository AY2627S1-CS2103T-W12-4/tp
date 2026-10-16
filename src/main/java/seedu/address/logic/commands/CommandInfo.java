package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

/**
 * One command described in four parts, for showing the command list as a table.
 * Built from the usage text of a command, so it stays in sync with the command itself.
 */
public record CommandInfo(String commandWord, String description, String parameters, String example) {

    private static final String PARAMETERS_MARKER = "Parameters: ";
    private static final String EXAMPLE_MARKER = "Example: ";

    /**
     * Splits {@code usage}, written as {@code "word: description [Parameters: ...]\nExample: ..."},
     * into its parts. {@code parameters} is empty for a command that takes none.
     *
     * @throws IllegalArgumentException if {@code usage} does not start with "word: " or has no example.
     */
    public static CommandInfo fromUsage(String usage) {
        requireNonNull(usage);
        int wordEnd = usage.indexOf(": ");
        int exampleStart = usage.lastIndexOf(EXAMPLE_MARKER);
        if (wordEnd <= 0 || exampleStart < wordEnd) {
            throw new IllegalArgumentException("Usage must look like 'word: description ... Example: ...': " + usage);
        }

        String commandWord = usage.substring(0, wordEnd);
        String beforeExample = usage.substring(wordEnd + 2, exampleStart);
        String example = tidy(usage.substring(exampleStart + EXAMPLE_MARKER.length()));

        int parametersStart = beforeExample.indexOf(PARAMETERS_MARKER);
        if (parametersStart < 0) {
            return new CommandInfo(commandWord, tidy(beforeExample), "", example);
        }
        String description = tidy(beforeExample.substring(0, parametersStart));
        String parameters = tidy(beforeExample.substring(parametersStart + PARAMETERS_MARKER.length()));
        return new CommandInfo(commandWord, description, parameters, example);
    }

    /** Joins the lines of {@code text} into one and trims the ends. */
    private static String tidy(String text) {
        return text.replaceAll("\\s+", " ").strip();
    }
}
