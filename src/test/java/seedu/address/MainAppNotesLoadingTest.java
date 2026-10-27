package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.ride.Ride;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

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
            IllegalStateException error = assertThrows(IllegalStateException.class, app::init);
            assertTrue(error.getMessage().contains(storage.getAddressBookFilePath().toString()));
            assertTrue(error.getMessage().contains("Startup stopped to protect the original data"));
            assertTrue(error.getMessage().contains("Back up and correct the file"));
            assertInstanceOf(DataLoadingException.class, error.getCause());
            assertNull(app.model);
            assertNull(app.logic, "A failed startup must not expose commands that could save an empty book.");
            assertNull(app.ui);
            assertArrayEquals(legacyJson.getBytes(StandardCharsets.UTF_8),
                    Files.readAllBytes(storage.getAddressBookFilePath()));
            assertThrows(IllegalStateException.class, new IsolatedMainApp(storage)::init);
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
    public void init_repairedFile_allowsCommandsWithoutWeakeningNotesValidation() throws Exception {
        writeLegacyNotes(INVALID_LEGACY_NOTES.getLast());
        assertThrows(IllegalStateException.class, new IsolatedMainApp(storage)::init);
        writeLegacyNotes(TEN_WORDS.replace(" ", "\u00a0")); // Explicit user repair, not automatic truncation.
        IsolatedMainApp app = new IsolatedMainApp(storage);
        app.init();
        app.logic.execute("list");
        AddressBook expected = withNotes(TEN_WORDS);
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String notes : INVALID_LEGACY_NOTES) {
            assertThrows(ParseException.class, () -> app.logic.execute("rideadd n/Rejected "
                    + "from/29-10-2026 0800 to/29-10-2026 1000 l/East Coast d/20 f/180 s/2 notes/" + notes));
            assertThrows(ParseException.class, () -> app.logic.execute("rideedit 1 f/200 notes/\"" + notes + "\""));
            assertEquals(expected, app.model.getAddressBook());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
        app.logic.execute("rideedit 1 notes/\"Bring\u2003camera with aperture f/2\" s/5");
        Ride first = expected.getRideList().getFirst();
        expected.setRide(first, new RideBuilder(first).withNotes("Bring camera with aperture f/2")
                .withMaxGroupSize("5").build());
        assertEquals(expected, app.model.getAddressBook());
        assertEquals(expected, storage.readAddressBook().orElseThrow());
        IsolatedMainApp restarted = new IsolatedMainApp(storage);
        restarted.init();
        assertEquals(expected, restarted.model.getAddressBook());
        restarted.logic.execute("list");
        assertEquals(expected, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void init_malformedJson_alsoStopsWithoutReplacingExistingFile() throws Exception {
        String malformed = "{invalid json";
        Files.writeString(storage.getAddressBookFilePath(), malformed);
        IsolatedMainApp app = new IsolatedMainApp(storage);
        assertThrows(IllegalStateException.class, app::init);
        assertNull(app.logic);
        assertEquals(malformed, Files.readString(storage.getAddressBookFilePath()));
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

    /**
     * Runs the real init lifecycle while redirecting all data access to disposable test storage.
     */
    private static class IsolatedMainApp extends MainApp {
        private final Storage testStorage;

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
    }
}
