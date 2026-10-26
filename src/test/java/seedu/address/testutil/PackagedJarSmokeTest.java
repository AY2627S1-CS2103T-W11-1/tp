package seedu.address.testutil;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Window;
import seedu.address.Main;
import seedu.address.logic.commands.ListCommand;

/**
 * Launches the packaged application with only its JAR and test classes on the classpath.
 * Run through {@code smokeTestJar} in a disposable directory with a graphical display.
 */
public class PackagedJarSmokeTest {

    /**
     * Checks startup, rendering, command entry, help, persistence, and normal shutdown.
     */
    public static void main(String[] args) throws Exception {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> failure.compareAndSet(null, error));
        Thread launcher = new Thread(() -> Main.main(new String[0]), "packaged-app-launcher");
        launcher.start();
        try {
            boolean tested = false;
            for (int attempt = 0; attempt < 150 && !tested; attempt++) {
                if (failure.get() != null) {
                    throw new AssertionError("Application startup failed", failure.get());
                }
                CompletableFuture<Boolean> result = new CompletableFuture<>();
                try {
                    Platform.runLater(() -> {
                        try {
                            result.complete(checkMainWindow());
                        } catch (Throwable error) {
                            result.completeExceptionally(error);
                        }
                    });
                    tested = result.get(30, TimeUnit.SECONDS);
                } catch (IllegalStateException error) {
                    // Application.launch has not initialized JavaFX yet.
                }
                if (!tested) {
                    Thread.sleep(100);
                }
            }
            require(tested, "Main window did not become ready");
            launcher.join(10_000);
            require(!launcher.isAlive(), "Exit command did not stop the application");
            require(failure.get() == null, "Application raised an uncaught exception: " + failure.get());
            require(Files.exists(Path.of("preferences.json")), "Preferences were not saved");
            System.out.println("PACKAGED JAR SMOKE TEST PASSED: " + Path.of("").toAbsolutePath());
        } catch (Throwable error) {
            error.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Runs checks after the opening artwork gives way to the real main window.
     */
    private static boolean checkMainWindow() throws Exception {
        Window mainWindow = Window.getWindows().stream()
                .filter(window -> window.isShowing() && window.getScene() != null
                        && window.getScene().lookup("#commandTextField") != null)
                .findFirst().orElse(null);
        if (mainWindow == null) {
            return false;
        }
        require(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar"),
                "Application classes must come from the packaged JAR");
        TextField commandBox = (TextField) mainWindow.getScene().lookup("#commandTextField");
        TextArea result = (TextArea) mainWindow.getScene().lookup("#resultDisplay");
        enter(commandBox, "list");
        require(result.getText().equals(ListCommand.MESSAGE_SUCCESS), "List command failed");
        enter(commandBox, "ridedeletecyclist");
        require(result.getText().contains("Example: ridedeletecyclist"), "Roster validation failed");
        enter(commandBox, "add n/Packaging Smoke Rider p/+65 90001234 sos/+65 99991234 "
                + "e/packaging@example.com f/200 x/Beginner l/any");
        require(commandBox.getText().isEmpty(), "Add command failed: " + result.getText());
        require(Files.readString(Path.of("data/addressbook.json")).contains("Packaging Smoke Rider"),
                "Cyclist was not saved");

        WritableImage snapshot = mainWindow.getScene().snapshot(null);
        BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        ImageIO.write(image, "png", Path.of("main-window.png").toFile());
        enter(commandBox, "help");
        require(Window.getWindows().stream().filter(Window::isShowing).count() >= 2,
                "Help window did not open");
        enter(commandBox, "exit");
        return true;
    }

    private static void enter(TextField commandBox, String command) {
        commandBox.setText(command);
        commandBox.fireEvent(new ActionEvent());
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
