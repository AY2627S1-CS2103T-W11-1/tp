package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.RideListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.RideBuilder;

public class RideListIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;
    private AddressBook originalAddressBook;

    @BeforeEach
    public void setUp() throws Exception {
        AddressBook addressBook = getTypicalAddressBook();
        addressBook.setRides(List.of(
                new RideBuilder().withId(101).withMinimumPower("1")
                        .withRoster(ALICE.getPhone().value, CARL.getPhone().value).build(),
                new RideBuilder().withId(205).withMinimumPower("1")
                        .withRoster(BENSON.getPhone().value).build(),
                new RideBuilder().withId(310).withName("Empty Ride").withMinimumPower("1").build()), 311);
        model = new ModelManager(addressBook, new UserPrefs());
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("book.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        storage.saveAddressBook(model.getAddressBook());
        logic = new LogicManager(model, storage);
        originalAddressBook = new AddressBook(model.getAddressBook());
    }

    @Test
    public void execute_filterCommands_replacePriorFilterAndPreserveSavedData() throws Exception {
        logic.execute("find n/Alice");
        assertEquals(List.of(ALICE), model.getFilteredPersonList());

        logic.execute("ridelist 101");
        assertEquals(List.of(ALICE, CARL), model.getFilteredPersonList());

        logic.execute("list");
        assertEquals(getTypicalPersons(), model.getFilteredPersonList());

        logic.execute("ridelist 205");
        assertEquals(List.of(BENSON), model.getFilteredPersonList());

        logic.execute("find n/Carl");
        assertEquals(List.of(CARL), model.getFilteredPersonList());

        assertEquals(originalAddressBook, model.getAddressBook());
        assertEquals(originalAddressBook, storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_pendingDeletion_cancelsDeletionAndListsRoster() throws Exception {
        logic.execute("delete " + ALICE.getPhone().value);
        String feedback = logic.execute("ridelist 101").getFeedbackToUser();

        assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"
                + String.format(RideListCommand.MESSAGE_SUCCESS, 101), feedback);
        assertEquals(List.of(ALICE, CARL), model.getFilteredPersonList());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_selectedRosterChanges_refreshesIncludingFirstAndLastMember() throws Exception {
        logic.execute("ridelist 310");
        assertEquals(List.of(), model.getFilteredPersonList());

        logic.execute("rideaddcyclist 310 " + ALICE.getPhone().value);
        logic.execute("yes");
        assertEquals(List.of(ALICE), model.getFilteredPersonList());

        logic.execute("ridedeletecyclist 310 " + ALICE.getPhone().value);
        assertEquals(List.of(), model.getFilteredPersonList());

        logic.execute("ridelist 101");
        logic.execute("rideaddcyclist 101 " + BENSON.getPhone().value);
        logic.execute("yes");
        assertEquals(List.of(ALICE, BENSON, CARL), model.getFilteredPersonList());

        logic.execute("ridedeletecyclist 101 " + CARL.getPhone().value);
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_otherRideChanges_preservesSelectionAndRideListSwitchesIt() throws Exception {
        logic.execute("ridelist 101");
        logic.execute("rideaddcyclist 205 " + DANIEL.getPhone().value);
        assertEquals(List.of(ALICE, CARL), model.getFilteredPersonList());

        logic.execute("ridelist 205");
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPersonList());

        logic.execute("rideaddcyclist 101 " + ELLE.getPhone().value);
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_listAndFind_cancelLiveRideListFilter() throws Exception {
        logic.execute("ridelist 101");
        logic.execute("list");
        logic.execute("rideaddcyclist 101 " + BENSON.getPhone().value);
        assertEquals(getTypicalPersons(), model.getFilteredPersonList());

        logic.execute("ridelist 101");
        logic.execute("find n/Daniel");
        logic.execute("ridedeletecyclist 101 " + ALICE.getPhone().value);
        assertEquals(List.of(DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_failedRosterCommand_preservesDisplayedRoster() throws Exception {
        logic.execute("ridelist 101");

        assertThrows(CommandException.class, () ->
                logic.execute("rideaddcyclist 101 " + ALICE.getPhone().value));

        assertEquals(List.of(ALICE, CARL), model.getFilteredPersonList());
    }

    @Test
    public void execute_deleteSelectedRide_keepsEmptyRideListFilter() throws Exception {
        logic.execute("ridelist 101");
        logic.execute("ridedelete 101");

        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(getTypicalPersons(), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_cyclistPhoneEditAndDeletion_remainConsistentWithRoster() throws Exception {
        String editedPhone = "+65 91111111";
        logic.execute("ridelist 101");
        logic.execute("edit " + ALICE.getPhone().value + " p/" + editedPhone);

        Person editedAlice = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getPhone().equals(new Phone(editedPhone)))
                .findFirst()
                .orElseThrow();
        assertEquals(List.of(editedAlice, CARL), model.getFilteredPersonList());
        assertEquals(List.of(new Phone(editedPhone), CARL.getPhone()),
                model.getAddressBook().getRideList().get(0).getRoster());

        logic.execute("delete " + editedPhone);
        logic.execute("no");
        assertEquals(List.of(editedAlice, CARL), model.getFilteredPersonList());

        logic.execute("delete " + editedPhone);
        logic.execute("yes");
        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }
}
