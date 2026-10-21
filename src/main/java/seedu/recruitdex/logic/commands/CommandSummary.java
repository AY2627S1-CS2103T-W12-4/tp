package seedu.recruitdex.logic.commands;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lists every command with its usage text, for the help window.
 * The usage text comes from each command class, so it stays in sync when a command's format changes.
 */
public final class CommandSummary {

    /** The usage text of every command, in the order it is shown. */
    public static final String APPLICANTS_HEADING = "Applicants";
    public static final String COMPARISON_HEADING = "Comparison";
    public static final String GENERAL_HEADING = "General";

    private static final List<String> APPLICANT_COMMANDS = List.of(
            AddCommand.MESSAGE_USAGE,
            EditCommand.MESSAGE_USAGE,
            DeleteCommand.MESSAGE_USAGE,
            ListCommand.MESSAGE_USAGE,
            FindCommand.MESSAGE_USAGE,
            ClearCommand.MESSAGE_USAGE);

    private static final List<String> COMPARISON_COMMANDS = List.of(
            AddToComparisonCommand.MESSAGE_USAGE,
            RemoveFromComparisonCommand.MESSAGE_USAGE,
            CompareCommand.MESSAGE_USAGE);

    private static final List<String> GENERAL_COMMANDS = List.of(
            SwitchTabCommand.MESSAGE_USAGE,
            HelpCommand.MESSAGE_USAGE,
            ExitCommand.MESSAGE_USAGE);

    private CommandSummary() {}

    /** Returns the usage text of every command, in display order. */
    public static List<String> getUsages() {
        return List.of(APPLICANT_COMMANDS, COMPARISON_COMMANDS, GENERAL_COMMANDS).stream()
                .flatMap(List::stream)
                .toList();
    }

    /**
     * Returns the commands grouped under headings, in display order.
     * The keys are the group headings and each value is the commands of that group.
     */
    public static Map<String, List<CommandInfo>> getGroups() {
        Map<String, List<CommandInfo>> groups = new LinkedHashMap<>();
        groups.put(APPLICANTS_HEADING, describe(APPLICANT_COMMANDS));
        groups.put(COMPARISON_HEADING, describe(COMPARISON_COMMANDS));
        groups.put(GENERAL_HEADING, describe(GENERAL_COMMANDS));
        return Collections.unmodifiableMap(groups);
    }

    private static List<CommandInfo> describe(List<String> usages) {
        return usages.stream().map(CommandInfo::fromUsage).toList();
    }
}
