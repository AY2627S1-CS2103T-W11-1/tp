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
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Tests FTP edits against all registered rides through command execution and JSON storage.
 */
public class CyclistFtpEditIntegrationTest {
    private static final String PHONE = "+65 93210283";
    private static final Person CHARLOTTE = new PersonBuilder().withName("Charlotte Ng").withPhone(PHONE)
            .withEmail("charlotte@example.com").withFtp("280").build();
    private static final Person ALEX = new PersonBuilder().withName("Alex Yeoh").withPhone("+65 87438807")
            .withEmail("alex@example.com").withFtp("600").build();

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;
    private Path bookPath;

    @BeforeEach
    public void setUp() throws Exception {
        AddressBook book = new AddressBook();
        book.setPersons(List.of(CHARLOTTE, ALEX));
        book.setRides(List.of(
                new RideBuilder().withId(1).withName("Easy Ride").withMinimumPower("180")
                        .withRoster(PHONE).build(),
                new RideBuilder().withId(2).withName("Hill Repeats").withMinimumPower("250")
                        .withRoster(PHONE).build(),
                new RideBuilder().withId(3).withName("Other Ride").withMinimumPower("600")
                        .withRoster(ALEX.getPhone().value).build()), 4);
        model = new ModelManager(book, new UserPrefs());
        bookPath = temporaryFolder.resolve("book.json");
        storage = new StorageManager(new JsonAddressBookStorage(bookPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        storage.saveAddressBook(book);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_belowRegisteredMinimum_rejectsWithoutChanges() throws Exception {
        // The first registration allows 200 W; the second must still prevent this edit.
        assertRejectedWithoutChanges("edit " + PHONE + " f/200", 200);
        assertRejectedWithoutChanges("edit " + PHONE + " f/249", 249);
    }

    @Test
    public void execute_combinedPhoneAndFtpEdit_rejectsEverySuppliedField() throws Exception {
        assertRejectedWithoutChanges("edit " + PHONE
                + " n/Changed p/+65 91111111 e/changed@example.com f/249 x/Advanced l/Mandai", 249);
    }

    @Test
    public void execute_hiddenConflictingRide_preservesFindAndRideListFilters() throws Exception {
        logic.execute("ridefind n/NoSuchRide");
        for (String filter : List.of("find n/Charlotte", "ridelist 1")) {
            logic.execute(filter);
            assertRejectedWithoutChanges("edit " + PHONE + " f/200", 200);
        }
    }

    @Test
    public void execute_eligibleFtpBoundaries_succeedsAndPersists() throws Exception {
        logic.execute("ridelist 2");
        for (String ftp : List.of("250", "251", "600")) {
            logic.execute("edit " + PHONE + " f/" + ftp);
            Person editedCharlotte = new PersonBuilder(CHARLOTTE).withFtp(ftp).build();
            assertEquals(List.of(editedCharlotte), model.getFilteredPersonList());
            assertEquals(List.of(CHARLOTTE.getPhone()), model.getAddressBook().getRideList().get(1).getRoster());
            assertSavedState();
        }
    }

    @Test
    public void execute_eligibleCombinedPhoneAndFtpEdit_updatesEveryRegistration() throws Exception {
        logic.execute("ridelist 2");
        logic.execute("edit " + PHONE + " p/+65 91111111 f/250");
        Person editedCharlotte = new PersonBuilder(CHARLOTTE).withPhone("+65 91111111").withFtp("250").build();
        assertEquals(List.of(editedCharlotte), model.getFilteredPersonList());
        for (Ride ride : model.getAddressBook().getRideList().subList(0, 2)) {
            assertEquals(List.of(new Phone("+65 91111111")), ride.getRoster());
        }
        // The reciprocal ride edit must still accept equality with the cyclist's updated FTP.
        logic.execute("rideedit 2 f/250");
        assertSavedState();
    }

    @Test
    public void execute_afterRemovingRegistrations_allowsLowerFtp() throws Exception {
        logic.execute("ridedeletecyclist 1 " + PHONE);
        logic.execute("ridedeletecyclist 2 " + PHONE);
        logic.execute("edit " + PHONE + " f/1");

        assertEquals(new Ftp("1"), model.getAddressBook().getPersonList().get(0).getFtp());
        assertEquals(List.of(), model.getAddressBook().getRideList().get(0).getRoster());
        assertEquals(List.of(), model.getAddressBook().getRideList().get(1).getRoster());
        assertSavedState();
    }

    @Test
    public void execute_withoutFtp_preservesFtpAndRegistrations() throws Exception {
        List<Ride> originalRides = List.copyOf(model.getAddressBook().getRideList());
        logic.execute("edit " + PHONE + " n/Charlotte Tan");

        assertEquals(CHARLOTTE.getFtp(), model.getAddressBook().getPersonList().get(0).getFtp());
        assertEquals(originalRides, model.getAddressBook().getRideList());
        assertSavedState();
    }

    private void assertRejectedWithoutChanges(String command, int ftp) throws Exception {
        AddressBook originalBook = new AddressBook(model.getAddressBook());
        List<Person> originalCyclistDisplay = List.copyOf(model.getFilteredPersonList());
        List<Ride> originalRideDisplay = List.copyOf(model.getFilteredRideList());
        String originalJson = Files.readString(bookPath);
        String expectedMessage = "Cannot edit cyclist: FTP " + ftp
                + " W is below the minimum 250 W for ride 2 (Hill Repeats).";

        assertThrows(CommandException.class, expectedMessage, () -> logic.execute(command));

        assertEquals(originalBook, model.getAddressBook());
        assertEquals(originalCyclistDisplay, model.getFilteredPersonList());
        assertEquals(originalRideDisplay, model.getFilteredRideList());
        assertEquals(originalJson, Files.readString(bookPath));
    }

    private void assertSavedState() throws Exception {
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
