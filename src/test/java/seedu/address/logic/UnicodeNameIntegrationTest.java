package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests Unicode cyclist and ride names through commands and JSON persistence.
 */
public class UnicodeNameIntegrationTest {
    private static final String CYCLIST_FIELDS = " p/+65 91234567 sos/+65 99999999"
            + " e/jose@example.com f/200 x/Beginner l/any";
    private static final String RIDE_FIELDS = " from/29-10-2026 0630 to/29-10-2026 0930"
            + " l/Mandai d/40 f/150 s/5";

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;
    private Path bookPath;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        bookPath = temporaryFolder.resolve("book.json");
        storage = new StorageManager(new JsonAddressBookStorage(bookPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_addAndEditUnicodeCyclistName_preservesSpellingAfterReload() throws Exception {
        // Cover accents, non-Latin scripts, Unicode digits, and internal punctuation through the full command flow.
        for (String name : new String[] {"José Tan", "Jose\u0301 Tan", "李明", "அருண்", "Rider ٢", "Jose Tan",
            "Tan Wei-Ling", "John O'Connor", "Jose\u0301-Tan"}) {
            logic.execute("add n/" + name + CYCLIST_FIELDS);
            assertEquals(name, model.getAddressBook().getPersonList().get(0).getName().fullName);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            // Change the name before restoring it so the edit must replace the stored spelling.
            logic.execute("edit +65 91234567 n/Zoë Tan");
            logic.execute("edit +65 91234567 n/" + name);
            assertEquals(name, model.getAddressBook().getPersonList().get(0).getName().fullName);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            // Reuse the same phone and email for each independent name case without triggering duplicate checks.
            logic.execute("clear");
        }
    }

    @Test
    public void execute_addReportedCyclistNames_findAndReloadPreserveSpelling() throws Exception {
        // Reproduce both reported add commands in the same book with distinct phone numbers and emails.
        logic.execute("add n/Tan Wei-Ling p/+65 91111111 sos/+65 99999999"
                + " e/weiling@example.com f/250 x/Beginner l/any");
        logic.execute("add n/John O'Connor p/+65 92222222 sos/+65 88888888"
                + " e/johnoconnor@example.com f/250 x/Beginner l/any");
        assertEquals(2, model.getAddressBook().getPersonList().size());
        // Search using the punctuated name part to verify that users can find the original spelling.
        for (String[] search : new String[][] {{"Wei-Ling", "Tan Wei-Ling"}, {"O'Connor", "John O'Connor"}}) {
            logic.execute("find n/" + search[0]);
            assertEquals(1, model.getFilteredPersonList().size());
            assertEquals(search[1], model.getFilteredPersonList().get(0).getName().fullName);
        }
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_addAndEditUnicodeRideName_preservesSpellingAfterReload() throws Exception {
        // Ride commands share Name validation and must also preserve spelling when saved and read back.
        logic.execute("rideadd n/Élan Ride" + RIDE_FIELDS);
        assertEquals("Élan Ride", model.getAddressBook().getRideList().get(0).getName().fullName);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        for (String name : new String[] {"E\u0301lan Ride", "晨骑", "Ride ٢", "Coast-to-Coast", "John's Ride"}) {
            logic.execute("rideedit 1 n/" + name);
            assertEquals(name, model.getAddressBook().getRideList().get(0).getName().fullName);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void execute_invalidNames_rejectsCommandsWithoutChanges() throws Exception {
        logic.execute("add n/José Tan" + CYCLIST_FIELDS);
        logic.execute("rideadd n/Élan Ride" + RIDE_FIELDS);
        AddressBook originalBook = new AddressBook(model.getAddressBook());
        String originalJson = Files.readString(bookPath);

        // Cover blank names, misplaced punctuation, detached marks, and unsupported symbols across all four commands.
        for (String name : new String[] {"", "José--Tan", "O''Connor", "-Tan", "Tan'", "Jose-\u0301Tan",
            "\u0301Jose", "Jose \u0301Tan", "Rider 🚲"}) {
            String[] commands = {"add n/" + name + CYCLIST_FIELDS, "edit +65 91234567 n/" + name,
                "rideadd n/" + name + RIDE_FIELDS, "rideedit 1 n/" + name};
            for (String command : commands) {
                // Rejection must leave both the in-memory records and the saved JSON unchanged.
                assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> logic.execute(command));
                assertEquals(originalBook, model.getAddressBook());
                assertEquals(originalJson, Files.readString(bookPath));
            }
        }
    }
}
