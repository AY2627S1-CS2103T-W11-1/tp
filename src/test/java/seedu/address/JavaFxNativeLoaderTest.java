package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JavaFxNativeLoaderTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void getPlatform_supportedPlatforms_returnsClassifier() {
        assertEquals("mac", JavaFxNativeLoader.getPlatform("Mac OS X", "x86_64"));
        assertEquals("mac-aarch64", JavaFxNativeLoader.getPlatform("Mac OS X", "aarch64"));
        assertEquals("mac-aarch64", JavaFxNativeLoader.getPlatform("Mac OS X", "arm64"));
        assertEquals("win", JavaFxNativeLoader.getPlatform("Windows 11", "amd64"));
        assertEquals("linux", JavaFxNativeLoader.getPlatform("Linux", "amd64"));
        assertEquals("linux-aarch64", JavaFxNativeLoader.getPlatform("Linux", "aarch64"));
    }

    @Test
    public void getPlatform_unsupportedPlatforms_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> JavaFxNativeLoader.getPlatform("Windows 11", "aarch64"));
        assertThrows(IllegalArgumentException.class, () -> JavaFxNativeLoader.getPlatform("Linux", "x86"));
        assertThrows(IllegalArgumentException.class, () -> JavaFxNativeLoader.getPlatform("FreeBSD", "amd64"));
    }

    @Test
    public void extractLibraries_sameNamedLibraries_extractsOnlySelectedArchitecture() throws Exception {
        Path archive = createArchive(Map.of(
                "javafx-natives/mac/libglass.dylib", "intel",
                "javafx-natives/mac-aarch64/libglass.dylib", "arm",
                "javafx-natives/mac-aarch64/libprism_sw.dylib", "arm software renderer",
                "javafx-natives/linux/libglass.so", "linux"));
        Path intelDirectory = Files.createDirectory(temporaryFolder.resolve("intel"));
        Path armDirectory = Files.createDirectory(temporaryFolder.resolve("arm"));

        JavaFxNativeLoader.extractLibraries(archive, "mac", intelDirectory);
        JavaFxNativeLoader.extractLibraries(archive, "mac-aarch64", armDirectory);

        assertEquals("intel", Files.readString(intelDirectory.resolve("libglass.dylib")));
        assertEquals("arm", Files.readString(armDirectory.resolve("libglass.dylib")));
        assertEquals("arm software renderer", Files.readString(armDirectory.resolve("libprism_sw.dylib")));
        assertFalse(Files.exists(intelDirectory.resolve("libprism_sw.dylib")));
        assertFalse(Files.exists(armDirectory.resolve("libglass.so")));
    }

    @Test
    public void extractLibraries_missingPlatform_throwsIoException() throws Exception {
        Path archive = createArchive(Map.of("javafx-natives/mac/libglass.dylib", "intel"));
        Path directory = Files.createDirectory(temporaryFolder.resolve("libraries"));

        assertThrows(IOException.class, () -> JavaFxNativeLoader.extractLibraries(archive, "win", directory));
    }

    @Test
    public void extractLibraries_nestedPath_throwsWithoutWritingOutsideDirectory() throws Exception {
        Path archive = createArchive(Map.of("javafx-natives/mac/../outside.dylib", "invalid"));
        Path directory = Files.createDirectory(temporaryFolder.resolve("libraries"));

        assertThrows(IOException.class, () -> JavaFxNativeLoader.extractLibraries(archive, "mac", directory));
        assertFalse(Files.exists(temporaryFolder.resolve("outside.dylib")));
    }

    private Path createArchive(Map<String, String> resources) throws IOException {
        Path archive = temporaryFolder.resolve("cyclique.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(archive))) {
            for (Map.Entry<String, String> resource : resources.entrySet()) {
                output.putNextEntry(new JarEntry(resource.getKey()));
                output.write(resource.getValue().getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
        }
        return archive;
    }
}
