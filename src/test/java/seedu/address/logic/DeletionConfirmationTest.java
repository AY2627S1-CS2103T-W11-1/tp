package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RideDeleteCommand;
import seedu.address.logic.commands.RideEditCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;
import seedu.address.testutil.RideUtil;

/**
 * Tests deletion confirmation through the real command pipeline and JSON storage.
 */
public class DeletionConfirmationTest {
    private static final Person JOHN = new PersonBuilder().withName("John Doe")
            .withPhone("+65 98765432").withEmail("john@example.com").build();
    private static final Person ALICE = new PersonBuilder().withName("Alice Tan")
            .withPhone("+65 91234567").withEmail("alice@example.com").build();
    private static final String DELETE_JOHN = "delete +65 98765432";

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private Storage storage;
    private TrackingAddressBookStorage addressBookStorage;
    private String originalJson;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        model.addPerson(JOHN);
        model.addPerson(ALICE);
        addressBookStorage = new TrackingAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        storage.saveAddressBook(model.getAddressBook());
        addressBookStorage.saveCalls = 0;
        originalJson = Files.readString(storage.getAddressBookFilePath());
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_delete_requestsConfirmationWithoutMutationOrSave() throws Exception {
        model.updateFilteredPersonList(person -> person.equals(JOHN));
        CommandResult result = logic.execute(DELETE_JOHN);
        assertEquals(prompt(JOHN), result.getFeedbackToUser());
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
        assertEquals(List.of(JOHN), model.getFilteredPersonList());
        assertUnchangedWithoutSave();
    }

    @Test
    public void execute_yesVariants_deleteOnlyTargetAndPersist() throws Exception {
        // EP: affirmative replies (short/full words, any letter case, and surrounding spaces).
        for (String reply : List.of("y", "Y", "yes", "Yes", "YES", "yEs", "YeS", "yeS", "YEs", "yES")) {
            setUp();
            logic.execute(DELETE_JOHN);
            CommandResult result = logic.execute("  " + reply + "  ");
            assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(JOHN)),
                    result.getFeedbackToUser());
            assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
            assertEquals(1, addressBookStorage.saveCalls);
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
        }
    }

    @Test
    public void execute_noVariants_preserveRecordsFilterAndSavedFile() throws Exception {
        // EP: negative replies (short/full words, any letter case, and surrounding spaces).
        for (String reply : List.of("n", "N", "no", "No", "NO", "nO")) {
            setUp();
            model.updateFilteredPersonList(person -> person.equals(JOHN));
            logic.execute(DELETE_JOHN);
            assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED, logic.execute(" " + reply + " ").getFeedbackToUser());
            assertUnchangedWithoutSave();
            assertEquals(List.of(JOHN), model.getFilteredPersonList());
            assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
        }
    }

    @Test
    public void execute_invalidReplies_keepPendingDeletionWithoutSave() throws Exception {
        // EP: blank replies, unknown words, multiword replies, internally split replies, and wrong-case commands.
        logic.execute(DELETE_JOHN);
        for (String reply : List.of("maybe", "", "  ", "yes please", "y es", "no thanks", "Y eS", "LIST")) {
            assertThrows(CommandException.class,
                    String.format(DeleteCommand.MESSAGE_INVALID_CONFIRMATION, Messages.format(JOHN)), () ->
                    logic.execute(reply));
            assertUnchangedWithoutSave();
        }
        logic.execute("Yes");
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_newCommand_reportsCancellationAndNormalOutput() throws Exception {
        logic.execute(DELETE_JOHN);
        assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n" + ListCommand.MESSAGE_SUCCESS,
                logic.execute("list").getFeedbackToUser());
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertEquals(originalJson, Files.readString(storage.getAddressBookFilePath()));
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_rideDelete_cancelsPendingDeletionAndPersistsRideRemoval() throws Exception {
        logic.execute(RideUtil.getRideAddCommand(new RideBuilder().withRoster(JOHN.getPhone().value).build()));
        String deletedRide = Messages.formatRide(model.getAddressBook().getRideList().get(0));
        addressBookStorage.saveCalls = 0;
        logic.execute(DELETE_JOHN);

        CommandResult result = logic.execute("ridedelete 1");

        assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(RideDeleteCommand.MESSAGE_DELETE_RIDE_SUCCESS, deletedRide),
                result.getFeedbackToUser());
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertTrue(model.getAddressBook().getRideList().isEmpty());
        assertEquals(1, addressBookStorage.saveCalls);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_newFind_cancelsDeletionAndAppliesNormalFilter() throws Exception {
        logic.execute(DELETE_JOHN);
        assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 1),
                logic.execute("find n/alice").getFeedbackToUser());
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_newRideEdit_cancelsDeletionAndEditsRide() throws Exception {
        Ride draft = new RideBuilder().build();
        logic.execute(RideUtil.getRideAddCommand(draft));
        logic.execute(DELETE_JOHN);
        Ride editedRide = new RideBuilder(draft).withId(1).withName("Night Ride").build();

        CommandResult result = logic.execute("rideedit 1 n/Night Ride");

        assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(RideEditCommand.MESSAGE_SUCCESS, Messages.formatRide(editedRide)),
                result.getFeedbackToUser());
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertEquals(List.of(editedRide), model.getAddressBook().getRideList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_newCommandStorageFailure_includesCancellationAndExistingSaveError() throws Exception {
        logic.execute(DELETE_JOHN);
        addressBookStorage.saveFailure = new IOException("test failure");
        String expectedMessage = DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(LogicManager.FILE_OPS_ERROR_FORMAT, "test failure");
        assertThrows(CommandException.class, expectedMessage, () -> logic.execute("list"));
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertEquals(originalJson, Files.readString(storage.getAddressBookFilePath()));
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_newDelete_replacesPendingTargetAndReportsCancellation() throws Exception {
        logic.execute(DELETE_JOHN);
        assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n" + prompt(ALICE),
                logic.execute("delete +65 91234567").getFeedbackToUser());
        assertUnchangedWithoutSave();
        logic.execute("yes");
        assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_newMalformedCommand_reportsCancellationWithOriginalParseError() throws Exception {
        logic.execute(DELETE_JOHN);
        assertThrows(ParseException.class, DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), () ->
                logic.execute("delete"));
        assertUnchangedWithoutSave();
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_newRejectedCommand_reportsCancellationWithOriginalExecutionError() throws Exception {
        logic.execute(DELETE_JOHN);
        assertThrows(CommandException.class, DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(EditCommand.MESSAGE_PERSON_NOT_FOUND, "+65 99999999"), () ->
                logic.execute("edit +65 99999999 n/Unknown"));
        assertUnchangedWithoutSave();
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_newHelpOrExit_preservesUiFlagsAndCancellationNotice() throws Exception {
        for (String command : List.of("help", "exit")) {
            setUp();
            logic.execute(DELETE_JOHN);
            CommandResult result = logic.execute(command);
            assertTrue(result.getFeedbackToUser().startsWith(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"));
            assertEquals(command.equals("help"), result.isShowHelp());
            assertEquals(command.equals("exit"), result.isExit());
            assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        }
    }

    @Test
    public void execute_newEditOrClear_cancelsPendingDeletionBeforeMutation() throws Exception {
        logic.execute(DELETE_JOHN);
        Person editedJohn = new PersonBuilder(JOHN).withName("Updated Rider").build();
        assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedJohn)),
                logic.execute("edit +65 98765432 n/Updated Rider").getFeedbackToUser());
        assertEquals(List.of(editedJohn, ALICE), model.getAddressBook().getPersonList());
        logic.execute(DELETE_JOHN);
        assertTrue(logic.execute("clear").getFeedbackToUser().startsWith(DeleteCommand.MESSAGE_DELETE_CANCELLED));
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_hiddenUnknownAndMalformedTargets_keepOriginalErrorsAndNoPendingRequest() throws Exception {
        model.updateFilteredPersonList(person -> person.equals(JOHN));
        for (String phone : List.of("+65 91234567", "+65 99999999")) {
            assertThrows(CommandException.class, String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, phone), () ->
                    logic.execute("delete " + phone));
            assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
        }
        assertThrows(ParseException.class, String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_USAGE), () -> logic.execute("delete"));
        assertUnchangedWithoutSave();
    }

    @Test
    public void execute_ambiguousTarget_preservesExistingErrorAndDoesNotSave() {
        // EP: more than one displayed cyclist matches the target, unlike zero matches or a unique match.
        Person otherJohn = new PersonBuilder(JOHN).withEmail("other@example.com").build();
        Model ambiguousModel = new ModelManager() {
            @Override
            public ObservableList<Person> getFilteredPersonList() {
                return FXCollections.observableArrayList(JOHN, otherJohn);
            }
        };
        logic = new LogicManager(ambiguousModel, storage);
        assertThrows(CommandException.class, DeleteCommand.MESSAGE_AMBIGUOUS_PHONE, () -> logic.execute(DELETE_JOHN));
        assertEquals(0, addressBookStorage.saveCalls);
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_sameNameCyclists_confirmsOnlyRequestedPhone() throws Exception {
        Person otherJohn = new PersonBuilder(ALICE).withName("John Doe").build();
        model.setPerson(ALICE, otherJohn);
        assertEquals(prompt(otherJohn), logic.execute("delete +65 91234567").getFeedbackToUser());
        logic.execute("yes");
        assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_targetChangedOutsideCommandPipeline_abortsInsteadOfDeletingDifferentDetails() throws Exception {
        logic.execute(DELETE_JOHN);
        Person editedJohn = new PersonBuilder(JOHN).withEmail("updated@example.com").build();
        model.setPerson(JOHN, editedJohn);
        assertThrows(CommandException.class, DeleteCommand.MESSAGE_TARGET_CHANGED, () -> logic.execute("yes"));
        assertEquals(List.of(editedJohn, ALICE), model.getAddressBook().getPersonList());
        assertEquals(0, addressBookStorage.saveCalls);
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
    }

    @Test
    public void execute_targetHiddenBeforeConfirmation_keepsOriginalError() throws Exception {
        logic.execute(DELETE_JOHN);
        model.updateFilteredPersonList(person -> person.equals(ALICE));
        String expectedMessage = String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, JOHN.getPhone());
        assertThrows(CommandException.class, expectedMessage, () -> logic.execute("yes"));
        assertUnchangedWithoutSave();
    }

    @Test
    public void execute_restart_discardsUnconfirmedDeletion() throws Exception {
        logic.execute(DELETE_JOHN);
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
        assertUnchangedWithoutSave();
    }

    @Test
    public void execute_storageFailure_occursOnlyAfterConfirmationAndKeepsExistingError() throws Exception {
        for (IOException failure : List.of(new IOException("test failure"), new AccessDeniedException("test path"))) {
            setUp();
            addressBookStorage.saveFailure = failure;
            logic.execute(DELETE_JOHN);
            logic.execute("no");
            assertUnchangedWithoutSave();
            logic.execute(DELETE_JOHN);
            String expectedMessage = failure instanceof AccessDeniedException
                    ? String.format(LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, failure.getMessage())
                    : String.format(LogicManager.FILE_OPS_ERROR_FORMAT, failure.getMessage());
            assertThrows(CommandException.class, expectedMessage, () -> logic.execute("yes"));
            assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
            assertEquals(originalJson, Files.readString(storage.getAddressBookFilePath()));
            assertEquals(1, addressBookStorage.saveCalls);
            assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("yes"));
        }
    }

    private String prompt(Person cyclist) {
        return String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(cyclist));
    }

    private void assertUnchangedWithoutSave() throws IOException {
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertEquals(originalJson, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(0, addressBookStorage.saveCalls);
    }

    /**
     * Tracks writes and can reproduce a storage failure without replacing the real JSON serializer.
     */
    private static class TrackingAddressBookStorage extends JsonAddressBookStorage {
        private int saveCalls;
        private IOException saveFailure;

        TrackingAddressBookStorage(Path path) {
            super(path);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            saveCalls++;
            if (saveFailure != null) {
                throw saveFailure;
            }
            super.saveAddressBook(addressBook);
        }
    }
}
