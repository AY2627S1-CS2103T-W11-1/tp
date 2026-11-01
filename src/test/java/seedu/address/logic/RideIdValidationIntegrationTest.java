package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
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
 * Tests ride ID overflow and unknown IDs through command parsing, filtering, and JSON storage.
 */
public class RideIdValidationIntegrationTest {
    private static final List<String> COMMAND_FORMATS = List.of(
            "ridelist %s", "rl %s", "rideedit %s n/Changed", "re %s n/Changed", "ridedelete %s", "rd %s",
            "rideaddcyclist %s +65 91234567", "rac %s +65 91234567",
            "ridedeletecyclist %s +65 91234567", "rdc %s +65 91234567");

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private Path bookPath;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        bookPath = temporaryFolder.resolve("book.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(bookPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage);
        logic.execute("add n/Test Rider p/+65 91234567 sos/+65 99999999"
                + " e/rider@example.com f/250 x/Beginner l/any");
        logic.execute("rideadd n/Test Ride from/01-11-2026 0600 to/01-11-2026 0800"
                + " l/Mandai d/40 f/150 s/5");
        logic.execute("rideaddcyclist 1 +65 91234567");
        logic.execute("find n/Missing");
        logic.execute("ridefind n/Missing");
    }

    @Test
    public void execute_overflowingRideIds_reportsRangeWithoutChanges() throws Exception {
        // Include the first overflowing values, leading zeros, and values larger than long.
        AddressBook original = new AddressBook(model.getAddressBook());
        String originalJson = Files.readString(bookPath);
        List<Person> originalCyclists = List.copyOf(model.getFilteredPersonList());
        List<Ride> originalRides = List.copyOf(model.getFilteredRideList());
        for (String id : List.of("2147483648", "2147483649", "02147483648", "9".repeat(100))) {
            for (String format : COMMAND_FORMATS) {
                String command = String.format(format, id);
                assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_RIDE_ID, () -> logic.execute(command));
                assertEquals(original, model.getAddressBook(), command);
                assertEquals(originalJson, Files.readString(bookPath), command);
                assertEquals(originalCyclists, model.getFilteredPersonList(), command);
                assertEquals(originalRides, model.getFilteredRideList(), command);
            }
        }
    }

    @Test
    public void execute_unknownInRangeIds_reportsMissingRideWithoutChanges() throws Exception {
        // Parseable IDs still reach lookup, including the maximum int value and its leading-zero spelling.
        AddressBook original = new AddressBook(model.getAddressBook());
        String originalJson = Files.readString(bookPath);
        for (String id : List.of("2", "2147483647", "02147483647")) {
            for (String format : COMMAND_FORMATS) {
                String message = format.contains("cyclist") || format.startsWith("rac ") || format.startsWith("rdc ")
                        ? "Ride ID cannot be found."
                        : "Ride with ID " + Integer.parseInt(id) + " cannot be found.";
                assertThrows(CommandException.class, message, () -> logic.execute(String.format(format, id)));
                assertEquals(original, model.getAddressBook());
                assertEquals(originalJson, Files.readString(bookPath));
                assertEquals(List.of(), model.getFilteredPersonList());
                assertEquals(List.of(), model.getFilteredRideList());
            }
        }
    }
}
