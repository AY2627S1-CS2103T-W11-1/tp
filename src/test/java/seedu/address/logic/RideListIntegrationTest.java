package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.RideListCommand;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
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
                new RideBuilder().withId(101)
                        .withRoster(ALICE.getPhone().value, CARL.getPhone().value).build(),
                new RideBuilder().withId(205).withRoster(BENSON.getPhone().value).build()), 206);
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
}
