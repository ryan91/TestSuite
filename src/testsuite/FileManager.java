package testsuite;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides methods that interact with the file system in some way
 */
public class FileManager {
    private FileManager() {}

    public static boolean directoryExists(String directory) {
        Path path = Paths.get(directory);
        return Files.exists(path) && Files.isDirectory(path);
    }

    public static boolean fileExists(String file) {
        Path path = Paths.get(file);
        return Files.isExecutable(path) && !Files.isDirectory(path);
    }
}