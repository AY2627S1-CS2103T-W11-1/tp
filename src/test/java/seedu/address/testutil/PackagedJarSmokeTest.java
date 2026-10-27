package seedu.address.testutil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import seedu.address.Main;

/**
 * Launches the packaged application and checks its UI resources and shutdown.
 * The classpath must contain the release JAR and this helper, without development dependencies.
 */
public final class PackagedJarSmokeTest {
    private static final AtomicBoolean isComplete = new AtomicBoolean();

    private PackagedJarSmokeTest() {}

    /**
     * Starts the application on the main thread while a watchdog waits for the real main window.
     */
    public static void main(String[] args) throws Exception {
        require(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar"),
                "Application classes must come from the release JAR");
        require(Main.class.getProtectionDomain().getCodeSource().getLocation().equals(
                Platform.class.getProtectionDomain().getCodeSource().getLocation()),
                "JavaFX classes must come from the release JAR");
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> fail(error));
        Thread.ofPlatform().daemon(true).name("packaged-jar-watchdog").start(() -> {
            try {
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
                while (!isComplete.get() && System.nanoTime() < deadline) {
                    try {
                        Platform.runLater(PackagedJarSmokeTest::checkMainWindow);
                    } catch (IllegalStateException exception) {
                        // JavaFX has not initialized its event queue yet.
                    }
                    Thread.sleep(100);
                }
                require(isComplete.get(), "Main window did not become ready within 60 seconds");
                // A second deadline also detects an exit command that leaves the JVM running.
                Thread.sleep(10_000);
                fail(new AssertionError("Exit command did not stop the application"));
            } catch (Throwable error) {
                fail(error);
            }
        });
        Main.main(args);
        require(isComplete.get(), "Application exited before the smoke checks completed");
        require(Files.exists(Path.of("preferences.json")), "Preferences were not saved on exit");
        System.out.println("PACKAGED JAR SMOKE TEST PASSED: " + System.getProperty("os.name")
                + " / " + System.getProperty("os.arch"));
    }

    /**
     * Waits for the main window and submits the exit command through the actual command box.
     */
    private static void checkMainWindow() {
        if (isComplete.get()) {
            return;
        }
        Window mainWindow = Window.getWindows().stream()
                .filter(window -> window.isShowing() && window.getScene() != null
                        && window.getScene().lookup("#commandTextField") != null)
                .findFirst().orElse(null);
        if (mainWindow == null) {
            return;
        }
        TextField input = (TextField) mainWindow.getScene().lookup("#commandTextField");
        input.setText("exit");
        input.fireEvent(new ActionEvent());
        isComplete.set(true);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void fail(Throwable error) {
        error.printStackTrace();
        System.exit(1);
    }
}
