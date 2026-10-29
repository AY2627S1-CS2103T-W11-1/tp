package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.JavaFxToolkit;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;
import seedu.address.ui.UiManager;

/**
 * Exercises application initialization and subsequent command saves using disposable legacy data.
 */
public class MainAppNotesLoadingTest {
    private static final String TEN_WORDS = "one two three four five six seven eight nine ten";
    private static final String PHONE = "+65 91234567";
    private static final List<String> INVALID_LEGACY_NOTES = List.of("\u2003", "\u00a0",
            TEN_WORDS.concat(" eleven").replace(" ", "\u2003"), TEN_WORDS.concat(" eleven").replace(" ", "\u00a0"));

    @TempDir
    public Path temporaryFolder;

    private Storage storage;
    private AddressBook originalBook;
    private String templateJson;

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        JavaFxToolkit.start();
    }

    @BeforeEach
    public void setUp() throws Exception {
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("book.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        ModelManager original = new ModelManager();
        original.addPerson(new PersonBuilder().withPhone(PHONE).withFtp("250").build());
        original.addRide(new RideBuilder().withNotes("LegacyMarker").withRoster(PHONE).build());
        original.addRide(new RideBuilder().withName("Unrelated Ride").withNotes("Keep me").withRoster(PHONE).build());
        originalBook = new AddressBook(original.getAddressBook());
        storage.saveAddressBook(originalBook);
        templateJson = Files.readString(storage.getAddressBookFilePath());
    }

    @Test
    public void init_invalidLegacyNotes_stopsBeforeCommandsAndPreservesWholeFile() throws Exception {
        for (String notes : INVALID_LEGACY_NOTES) {
            String legacyJson = writeLegacyNotes(notes);
            IsolatedMainApp app = new IsolatedMainApp(storage);
            app.init();
            assertFatalDialogAndShutdown(app, Notes.MESSAGE_CONSTRAINTS);
            assertEquals(legacyJson, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void init_validLegacyNotes_preservesRecordsAndNormalizesOnSubsequentSave() throws Exception {
        for (String notes : List.of(TEN_WORDS.replace(" ", "\u2003"), TEN_WORDS.replace(" ", "\u00a0"),
                "Bring lights")) {
            String legacyJson = writeLegacyNotes(notes);
            IsolatedMainApp app = new IsolatedMainApp(storage);
            app.init();
            String normalizedNotes = notes.equals("Bring lights") ? notes : TEN_WORDS;
            AddressBook expected = withNotes(normalizedNotes);
            assertEquals(expected, app.model.getAddressBook());
            assertEquals(legacyJson, Files.readString(storage.getAddressBookFilePath()));
            app.logic.execute("list");
            assertEquals(expected, storage.readAddressBook().orElseThrow());
            assertEquals(normalizedNotes, storage.readAddressBook().orElseThrow().getRideList().getFirst()
                    .getNotes().orElseThrow().value);
        }
    }

    @Test
    public void init_repairedFile_allowsCommandsAndPreservesRecordsOnRestart() throws Exception {
        writeLegacyNotes(INVALID_LEGACY_NOTES.getLast());
        IsolatedMainApp failed = new IsolatedMainApp(storage);
        failed.init();
        assertNull(failed.logic);
        assertFatalDialogAndShutdown(failed, Notes.MESSAGE_CONSTRAINTS);
        writeLegacyNotes(TEN_WORDS.replace(" ", "\u00a0")); // Explicit user repair, not automatic truncation.
        IsolatedMainApp app = new IsolatedMainApp(storage);
        app.init();
        app.logic.execute("list");
        AddressBook expected = withNotes(TEN_WORDS);
        assertEquals(expected, storage.readAddressBook().orElseThrow());
        IsolatedMainApp restarted = new IsolatedMainApp(storage);
        restarted.init();
        assertEquals(expected, restarted.model.getAddressBook());
        restarted.logic.execute("list");
        assertEquals(expected, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void init_malformedJson_alsoStopsWithoutReplacingExistingFile() throws Exception {
        String malformed = "{\"private\": \"DO_NOT_DISPLAY\", broken}";
        Files.writeString(storage.getAddressBookFilePath(), malformed);
        IsolatedMainApp app = new IsolatedMainApp(storage);
        app.init();
        assertNull(app.logic);
        assertFatalDialogAndShutdown(app, "The file is not valid JSON");
        assertEquals(malformed, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void start_duplicatePhoneAndOtherInvalidData_showsFatalDialogWithoutCommandsOrWrites() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode duplicate = (ObjectNode) mapper.readTree(templateJson);
        ArrayNode people = (ArrayNode) duplicate.get("persons");
        people.add(people.get(0).deepCopy());
        ObjectNode invalidRide = (ObjectNode) mapper.readTree(templateJson);
        ObjectNode firstInvalidRide = (ObjectNode) invalidRide.get("rides").get(0);
        firstInvalidRide.put("id", 0);
        for (String[] fixture : new String[][] {
            {mapper.writeValueAsString(duplicate), "Cyclist list contains duplicate cyclists."},
            {mapper.writeValueAsString(invalidRide), "Ride's id should be a positive whole number."},
            {"{}", "Cyclist list is missing."},
            {"null", "The data could not be read."}
        }) {
            String invalid = fixture[0];
            Files.writeString(storage.getAddressBookFilePath(), invalid);
            IsolatedMainApp app = new IsolatedMainApp(storage);
            app.init();
            assertFatalDialogAndShutdown(app, fixture[1]);
            assertArrayEquals(invalid.getBytes(StandardCharsets.UTF_8),
                    Files.readAllBytes(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void init_missingFileAndValidEmptyBook_retainNormalStartupAndSaving() throws Exception {
        Storage missingStorage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("missing.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("missing-prefs.json")));
        IsolatedMainApp missing = new IsolatedMainApp(missingStorage);
        missing.init();
        assertEquals(SampleDataUtil.getSampleAddressBook(), missing.model.getAddressBook());
        assertFalse(Files.exists(missingStorage.getAddressBookFilePath()));
        missing.logic.execute("list");
        assertEquals(missing.model.getAddressBook(), missingStorage.readAddressBook().orElseThrow());

        storage.saveAddressBook(new AddressBook());
        IsolatedMainApp empty = new IsolatedMainApp(storage);
        empty.init();
        assertEquals(new AddressBook(), empty.model.getAddressBook());
        empty.logic.execute("rideadd n/New Ride from/29-10-2026 0800 to/29-10-2026 1000 "
                + "l/East Coast d/20 f/180 s/2 notes/Bring lights");
        assertEquals(1, empty.model.getAddressBook().getRideList().getFirst().getId());
        assertEquals(empty.model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void start_longPathAndValidationReason_wrapsWithinScreenWithAccessibleDismissal() throws Exception {
        Path longFile = temporaryFolder.resolve("long-directory-name-".repeat(3))
                .resolve("long-data-file-name-".repeat(2) + ".json");
        String reason = "The stored ride has an invalid field. ".repeat(120);
        storage = storageWithFailure(longFile, new IllegalValueException(reason));
        storage.saveAddressBook(originalBook);
        IsolatedMainApp app = new IsolatedMainApp(storage);
        app.init();
        assertFatalDialogAndShutdown(app, reason);
    }

    @Test
    public void start_unhelpfulOrUntrustedCause_usesSafeFallbackWithoutExceptionDetails() throws Exception {
        for (Exception cause : List.of(new IOException(), new IOException("DO_NOT_DISPLAY"),
                new IllegalValueException("   "), new IllegalValueException(null))) {
            storage = storageWithFailure(storage.getAddressBookFilePath(), cause);
            IsolatedMainApp app = new IsolatedMainApp(storage);
            app.init();
            assertFatalDialogAndShutdown(app, "The data could not be read.");
        }
    }

    private Storage storageWithFailure(Path file, Exception cause) {
        return new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))) {
            @Override
            public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                throw new DataLoadingException(cause);
            }
        };
    }

    @Test
    public void start_validBook_delegatesToUiAndSavesPreferencesOnStop() throws Exception {
        IsolatedMainApp app = new IsolatedMainApp(storage);
        app.init();
        JavaFxToolkit.call(() -> {
            Stage stage = new Stage();
            List<Stage> started = new ArrayList<>();
            app.ui = started::add;
            app.start(stage);
            assertEquals(List.of(stage), started);
            app.stop();
            return null;
        });
        assertEquals(app.model.getUserPrefs(), storage.readUserPrefs().orElseThrow());
        assertEquals(originalBook, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void fatalFailures_inChildProcesses_dismissAndTerminateWithoutOverwritingData() throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        String classpath = loader instanceof URLClassLoader urls
                ? Arrays.stream(urls.getURLs()).map(url -> Path.of(URI.create(url.toExternalForm())).toString())
                        .collect(Collectors.joining(File.pathSeparator)) : System.getProperty("java.class.path");
        for (String mode : List.of("loading", "null-message", "blank-message")) {
            Path folder = temporaryFolder.resolve(mode);
            Path file = folder.resolve("data/addressbook.json");
            Files.createDirectories(file.getParent());
            String json = mode.equals("loading") ? templateJson.replace("LegacyMarker", "\u2003") : templateJson;
            Files.writeString(file, json);
            List<String> command = new ArrayList<>(List.of(Path.of(System.getProperty("java.home"),
                    "bin", "java").toString()));
            // Keep the report destination absolute when the child uses a disposable working directory.
            ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                    .filter(arg -> arg.startsWith("-javaagent:") && arg.contains("jacoco"))
                    .map(arg -> {
                        String destination = arg.split("destfile=", 2)[1].split(",", 2)[0];
                        return arg.replace("destfile=" + destination,
                                "destfile=" + Path.of(destination).toAbsolutePath());
                    }).forEach(command::add);
            command.addAll(List.of("-cp", classpath, getClass().getName(), mode));
            Path log = folder.resolve("process.log");
            Process child = new ProcessBuilder(command).directory(folder.toFile())
                    .redirectErrorStream(true).redirectOutput(log.toFile()).start();
            try {
                boolean exited = child.waitFor(20, TimeUnit.SECONDS);
                String output = Files.readString(log);
                assertTrue(exited, "Fatal dialog did not terminate: " + mode + "\n" + output);
                assertEquals(mode.equals("loading") ? 0 : 1, child.exitValue(), output);
                assertTrue(output.contains("FATAL_OBSERVED " + mode), output);
                assertTrue(output.contains(mode.equals("loading") ? "DataLoadingException" : "IllegalStateException"));
                assertEquals(json, Files.readString(file));
            } finally {
                child.destroyForcibly();
                child.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Runs real JavaFX shutdown in a separate JVM, keeping the JUnit toolkit alive.
     */
    public static void main(String[] args) {
        Application.launch(ShutdownApplication.class, args);
    }

    /**
     * Exercises startup failure and fatal UI initialization errors with absent or blank messages.
     */
    public static class ShutdownApplication extends MainApp {
        @Override
        public void start(Stage primaryStage) {
            String mode = getParameters().getRaw().getFirst();
            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    for (Window window : List.copyOf(Window.getWindows())) {
                        DialogPane pane = (DialogPane) window.getScene().lookup("#alertDialogPane");
                        if (pane == null) {
                            continue;
                        }
                        stop();
                        Platform.runLater(() -> {
                            try {
                                TextArea content = (TextArea) pane.getContent();
                                assertTrue(content.getText().contains(mode.equals("loading")
                                        ? Notes.MESSAGE_CONSTRAINTS : "Cyclique could not continue."));
                                if (mode.equals("loading")) {
                                    assertFalse(primaryStage.isShowing());
                                    assertNull(primaryStage.getScene());
                                    assertNull(model);
                                    assertNull(logic);
                                    assertNull(ui);
                                }
                                System.out.println("FATAL_OBSERVED " + mode);
                            } finally {
                                Button button = (Button) pane.lookupButton(ButtonType.OK);
                                button.fire();
                            }
                        });
                    }
                }
            }.start();
            if (mode.equals("loading")) {
                super.start(primaryStage);
            } else {
                LogicManager failing = new LogicManager(model, storage) {
                    @Override
                    public GuiSettings getGuiSettings() {
                        throw new IllegalStateException(mode.equals("blank-message") ? " " : null);
                    }
                };
                new UiManager(failing, Path.of("data/addressbook.json")).start(primaryStage);
            }
        }
    }

    private String writeLegacyNotes(String notes) throws Exception {
        String json = templateJson.replace("LegacyMarker", notes);
        Files.writeString(storage.getAddressBookFilePath(), json);
        return json;
    }

    private AddressBook withNotes(String notes) {
        AddressBook expected = new AddressBook(originalBook);
        Ride first = expected.getRideList().getFirst();
        expected.setRide(first, new RideBuilder(first).withNotes(notes).build());
        return expected;
    }

    private void assertFatalDialogAndShutdown(IsolatedMainApp app, String expectedReason) throws Exception {
        byte[] before = Files.readAllBytes(storage.getAddressBookFilePath());
        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        try {
            JavaFxToolkit.call(() -> {
                Stage primaryStage = new Stage();
                List<Window> previousWindows = List.copyOf(Window.getWindows());
                FutureTask<Void> dismissDialog = new FutureTask<>(() -> {
                    List<Window> newWindows = Window.getWindows().stream()
                            .filter(window -> !previousWindows.contains(window)).toList();
                    try {
                        assertEquals(1, newWindows.size(), "Only the fatal dialog should become visible.");
                        Stage dialog = (Stage) newWindows.getFirst();
                        assertTrue(dialog.isShowing());
                        assertNull(dialog.getOwner(), "Startup has no owner scene.");
                        assertEquals("Could not load Cyclique data", dialog.getTitle());
                        DialogPane pane = (DialogPane) dialog.getScene()
                                .lookup("#" + UiManager.ALERT_DIALOG_PANE_FIELD_ID);
                        assertEquals("Could not load Cyclique data", pane.getHeaderText());
                        TextArea content = (TextArea) pane.getContent();
                        String message = content.getText();
                        assertTrue(content.isWrapText());
                        assertFalse(content.isEditable());
                        assertTrue(message.contains(expectedReason));
                        assertTrue(message.contains("The original file was not overwritten"));
                        assertFalse(message.contains("DO_NOT_DISPLAY"));
                        assertFalse(message.contains("IllegalStateException"));
                        assertFalse(message.contains("JsonParseException"));
                        assertTrue(message.contains(storage.getAddressBookFilePath().toAbsolutePath().toString()));
                        assertTrue(message.contains("Could not load data file"));
                        assertTrue(message.contains("Back up and correct the file before restarting Cyclique"));
                        assertEquals(List.of(ButtonType.OK), pane.getButtonTypes());
                        assertFalse(app.exitRequested);
                        Button dismissButton = (Button) pane.lookupButton(ButtonType.OK);
                        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
                        assertTrue(dialog.getWidth() <= screen.getWidth() * 0.9 + 2);
                        assertTrue(dialog.getHeight() <= screen.getHeight() * 0.9 + 2);
                        assertTrue(screen.contains(dialog.getX(), dialog.getY(),
                                dialog.getWidth(), dialog.getHeight()));
                        assertEquals(StageStyle.DECORATED, dialog.getStyle());
                        Bounds buttonBounds = dismissButton.localToScreen(dismissButton.getBoundsInLocal());
                        assertNotNull(buttonBounds);
                        assertTrue(dismissButton.isVisible());
                        assertTrue(screen.contains(buttonBounds.getMinX(), buttonBounds.getMinY(),
                                buttonBounds.getWidth(), buttonBounds.getHeight()));
                        assertTrue(new Rectangle2D(dialog.getX(), dialog.getY(), dialog.getWidth(), dialog.getHeight())
                                .contains(buttonBounds.getMinX(), buttonBounds.getMinY(),
                                        buttonBounds.getWidth(), buttonBounds.getHeight()));
                        assertTrue(pane.getScene().getRoot().getBoundsInLocal().contains(
                                pane.getScene().getRoot().sceneToLocal(dismissButton.localToScene(
                                        dismissButton.getBoundsInLocal()))));
                        if (expectedReason.length() > 2000) {
                            content.setScrollTop(Double.MAX_VALUE);
                            assertTrue(content.getScrollTop() > 0, "Long messages must remain scrollable.");
                        }
                        dismissButton.fire();
                        assertFalse(dialog.isShowing());
                    } finally {
                        newWindows.forEach(Window::hide);
                    }
                    return null;
                });
                // Observe an actual shown window, then wait behind the dialog's queued native sizing.
                ListChangeListener<Window> shownListener = change -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            Platform.runLater(() -> Platform.runLater(dismissDialog));
                        }
                    }
                };
                Window.getWindows().addListener(shownListener);
                try {
                    app.start(primaryStage);
                    assertTrue(dismissDialog.isDone(), "The fatal dialog must be inspected before startup returns.");
                    dismissDialog.get();
                } finally {
                    Window.getWindows().removeListener(shownListener);
                }
                assertTrue(app.exitRequested);
                assertFalse(primaryStage.isShowing());
                assertNull(primaryStage.getScene());
                app.stop(); // The real stop() must tolerate failed initialization without writing preferences.
                return null;
            });
        } catch (TimeoutException e) {
            Platform.runLater(() -> {
                for (Window window : List.copyOf(Window.getWindows())) {
                    if (window.getScene() != null
                            && window.getScene().lookup("#" + UiManager.ALERT_DIALOG_PANE_FIELD_ID) != null) {
                        System.err.println("Timed-out fatal dialog: " + window.getX() + "," + window.getY()
                                + " " + window.getWidth() + "x" + window.getHeight());
                        window.hide();
                    }
                }
            });
            throw new AssertionError("Fatal dialog did not complete for " + storage.getAddressBookFilePath(), e);
        }
        assertNull(app.model);
        assertNull(app.logic);
        assertNull(app.ui);
        assertArrayEquals(before, Files.readAllBytes(storage.getAddressBookFilePath()));
        assertFalse(Files.exists(storage.getUserPrefsFilePath()));
    }

    /**
     * Runs the real init lifecycle while redirecting all data access to disposable test storage.
     */
    private static class IsolatedMainApp extends MainApp {
        private final Storage testStorage;
        private boolean exitRequested;

        IsolatedMainApp(Storage testStorage) {
            this.testStorage = testStorage;
        }

        @Override
        protected UserPrefs initPrefs(JsonUserPrefsStorage ignoredStorage) {
            return new UserPrefs();
        }

        @Override
        protected Model initModelManager(Storage ignoredStorage, ReadOnlyUserPrefs userPrefs) {
            storage = testStorage;
            return super.initModelManager(testStorage, userPrefs);
        }

        @Override
        protected void exitAfterStartupFailure() {
            exitRequested = true; // Keep the shared test toolkit alive; real application shutdown is tested separately.
        }
    }
}
