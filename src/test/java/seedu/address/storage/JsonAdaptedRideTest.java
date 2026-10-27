package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedRide.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;
import seedu.address.testutil.RideBuilder;

public class JsonAdaptedRideTest {

    private static final Ride VALID_RIDE = new RideBuilder().withId(3).withNotes("Bring lights")
            .withRoster("+65 91234567", "+65 98765432").build();

    private static final Integer ID = 3;
    private static final String NAME = "Sunday Sunrise Ride";
    private static final String FROM = "18-10-2026 0630";
    private static final String TO = "18-10-2026 0930";
    private static final String LOCATION = "East Coast";
    private static final String DISTANCE = "45";
    private static final String POWER = "180";
    private static final String SIZE = "12";
    private static final String NOTES = "Bring lights";
    private static final List<String> ROSTER = List.of("+65 91234567");

    private static JsonAdaptedRide ride(Integer id, String name, String from, String to, String location,
            String distance, String power, String size, String notes, List<String> roster) {
        return new JsonAdaptedRide(id, name, from, to, location, distance, power, size, notes, roster);
    }

    @Test
    public void toModelType_legacyUnicodeNotes_normalizesValidAndRejectsInvalidValues() throws Exception {
        Ride normalized = ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE,
                "\u3000Bring\u00a0\u2003lights\u202f", ROSTER).toModelType();
        assertEquals(new Notes(NOTES), normalized.getNotes().orElseThrow());
        assertEquals(ID.intValue(), normalized.getId());
        assertEquals(ROSTER.getFirst(), normalized.getRoster().getFirst().value);
        for (String separator : new String[] {" ", "\u00a0", "\u2003", "\u202f", "\u3000"}) {
            for (String invalid : new String[] {separator,
                "one two three four five six seven eight nine ten eleven".replace(" ", separator)}) {
                assertThrows(IllegalValueException.class, Notes.MESSAGE_CONSTRAINTS, () ->
                        ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, invalid, ROSTER).toModelType());
            }
        }
    }

    @Test
    public void toModelType_validRideDetails_returnsRide() throws Exception {
        assertEquals(VALID_RIDE, new JsonAdaptedRide(VALID_RIDE).toModelType());
    }

    @Test
    public void toModelType_rideWithoutNotes_returnsRideWithoutNotes() throws Exception {
        Ride rideWithoutNotes = new RideBuilder().withId(1).build();

        assertEquals(rideWithoutNotes, new JsonAdaptedRide(rideWithoutNotes).toModelType());
    }

    @Test
    public void toModelType_nullRoster_returnsRideWithEmptyRoster() throws Exception {
        Ride expected = new RideBuilder().withId(3).withNotes(NOTES).build();

        assertEquals(expected, ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, null).toModelType());
    }

    @Test
    public void toModelType_nullId_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"), () ->
                ride(null, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_nonPositiveId_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, JsonAdaptedRide.MESSAGE_INVALID_ID, () ->
                ride(0, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
        assertThrows(IllegalValueException.class, JsonAdaptedRide.MESSAGE_INVALID_ID, () ->
                ride(-4, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, () ->
                ride(ID, "Bad&Name", FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()), () ->
                ride(ID, null, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidFrom_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, RideDateTime.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, "2026-10-18 0630", TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER)
                        .toModelType());
    }

    @Test
    public void toModelType_nullFrom_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "from"), () ->
                ride(ID, NAME, null, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidTo_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, RideDateTime.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, "31-02-2026 0930", LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER)
                        .toModelType());
    }

    @Test
    public void toModelType_nullTo_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "to"), () ->
                ride(ID, NAME, FROM, null, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_endBeforeStart_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Ride.MESSAGE_INVALID_PERIOD, () ->
                ride(ID, NAME, TO, FROM, LOCATION, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidLocation_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Location.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, "East Coast!", DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_nullLocation_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(MISSING_FIELD_MESSAGE_FORMAT, Location.class.getSimpleName()), () ->
                ride(ID, NAME, FROM, TO, null, DISTANCE, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidDistance_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Distance.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, LOCATION, "0", POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_nullDistance_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(MISSING_FIELD_MESSAGE_FORMAT, Distance.class.getSimpleName()), () ->
                ride(ID, NAME, FROM, TO, LOCATION, null, POWER, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidMinimumPower_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, MinimumPower.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, "601", SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_nullMinimumPower_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(MISSING_FIELD_MESSAGE_FORMAT, MinimumPower.class.getSimpleName()), () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, null, SIZE, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidGroupSize_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, GroupSize.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, "0", NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_nullGroupSize_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(MISSING_FIELD_MESSAGE_FORMAT, GroupSize.class.getSimpleName()), () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, null, NOTES, ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidNotes_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Notes.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, "   ", ROSTER).toModelType());
        assertThrows(IllegalValueException.class, Notes.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE,
                        "one two three four five six seven eight nine ten eleven", ROSTER).toModelType());
    }

    @Test
    public void toModelType_invalidRosterPhone_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, List.of("91234567"))
                        .toModelType());
    }

    @Test
    public void toModelType_nullRosterEntry_throwsIllegalValueException() {
        java.util.ArrayList<String> rosterWithNull = new java.util.ArrayList<>();
        rosterWithNull.add(null);

        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, () ->
                ride(ID, NAME, FROM, TO, LOCATION, DISTANCE, POWER, SIZE, NOTES, rosterWithNull).toModelType());
    }
}
