package seedu.address.testutil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import seedu.address.Main;
import seedu.address.logic.commands.ListCommand;

/**
 * Launches the packaged application in a separate JVM and checks real UI commands and saved data.
 * The classpath must contain the release JAR and this helper, without development dependencies.
 */
public final class PackagedJarSmokeTest {
    private static final String PHONE = "+65 90001234";
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
                    CompletableFuture<Boolean> result = new CompletableFuture<>();
                    try {
                        Platform.runLater(() -> {
                            try {
                                result.complete(checkMainWindow());
                            } catch (Throwable error) {
                                result.completeExceptionally(error);
                            }
                        });
                        isComplete.set(result.get(10, TimeUnit.SECONDS));
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
     * Checks startup, roster validation, saving, overlap confirmation, Help, and shutdown through the command box.
     */
    private static boolean checkMainWindow() throws Exception {
        Window mainWindow = Window.getWindows().stream()
                .filter(window -> window.isShowing() && window.getScene() != null
                        && window.getScene().lookup("#commandTextField") != null)
                .findFirst().orElse(null);
        if (mainWindow == null) {
            return false;
        }
        TextField input = (TextField) mainWindow.getScene().lookup("#commandTextField");
        TextArea feedback = (TextArea) mainWindow.getScene().lookup("#resultDisplay");
        enter(input, "list");
        require(feedback.getText().equals(ListCommand.MESSAGE_SUCCESS), "List command failed");
        enter(input, "ridedeletecyclist");
        require(feedback.getText().contains("Example: ridedeletecyclist"), "Roster validation failed");
        enter(input, "add n/Packaging Smoke Rider p/" + PHONE + " sos/+65 99991234 "
                + "e/packaging@example.com f/250 x/Beginner l/any");
        require(input.getText().isEmpty(), "Add command failed: " + feedback.getText());
        Path savedFile = Path.of("data/addressbook.json");
        require(Files.readString(savedFile).contains("Packaging Smoke Rider"), "Cyclist was not saved");
        enter(input, "rideaddcyclist 1 " + PHONE);
        require(feedback.getText().contains("added to ride 1"), "Roster addition failed");
        enter(input, "ridedeletecyclist 1 " + PHONE);
        require(feedback.getText().contains("removed from ride 1"), "Roster removal failed");
        enter(input, "rideedit 2 from/25-10-2026 0700 to/25-10-2026 0800");
        enter(input, "rideaddcyclist 1 " + PHONE);
        String beforeWarning = Files.readString(savedFile);
        enter(input, "rideaddcyclist 2 " + PHONE);
        require(mainWindow.getScene().lookup("#confirmationInstructions").isVisible(), "Overlap warning missing");
        require(beforeWarning.equals(Files.readString(savedFile)), "Unconfirmed change was saved");
        enter(input, "yes");
        require(feedback.getText().contains("added to ride 2"), "Overlap confirmation failed");
        require(!beforeWarning.equals(Files.readString(savedFile)), "Confirmed change was not saved");
        enter(input, "help");
        require(Window.getWindows().stream().filter(Window::isShowing).count() >= 2, "Help did not open");
        enter(input, "exit");
        return true;
    }

    private static void enter(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
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
