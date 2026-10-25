package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.testutil.RideBuilder;

public class RideMatchesFindCriteriaPredicateTest {

    @Test
    public void test_noCriteria_matchesRide() {
        assertTrue(predicate().test(new RideBuilder().build()));
    }

    @Test
    public void test_fromAndTo_areInclusiveAtRideBoundaries() {
        Ride ride = new RideBuilder().withFrom("18-10-2026 0630").withTo("18-10-2026 0930").build();
        RideMatchesFindCriteriaPredicate predicate = new RideMatchesFindCriteriaPredicate(Optional.empty(),
                Optional.of(new RideDateTime("18-10-2026 0630")), Optional.of(new RideDateTime("18-10-2026 0930")),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        assertTrue(predicate.test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(),
                Optional.of(new RideDateTime("18-10-2026 0631")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(),
                Optional.of(new RideDateTime("18-10-2026 0929")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()).test(ride));
    }

    @Test
    public void test_allFields_requireEveryFieldToMatch() {
        Ride ride = new RideBuilder().withName("Evening Loop").withLocation("Tampines").withDistance("50")
                .withMinimumPower("200").withMaxGroupSize("15").withNotes("Bring lights").build();
        RideMatchesFindCriteriaPredicate predicate = new RideMatchesFindCriteriaPredicate(
                Optional.of(new Name("Evening Loop")), Optional.of(ride.getFrom()), Optional.of(ride.getTo()),
                Optional.of(new Location("Tampines")), Optional.of(new Distance("50")),
                Optional.of(new MinimumPower("200")), Optional.of(new GroupSize("15")),
                Optional.of(new Notes("Bring lights")));

        assertTrue(predicate.test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.of(new Name("Morning Loop")), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty()).test(ride));
    }

    @Test
    public void test_optionalNotes_requiresNotesWhenSpecified() {
        Ride ride = new RideBuilder().build();
        RideMatchesFindCriteriaPredicate predicate = new RideMatchesFindCriteriaPredicate(Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(new Notes("Bring lights")));

        assertFalse(predicate.test(ride));
    }

    @Test
    public void test_location_isCaseInsensitive() {
        Ride ride = new RideBuilder().withLocation("Tampines").build();

        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Location("tampines")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty()).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Location("Punggol")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty()).test(ride));
    }

    @Test
    public void test_distance_requiresExactValue() {
        Ride ride = new RideBuilder().withDistance("45.5").build();

        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(new Distance("45.50")), Optional.empty(), Optional.empty(),
                Optional.empty()).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(new Distance("45.51")), Optional.empty(), Optional.empty(),
                Optional.empty()).test(ride));
    }

    @Test
    public void test_minimumPower_isAtLeastTheSpecifiedValueAndGroupSizeIsExact() {
        Ride ride = new RideBuilder().withMinimumPower("600").withMaxGroupSize("1").build();

        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(new MinimumPower("600")),
                Optional.of(new GroupSize("1")), Optional.empty()).test(ride));
        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(new MinimumPower("599")),
                Optional.empty(), Optional.empty()).test(ride));
        Ride lowerPowerRide = new RideBuilder().withMinimumPower("599").build();
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(new MinimumPower("600")),
                Optional.empty(), Optional.empty()).test(lowerPowerRide));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new GroupSize("2")),
                Optional.empty()).test(ride));
    }

    @Test
    public void test_notes_matchesPartialValueIgnoringCase() {
        Ride ride = new RideBuilder().withNotes("Bring lights").build();

        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Notes("Bring lights"))).test(ride));
        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Notes("Bring"))).test(ride));
        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Notes("lights"))).test(ride));
        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Notes("BRING"))).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Notes("Bring water"))).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Notes("Bring lights and a spare tube"))).test(ride));
    }

    @Test
    public void test_name_isCaseInsensitive() {
        Ride ride = new RideBuilder().withName("Sunday Sunrise Ride").build();

        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.of(new Name("sunday sunrise ride")),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.of(new Name("Sunday Sunset Ride")),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()).test(ride));
    }

    @Test
    public void test_fromOnlyAndToOnly_applyOneSidedBounds() {
        Ride ride = new RideBuilder().withFrom("18-10-2026 0630").withTo("18-10-2026 0930").build();

        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(),
                Optional.of(new RideDateTime("18-10-2026 0630")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).test(ride));
        assertTrue(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(),
                Optional.of(new RideDateTime("18-10-2026 0930")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(),
                Optional.of(new RideDateTime("18-10-2026 0631")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).test(ride));
        assertFalse(new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(),
                Optional.of(new RideDateTime("18-10-2026 0929")), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()).test(ride));
    }

    private RideMatchesFindCriteriaPredicate predicate() {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }
}
