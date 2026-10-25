package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.RideRosterCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Exercises overlap warnings, confirmation state, and persistence through the real command pipeline.
 */
public class RideOverlapConfirmationTest {
    private static final String PHONE = "+65 91234567";
    private static final String OTHER_PHONE = "+65 98765432";
    private static final String JOIN = "rideaddcyclist 2 " + PHONE;
    private static final String EDIT = "rideedit 2 from/29-10-2026 0900 to/29-10-2026 1100";

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;
    private TrackingAddressBookStorage bookStorage;
    private AddressBook originalBook;
    private String originalJson;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        model.addPerson(new PersonBuilder().withPhone(PHONE).withFtp("250").build());
        model.addPerson(new PersonBuilder().withPhone(OTHER_PHONE).withEmail("other@example.com")
                .withFtp("250").build());
        model.addRide(new RideBuilder().withName("Ride A").withFrom("29-10-2026 0800")
                .withTo("29-10-2026 1000").withRoster(PHONE).build());
        model.addRide(new RideBuilder().withName("Ride B").withFrom("29-10-2026 0900")
                .withTo("29-10-2026 1100").build());
        bookStorage = new TrackingAddressBookStorage(temporaryFolder.resolve("book.json"));
        storage = new StorageManager(bookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage);
        saveBaseline();
    }

    @Test
    public void execute_join_warnsWithDetailsWithoutMutationOrSaveEvenWhenFiltered() throws Exception {
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredRideList(ride -> ride.getId() == 2);
        String warning = logic.execute(JOIN).getFeedbackToUser();
        assertTrue(warning.startsWith("Warning: overlapping ride times."));
        assertTrue(warning.contains("Proposed ride 2 (Ride B): 29-10-2026 0900 to 29-10-2026 1100"));
        assertTrue(warning.contains("Cyclist " + PHONE + ": ride 1 (Ride A): "
                + "29-10-2026 0800 to 29-10-2026 1000"));
        assertTrue(warning.contains("y/yes"));
        assertUnchanged();
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(List.of(ride(2)), model.getFilteredRideList());
    }

    @Test
    public void execute_yesVariants_joinAndPersistExactlyOnce() throws Exception {
        for (String reply : List.of("y", "Y", "yes", "YES", "yEs")) {
            setUp();
            logic.execute(JOIN);
            assertTrue(logic.execute(" " + reply + " ").getFeedbackToUser().contains("added to ride 2"));
            assertEquals(List.of(new Phone(PHONE)), ride(2).getRoster());
            assertEquals(1, bookStorage.saveCalls);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            assertThrows(ParseException.class, () -> logic.execute("yes"));
        }
    }

    @Test
    public void execute_noVariants_cancelWithoutMutationOrSave() throws Exception {
        for (String reply : List.of("n", "N", "no", "NO", "nO")) {
            setUp();
            logic.execute(JOIN);
            assertEquals(LogicManager.MESSAGE_OVERLAP_CANCELLED,
                    logic.execute(" " + reply + " ").getFeedbackToUser());
            assertUnchanged();
            assertThrows(ParseException.class, () -> logic.execute("yes"));
        }
    }

    @Test
    public void execute_invalidReply_preservesPendingWarning() throws Exception {
        String prompt = logic.execute(JOIN).getFeedbackToUser();
        for (String reply : List.of("", "maybe", "yes please", "LIST")) {
            assertEquals(String.format(LogicManager.MESSAGE_INVALID_OVERLAP_REPLY, prompt),
                    assertThrows(CommandException.class, () -> logic.execute(reply)).getMessage());
            assertUnchanged();
        }
        logic.execute("yes");
        assertEquals(1, ride(2).getRoster().size());
    }

    @Test
    public void execute_newCommand_cancelsWarningAndPreservesHelpAndExitFlags() throws Exception {
        for (String command : List.of("list", "help", "exit", "ridefind all")) {
            setUp();
            logic.execute(JOIN);
            var result = logic.execute(command);
            assertTrue(result.getFeedbackToUser().startsWith(LogicManager.MESSAGE_OVERLAP_CANCELLED + "\n"));
            assertEquals(command.equals("help"), result.isShowHelp());
            assertEquals(command.equals("exit"), result.isExit());
            assertEquals(originalBook, model.getAddressBook());
            assertThrows(ParseException.class, () -> logic.execute("yes"));
        }
    }

    @Test
    public void execute_invalidNewCommand_reportsCancellationAndOriginalError() throws Exception {
        for (String command : List.of("rideedit", "rideaddcyclist 999 " + PHONE)) {
            setUp();
            logic.execute(JOIN);
            Exception error = assertThrows(Exception.class, () -> logic.execute(command));
            assertTrue(error.getMessage().startsWith(LogicManager.MESSAGE_OVERLAP_CANCELLED + "\n"));
            assertUnchanged();
            assertThrows(ParseException.class, () -> logic.execute("yes"));
        }
    }

    @Test
    public void execute_deletionAndOverlapRequests_replaceEachOther() throws Exception {
        logic.execute("delete " + PHONE);
        assertTrue(logic.execute(JOIN).getFeedbackToUser().startsWith(DeleteCommand.MESSAGE_DELETE_CANCELLED));
        logic.execute("yes");
        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertEquals(1, ride(2).getRoster().size());
        logic.execute("ridedeletecyclist 2 " + PHONE);
        logic.execute(JOIN);
        assertTrue(logic.execute("delete " + PHONE).getFeedbackToUser()
                .startsWith(LogicManager.MESSAGE_OVERLAP_CANCELLED));
        logic.execute("yes");
        assertEquals(1, model.getAddressBook().getPersonList().size());
        assertTrue(ride(2).getRoster().isEmpty());
    }

    @Test
    public void execute_newOverlapRequest_replacesPreviousRequest() throws Exception {
        model.addRide(new RideBuilder(ride(2)).withName("Ride C").build());
        logic.execute(JOIN);
        assertTrue(logic.execute("rideaddcyclist 3 " + PHONE).getFeedbackToUser()
                .startsWith(LogicManager.MESSAGE_OVERLAP_CANCELLED));
        logic.execute("yes");
        assertTrue(ride(2).getRoster().isEmpty());
        assertEquals(1, ride(3).getRoster().size());
    }

    @Test
    public void execute_changedData_rejectsStaleConfirmation() throws Exception {
        logic.execute(JOIN);
        model.setRide(ride(1), new RideBuilder(ride(1)).withName("Changed Ride").build());
        assertEquals(LogicManager.MESSAGE_OVERLAP_DATA_CHANGED,
                assertThrows(CommandException.class, () -> logic.execute("yes")).getMessage());
        assertTrue(ride(2).getRoster().isEmpty());
        assertEquals(0, bookStorage.saveCalls);
        assertThrows(ParseException.class, () -> logic.execute("yes"));
    }

    @Test
    public void execute_restart_discardsPendingRequest() throws Exception {
        logic.execute(JOIN);
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertThrows(ParseException.class, () -> logic.execute("yes"));
        assertUnchanged();
    }

    @Test
    public void execute_saveFailure_consumesConfirmationAndKeepsExistingError() throws Exception {
        logic.execute(JOIN);
        bookStorage.failSave = true;
        assertTrue(assertThrows(CommandException.class, () -> logic.execute("yes"))
                .getMessage().startsWith("Could not save data"));
        assertEquals(1, ride(2).getRoster().size());
        assertEquals(originalJson, Files.readString(storage.getAddressBookFilePath()));
        assertThrows(ParseException.class, () -> logic.execute("yes"));
    }

    @Test
    public void execute_invalidEligibility_reportsErrorBeforeOverlapWarning() throws Exception {
        model.setRide(ride(2), new RideBuilder(ride(2)).withMinimumPower("300").build());
        saveBaseline();
        assertEquals(RideRosterCommand.MESSAGE_LOW_POWER,
                assertThrows(CommandException.class, () -> logic.execute(JOIN)).getMessage());
        assertUnchanged();
        assertThrows(ParseException.class, () -> logic.execute("yes"));
    }

    @Test
    public void execute_otherCyclistOrRosterRemoval_doesNotWarn() throws Exception {
        assertFalse(logic.execute("rideaddcyclist 2 " + OTHER_PHONE).getFeedbackToUser().contains("Warning"));
        assertFalse(logic.execute("ridedeletecyclist 1 " + PHONE).getFeedbackToUser().contains("Warning"));
        assertFalse(logic.execute(JOIN).getFeedbackToUser().contains("Warning"));
        assertEquals(List.of(new Phone(OTHER_PHONE), new Phone(PHONE)), ride(2).getRoster());
    }

    @Test
    public void execute_timeEdit_warnsForAllAffectedCyclistsAndSupportsCancelAndConfirm() throws Exception {
        model.setRide(ride(1), ride(1).withRoster(List.of(new Phone(PHONE), new Phone(OTHER_PHONE))));
        model.setRide(ride(2), new RideBuilder(ride(2)).withFrom("29-10-2026 1200")
                .withTo("29-10-2026 1400").withRoster(PHONE, OTHER_PHONE).build());
        saveBaseline();
        String warning = logic.execute(EDIT).getFeedbackToUser();
        assertTrue(warning.contains("Cyclist " + PHONE));
        assertTrue(warning.contains("Cyclist " + OTHER_PHONE));
        assertUnchanged();
        logic.execute("no");
        assertUnchanged();
        logic.execute(EDIT);
        logic.execute("yes");
        assertEquals("29-10-2026 0900", ride(2).getFrom().toString());
        assertEquals(originalBook.getRideList().get(1).getRoster(), ride(2).getRoster());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_nonTimeEditAndUnchangedTimes_doNotWarnAboutExistingOverlap() throws Exception {
        model.setRide(ride(2), ride(2).withRoster(List.of(new Phone(PHONE))));
        assertFalse(logic.execute("rideedit 2 notes/Bring lights").getFeedbackToUser().contains("Warning"));
        assertFalse(logic.execute(EDIT).getFeedbackToUser().contains("Warning"));
        assertFalse(logic.execute("rideedit 2 from/29-10-2026 1000").getFeedbackToUser().contains("Warning"));
        assertEquals("29-10-2026 1000", ride(2).getFrom().toString());
    }

    private Ride ride(int id) {
        return model.getAddressBook().getRideList().get(id - 1);
    }

    private void saveBaseline() throws IOException {
        storage.saveAddressBook(model.getAddressBook());
        bookStorage.saveCalls = 0;
        originalBook = new AddressBook(model.getAddressBook());
        originalJson = Files.readString(storage.getAddressBookFilePath());
    }

    private void assertUnchanged() throws IOException {
        assertEquals(originalBook, model.getAddressBook());
        assertEquals(originalJson, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(0, bookStorage.saveCalls);
    }

    /** Tracks writes while retaining real JSON storage. */
    private static class TrackingAddressBookStorage extends JsonAddressBookStorage {
        private int saveCalls;
        private boolean failSave;

        TrackingAddressBookStorage(Path path) {
            super(path);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            saveCalls++;
            if (failSave) {
                throw new IOException("test failure");
            }
            super.saveAddressBook(addressBook);
        }
    }
}
