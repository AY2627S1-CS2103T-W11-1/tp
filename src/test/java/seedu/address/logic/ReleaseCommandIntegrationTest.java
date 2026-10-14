package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

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
    private static final String ADD_JOHN = "add n/John Doe p/+65 98765432 sos/+65 99999999 e/johnd@example.com"
            + " f/300 x/Beginner l/any";
    private static final String ADD_ALICE = "add n/Alice Tan p/+65 91234567 sos/+65 99999999 e/alice@example.com"
            + " f/1 x/Beginner l/East Coast l/Punggol";
    private static final Person JOHN = new PersonBuilder()
            .withEmergencyPhone("+65 99999999").withName("John Doe").withPhone("+65 98765432")
            .withEmail("johnd@example.com").withFtp("300")
            .withLocations("any").build();
    private static final Person ALICE = new PersonBuilder()
            .withEmergencyPhone("+65 99999999").withName("Alice Tan").withPhone("+65 91234567")
            .withEmail("alice@example.com").withFtp("1")
            .withLocations("East Coast", "Punggol").build();

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private Storage storage;

    @Test
    public void execute_emergencyPhone_sharedEditedFoundAndReloaded() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find sos/+65 99999999");
        assertEquals(List.of(ALICE, JOHN), model.getFilteredPersonList());
        logic.execute("edit +65 98765432 sos/+65 88888888");
        Person editedJohn = new PersonBuilder(JOHN).withEmergencyPhone("+65 88888888").build();
        assertEquals(List.of(editedJohn, ALICE), model.getAddressBook().getPersonList());
        assertSavedState();
        logic.execute("find n/john p/+65 98765432 sos/+65 88888888");
        assertEquals(List.of(editedJohn), model.getFilteredPersonList());
        logic.execute("edit +65 98765432 f/400");
        assertEquals(editedJohn.getEmergencyPhone(), model.getAddressBook().getPersonList().get(0).getEmergencyPhone());
        assertSavedState();
        logic.execute("find sos/+65 99999999 sos/+65 88888888");
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_legacyEmergencyPhone_preservesAbsenceAndAllowsUpgrade() throws Exception {
        model.addPerson(new PersonBuilder().withName("Legacy Rider").withPhone("+65 91111111").build());
        logic.execute("edit +65 91111111 f/300");
        assertTrue(model.getAddressBook().getPersonList().get(0).getEmergencyPhone().isEmpty());
        assertSavedState();
        logic.execute("find sos/+65 99999999");
        assertTrue(model.getFilteredPersonList().isEmpty());
        logic.execute("list");
        logic.execute("edit +65 91111111 sos/+65 99999999");
        assertEquals("+65 99999999",
                model.getAddressBook().getPersonList().get(0).getEmergencyPhone().orElseThrow().value);
        assertSavedState();
    }

    @Test
    public void execute_invalidEmergencyPhone_leavesSavedDataUnchanged() throws Exception {
        logic.execute(ADD_JOHN);
        String savedData = Files.readString(storage.getAddressBookFilePath());
        for (String command : List.of(ADD_ALICE.replace(" sos/+65 99999999", ""),
                ADD_ALICE.replace("sos/+65 99999999", "sos/"), ADD_ALICE + " sos/+65 111",
                "edit +65 98765432 sos/+65 abc", "edit +65 98765432 sos/",
                "edit +65 98765432 sos/+65 111 sos/+65 222", "find sos/")) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
            assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_legacyTags_preservesSupportedFieldsAcrossSaveAndReload() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        Path dataFile = storage.getAddressBookFilePath();
        String legacyJson = Files.readString(dataFile).replace("\"name\"",
                "\"tags\": [\"friends\", \"invalid tag!\"], \"name\"");
        Files.writeString(dataFile, legacyJson);
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());

        String feedback = logic.execute("edit +65 98765432 n/Updated Rider").getFeedbackToUser();
        Person editedJohn = new PersonBuilder(JOHN).withName("Updated Rider").build();
        assertFalse(feedback.contains("Tags:"));
        assertFalse(Files.readString(dataFile).contains("\"tags\""));
        assertEquals(List.of(editedJohn, ALICE), model.getAddressBook().getPersonList());
        assertSavedState();
    }

    @Test
    public void execute_removedTagPrefix_rejectedWithoutChangingData() throws Exception {
        logic.execute(ADD_JOHN);
        String savedData = Files.readString(storage.getAddressBookFilePath());
        for (String command : List.of(ADD_ALICE + " t/friend", "edit +65 98765432 t/",
                "edit +65 98765432 n/Changed t/friend")) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
            assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_addAndEdit_omitAddressFromFeedbackAndStorage() throws Exception {
        assertFalse(logic.execute(ADD_JOHN).getFeedbackToUser().contains("Address:"));
        assertFalse(logic.execute("edit +65 98765432 f/400").getFeedbackToUser().contains("Address:"));
        assertFalse(Files.readString(storage.getAddressBookFilePath()).contains("\"address\""));
        assertSavedState();
    }

    @Test
    public void execute_retiredAddressPrefix_rejectedWithoutChangingData() throws Exception {
        logic.execute(ADD_JOHN);
        String savedData = Files.readString(storage.getAddressBookFilePath());
        for (String command : List.of(ADD_ALICE + " a/10 Test Road",
                "add a/10 Test Road n/Alice Tan p/+65 91234567 sos/+65 99999999 e/alice@example.com"
                        + " f/1 x/Beginner l/any",
                "edit +65 98765432 a/10 Test Road", "edit +65 98765432 n/John Updated a/10 Test Road")) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(List.of(JOHN), model.getAddressBook().getPersonList());
            assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_alphabeticalDisplay_preservesPhoneTargetsAndReloadOrder() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("add n/Bernice p/+65 91111111 sos/+65 99999999 e/bernice@example.com f/300 x/Beginner l/any");
        Person bernice = new PersonBuilder()
                .withEmergencyPhone("+65 99999999").withName("Bernice").withPhone("+65 91111111")
                .withEmail("bernice@example.com").withFtp("300").build();
        assertEquals(List.of(ALICE, bernice, JOHN), logic.getFilteredPersonList());

        logic.execute("find n/jOHn n/alice");
        assertEquals(List.of(ALICE, JOHN), logic.getFilteredPersonList());
        logic.execute("edit +65 98765432 n/aaron Doe");
        Person aaron = new PersonBuilder(JOHN).withName("aaron Doe").build();
        assertEquals(List.of(aaron, ALICE, bernice), logic.getFilteredPersonList());
        assertSavedState();

        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertEquals(List.of(aaron, ALICE, bernice), logic.getFilteredPersonList());
        logic.execute("find n/Bernice");
        assertRejectedWithoutChanges("delete +65 98765432",
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, "+65 98765432"));
        assertEquals(List.of(bernice), logic.getFilteredPersonList());
        logic.execute("list");
        logic.execute("delete +65 98765432");
        assertEquals(List.of(ALICE, bernice), logic.getFilteredPersonList());
        assertSavedState();
    }

    @Test
    public void execute_equalNames_preservesRelativeOrderAfterEditAndReload() throws Exception {
        logic.execute("add n/aLEX p/+65 91111111 sos/+65 99999999 e/first@example.com f/300 x/Beginner l/any");
        logic.execute("add n/Alex p/+65 92222222 sos/+65 99999999 e/second@example.com f/300 x/Beginner l/any");
        logic.execute("add n/ALEX p/+65 93333333 sos/+65 99999999 e/third@example.com f/300 x/Beginner l/any");
        logic.execute("find n/alex");
        logic.execute("edit +65 91111111 n/alex");
        List<String> expectedPhones = List.of("+65 91111111", "+65 92222222", "+65 93333333");
        assertEquals(expectedPhones,
                logic.getFilteredPersonList().stream().map(person -> person.getPhone().value).toList());
        assertSavedState();

        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertEquals(expectedPhones,
                logic.getFilteredPersonList().stream().map(person -> person.getPhone().value).toList());
    }

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
                + " x/Advanced l/East Coast l/Punggol");
        Person editedJohn = new PersonBuilder(JOHN).withPhone("+65 96742165")
                .withEmail("johndoe@example.com").withFtp("600").withExperienceLevel("Advanced")
                .withLocations("East Coast", "Punggol").build();
        assertEquals(List.of(editedJohn), model.getAddressBook().getPersonList());
        assertSavedState();

    }

    @Test
    public void execute_repeatedFindFields_filtersWithOrWithinFieldsAndAndBetweenFields() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/jOHn n/alice p/+65 98765432 p/+65 91234567");
        assertEquals(List.of(ALICE, JOHN), logic.getFilteredPersonList());

        logic.execute("find n/john p/+65 91234567");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        logic.execute("find n/Jo");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        logic.execute("find p/+65 987");
        assertTrue(logic.getFilteredPersonList().isEmpty());
        logic.execute("list");
        assertEquals(List.of(ALICE, JOHN), logic.getFilteredPersonList());
        assertSavedState();
    }

    @Test
    public void execute_editHiddenPerson_rejectsWithoutChangingSavedData() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/john");
        String savedData = Files.readString(storage.getAddressBookFilePath());

        assertThrows(CommandException.class, String.format(EditCommand.MESSAGE_PERSON_NOT_FOUND, "+65 91234567"), () ->
                logic.execute("edit +65 91234567 n/Alice Updated"));
        assertEquals(List.of(JOHN), logic.getFilteredPersonList());
        assertEquals(List.of(JOHN, ALICE), model.getAddressBook().getPersonList());
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_hiddenAndUnknownTargets_reportsSameEditAndDeleteError() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/john");
        for (String phone : List.of("+65 91234567", "+65 99999999")) {
            String expectedMessage = "Cyclist with phone number " + phone
                    + " cannot be found in the displayed list. Use list to show all cyclists.";
            assertRejectedWithoutChanges("delete " + phone, expectedMessage);
            assertRejectedWithoutChanges("edit " + phone + " n/Ben", expectedMessage);
        }
    }

    @Test
    public void execute_invalidFields_reportsPunctuatedErrorsWithoutChangingSavedData() throws Exception {
        logic.execute(ADD_JOHN);
        String savedData = Files.readString(storage.getAddressBookFilePath());
        String[][] invalidEdits = {
            {"n/!", "Names should only contain alphanumeric characters and spaces, and should not be blank."},
            {"x/Expert", "Experience level should be one of: Beginner, Intermediate, Advanced (case-insensitive)."},
            {"n/John n/Ben", "Multiple values specified for the following single-valued field(s): n/."}
        };
        assertThrows(ParseException.class, "Invalid command format.\n" + DeleteCommand.MESSAGE_USAGE, () ->
                logic.execute("delete"));
        for (String[] invalidEdit : invalidEdits) {
            assertThrows(ParseException.class, invalidEdit[1], () ->
                    logic.execute("edit +65 98765432 " + invalidEdit[0]));
            assertEquals(List.of(JOHN), logic.getFilteredPersonList());
            assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_editVisiblePerson_resetsFilterAndPreservesUnspecifiedFields() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/john");
        logic.execute("edit +65 98765432 n/John Updated");

        Person editedJohn = new PersonBuilder(JOHN).withName("John Updated").build();
        assertEquals(List.of(ALICE, editedJohn), logic.getFilteredPersonList());
        assertSavedState();
    }

    @Test
    public void execute_deleteHiddenPerson_rejectsUntilDisplayedAndPersistsRemovals() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/john");
        assertRejectedWithoutChanges("delete +65 91234567",
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, "+65 91234567"));
        logic.execute("find n/nobody");
        assertRejectedWithoutChanges("delete +65 98765432",
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, "+65 98765432"));
        logic.execute("find n/alice");
        logic.execute("delete +65 91234567");
        assertTrue(logic.getFilteredPersonList().isEmpty());
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
        assertRejectedWithoutChanges("add n/Jane Doe p/+65 98765432 sos/+65 99999999 e/jane@example.com"
                + " f/300 x/Beginner l/any", AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_editDuplicatePhoneOutsideFilteredList_rejectsWithoutChanges() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/alice");
        assertRejectedWithoutChanges("edit +65 91234567 p/+65 98765432", EditCommand.MESSAGE_DUPLICATE_PERSON);
        assertRejectedWithoutChanges("edit +65 91234567 n/Alice Updated p/+65 98765432",
                EditCommand.MESSAGE_DUPLICATE_PERSON);
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
    public void execute_addDuplicateEmailOutsideFilteredList_rejectsWithoutChanges() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute("find n/nobody");
        assertRejectedWithoutChanges("add n/Jane Doe p/+65 91111111 sos/+65 99999999 e/"
                + JOHN.getEmail().value.toUpperCase(Locale.ROOT)
                + " f/300 x/Beginner l/any", AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_filteredUniquePhoneEdit_preservesContactsAcrossReload() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/alice");
        logic.execute("edit +65 91234567 p/+65 91111111");
        Person editedAlice = new PersonBuilder(ALICE).withPhone("+65 91111111").build();
        assertEquals(List.of(editedAlice, JOHN), logic.getFilteredPersonList());
        assertRejectedWithoutChanges("edit +65 98765432 p/+65 91111111",
                EditCommand.MESSAGE_DUPLICATE_PERSON);
        assertSavedState();

        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertEquals(List.of(JOHN, editedAlice), model.getAddressBook().getPersonList());
        assertEquals(List.of(editedAlice, JOHN), logic.getFilteredPersonList());
        logic.execute("delete +65 98765432");
        assertEquals(List.of(editedAlice), model.getAddressBook().getPersonList());
        assertSavedState();
    }

    @Test
    public void execute_editDuplicateEmailOutsideFilteredList_rejectsWithoutChanges() throws Exception {
        logic.execute(ADD_JOHN);
        logic.execute(ADD_ALICE);
        logic.execute("find n/alice");
        assertRejectedWithoutChanges("edit +65 91234567 e/"
                + JOHN.getEmail().value.toUpperCase(Locale.ROOT),
                EditCommand.MESSAGE_DUPLICATE_PERSON);
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
