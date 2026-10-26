package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests consistent phone errors through command parsing, filtering and JSON storage.
 */
public class PhoneValidationIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private Path bookPath;
    private AddressBook originalBook;
    private List<Person> originalDisplay;
    private String originalJson;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        bookPath = temporaryFolder.resolve("book.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(bookPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage);
        logic.execute("add n/Alpha Rider p/+65 87438807 sos/+65 99999999"
                + " e/alpha@example.com f/200 x/Beginner l/any");
        logic.execute("add n/Beta Rider p/+65 87438808 sos/+65 99999999"
                + " e/beta@example.com f/200 x/Beginner l/any");
        logic.execute("rideadd n/Morning Ride from/29-10-2026 0630 to/29-10-2026 0930"
                + " l/Mandai d/40 f/150 s/5");
        logic.execute("rideaddcyclist 1 +65 87438807");
        logic.execute("find n/Alpha");
        originalBook = new AddressBook(model.getAddressBook());
        originalDisplay = List.copyOf(model.getFilteredPersonList());
        originalJson = Files.readString(bookPath);
    }

    @Test
    public void execute_invalidPhone_reportsSameErrorAcrossCommandsWithoutChanges() throws Exception {
        String[] phones = {"87438807", "+6587438807", "+65  87438807", "+65 12", "+65 abc"};
        String[] messages = {Phone.MESSAGE_MISSING_COUNTRY_CODE, Phone.MESSAGE_MISSING_SEPARATOR,
            Phone.MESSAGE_MULTIPLE_SEPARATORS, Phone.MESSAGE_CONSTRAINTS, Phone.MESSAGE_CONSTRAINTS};
        for (int i = 0; i < phones.length; i++) {
            String[] commands = {"edit " + phones[i] + " f/130", "delete " + phones[i],
                "find p/" + phones[i], "add n/Phone Test p/" + phones[i]
                    + " sos/+65 89999999 e/phone.test@example.com f/200 x/Beginner l/any"};
            for (String command : commands) {
                assertThrows(ParseException.class, messages[i], () -> logic.execute(command));
                assertUnchanged();
            }
        }
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
        assertUnchanged();
    }

    @Test
    public void execute_missingTarget_reportsCommandUsageWithoutChanges() throws Exception {
        for (String command : new String[] {"edit", "edit   ", "edit f/130"}) {
            assertThrows(ParseException.class,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), () ->
                            logic.execute(command));
            assertUnchanged();
        }
        for (String command : new String[] {"delete", "delete   "}) {
            assertThrows(ParseException.class,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), () ->
                            logic.execute(command));
            assertUnchanged();
        }
    }

    private void assertUnchanged() throws Exception {
        assertEquals(originalBook, model.getAddressBook());
        assertEquals(originalDisplay, model.getFilteredPersonList());
        assertEquals(originalJson, Files.readString(bookPath));
    }
}
