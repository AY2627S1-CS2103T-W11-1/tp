package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests quoted ride notes searches through parsing, filtering, and JSON storage.
 */
public class QuotedRideFindIntegrationTest {
    private static final String RIDE_FIELDS = " from/01-11-2026 0600 to/01-11-2026 0800"
            + " l/Mandai d/40 s/5";

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;
    private Path bookPath;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        bookPath = temporaryFolder.resolve("book.json");
        storage = new StorageManager(new JsonAddressBookStorage(bookPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage);
        logic.execute("add n/Test Rider p/+65 91234567 sos/+65 99999999"
                + " e/rider@example.com f/250 x/Beginner l/any");
        logic.execute("rideadd n/Camera Ride" + RIDE_FIELDS + " f/180 notes/\"Bring camera with aperture f/2\"");
        logic.execute("rideadd n/Prefix Ride" + RIDE_FIELDS + " f/250 notes/“n/Ride from/now to/later l/Mandai"
                + " d/40 f/2 s/5 notes/other”");
        logic.execute("rideadd n/Plain Ride" + RIDE_FIELDS + " f/200");
        logic.execute("find n/Missing");
    }

    @Test
    public void execute_quotedNotesAndOtherCriteria_filtersByLiteralPhrase() throws Exception {
        // Both quote styles and the alias must protect f/2 without creating a minimum-power criterion.
        AddressBook original = new AddressBook(model.getAddressBook());
        for (String command : List.of("ridefind notes/\"aperture f/2\"", "rf notes/“APERTURE f/2”",
                "ridefind f/180 notes/\"aperture f/2\"", "ridefind notes/“aperture f/2” f/180")) {
            logic.execute(command);
            assertEquals(List.of(original.getRideList().get(0)), model.getFilteredRideList(), command);
            assertEquals(List.of(), model.getFilteredPersonList(), command);
            assertEquals(original, model.getAddressBook(), command);
            assertEquals(original, storage.readAddressBook().orElseThrow(), command);
        }
        // A real power criterion outside the quotes still uses AND matching; partial words must not match.
        for (String criteria : List.of("notes/\"aperture f/2\" f/200", "notes/\"aperture f/20\"",
                "notes/\"perture f/2\"")) {
            logic.execute("ridefind " + criteria);
            assertEquals(List.of(), model.getFilteredRideList(), criteria);
        }
        logic.execute("ridefind all");
        assertEquals(original.getRideList(), model.getFilteredRideList());
    }

    @Test
    public void execute_eachQuotedPrefixAndReload_preservesMatching() throws Exception {
        // Reload the saved book before searching; every recognized prefix is literal inside notes quotes.
        model.setAddressBook(storage.readAddressBook().orElseThrow());
        Ride expected = model.getAddressBook().getRideList().get(1);
        for (String phrase : List.of("n/Ride", "from/now", "to/later", "l/Mandai", "d/40", "s/5", "notes/other")) {
            logic.execute("ridefind notes/\"" + phrase + "\"");
            assertEquals(List.of(expected), model.getFilteredRideList(), phrase);
        }
        // Omitting quotes retains the existing prefix interpretation and ordinary word searches.
        logic.execute("ridefind notes/aperture f/2");
        assertEquals(1, model.getFilteredRideList().size());
        assertEquals("Camera Ride", model.getFilteredRideList().get(0).getName().fullName);
    }

    @Test
    public void execute_invalidQuotedSearch_preservesBothFiltersAndSavedData() throws Exception {
        logic.execute("ridefind notes/\"aperture f/2\"");
        AddressBook original = new AddressBook(model.getAddressBook());
        String originalJson = Files.readString(bookPath);
        List<Ride> originalRides = List.copyOf(model.getFilteredRideList());
        List<Person> originalCyclists = List.copyOf(model.getFilteredPersonList());
        // Invalid quoting, duplicate outside fields, blank notes, and the word limit fail before filter updates.
        for (String args : List.of("notes/\"camera", "notes/“camera\"", "notes/\"\"", "notes/\"camera\"extra",
                "notes/\"camera\" notes/lights", "notes/\"camera\" f/180 f/200",
                "notes/\"one two three four five six seven eight nine ten eleven\"")) {
            assertThrows(ParseException.class, () -> logic.execute("ridefind " + args));
            assertEquals(original, model.getAddressBook());
            assertEquals(originalJson, Files.readString(bookPath));
            assertEquals(originalRides, model.getFilteredRideList());
            assertEquals(originalCyclists, model.getFilteredPersonList());
        }
    }
}
