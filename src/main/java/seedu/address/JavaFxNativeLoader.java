package seedu.address;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Selects the release JAR's JavaFX native libraries before the toolkit starts.
 */
final class JavaFxNativeLoader {
    private JavaFxNativeLoader() {}

    /**
     * Extracts bundled libraries for the current JVM and adds them to JavaFX's library search path.
     * Gradle and IDE runs already use platform-specific dependencies and need no extraction.
     */
    public static void configure() {
        try {
            Path archive = Path.of(JavaFxNativeLoader.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            if (Files.isDirectory(archive)) {
                return;
            }
            String platform = getPlatform(System.getProperty("os.name"), System.getProperty("os.arch"));
            Path directory = Files.createTempDirectory("cyclique-javafx-");
            directory.toFile().deleteOnExit();
            extractLibraries(archive, platform, directory);
            String existingPath = System.getProperty("java.library.path", "");
            // JavaFX reads this property when loading each native library, even after JVM startup.
            System.setProperty("java.library.path", directory + File.pathSeparator + existingPath);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Cannot prepare bundled JavaFX libraries.", e);
        }
    }

    /**
     * Returns the JavaFX classifier matching the operating system and JVM architecture.
     */
    static String getPlatform(String osName, String osArch) {
        String os = osName.toLowerCase(Locale.ROOT);
        String arch = osArch.toLowerCase(Locale.ROOT);
        boolean isArm64 = List.of("aarch64", "arm64").contains(arch);
        boolean isX64 = List.of("amd64", "x86_64").contains(arch);
        if (os.contains("mac") && (isArm64 || isX64)) {
            return isArm64 ? "mac-aarch64" : "mac";
        }
        if (os.contains("linux") && (isArm64 || isX64)) {
            return isArm64 ? "linux-aarch64" : "linux";
        }
        if (os.startsWith("windows") && isX64) {
            return "win";
        }
        throw new IllegalArgumentException("Unsupported JavaFX platform: " + osName + " (" + osArch + ").");
    }

    /**
     * Copies only the selected platform's libraries into an empty temporary directory.
     *
     * @throws IOException If the archive is invalid or lacks libraries for the selected platform.
     */
    static void extractLibraries(Path archive, String platform, Path directory) throws IOException {
        String prefix = "javafx-natives/" + platform + "/";
        try (JarFile jar = new JarFile(archive.toFile())) {
            List<JarEntry> entries = jar.stream()
                    .filter(entry -> !entry.isDirectory() && entry.getName().startsWith(prefix))
                    .toList();
            if (entries.isEmpty()) {
                throw new IOException("No bundled JavaFX libraries for " + platform + ".");
            }
            for (JarEntry entry : entries) {
                String name = entry.getName().substring(prefix.length());
                if (name.contains("/") || name.contains("\\")) {
                    throw new IOException("Invalid bundled JavaFX library path: " + entry.getName());
                }
                Path library = directory.resolve(name);
                try (InputStream input = jar.getInputStream(entry)) {
                    Files.copy(input, library);
                }
                library.toFile().deleteOnExit();
            }
        }
    }
}
