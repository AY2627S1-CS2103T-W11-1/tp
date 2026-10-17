package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EXPERIENCE_LEVEL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.FTP_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.LOCATION_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RideDeleteCommand;
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
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;
import seedu.address.testutil.RideUtil;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete +65 99999999";
        assertCommandException(deleteCommand, String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, "+65 99999999"));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_rideAdd_addsRideAndSavesItToStorage() throws Exception {
        Ride draft = new RideBuilder().withNotes("Bring lights").build();

        logic.execute(RideUtil.getRideAddCommand(draft));
        logic.execute(RideUtil.getRideAddCommand(new RideBuilder(draft).withName("Evening Spin").build()));

        assertEquals(2, model.getAddressBook().getRideList().size());
        JsonAddressBookStorage reader = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        ReadOnlyAddressBook saved = reader.readAddressBook().get();
        assertEquals(new RideBuilder(draft).withId(1).build(), saved.getRideList().get(0));
        assertEquals("Evening Spin", saved.getRideList().get(1).getName().fullName);
        assertEquals(2, saved.getRideList().get(1).getId());
        assertEquals(3, saved.getNextRideId());
    }

    @Test
    public void getRideList_afterRideAdd_isLiveViewOfAddedRides() throws Exception {
        assertEquals(0, logic.getRideList().size());

        logic.execute(RideUtil.getRideAddCommand(new RideBuilder().build()));

        assertEquals(1, logic.getRideList().size());
        assertEquals(1, logic.getRideList().get(0).getId());
        assertThrows(UnsupportedOperationException.class, () -> logic.getRideList().clear());
    }

    @Test
    public void execute_clearThenRideAdd_doesNotReuseRideIdsAndSavesCounter() throws Exception {
        Ride draft = new RideBuilder().build();
        logic.execute(RideUtil.getRideAddCommand(draft));
        logic.execute(RideUtil.getRideAddCommand(new RideBuilder(draft).withName("Evening Spin").build()));

        logic.execute("clear");
        assertEquals(0, model.getAddressBook().getRideList().size());
        JsonAddressBookStorage reader = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        assertEquals(3, reader.readAddressBook().get().getNextRideId());

        logic.execute(RideUtil.getRideAddCommand(new RideBuilder(draft).withName("Night Ride").build()));

        assertEquals(3, model.getAddressBook().getRideList().get(0).getId());
        assertEquals(4, reader.readAddressBook().get().getNextRideId());
    }

    @Test
    public void execute_rideAddWithInvalidField_doesNotAddRide() {
        assertParseException("rideadd n/Ride from/18-10-2026 0930 to/18-10-2026 0630 l/East Coast d/45 f/180 s/12",
                Ride.MESSAGE_INVALID_PERIOD);
        assertEquals(0, model.getAddressBook().getRideList().size());
    }

    @Test
    public void execute_invalidOrUnknownRideId_preservesModelAndSavedData() throws Exception {
        logic.execute(RideUtil.getRideAddCommand(new RideBuilder().build()));
        Path addressBookFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(addressBookFile);

        assertParseException("ridedelete 0", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                RideDeleteCommand.MESSAGE_USAGE));
        assertCommandException("ridedelete 999",
                String.format(RideDeleteCommand.MESSAGE_RIDE_NOT_FOUND, 999));

        assertEquals(savedData, Files.readString(addressBookFile));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + " sos/+65 99999999" + EMAIL_DESC_AMY + FTP_DESC_BOB
                + EXPERIENCE_LEVEL_DESC_AMY + LOCATION_DESC_BOB;
        Person expectedPerson = new PersonBuilder(AMY).withEmergencyPhone("+65 99999999").build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
