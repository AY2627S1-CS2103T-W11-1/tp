package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Phone;
import seedu.address.testutil.RideBuilder;

public class RideTest {

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        Ride ride = new RideBuilder().build();

        assertThrows(NullPointerException.class, () -> new Ride(null, ride.getFrom(), ride.getTo(),
                ride.getLocation(), ride.getDistance(), ride.getMinimumPower(), ride.getMaxGroupSize(),
                Optional.empty()));
        assertThrows(NullPointerException.class, () -> new Ride(ride.getName(), ride.getFrom(), ride.getTo(),
                ride.getLocation(), ride.getDistance(), ride.getMinimumPower(), ride.getMaxGroupSize(), null));
        assertThrows(NullPointerException.class, () -> new Ride(1, ride.getName(), ride.getFrom(), ride.getTo(),
                ride.getLocation(), ride.getDistance(), ride.getMinimumPower(), ride.getMaxGroupSize(),
                Optional.empty(), null));
    }

    @Test
    public void constructor_endBeforeStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Ride.MESSAGE_INVALID_PERIOD, () ->
                new RideBuilder().withFrom("18-10-2026 0930").withTo("18-10-2026 0929").build());
    }

    @Test
    public void constructor_endSameAsStart_isAllowed() {
        Ride ride = new RideBuilder().withFrom("18-10-2026 0930").withTo("18-10-2026 0930").build();

        assertEquals(ride.getFrom(), ride.getTo());
    }

    @Test
    public void constructor_negativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Ride.MESSAGE_INVALID_ID, () ->
                new RideBuilder().withId(-1).build());
    }

    @Test
    public void constructor_withoutIdAndRoster_hasUnassignedIdAndEmptyRoster() {
        Ride base = new RideBuilder().build();
        Ride ride = new Ride(base.getName(), base.getFrom(), base.getTo(), base.getLocation(), base.getDistance(),
                base.getMinimumPower(), base.getMaxGroupSize(), base.getNotes());

        assertEquals(Ride.UNASSIGNED_ID, ride.getId());
        assertTrue(ride.getRoster().isEmpty());
    }

    @Test
    public void isValidPeriod() {
        RideDateTime early = new RideDateTime("18-10-2026 0630");
        RideDateTime late = new RideDateTime("18-10-2026 0930");

        assertTrue(Ride.isValidPeriod(early, late));
        assertTrue(Ride.isValidPeriod(early, early)); // same time is allowed
        assertFalse(Ride.isValidPeriod(late, early));
    }

    @Test
    public void getRoster_modifyList_throwsUnsupportedOperationException() {
        Ride ride = new RideBuilder().build();

        assertThrows(UnsupportedOperationException.class, () -> ride.getRoster().add(new Phone("+65 91234567")));
    }

    @Test
    public void constructor_rosterIsCopied() {
        java.util.ArrayList<Phone> roster = new java.util.ArrayList<>(List.of(new Phone("+65 91234567")));
        Ride base = new RideBuilder().build();
        Ride ride = new Ride(1, base.getName(), base.getFrom(), base.getTo(), base.getLocation(),
                base.getDistance(), base.getMinimumPower(), base.getMaxGroupSize(), Optional.empty(), roster);

        roster.clear();

        assertEquals(1, ride.getRoster().size());
    }

    @Test
    public void withId_returnsCopyWithNewIdAndSameDetails() {
        Ride ride = new RideBuilder().withNotes("Bring lights").withRoster("+65 91234567").build();

        Ride copy = ride.withId(5);

        assertEquals(5, copy.getId());
        assertEquals(new RideBuilder(ride).withId(5).build(), copy);
        assertEquals(Ride.UNASSIGNED_ID, ride.getId()); // the original is unchanged
    }

    @Test
    public void equals() {
        Ride ride = new RideBuilder().withId(1).build();

        // same values -> returns true
        assertTrue(ride.equals(new RideBuilder().withId(1).build()));

        // same object -> returns true
        assertTrue(ride.equals(ride));

        // null -> returns false
        assertFalse(ride.equals(null));

        // different types -> returns false
        assertFalse(ride.equals(5));

        // different id -> returns false
        assertFalse(ride.equals(new RideBuilder().withId(2).build()));

        // different name -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withName("Evening Spin").build()));

        // different from -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withFrom("18-10-2026 0600").build()));

        // different to -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withTo("18-10-2026 1000").build()));

        // different location -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withLocation("Punggol").build()));

        // different distance -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withDistance("46").build()));

        // different minimum power -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withMinimumPower("181").build()));

        // different group size -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withMaxGroupSize("13").build()));

        // different notes -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withNotes("Bring lights").build()));

        // different roster -> returns false
        assertFalse(ride.equals(new RideBuilder(ride).withRoster("+65 91234567").build()));
    }

    @Test
    public void hashCode_equalRides_haveEqualHashCodes() {
        assertEquals(new RideBuilder().withId(1).build().hashCode(), new RideBuilder().withId(1).build().hashCode());
    }

    @Test
    public void toString_containsDetails() {
        String text = new RideBuilder().withId(3).withNotes("Bring lights").build().toString();

        assertTrue(text.contains("id=3"));
        assertTrue(text.contains("Sunday Sunrise Ride"));
        assertTrue(text.contains("18-10-2026 0630"));
        assertTrue(text.contains("Bring lights"));
    }
}
