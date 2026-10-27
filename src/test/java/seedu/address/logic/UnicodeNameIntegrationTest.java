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
        for (String name : new String[] {"José Tan", "Jose\u0301 Tan", "李明", "அருண்", "Rider ٢", "Jose Tan"}) {
            logic.execute("add n/" + name + CYCLIST_FIELDS);
            assertEquals(name, model.getAddressBook().getPersonList().get(0).getName().fullName);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            logic.execute("edit +65 91234567 n/Zoë Tan");
            logic.execute("edit +65 91234567 n/" + name);
            assertEquals(name, model.getAddressBook().getPersonList().get(0).getName().fullName);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            logic.execute("clear");
        }
    }

    @Test
    public void execute_addAndEditUnicodeRideName_preservesSpellingAfterReload() throws Exception {
        logic.execute("rideadd n/Élan Ride" + RIDE_FIELDS);
        assertEquals("Élan Ride", model.getAddressBook().getRideList().get(0).getName().fullName);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        for (String name : new String[] {"E\u0301lan Ride", "晨骑", "Ride ٢"}) {
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

        for (String name : new String[] {"", "José-Tan", "\u0301Jose", "Jose \u0301Tan", "Rider 🚲"}) {
            String[] commands = {"add n/" + name + CYCLIST_FIELDS, "edit +65 91234567 n/" + name,
                "rideadd n/" + name + RIDE_FIELDS, "rideedit 1 n/" + name};
            for (String command : commands) {
                assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> logic.execute(command));
                assertEquals(originalBook, model.getAddressBook());
                assertEquals(originalJson, Files.readString(bookPath));
            }
        }
    }
}
