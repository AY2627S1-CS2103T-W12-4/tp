package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class CommandSummaryTest {

    private static final String COMMAND_PACKAGE = "seedu.address.logic.commands";

    /** Returns the command word of every concrete command class in the commands package. */
    private static Set<String> allCommandWords() throws IOException, URISyntaxException, ReflectiveOperationException {
        Set<String> commandWords = new HashSet<>();
        // main and test classes sit in different folders, so look in every folder for this package
        Enumeration<URL> folders = CommandSummaryTest.class.getClassLoader()
                .getResources(COMMAND_PACKAGE.replace('.', '/'));
        while (folders.hasMoreElements()) {
            File folder = new File(folders.nextElement().toURI());
            for (File file : folder.listFiles()) {
                String fileName = file.getName();
                if (!fileName.endsWith(".class") || fileName.contains("$")) {
                    continue;
                }
                Class<?> candidate = Class.forName(COMMAND_PACKAGE + "." + fileName.replace(".class", ""));
                if (!Command.class.isAssignableFrom(candidate) || Modifier.isAbstract(candidate.getModifiers())) {
                    continue;
                }
                Field commandWord = candidate.getField("COMMAND_WORD");
                commandWords.add((String) commandWord.get(null));
            }
        }
        return commandWords;
    }

    private static String commandWordOf(String usage) {
        return usage.substring(0, usage.indexOf(':'));
    }

    @Test
    public void getUsages_isNotEmpty() {
        assertFalse(CommandSummary.getUsages().isEmpty());
    }

    @Test
    public void getUsages_everyUsageStartsWithItsCommandWord() {
        for (String usage : CommandSummary.getUsages()) {
            assertTrue(usage.matches("[a-z-]+: .*(?s).*"), "usage should start with 'word: ' : " + usage);
        }
    }

    @Test
    public void getUsages_everyUsageHasNonBlankDescriptionAndAnExample() {
        for (String usage : CommandSummary.getUsages()) {
            assertFalse(usage.isBlank());
            assertEquals(usage.strip(), usage, "no stray whitespace around: " + usage);
            assertTrue(usage.contains("Example:"), "missing an example: " + usage);
        }
    }

    @Test
    public void getUsages_commandWordsAreUnique() {
        List<String> words = CommandSummary.getUsages().stream()
                .map(CommandSummaryTest::commandWordOf)
                .collect(Collectors.toList());
        assertEquals(new HashSet<>(words).size(), words.size());
    }

    @Test
    public void getUsages_coversEveryCommandClass() throws Exception {
        Set<String> documented = CommandSummary.getUsages().stream()
                .map(CommandSummaryTest::commandWordOf)
                .collect(Collectors.toSet());

        // a command added without a help entry makes this fail
        assertEquals(allCommandWords(), documented);
    }

    @Test
    public void getUsages_containsEachKnownCommandWord() {
        Set<String> documented = CommandSummary.getUsages().stream()
                .map(CommandSummaryTest::commandWordOf)
                .collect(Collectors.toSet());
        List<String> expected = List.of(AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD,
                DeleteCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, FindCommand.COMMAND_WORD,
                ClearCommand.COMMAND_WORD, AddToComparisonCommand.COMMAND_WORD,
                RemoveFromComparisonCommand.COMMAND_WORD, CompareCommand.COMMAND_WORD,
                HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD);
        assertTrue(documented.containsAll(expected));
    }

    @Test
    public void getUsages_usesTheUsageTextOfEachCommand() {
        List<String> usages = CommandSummary.getUsages();
        assertTrue(usages.contains(AddCommand.MESSAGE_USAGE));
        assertTrue(usages.contains(EditCommand.MESSAGE_USAGE));
        assertTrue(usages.contains(DeleteCommand.MESSAGE_USAGE));
        assertTrue(usages.contains(FindCommand.MESSAGE_USAGE));
        assertTrue(usages.contains(CompareCommand.MESSAGE_USAGE));
        assertTrue(usages.contains(HelpCommand.MESSAGE_USAGE));
    }

    @Test
    public void getUsages_listIsUnmodifiable() {
        List<String> usages = CommandSummary.getUsages();
        assertThrows(UnsupportedOperationException.class, () -> usages.add("extra"));
        assertThrows(UnsupportedOperationException.class, () -> usages.remove(0));
    }

    @Test
    public void getUsages_calledTwice_returnsTheSameContent() {
        assertEquals(CommandSummary.getUsages(), CommandSummary.getUsages());
    }

    @Test
    public void getGroups_hasTheThreeGroupsInOrder() {
        assertEquals(List.of(CommandSummary.APPLICANTS_HEADING, CommandSummary.COMPARISON_HEADING,
                CommandSummary.GENERAL_HEADING), new ArrayList<>(CommandSummary.getGroups().keySet()));
    }

    @Test
    public void getGroups_everyGroupHasCommands() {
        for (List<CommandInfo> commands : CommandSummary.getGroups().values()) {
            assertFalse(commands.isEmpty());
        }
    }

    @Test
    public void getGroups_containsTheSameCommandsAsGetUsages() {
        List<String> fromGroups = CommandSummary.getGroups().values().stream()
                .flatMap(List::stream)
                .map(CommandInfo::commandWord)
                .collect(Collectors.toList());
        List<String> fromUsages = CommandSummary.getUsages().stream()
                .map(CommandSummaryTest::commandWordOf)
                .collect(Collectors.toList());
        assertEquals(fromUsages, fromGroups);
    }

    @Test
    public void getGroups_putsEachCommandInTheRightGroup() {
        assertEquals(List.of("add", "edit", "delete", "list", "find", "clear"),
                words(CommandSummary.getGroups().get(CommandSummary.APPLICANTS_HEADING)));
        assertEquals(List.of(AddToComparisonCommand.COMMAND_WORD, RemoveFromComparisonCommand.COMMAND_WORD,
                CompareCommand.COMMAND_WORD), words(CommandSummary.getGroups().get(CommandSummary.COMPARISON_HEADING)));
        assertEquals(List.of(HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD),
                words(CommandSummary.getGroups().get(CommandSummary.GENERAL_HEADING)));
    }

    @Test
    public void getGroups_everyCommandHasADescriptionAndAnExampleStartingWithItsWord() {
        for (List<CommandInfo> commands : CommandSummary.getGroups().values()) {
            for (CommandInfo info : commands) {
                assertFalse(info.description().isBlank(), "no description: " + info);
                assertTrue(info.example().startsWith(info.commandWord()), "bad example: " + info);
                assertFalse(info.description().contains("\n"));
                assertFalse(info.parameters().contains("\n"));
            }
        }
    }

    @Test
    public void getGroups_commandsWithParametersShowThem() {
        CommandInfo add = infoOf("add");
        assertTrue(add.parameters().contains("n/NAME"));
        assertTrue(add.parameters().contains("[t/SKILL]..."));
        assertTrue(infoOf("delete").parameters().startsWith("INDEX"));
        assertTrue(infoOf("find").parameters().startsWith("KEYWORD"));
    }

    @Test
    public void getGroups_commandsWithoutParametersHaveNone() {
        for (String word : List.of("list", "clear", "help", "exit")) {
            assertEquals("", infoOf(word).parameters(), word);
        }
    }

    @Test
    public void getGroups_isUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () -> CommandSummary.getGroups().put("extra", List.of()));
    }

    private static List<String> words(List<CommandInfo> commands) {
        return commands.stream().map(CommandInfo::commandWord).collect(Collectors.toList());
    }

    private static CommandInfo infoOf(String commandWord) {
        return CommandSummary.getGroups().values().stream().flatMap(List::stream)
                .filter(info -> info.commandWord().equals(commandWord)).findFirst().get();
    }

    @Test
    public void usageOfBareCommands_isDescribedAndHasAnExample() {
        assertEquals("list: Shows all applicants, undoing any search.\nExample: list", ListCommand.MESSAGE_USAGE);
        assertTrue(ClearCommand.MESSAGE_USAGE.startsWith("clear: "));
        assertTrue(ClearCommand.MESSAGE_USAGE.endsWith("Example: clear"));
        assertEquals("exit: Closes RecruitDex.\nExample: exit", ExitCommand.MESSAGE_USAGE);
    }
}
