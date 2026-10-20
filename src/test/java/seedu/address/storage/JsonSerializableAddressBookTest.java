package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_missingOrNullCyclistData_throwsIllegalValueException() throws Exception {
        for (String json : List.of("{}", "{\"persons\":null}")) {
            JsonSerializableAddressBook data = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);
            assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_MISSING_PERSONS,
                    data::toModelType);
        }
        JsonSerializableAddressBook nullEntry = JsonUtil.fromJsonString("{\"persons\":[null]}",
                JsonSerializableAddressBook.class);
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_PERSON,
                nullEntry::toModelType);
    }

    @Test
    public void toModelType_maximumRideId_throwsIllegalValueException() {
        JsonSerializableAddressBook data = createSerializableAddressBook(
                List.of(new RideBuilder().withId(Integer.MAX_VALUE).build()), null);
        assertThrows(IllegalValueException.class, AddressBook.MESSAGE_INVALID_RIDE_IDS, data::toModelType);
    }

    @Test
    public void toModelType_exhaustedCounter_roundTripPreservesCounter() throws Exception {
        AddressBook original = new AddressBook();
        original.setRides(List.of(new RideBuilder().withId(Integer.MAX_VALUE - 1).build()), Integer.MAX_VALUE);
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(original));
        JsonSerializableAddressBook data = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);
        assertEquals(original, data.toModelType());
    }

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_samePhoneDifferentEmails_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_differentPhonesSameEmail_throwsIllegalValueException() {
        Person personWithSameEmail = new PersonBuilder(ALICE).withName("Different Name")
                .withPhone("+65 80000000").build();
        JsonSerializableAddressBook data = createSerializableAddressBook(ALICE, personWithSameEmail);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                data::toModelType);
    }

    @Test
    public void toModelType_emailsDifferOnlyInCase_throwsIllegalValueException() {
        Person personWithSameEmail = new PersonBuilder(ALICE).withName("Different Name")
                .withPhone("+65 80000000")
                .withEmail(ALICE.getEmail().value.toUpperCase(Locale.ROOT)).build();
        JsonSerializableAddressBook data = createSerializableAddressBook(ALICE, personWithSameEmail);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                data::toModelType);
    }

    @Test
    public void toModelType_sameNameWithDistinctIdentity_loadsAllCyclists() throws Exception {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone("+65 80000000")
                .withEmail("other.alice@example.com").build();
        JsonSerializableAddressBook data = createSerializableAddressBook(ALICE, personWithSameName);
        AddressBook expectedAddressBook = new AddressBook();
        expectedAddressBook.addPerson(ALICE);
        expectedAddressBook.addPerson(personWithSameName);
        AddressBook actualAddressBook = data.toModelType();

        assertEquals(expectedAddressBook, actualAddressBook);
        assertEquals(2, actualAddressBook.getPersonList().size());
    }

    @Test
    public void toModelType_ridesAndNextRideId_loadsRides() throws Exception {
        Ride first = new RideBuilder().withId(1).build();
        Ride second = new RideBuilder().withId(2).withName("Evening Spin").withNotes("Bring lights").build();
        JsonSerializableAddressBook data = createSerializableAddressBook(List.of(first, second), 3);

        AddressBook addressBook = data.toModelType();

        assertEquals(List.of(first, second), addressBook.getRideList());
        assertEquals(3, addressBook.getNextRideId());
    }

    @Test
    public void toModelType_savedNextRideIdAboveHighestId_isKeptSoIdsAreNotReused() throws Exception {
        Ride onlyRide = new RideBuilder().withId(1).build();
        JsonSerializableAddressBook data = createSerializableAddressBook(List.of(onlyRide), 6);

        assertEquals(6, data.toModelType().getNextRideId());
    }

    @Test
    public void toModelType_noNextRideId_continuesAfterHighestId() throws Exception {
        Ride ride = new RideBuilder().withId(4).build();
        JsonSerializableAddressBook data = createSerializableAddressBook(List.of(ride), null);

        assertEquals(5, data.toModelType().getNextRideId());
    }

    @Test
    public void toModelType_nextRideIdBelowHighestId_isRaisedAboveHighestId() throws Exception {
        Ride ride = new RideBuilder().withId(4).build();
        JsonSerializableAddressBook data = createSerializableAddressBook(List.of(ride), 2);

        assertEquals(5, data.toModelType().getNextRideId());
    }

    @Test
    public void toModelType_noRidesAndNoNextRideId_startsNumberingAtOne() throws Exception {
        JsonSerializableAddressBook data = createSerializableAddressBook(List.of(), null);

        AddressBook addressBook = data.toModelType();

        assertEquals(List.of(), addressBook.getRideList());
        assertEquals(1, addressBook.getNextRideId());
    }

    @Test
    public void toModelType_nullRideEntry_throwsIllegalValueException() {
        List<JsonAdaptedRide> ridesWithNull = new java.util.ArrayList<>();
        ridesWithNull.add(new JsonAdaptedRide(new RideBuilder().withId(1).build()));
        ridesWithNull.add(null);
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), ridesWithNull, 3);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_RIDE,
                data::toModelType);
    }

    @Test
    public void toModelType_duplicateRideIds_throwsIllegalValueException() {
        Ride first = new RideBuilder().withId(1).build();
        Ride second = new RideBuilder().withId(1).withName("Evening Spin").build();
        JsonSerializableAddressBook data = createSerializableAddressBook(List.of(first, second), 3);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_RIDE_ID,
                data::toModelType);
    }

    @Test
    public void toModelType_sameRideDetailsWithDifferentIds_loadsBothRides() throws Exception {
        Ride first = new RideBuilder().withId(1).build();
        Ride second = new RideBuilder().withId(2).build();
        JsonSerializableAddressBook data = createSerializableAddressBook(List.of(first, second), 3);

        assertEquals(2, data.toModelType().getRideList().size());
    }

    @Test
    public void toModelType_nonPositiveNextRideId_throwsIllegalValueException() {
        JsonSerializableAddressBook zero = createSerializableAddressBook(List.of(), 0);
        JsonSerializableAddressBook negative = createSerializableAddressBook(List.of(), -3);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_NEXT_RIDE_ID,
                zero::toModelType);
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_INVALID_NEXT_RIDE_ID,
                negative::toModelType);
    }

    @Test
    public void toModelType_invalidRide_throwsIllegalValueException() {
        JsonAdaptedRide invalidRide = new JsonAdaptedRide(1, "Bad&Name", "18-10-2026 0630", "18-10-2026 0930",
                "East Coast", "45", "180", "12", null, null);
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), List.of(invalidRide), 2);

        assertThrows(IllegalValueException.class, data::toModelType);
    }

    @Test
    public void constructorFromAddressBook_roundTripKeepsRidesAndNextRideId() throws Exception {
        AddressBook original = new AddressBook();
        original.addPerson(ALICE);
        original.addRide(new RideBuilder().withNotes("Bring lights").withRoster(ALICE.getPhone().value).build());
        original.addRide(new RideBuilder().withName("Evening Spin").build());

        AddressBook roundTripped = new JsonSerializableAddressBook(original).toModelType();

        assertEquals(original, roundTripped);
        assertEquals(3, roundTripped.getNextRideId());
    }

    private JsonSerializableAddressBook createSerializableAddressBook(List<Ride> rides, Integer nextRideId) {
        return new JsonSerializableAddressBook(List.of(), rides.stream().map(JsonAdaptedRide::new).toList(),
                nextRideId);
    }

    private JsonSerializableAddressBook createSerializableAddressBook(Person... persons) {
        return new JsonSerializableAddressBook(Arrays.stream(persons).map(JsonAdaptedPerson::new).toList(),
                null, null);
    }

}
