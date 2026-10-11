package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises release command workflows through parsing, execution, filtering, and JSON storage.
 */
public class ReleaseCommandIntegrationTest {
    private static final String ADD_JOHN = "add n/John Doe p/+65 98765432 e/johnd@example.com"
            + " f/300 x/Beginner l/any t/friends t/owesMoney";
    private static final String ADD_ALICE = "add n/Alice Tan p/+65 91234567 e/alice@example.com"
            + " f/1 x/Beginner l/East Coast l/Punggol";
    private static final Person JOHN = new PersonBuilder().withName("John Doe").withPhone("+65 98765432")
            .withEmail("johnd@example.com").withAddress("Not specified").withFtp("300")
            .withLocations("any").withTags("friends", "owesMoney").build();
    private static final Person ALICE = new PersonBuilder().withName("Alice Tan").withPhone("+65 91234567")
            .withEmail("alice@example.com").withAddress("Not specified").withFtp("1")
            .withLocations("East Coast", "Punggol").build();

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_helpGuideAddAndEdit_persistsAllFields() throws Exception {
        logic.execute(ADD_JOHN);
        assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
        assertSavedState();

        logic.execute("edit +65 98765432 p/+65 96742165 e/johndoe@example.com f/600"
                + " x/Advanced l/East Coast l/Punggol a/10 Test Road t/cyclists");
        Person editedJohn = new PersonBuilder(JOHN).withPhone("+65 96742165")
                .withEmail("johndoe@example.com").withFtp("600").withExperienceLevel("Advanced")
                .withLocations("East Coast", "Punggol").withAddress("10 Test Road").withTags("cyclists").build();
        assertEquals(List.of(editedJohn), model.getAddressBook().getPersonList());
        assertSavedState();

        logic.execute("edit +65 96742165 t/");
        assertEquals(List.of(new PersonBuilder(editedJohn).withTags().build()),
                model.getAddressBook().getPersonList());
        assertSavedState();
    }

    @Test
    public void execute_repeatedFindFields_filtersWithOrWithinFieldsAndAndBetweenFields() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/jOHn n/alice p/+65 98765432 p/+65 91234567");
        assertEquals(List.of(JOHN, ALICE), logic.getFilteredPersonList());

        logic.execute("find n/john p/+65 91234567");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        logic.execute("find n/Jo");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        logic.execute("find p/+65 987");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        logic.execute("list");
        assertEquals(List.of(JOHN, ALICE), logic.getFilteredPersonList());
        assertSavedState();
    }

    @Test
    public void execute_editHiddenPerson_rejectsWithoutChangingSavedData() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/john");
        String savedData = Files.readString(storage.getAddressBookFilePath());

        assertThrows(CommandException.class, EditCommand.MESSAGE_PERSON_NOT_FOUND, () ->
                logic.execute("edit +65 91234567 n/Alice Updated"));
        assertEquals(List.of(JOHN), logic.getFilteredPersonList());
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_editVisiblePerson_resetsFilterAndPreservesUnspecifiedFields() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/john");
        logic.execute("edit +65 98765432 n/John Updated");

        Person editedJohn = new PersonBuilder(JOHN).withName("John Updated").build();
        assertEquals(List.of(editedJohn, ALICE), logic.getFilteredPersonList());
        assertSavedState();
    }

    @Test
    public void execute_deleteHiddenPersonAndClear_persistsRemovals() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/john");
        logic.execute("delete +65 91234567");
        assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
        assertSavedState();

        logic.execute("clear");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertSavedState();
        logic.execute("clear");
        assertSavedState();
    }

    @Test
    public void execute_invalidOrDuplicateInput_preservesModelAndSavedData() throws Exception {
        logic.execute(ADD_JOHN);
        String savedData = Files.readString(storage.getAddressBookFilePath());
        String[] invalidCommands = {
            "delete 1", "delete +65 12", "edit +65 98765432", "find n/John Doe",
            "edit +65 98765432 n/John Updated f/601", "edit +65 98765432 l/",
            "edit +65 98765432 p/+65 111 p/+65 222"
        };
        for (String command : invalidCommands) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
            assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
        }
        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> logic.execute(ADD_JOHN));
        assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_helpAndExit_returnsUiFlagsWithoutChangingContacts() throws Exception {
        logic.execute(ADD_JOHN);
        assertTrue(logic.execute("help").isShowHelp());
        assertFalse(logic.execute("list").isExit());
        assertTrue(logic.execute("exit").isExit());
        assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
        assertSavedState();
    }

    @Test
    public void execute_addDuplicatePhoneOutsideFilteredList_rejectsWithoutChanges() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute("find n/nobody");
        assertRejectedWithoutChanges("add n/Jane Doe p/+65 98765432 e/jane@example.com"
                + " f/300 x/Beginner l/any", AddCommand.MESSAGE_DUPLICATE_PHONE);
    }

    @Test
    public void execute_editDuplicatePhoneOutsideFilteredList_rejectsWithoutChanges() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/alice");
        assertRejectedWithoutChanges("edit +65 91234567 p/+65 98765432", EditCommand.MESSAGE_DUPLICATE_PHONE);
        assertRejectedWithoutChanges("edit +65 91234567 n/Alice Updated p/+65 98765432",
                EditCommand.MESSAGE_DUPLICATE_PHONE);
    }

    @Test
    public void execute_editOwnPhone_acceptsUnchangedNumber() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("edit +65 98765432 p/+65 98765432 e/updated@example.com");
        assertEquals(List.of(new PersonBuilder(JOHN).withEmail("updated@example.com").build(), ALICE),
                model.getAddressBook().getPersonList());
        assertSavedState();
    }

    @Test
    public void execute_legacyCollisions_rejectsAmbiguousTargetsWithoutChanges() throws Exception {
        loadLegacyPhoneCollision();
        assertRejectedWithoutChanges("edit +65 98765432 p/+65 91111111", EditCommand.MESSAGE_AMBIGUOUS_PHONE);
        assertRejectedWithoutChanges("delete +65 98765432", DeleteCommand.MESSAGE_AMBIGUOUS_PHONE);

        logic.execute("find n/alice");
        assertRejectedWithoutChanges("delete +65 98765432", DeleteCommand.MESSAGE_AMBIGUOUS_PHONE);
        assertRejectedWithoutChanges("edit +65 98765432 e/updated@example.com", EditCommand.MESSAGE_DUPLICATE_PHONE);
    }

    @Test
    public void execute_filteredLegacyRepair_preservesContactsAcrossReload() throws Exception {
        Person legacyAlice = loadLegacyPhoneCollision();
        logic.execute("find n/alice");
        logic.execute("edit +65 98765432 p/+65 91111111");
        Person repairedAlice = new PersonBuilder(legacyAlice).withPhone("+65 91111111").build();
        assertEquals(List.of(JOHN, repairedAlice), logic.getFilteredPersonList());
        assertSavedState();

        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertEquals(List.of(JOHN, repairedAlice), model.getAddressBook().getPersonList());
        logic.execute("delete +65 98765432");
        assertEquals(List.of(repairedAlice), model.getAddressBook().getPersonList());
        assertSavedState();
    }

    @Test
    public void execute_filteredLegacyRepairToOccupiedNumber_rejectsWithoutChanges() throws Exception {
        loadLegacyPhoneCollision();
        logic.execute("add n/Carol Tan p/+65 91111111 e/carol@example.com f/300 x/Beginner l/any");
        logic.execute("find n/alice");
        assertRejectedWithoutChanges("edit +65 98765432 p/+65 91111111", EditCommand.MESSAGE_DUPLICATE_PHONE);
    }

    /** Loads a saved legacy book without losing contacts that share a phone number. */
    private Person loadLegacyPhoneCollision() throws Exception {
        Person legacyAlice = new PersonBuilder(ALICE).withPhone(JOHN.getPhone().value).build();
        model.addPerson(JOHN);
        model.addPerson(legacyAlice);
        storage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(storage.getAddressBookFilePath());
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertEquals(List.of(JOHN, legacyAlice), model.getAddressBook().getPersonList());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
        return legacyAlice;
    }

    /** Checks that a rejected command leaves contacts, filtering, and saved data unchanged. */
    private void assertRejectedWithoutChanges(String command, String message) throws Exception {
        List<Person> persons = List.copyOf(model.getAddressBook().getPersonList());
        List<Person> displayedPersons = List.copyOf(logic.getFilteredPersonList());
        String savedData = Files.readString(storage.getAddressBookFilePath());
        assertThrows(CommandException.class, message, () -> logic.execute(command));
        assertEquals(persons, model.getAddressBook().getPersonList());
        assertEquals(displayedPersons, logic.getFilteredPersonList());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }

    /** Checks that loading the saved JSON reproduces every contact field in the current model. */
    private void assertSavedState() throws Exception {
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
