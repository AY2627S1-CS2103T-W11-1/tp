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
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Tests cyclist edits with active search and live ride-roster filters through logic and storage.
 */
public class FilteredEditIntegrationTest {
    private static final Person JOHN = new PersonBuilder().withName("John Doe").withPhone("+65 87438809")
            .withEmail("john.doe@example.com").withFtp("280").build();
    private static final Person JOHN_LEE = new PersonBuilder().withName("John Lee").withPhone("+65 87438808")
            .withEmail("john.lee@example.com").withFtp("250").build();
    private static final Person ALEX = new PersonBuilder().withName("Alex Yeoh").withPhone("+65 87438807")
            .withEmail("alex@example.com").withFtp("200").build();

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;
    private Path bookPath;

    @BeforeEach
    public void setUp() throws Exception {
        AddressBook book = new AddressBook();
        book.setPersons(List.of(JOHN, JOHN_LEE, ALEX));
        book.setRides(List.of(new RideBuilder().withId(1).withMinimumPower("1")
                .withRoster(JOHN.getPhone().value, JOHN_LEE.getPhone().value).build()), 2);
        model = new ModelManager(book, new UserPrefs());
        bookPath = temporaryFolder.resolve("book.json");
        storage = new StorageManager(new JsonAddressBookStorage(bookPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        storage.saveAddressBook(book);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_findThenUnrelatedEdits_preservesFilterForMultipleCyclists() throws Exception {
        logic.execute("find n/John");
        logic.execute("edit +65 87438809 f/200");
        Person editedJohn = new PersonBuilder(JOHN).withFtp("200").build();
        assertEquals(List.of(editedJohn, JOHN_LEE), model.getFilteredPersonList());
        assertSavedState();

        logic.execute("edit +65 87438808 f/210");
        Person editedLee = new PersonBuilder(JOHN_LEE).withFtp("210").build();
        assertEquals(List.of(editedJohn, editedLee), model.getFilteredPersonList());
        assertSavedState();
    }

    @Test
    public void execute_findThenMatchingFieldEdit_removesNonMatchesAndPreservesRecords() throws Exception {
        logic.execute("find n/John");
        logic.execute("edit +65 87438809 n/James Doe");
        Person james = new PersonBuilder(JOHN).withName("James Doe").build();
        assertEquals(List.of(JOHN_LEE), model.getFilteredPersonList());

        logic.execute("edit +65 87438808 n/James Lee");
        Person jamesLee = new PersonBuilder(JOHN_LEE).withName("James Lee").build();
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(james, jamesLee, ALEX), model.getAddressBook().getPersonList());
        assertSavedState();

        logic.execute("list");
        assertEquals(List.of(ALEX, james, jamesLee), model.getFilteredPersonList());
    }

    @Test
    public void execute_findThenPhoneEdit_rechecksPhoneCriteriaAndUpdatesRoster() throws Exception {
        logic.execute("find p/+65 87438809");
        logic.execute("edit +65 87438809 p/+65 91111111");

        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(new Phone("+65 91111111"), JOHN_LEE.getPhone()),
                model.getAddressBook().getRideList().get(0).getRoster());
        assertSavedState();
    }

    @Test
    public void execute_rideListThenEdits_preservesSortedLiveRosterAndPhoneReferences() throws Exception {
        logic.execute("ridelist 1");
        logic.execute("edit +65 87438809 n/Zed Rider p/+65 91111111 f/200");
        Person zed = new PersonBuilder(JOHN).withName("Zed Rider").withPhone("+65 91111111")
                .withFtp("200").build();
        assertEquals(List.of(JOHN_LEE, zed), model.getFilteredPersonList());
        assertEquals(List.of(zed.getPhone(), JOHN_LEE.getPhone()),
                model.getAddressBook().getRideList().get(0).getRoster());
        assertSavedState();

        logic.execute("edit +65 87438808 f/210");
        Person editedLee = new PersonBuilder(JOHN_LEE).withFtp("210").build();
        assertEquals(List.of(editedLee, zed), model.getFilteredPersonList());
        logic.execute("ridedeletecyclist 1 +65 91111111");
        assertEquals(List.of(editedLee), model.getFilteredPersonList());
        logic.execute("rideaddcyclist 1 +65 87438807");
        assertEquals(List.of(ALEX, editedLee), model.getFilteredPersonList());
        assertSavedState();
    }

    @Test
    public void execute_filteredRejectedEdits_preservesFilterRecordsAndSavedData() throws Exception {
        for (String filter : List.of("find n/John", "ridelist 1")) {
            logic.execute(filter);
            AddressBook originalBook = new AddressBook(model.getAddressBook());
            String originalJson = Files.readString(bookPath);

            assertThrows(ParseException.class, () -> logic.execute("edit +65 87438809 f/601"));
            assertThrows(CommandException.class, () -> logic.execute("edit +65 87438809 p/+65 87438807"));
            assertThrows(CommandException.class, () -> logic.execute("edit +65 87438807 f/210"));

            assertEquals(List.of(JOHN, JOHN_LEE), model.getFilteredPersonList());
            assertEquals(originalBook, model.getAddressBook());
            assertEquals(originalJson, Files.readString(bookPath));
        }
    }

    private void assertSavedState() throws Exception {
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
