package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.RideRosterCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Exercises roster commands through parsing, model updates, and persistent storage.
 */
public class RideRosterIntegrationTest {
    private static final String PHONE = "+65 91234567";
    private static final String OTHER_PHONE = "+65 98765432";

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        model.addPerson(new PersonBuilder().withPhone(PHONE).withFtp("180").build());
        model.addPerson(new PersonBuilder().withPhone(OTHER_PHONE).withEmail("other@example.com")
                .withFtp("179").build());
        model.addRide(new RideBuilder().withMaxGroupSize("1").build());
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("book.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        storage.saveAddressBook(model.getAddressBook());
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_tabSeparatedRideEdit_updatesBothFields() throws Exception {
        logic.execute("rideedit 1 notes/Bring lights\ts/2");
        assertEquals("2", currentRide().getMaxGroupSize().value);
        assertEquals("Bring lights", currentRide().getNotes().orElseThrow().value);
        assertSavedState();
    }

    @Test
    public void execute_fullNames_updatesRosterAndPersists() throws Exception {
        model.updateFilteredPersonList(person -> false);
        Ride original = model.getAddressBook().getRideList().get(0);
        logic.execute("rideaddcyclist 1 " + PHONE);
        assertEquals(original.withRoster(List.of(new Phone(PHONE))), currentRide());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertSavedState();
        logic.execute("ridedeletecyclist 1 " + PHONE);
        assertEquals(original, currentRide());
        logic.execute("rideaddcyclist 1 " + PHONE);
        assertSavedState();
        logic.execute("ridedeletecyclist 1 " + PHONE);
        assertEquals(original, currentRide());
        assertEquals(2, model.getAddressBook().getNextRideId());
        assertSavedState();
    }

    @Test
    public void execute_afterReload_canRemovePersistedMembership() throws Exception {
        logic.execute("rideaddcyclist 1 " + PHONE);
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertEquals(List.of(new Phone(PHONE)), currentRide().getRoster());
        logic.execute("ridedeletecyclist 1 " + PHONE);
        assertTrue(currentRide().getRoster().isEmpty());
        assertSavedState();
    }

    @Test
    public void execute_invalidSyntax_preservesModelFilterAndSavedData() throws Exception {
        logic.execute("rideaddcyclist 1 " + PHONE);
        model.updateFilteredPersonList(person -> false);
        Ride before = currentRide();
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String input : List.of("rideaddcyclist 0 " + PHONE, "ridedeletecyclist 1 p/" + PHONE,
                "rideaddcyclist 1 " + PHONE + " extra")) {
            assertThrows(ParseException.class, () -> logic.execute(input));
            assertEquals(before, currentRide());
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_removeFromOneRide_preservesOtherRideAndCyclist() throws Exception {
        model.addRide(new RideBuilder().build());
        logic.execute("rideaddcyclist 1 " + PHONE);
        logic.execute("rideaddcyclist 2 " + PHONE);
        logic.execute("ridedeletecyclist 1 " + PHONE);
        assertTrue(currentRide().getRoster().isEmpty());
        assertEquals(List.of(new Phone(PHONE)), model.getAddressBook().getRideList().get(1).getRoster());
        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertSavedState();
    }

    @Test
    public void execute_invalidMembership_rejectsWithoutChanges() throws Exception {
        assertRejected("rideaddcyclist 99 " + PHONE, RideRosterCommand.MESSAGE_RIDE_NOT_FOUND);
        assertRejected("rideaddcyclist 1 +65 99999999", RideRosterCommand.MESSAGE_PERSON_NOT_FOUND);
        assertRejected("ridedeletecyclist 1 " + PHONE, RideRosterCommand.MESSAGE_NOT_JOINED);
        assertRejected("rideaddcyclist 1 " + OTHER_PHONE, RideRosterCommand.MESSAGE_LOW_POWER);
        logic.execute("rideaddcyclist 1 " + PHONE);
        assertRejected("rideaddcyclist 1 " + PHONE, RideRosterCommand.MESSAGE_ALREADY_JOINED);
        assertRejected("rideaddcyclist 1 " + OTHER_PHONE, RideRosterCommand.MESSAGE_FULL);
        logic.execute("ridedeletecyclist 1 " + PHONE);
        logic.execute("rideaddcyclist 1 " + PHONE);
        assertSavedState();
    }

    @Test
    public void execute_phoneEditAndConfirmedDeletion_updatesAllRosters() throws Exception {
        model.addRide(new RideBuilder().build());
        logic.execute("rideaddcyclist 1 " + PHONE);
        logic.execute("rideaddcyclist 2 " + PHONE);
        logic.execute("edit " + PHONE + " p/+65 91111111");
        for (Ride ride : model.getAddressBook().getRideList()) {
            assertEquals(List.of(new Phone("+65 91111111")), ride.getRoster());
        }
        assertSavedState();
        logic.execute("delete +65 91111111");
        logic.execute("no");
        assertEquals(1, currentRide().getRoster().size());
        logic.execute("delete +65 91111111");
        logic.execute("yes");
        for (Ride ride : model.getAddressBook().getRideList()) {
            assertTrue(ride.getRoster().isEmpty());
        }
        assertSavedState();
    }

    @Test
    public void execute_rosterCommand_cancelsPendingCyclistDeletion() throws Exception {
        logic.execute("delete " + PHONE);
        assertTrue(logic.execute("rideaddcyclist 1 " + PHONE).getFeedbackToUser().startsWith("Deletion cancelled."));
        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertEquals(List.of(new Phone(PHONE)), currentRide().getRoster());
    }

    @Test
    public void parse_invalidArguments_rejects() {
        AddressBookParser parser = new AddressBookParser();
        for (String word : List.of("rideaddcyclist", "ridedeletecyclist")) {
            assertTrue(parser.isRecognisedCommand(word));
            for (String args : List.of("", " 1", " 0 " + PHONE, " -1 " + PHONE,
                    " 2147483648 " + PHONE, " abc " + PHONE, " 1 91234567",
                    " 1 p/" + PHONE, " 1 " + PHONE + " extra", " 1 " + PHONE + " " + OTHER_PHONE)) {
                assertThrows(ParseException.class, () -> parser.parseCommand(word + args), word + args);
            }
        }
    }

    @Test
    public void execute_unsupportedAliasSpellings_rejectWithoutChangingDataOrPendingDeletion() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        String saved = Files.readString(storage.getAddressBookFilePath());
        Ride before = currentRide();
        for (String alias : List.of("RAC", "RDC", "rac1", "rdc1")) {
            assertFalse(parser.isRecognisedCommand(alias));
            ParseException exception = assertThrows(ParseException.class, () -> logic.execute(alias + " 1 " + PHONE));
            assertEquals(Messages.MESSAGE_UNKNOWN_COMMAND, exception.getMessage());
            assertEquals(before, currentRide());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
        logic.execute("delete " + PHONE);
        for (String alias : List.of("RAC", "RDC", "rac1", "rdc1")) {
            assertThrows(CommandException.class, () -> logic.execute(alias + " 1 " + PHONE));
        }
        assertEquals("Deletion cancelled.", logic.execute("no").getFeedbackToUser());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    private Ride currentRide() {
        return model.getAddressBook().getRideList().get(0);
    }

    private void assertRejected(String command, String message) throws Exception {
        Ride before = currentRide();
        String saved = Files.readString(storage.getAddressBookFilePath());
        CommandException exception = assertThrows(CommandException.class, () -> logic.execute(command));
        assertEquals(message, exception.getMessage());
        assertEquals(before, currentRide());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    private void assertSavedState() throws Exception {
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
