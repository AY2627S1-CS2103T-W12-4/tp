package seedu.recruitdex.commons.util;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        Files.write(file, content.getBytes(CHARSET));
    }

    /**
     * Writes given string to a file without ever leaving it half-written.
     * The content is first written to a temporary file next to {@code file}, which then replaces {@code file}
     * in a single step. If anything fails, {@code file} keeps its previous content and the temporary file
     * is removed. Will create the file if it does not exist yet, but not its parent directories.
     */
    public static void writeToFileAtomically(Path file, String content) throws IOException {
        Path tempFile = file.resolveSibling(file.getFileName() + TEMP_FILE_SUFFIX);
        try {
            Files.write(tempFile, content.getBytes(CHARSET));
            replaceFile(tempFile, file);
        } catch (IOException | RuntimeException e) {
            deleteQuietly(tempFile);
            throw e;
        }
    }

    /** Moves {@code source} over {@code target} in one step, or as close to that as the file system allows. */
    private static void replaceFile(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // the temporary file is only a leftover; the original error is the one worth reporting
        }
    }

}
