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
import seedu.address.testutil.RideBuilder;

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
    public void execute_recognisedParameterSeparators_addsAndEditsWithoutChangingOtherFields() throws Exception {
        // EP: ASCII space, tab, EM SPACE, and IDEOGRAPHIC SPACE introduce separate parameters.
        logic.execute("rideaddcyclist 1 " + PHONE);
        Ride original = currentRide();
        for (String separator : List.of(" ", "\t", "\u2003", "\u3000")) {
            logic.execute("rideedit 1 d/20" + separator + "f/180");
            assertEquals(new RideBuilder(original).withDistance("20").build(), currentRide());
            logic.execute("rideadd " + DETAILS.replace("d/45 f/180", "d/20" + separator + "f/180"));
            Ride added = model.getAddressBook().getRideList().getLast();
            assertEquals(new RideBuilder().withId(added.getId()).withName("Boundary").withDistance("20")
                    .withMaxGroupSize("2").build(), added);
            assertSavedState();
            logic.execute("rideedit 1 d/45");
        }
    }

    @Test
    public void execute_malformedParameterBoundaries_preservesStateIdsAndConfirmation() throws Exception {
        // EP: unrecognized separators, invalid field values, duplicate prefixes, and unclosed quotes.
        logic.execute("rideadd " + DETAILS.replace("0630", "1030").replace("0930", "1130"));
        logic.execute("rideaddcyclist 1 " + PHONE);
        logic.execute("rideaddcyclist 2 " + PHONE);
        logic.execute("ridelist 1");
        for (String fields : List.of("d/20\u00a0f/180", "d/20\u202ff/180", "d/20\u2003f/bad",
                "d/20\u3000f/180\u2003f/180", "notes/\"camera f/2")) {
            // These time changes would overlap registered rides if a partial edit were applied.
            assertRejected("rideedit 2 from/18-10-2026 0800 " + fields, ParseException.class);
            assertRejected("rideadd " + DETAILS.replace("d/45 f/180", fields), ParseException.class);
            assertThrows(ParseException.class, () -> logic.execute("yes"));
        }
        logic.execute("rideadd " + DETAILS);
        assertEquals(3, model.getAddressBook().getRideList().getLast().getId());
        assertSavedState();
    }

    @Test
    public void execute_unicodeWhitespaceAfterQuotes_preservesNotesParametersAndJson() throws Exception {
        // EP: supported Unicode whitespace after a closing quote, at command end or before an ASCII-delimited prefix.
        logic.execute("rideaddcyclist 1 " + PHONE);
        Ride original = currentRide();
        String notes = "Bring camera with aperture f/2";
        for (String separator : List.of(" ", "\u2003", "\u00a0", "\u202f", "\u3000", "\u00a0\u2003\u202f")) {
            logic.execute("rideedit 1 notes/\"" + notes + "\"" + separator + " s/5");
            assertEquals(new RideBuilder(original).withNotes(notes).withMaxGroupSize("5").build(), currentRide());
            logic.execute("rideadd " + DETAILS + " notes/\"" + notes + "\"" + separator);
            Ride added = model.getAddressBook().getRideList().getLast();
            assertEquals(notes, added.getNotes().orElseThrow().value);
            assertEquals(original.getMinimumPower(), added.getMinimumPower());
            assertSavedState();
        }
    }

    @Test
    public void execute_unicodeNotes_normalizesAndClearsWithoutChangingOtherFieldsOrFilters() throws Exception {
        logic.execute("rideaddcyclist 1 " + PHONE);
        logic.execute("ridelist 1");
        Ride original = currentRide();
        List<Person> visiblePeople = List.copyOf(model.getFilteredPersonList());
        logic.execute("ridefind notes/Bring");
        for (String separator : new String[] {" ", "\t", "\u2003", "\u00a0", "\u202f", "\u3000"}) {
            logic.execute("rideedit 1 notes/\"" + separator + "Bring" + separator + "camera"
                    + separator + "with aperture f/2" + separator + "\"");
            assertEquals(new RideBuilder(original).withNotes("Bring camera with aperture f/2").build(), currentRide());
            assertEquals(List.of(currentRide()), model.getFilteredRideList());
            assertSavedState();
            logic.execute("rideedit 1 notes/" + separator);
            assertEquals(original, currentRide());
            assertEquals(java.util.Optional.empty(), currentRide().getNotes());
            // EP: clearing notes makes the edited ride stop matching, so the filter resets.
            assertEquals(List.of(currentRide()), model.getFilteredRideList());
            assertEquals(visiblePeople, model.getFilteredPersonList());
            assertSavedState();
            model.setAddressBook(storage.readAddressBook().orElseThrow());
            assertEquals(original, currentRide());
        }
        logic.execute("rideadd " + DETAILS + " notes/\u3000Bring\u2003\u00a0CAF\u00c9,\u202f\u6c34!\u3000");
        Ride added = model.getAddressBook().getRideList().get(1);
        assertEquals(2, added.getId());
        assertEquals("Bring CAF\u00c9, \u6c34!", added.getNotes().orElseThrow().value);
        assertEquals(List.of(original, added), model.getFilteredRideList());
        assertSavedState();
    }

    @Test
    public void execute_invalidUnicodeNotes_preservesBookFiltersJsonAndNextId() throws Exception {
        // EP: blank notes and over-limit notes, with ASCII/Unicode separators and quoted/unquoted forms.
        // Boundary values: 0 words is invalid for Add/quoted Edit; 11 is invalid; the final 10-word Add is valid.
        logic.execute("rideaddcyclist 1 " + PHONE);
        logic.execute("rideedit 1 notes/Bring lights");
        logic.execute("ridelist 1");
        logic.execute("ridefind notes/lights");
        for (String separator : new String[] {" ", "\t", "\r\n", "\u2003", "\u00a0", "\u202f", "\u3000"}) {
            String eleven = "one two three four five six seven eight nine ten eleven".replace(" ", separator);
            assertRejected("rideadd " + DETAILS + " notes/" + separator, ParseException.class);
            for (String quote : new String[] {"", "\""}) {
                assertRejected("rideadd " + DETAILS + " notes/" + quote + eleven + quote, ParseException.class);
                assertRejected("rideedit 1 f/100 notes/" + quote + eleven + quote, ParseException.class);
            }
            assertRejected("rideadd " + DETAILS + " notes/\"" + separator + "\"", ParseException.class);
            assertRejected("rideedit 1 f/100 notes/\"" + separator + "\"", ParseException.class);
            assertThrows(ParseException.class, () -> logic.execute("yes"));
        }
        logic.execute("rideadd " + DETAILS + " notes/\"one\u2003two three four five six seven eight nine ten\"");
        assertEquals(2, model.getAddressBook().getRideList().get(1).getId());
        assertSavedState();
    }

    @Test
    public void execute_quotedNotes_preservesOtherFieldsRosterAndPersistence() throws Exception {
        // EP: recognized prefixes inside quoted notes are literal; prefixes outside quotes remain parameters.
        logic.execute("rideaddcyclist 1 " + PHONE);
        Ride original = currentRide();
        String notes = "Bring camera with aperture f/2";
        logic.execute("rideedit 1 notes/\"" + notes + "\"");
        assertEquals(new RideBuilder(original).withNotes(notes).build(), currentRide());
        assertSavedState();
        logic.execute("re 1 notes/\"" + notes + "\" s/5");
        assertEquals(new RideBuilder(original).withNotes(notes).withMaxGroupSize("5").build(), currentRide());
        assertSavedState();
        logic.execute("edit " + PHONE + " f/250");
        logic.execute("rideedit 1 f/200 notes/\"" + notes + "\"");
        assertEquals(new RideBuilder(original).withNotes(notes).withMaxGroupSize("5")
                .withMinimumPower("200").build(), currentRide());
        logic.execute("ra " + DETAILS + " notes/\"" + notes + "\"");
        assertEquals(new RideBuilder().withId(2).withName("Boundary").withMaxGroupSize("2")
                .withNotes(notes).build(), model.getAddressBook().getRideList().get(1));
        assertSavedState();
        model.setAddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(notes, currentRide().getNotes().orElseThrow().value);
        assertEquals(original.getRoster(), currentRide().getRoster());
    }

    @Test
    public void execute_invalidQuotedCommands_preservesBookFiltersFileAndNextId() throws Exception {
        // EP: unclosed/blank quotes, trailing text, embedded quotes, duplicate notes, and invalid external fields.
        logic.execute("rideaddcyclist 1 " + PHONE);
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredRideList(ride -> false);
        for (String field : List.of("notes/\"camera f/2", "notes/\"\"", "notes/\"  \"",
                "notes/\"camera\"tail f/2", "notes/\"camera\" extra f/2", "notes/\"camera\" q/test",
                "notes/\"camera \"lens\"\"", "notes/\"camera\" s/bad", "notes/\"camera\" f/0",
                "notes/\"one two three four five six seven eight nine ten eleven\"",
                "notes/\"camera\" notes/second")) {
            assertRejected("rideedit 1 " + field, ParseException.class);
            assertRejected("rideadd " + DETAILS + " " + field, ParseException.class);
        }
        for (String field : List.of("n/Bad&Name", "l/Coast!", "from/invalid", "to/invalid",
                "d/0", "f/601", "s/0", "f/180 f/200")) {
            assertRejected("rideedit 1 notes/\"camera f/2\" " + field, ParseException.class);
        }
        assertRejected("rideedit 0 notes/\"camera f/2\"", ParseException.class);
        assertRejected("rideedit 1 notes/\"camera f/2\" from/19-10-2026 0630", CommandException.class);
        assertRejected("rideedit 1 notes/\"camera f/2\" f/181", CommandException.class);
        model.addPerson(new PersonBuilder().withPhone("+65 92345678").withEmail("second@example.com")
                .withFtp("180").build());
        logic.execute("rideaddcyclist 1 +65 92345678");
        assertRejected("rideedit 1 notes/\"camera f/2\" s/1", CommandException.class);
        logic.execute("rideadd " + DETAILS + " notes/\"camera f/2\"");
        assertEquals(2, model.getAddressBook().getRideList().get(1).getId());
        assertSavedState();
    }

    @Test
    public void execute_unquotedNotes_preservesAmbiguityAndExistingOrdering() throws Exception {
        // EP: unquoted prefix-looking text is tokenized as a field; parameter order does not protect it.
        logic.execute("rideedit 1 notes/Bring camera with aperture f/2");
        assertEquals("Bring camera with aperture", currentRide().getNotes().orElseThrow().value);
        assertEquals("2", currentRide().getMinimumPower().toString());
        for (String fields : List.of("notes/Bring water", "notes/Bring water f/200",
                "f/200 notes/Bring water", "l/Marina Bay notes/Meet at the waterfront")) {
            logic.execute("rideedit 1 " + fields);
            assertSavedState();
        }
        logic.execute("rideadd " + DETAILS + " notes/Bring water");
        assertSavedState();
    }

    @Test
    public void execute_invalidFieldPartitions_rejectAddEditAndFindWithoutMutation() throws Exception {
        // EP: empty fields, unsupported characters, impossible dates/times, and invalid numeric syntax.
        // Boundary values: distance 0/0.001/1000000, power 0/601, group size 0/1000000000, and 11-word notes.
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
        // Boundary values: distance 0.01/0.02 and 999999.98/999999.99 km; power 1/2 and 599/600 W;
        // group size 1/2 and 999999998/999999999, covering each valid endpoint and its interior neighbor.
        for (String field : List.of("d/0.01", "d/0.02", "d/999999.98", "d/999999.99",
                "f/1", "f/2", "f/599", "f/600", "s/1", "s/2", "s/999999998", "s/999999999")) {
            logic.execute("rideedit 1 " + field);
            assertSavedState();
        }
    }

    @Test
    public void execute_notesZeroOneNineTenElevenWords_obeysOptionalAndMaximumRules() throws Exception {
        // Boundary values: 1 word is the minimum; 9/10/11 straddle the maximum.
        // EP: 0 words clears notes in Edit but is invalid in Add.
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
        // Boundary values: end 0629/0630/0631 around start 0630, and start 0631 just after preserved end 0630.
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
        // Boundary values: search starts 0629/0630/0631 around ride start 0630;
        // search ends 0929/0930/0931 around ride end 0930. Equality is included.
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
        // Boundary values: 0000/0001 and 2358/2359 test inclusion at both edges of a date-only search.
        logic.execute("rideadd " + DETAILS.replace("0630", "0000").replace("0930", "0000"));
        logic.execute("rideadd " + DETAILS.replace("0630", "2359").replace("0930", "2359"));
        logic.execute("ridefind from/18-10-2026 to/18-10-2026");
        assertEquals(3, model.getFilteredRideList().size());
        logic.execute("ridefind from/18-10-2026 0001 to/18-10-2026 2358");
        assertEquals(1, model.getFilteredRideList().size());
    }

    @Test
    public void execute_emptyDuplicateAndOverflowPartitions_preserveState() throws Exception {
        // EP: missing arguments, repeated prefixes, incompatible all/criteria, and invalid or unknown ride IDs.
        // Boundary values: 0, Integer.MAX_VALUE, and Integer.MAX_VALUE + 1 distinguish invalid/parseable IDs.
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
        // EP: unknown cyclist, insufficient FTP, eligible cyclist, duplicate membership, and absent membership.
        // Boundary values: FTP 179/180/181 around minimum 180 W; roster sizes 0/1 at capacity 1.
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
        // EP: selected ride exists, is deleted, or is replaced by a different persistent ID.
        // Boundary values: adding/removing the first/last roster member changes the visible count between 0 and 1.
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
