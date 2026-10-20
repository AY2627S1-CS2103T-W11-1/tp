package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Exercises equivalence partitions and adjacent boundaries through parsing, execution, and storage.
 */
public class RideBoundaryIntegrationTest {
    private static final String DETAILS = "n/Boundary from/18-10-2026 0630 to/18-10-2026 0930 "
            + "l/East Coast d/45 f/180 s/2";
    private static final String PHONE = "+65 91234567";

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager();
        model.addPerson(new PersonBuilder().withPhone(PHONE).withFtp("180").build());
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("book.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage);
        logic.execute("rideadd " + DETAILS);
    }

    @Test
    public void execute_invalidFieldPartitions_rejectAddEditAndFindWithoutMutation() throws Exception {
        List<String> invalidFields = List.of("n/", "n/Bad&Name", "from/29-02-2027 0630",
                "to/18-10-2026 2400", "to/18-10-2026 2360", "l/", "l/Coast!",
                "d/0", "d/-0.01", "d/0.001", "d/1000000", "d/NaN", "d/1e2",
                "f/0", "f/601", "f/1.5", "f/2147483648", "s/0", "s/1000000000", "s/1.5",
                "notes/one two three four five six seven eight nine ten eleven");
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredRideList(ride -> false);
        for (String field : invalidFields) {
            String prefix = field.substring(0, field.indexOf('/') + 1);
            String modified = DETAILS.replaceFirst(java.util.regex.Pattern.quote(prefix) + ".*?(?= [a-z]+/|$)",
                    java.util.regex.Matcher.quoteReplacement(field));
            if (prefix.equals("notes/")) {
                modified = DETAILS + " " + field;
            }
            assertRejected("rideadd " + modified, ParseException.class);
            assertRejected("rideedit 1 " + field, ParseException.class);
            assertRejected("ridefind " + field, ParseException.class);
        }
    }

    @Test
    public void execute_validNumericBoundaryNeighbours_roundTrip() throws Exception {
        for (String field : List.of("d/0.01", "d/0.02", "d/999999.98", "d/999999.99",
                "f/1", "f/2", "f/599", "f/600", "s/1", "s/2", "s/999999998", "s/999999999")) {
            logic.execute("rideedit 1 " + field);
            assertSavedState();
        }
    }

    @Test
    public void execute_notesZeroOneNineTenElevenWords_obeysOptionalAndMaximumRules() throws Exception {
        logic.execute("rideedit 1 notes/one");
        assertEquals("one", currentRide().getNotes().orElseThrow().value);
        logic.execute("rideedit 1 notes/one two three four five six seven eight nine");
        logic.execute("rideedit 1 notes/one two three four five six seven eight nine ten");
        assertRejected("rideedit 1 notes/one two three four five six seven eight nine ten eleven",
                ParseException.class);
        logic.execute("rideedit 1 notes/");
        assertEquals(java.util.Optional.empty(), currentRide().getNotes());
        assertRejected("rideadd " + DETAILS + " notes/", ParseException.class);
        assertSavedState();
    }

    @Test
    public void execute_ridePeriodBeforeEqualAfterBoundary_validatesPreservedEndpoint() throws Exception {
        assertRejected("rideedit 1 to/18-10-2026 0629", CommandException.class);
        logic.execute("rideedit 1 to/18-10-2026 0630");
        assertSavedState();
        assertRejected("rideedit 1 from/18-10-2026 0631", CommandException.class);
        logic.execute("rideedit 1 to/18-10-2026 0631");
        assertSavedState();
        assertRejected("rideadd " + DETAILS.replace("0930", "0629"), ParseException.class);
        logic.execute("rideadd " + DETAILS.replace("0930", "0630"));
        assertSavedState();
    }

    @Test
    public void execute_findMinuteBoundariesAndDateOnlyDefaults_matchInclusiveEndpoints() throws Exception {
        for (String from : List.of("0629", "0630", "0631")) {
            logic.execute("ridefind from/18-10-2026 " + from);
            assertEquals(from.equals("0631") ? 0 : 1, model.getFilteredRideList().size());
        }
        for (String to : List.of("0929", "0930", "0931")) {
            logic.execute("ridefind to/18-10-2026 " + to);
            assertEquals(to.equals("0929") ? 0 : 1, model.getFilteredRideList().size());
        }
        logic.execute("ridefind from/18-10-2026 to/18-10-2026");
        assertEquals(1, model.getFilteredRideList().size());
        assertRejected("ridefind from/18-10-2026 0931 to/18-10-2026 0930", ParseException.class);
        logic.execute("ridefind from/19-10-2026");
        assertEquals(0, model.getFilteredRideList().size());
        logic.execute("ridefind to/17-10-2026");
        assertEquals(0, model.getFilteredRideList().size());
        logic.execute("ridefind all");
        assertEquals(1, model.getFilteredRideList().size());
    }

    @Test
    public void execute_findMidnightAndLastMinute_includesDateOnlyBoundaries() throws Exception {
        logic.execute("rideadd " + DETAILS.replace("0630", "0000").replace("0930", "0000"));
        logic.execute("rideadd " + DETAILS.replace("0630", "2359").replace("0930", "2359"));
        logic.execute("ridefind from/18-10-2026 to/18-10-2026");
        assertEquals(3, model.getFilteredRideList().size());
        logic.execute("ridefind from/18-10-2026 0001 to/18-10-2026 2358");
        assertEquals(1, model.getFilteredRideList().size());
    }

    @Test
    public void execute_emptyDuplicateAndOverflowPartitions_preserveState() throws Exception {
        for (String command : List.of("rideedit 1", "ridefind", "rideadd", "ridedelete", "ridelist",
                "rideedit 1 n/One n/Two", "ridefind f/1 f/600", "ridefind all n/One",
                "rideedit 2147483648 n/One", "ridedelete 2147483648", "ridelist 2147483648",
                "rideaddcyclist 2147483648 " + PHONE, "ridedeletecyclist 0 " + PHONE)) {
            assertRejected(command, ParseException.class);
        }
        for (String command : List.of("rideedit 2147483647 n/One", "ridedelete 2147483647",
                "ridelist 2147483647", "rideaddcyclist 2147483647 " + PHONE)) {
            assertRejected(command, CommandException.class);
        }
    }

    @Test
    public void execute_rosterCapacityAndPowerNeighbours_preserveRejectedChanges() throws Exception {
        logic.execute("rideedit 1 s/1");
        assertRejected("rideaddcyclist 1 +65 99999999", CommandException.class);
        logic.execute("edit " + PHONE + " f/179");
        assertRejected("rideaddcyclist 1 " + PHONE, CommandException.class);
        logic.execute("edit " + PHONE + " f/180");
        logic.execute("rideaddcyclist 1 " + PHONE);
        assertRejected("rideaddcyclist 1 " + PHONE, CommandException.class);
        assertRejected("rideedit 1 f/181", CommandException.class);
        logic.execute("rideedit 1 f/180");
        logic.execute("ridedeletecyclist 1 " + PHONE);
        assertRejected("ridedeletecyclist 1 " + PHONE, CommandException.class);
        logic.execute("edit " + PHONE + " f/181");
        logic.execute("rideaddcyclist 1 " + PHONE);
        assertSavedState();
    }

    @Test
    public void execute_deletedIdsAndActiveRosterFilter_doNotRetargetLaterRide() throws Exception {
        logic.execute("rideaddcyclist 1 " + PHONE);
        logic.execute("ridelist 1");
        assertEquals(1, model.getFilteredPersonList().size());
        logic.execute("ridedelete 1");
        assertEquals(0, model.getFilteredPersonList().size());
        logic.execute("rideadd " + DETAILS);
        assertEquals(2, currentRide().getId());
        assertRejected("ridelist 1", CommandException.class);
        logic.execute("ridelist 2");
        logic.execute("rideaddcyclist 2 " + PHONE);
        assertEquals(1, model.getFilteredPersonList().size());
        logic.execute("ridedeletecyclist 2 " + PHONE);
        assertEquals(0, model.getFilteredPersonList().size());
        assertSavedState();
    }

    private Ride currentRide() {
        return model.getAddressBook().getRideList().getFirst();
    }

    private void assertRejected(String command, Class<? extends Exception> expectedType) throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> visiblePeople = List.copyOf(model.getFilteredPersonList());
        List<Ride> visibleRides = List.copyOf(model.getFilteredRideList());
        String saved = Files.readString(storage.getAddressBookFilePath());
        assertThrows(expectedType, () -> logic.execute(command), command);
        assertEquals(before, model.getAddressBook(), command);
        assertEquals(visiblePeople, model.getFilteredPersonList(), command);
        assertEquals(visibleRides, model.getFilteredRideList(), command);
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()), command);
    }

    private void assertSavedState() throws Exception {
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
