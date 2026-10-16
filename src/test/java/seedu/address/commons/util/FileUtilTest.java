package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    public Path temporaryFolder;

    private Path dataFile() {
        return temporaryFolder.resolve("data.json");
    }

    private Path tempFile() {
        return temporaryFolder.resolve("data.json.tmp");
    }

    private List<String> fileNamesInFolder() throws IOException {
        try (Stream<Path> files = Files.list(temporaryFolder)) {
            return files.map(path -> path.getFileName().toString()).sorted().collect(Collectors.toList());
        }
    }

    @Test
    public void writeToFileAtomically_newFile_createsFileWithContent() throws IOException {
        FileUtil.writeToFileAtomically(dataFile(), "hello");
        assertEquals("hello", FileUtil.readFromFile(dataFile()));
    }

    @Test
    public void writeToFileAtomically_existingFile_replacesWholeContent() throws IOException {
        FileUtil.writeToFile(dataFile(), "a much longer previous version of the data");
        FileUtil.writeToFileAtomically(dataFile(), "short");

        // nothing of the longer previous version may remain
        assertEquals("short", FileUtil.readFromFile(dataFile()));
    }

    @Test
    public void writeToFileAtomically_emptyContent_leavesEmptyFile() throws IOException {
        FileUtil.writeToFile(dataFile(), "something");
        FileUtil.writeToFileAtomically(dataFile(), "");
        assertEquals("", FileUtil.readFromFile(dataFile()));
    }

    @Test
    public void writeToFileAtomically_nonAsciiContent_isPreserved() throws IOException {
        String content = "café über 中文 😀";
        FileUtil.writeToFileAtomically(dataFile(), content);
        assertEquals(content, FileUtil.readFromFile(dataFile()));
    }

    @Test
    public void writeToFileAtomically_largeContent_isWrittenInFull() throws IOException {
        String content = "x".repeat(1_000_000);
        FileUtil.writeToFileAtomically(dataFile(), content);
        assertEquals(content, FileUtil.readFromFile(dataFile()));
    }

    @Test
    public void writeToFileAtomically_success_leavesNoTemporaryFile() throws IOException {
        FileUtil.writeToFileAtomically(dataFile(), "first");
        FileUtil.writeToFileAtomically(dataFile(), "second");

        assertEquals(List.of("data.json"), fileNamesInFolder());
    }

    @Test
    public void writeToFileAtomically_repeatedWrites_alwaysHoldLatestContent() throws IOException {
        for (int i = 0; i < 20; i++) {
            FileUtil.writeToFileAtomically(dataFile(), "version " + i);
            assertEquals("version " + i, FileUtil.readFromFile(dataFile()));
        }
        assertEquals(List.of("data.json"), fileNamesInFolder());
    }

    @Test
    public void writeToFileAtomically_staleTemporaryFile_isOverwritten() throws IOException {
        // a crash during an earlier save can leave a half-written temporary file behind
        FileUtil.writeToFile(tempFile(), "half-written junk from a crashed save");
        FileUtil.writeToFile(dataFile(), "good data");

        FileUtil.writeToFileAtomically(dataFile(), "new data");

        assertEquals("new data", FileUtil.readFromFile(dataFile()));
        assertFalse(Files.exists(tempFile()));
    }

    @Test
    public void writeToFileAtomically_missingParentFolder_throwsAndCreatesNothing() throws IOException {
        Path fileInMissingFolder = temporaryFolder.resolve("missing").resolve("data.json");

        assertThrows(IOException.class, () -> FileUtil.writeToFileAtomically(fileInMissingFolder, "content"));

        assertEquals(List.of(), fileNamesInFolder());
    }

    @Test
    public void writeToFileAtomically_temporaryFileCannotBeWritten_keepsOriginalContent() throws IOException {
        FileUtil.writeToFile(dataFile(), "precious data");
        // a non-empty folder where the temporary file should go makes writing it fail
        Files.createDirectory(tempFile());
        Files.writeString(tempFile().resolve("blocker"), "x");

        assertThrows(IOException.class, () -> FileUtil.writeToFileAtomically(dataFile(), "new data"));

        assertEquals("precious data", FileUtil.readFromFile(dataFile()));
    }

    @Test
    public void writeToFileAtomically_targetCannotBeReplaced_keepsTargetAndRemovesTemporaryFile()
            throws IOException {
        // a folder in place of the data file cannot be replaced by a file
        Files.createDirectory(dataFile());
        Files.writeString(dataFile().resolve("inside"), "x");

        assertThrows(IOException.class, () -> FileUtil.writeToFileAtomically(dataFile(), "new data"));

        assertTrue(Files.isDirectory(dataFile()));
        assertFalse(Files.exists(tempFile()));
    }

    @Test
    public void writeToFileAtomically_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFileAtomically(null, "content"));
        assertThrows(NullPointerException.class, () -> FileUtil.writeToFileAtomically(dataFile(), null));
    }

    @Test
    public void writeToFile_stillOverwritesDirectly() throws IOException {
        FileUtil.writeToFile(dataFile(), "one");
        FileUtil.writeToFile(dataFile(), "two");
        assertEquals("two", FileUtil.readFromFile(dataFile()));
    }
}
