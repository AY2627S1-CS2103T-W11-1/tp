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
 * Checks that the release JAR opens its main window and exits through its command box.
 * Run in a disposable directory with only the release JAR on the classpath.
 */
public final class PackagedJarSmokeTest {
    private static final AtomicBoolean isComplete = new AtomicBoolean();

    private PackagedJarSmokeTest() {}

    /**
     * Launches the packaged application and watches for startup or shutdown failures.
     */
    public static void main(String[] args) throws Exception {
        require(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar"),
                "Application classes must come from the release JAR");
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> fail(error));
        Thread.ofPlatform().daemon(true).name("packaged-jar-watchdog").start(() -> {
            try {
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
                while (!isComplete.get() && System.nanoTime() < deadline) {
                    try {
                        Platform.runLater(PackagedJarSmokeTest::checkMainWindow);
                    } catch (IllegalStateException exception) {
                        // The JavaFX event queue is not ready during early startup.
                    }
                    Thread.sleep(100);
                }
                require(isComplete.get(), "Main window did not become ready within 60 seconds");
                Thread.sleep(10_000);
                fail(new AssertionError("Exit command did not stop the application"));
            } catch (Throwable error) {
                fail(error);
            }
        });
        Main.main(args);
        require(isComplete.get(), "Application exited before the smoke check completed");
        require(Files.exists(Path.of("preferences.json")), "Preferences were not saved on exit");
        System.out.println("PACKAGED JAR SMOKE TEST PASSED: " + System.getProperty("os.name")
                + " / " + System.getProperty("os.arch"));
    }

    /**
     * Submits exit only after the actual application window and command box are visible.
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

    /** Throws if the packaged application does not meet a smoke-test expectation. */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /** Reports asynchronous startup errors and exits with a failing status. */
    private static void fail(Throwable error) {
        error.printStackTrace();
        System.exit(1);
    }
}
