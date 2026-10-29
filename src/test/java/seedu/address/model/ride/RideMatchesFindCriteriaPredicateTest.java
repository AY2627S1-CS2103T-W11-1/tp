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
        // EP: no supplied criteria represents the match-all case.
        assertTrue(predicate().test(new RideBuilder().build()));
    }

    @Test
    public void test_fromAndTo_areInclusiveAtRideBoundaries() {
        // Boundary values: exact ride start 0630/end 0930 are included;
        // moving the start to 0631 or the end to 0929 excludes the ride.
        Ride ride = new RideBuilder().withFrom("18-10-2026 0630").withTo("18-10-2026 0930").build();
        RideMatchesFindCriteriaPredicate predicate = predicateWithBounds("0630", "0930");

        assertTrue(predicate.test(ride));
        assertFalse(predicateWithBounds("0631", null).test(ride));
        assertFalse(predicateWithBounds(null, "0929").test(ride));
    }

    @Test
    public void test_allFields_requireEveryFieldToMatch() {
        // EP: all supplied fields must match; one incorrect field rejects the ride.
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
        // EP: a ride without notes belongs to the non-matching partition when notes are requested.
        Ride ride = new RideBuilder().build();
        RideMatchesFindCriteriaPredicate predicate = predicateWithNotes("Bring lights");

        assertFalse(predicate.test(ride));
    }

    @Test
    public void test_location_isCaseInsensitive() {
        // EP: location matching ignores case and rejects a different location.
        Ride ride = new RideBuilder().withLocation("Tampines").build();

        assertTrue(predicateWithLocation("tampines").test(ride));
        assertFalse(predicateWithLocation("Punggol").test(ride));
    }

    @Test
    public void test_distance_requiresExactValue() {
        // EP: numerically equivalent values match, while a different distance does not.
        Ride ride = new RideBuilder().withDistance("45.5").build();

        assertTrue(predicateWithDistance("45.50").test(ride));
        assertFalse(predicateWithDistance("45.51").test(ride));
    }

    @Test
    public void test_minimumPower_isAtLeastTheSpecifiedValueAndGroupSizeIsExact() {
        // EP: minimum power accepts values at or below the requested threshold;
        // a different maximum group size is rejected.
        Ride ride = new RideBuilder().withMinimumPower("600").withMaxGroupSize("1").build();

        // Boundary value: stored minimum power equals the requested threshold at 600 W.
        assertTrue(predicateWithMinimumPower("600").test(ride));
        assertTrue(predicateWithMinimumPower("599").test(ride));
        Ride lowerPowerRide = new RideBuilder().withMinimumPower("599").build();
        // Boundary value: stored power 599 W is one below the requested 600 W minimum.
        assertFalse(predicateWithMinimumPower("600").test(lowerPowerRide));
        assertFalse(predicateWithGroupSize("2").test(ride));
    }

    @Test
    public void test_notes_matchesWholeWordsIgnoringCase() {
        // EP: complete words match regardless of case, while partial or incomplete phrases do not.
        Ride ride = new RideBuilder().withNotes("Bring lights").build();

        assertTrue(predicateWithNotes("Bring lights").test(ride));
        assertTrue(predicateWithNotes("Bring").test(ride));
        assertTrue(predicateWithNotes("lights").test(ride));
        assertTrue(predicateWithNotes("BRING").test(ride));
        assertFalse(predicateWithNotes("lig").test(ride));
        assertFalse(predicateWithNotes("Bring water").test(ride));
        assertFalse(predicateWithNotes("Bring lights and a spare tube").test(ride));
    }

    @Test
    public void test_name_isCaseInsensitive() {
        // EP: name matching ignores case but rejects a different name.
        Ride ride = new RideBuilder().withName("Sunday Sunrise Ride").build();

        assertTrue(predicateWithName("sunday sunrise ride").test(ride));
        assertFalse(predicateWithName("Sunday Sunset Ride").test(ride));
    }

    @Test
    public void test_fromOnlyAndToOnly_applyOneSidedBounds() {
        // EP: each optional time boundary works independently of the other boundary.
        Ride ride = new RideBuilder().withFrom("18-10-2026 0630").withTo("18-10-2026 0930").build();

        assertTrue(predicateWithBounds("0630", null).test(ride));
        assertTrue(predicateWithBounds(null, "0930").test(ride));
        assertFalse(predicateWithBounds("0631", null).test(ride));
        assertFalse(predicateWithBounds(null, "0929").test(ride));
    }

    private RideMatchesFindCriteriaPredicate predicateWithBounds(String from, String to) {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(),
                Optional.ofNullable(from).map(value -> new RideDateTime("18-10-2026 " + value)),
                Optional.ofNullable(to).map(value -> new RideDateTime("18-10-2026 " + value)), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    private RideMatchesFindCriteriaPredicate predicate() {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    private RideMatchesFindCriteriaPredicate predicateWithLocation(String location) {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Location(location)), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty());
    }

    private RideMatchesFindCriteriaPredicate predicateWithDistance(String distance) {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(new Distance(distance)), Optional.empty(), Optional.empty(),
                Optional.empty());
    }

    private RideMatchesFindCriteriaPredicate predicateWithMinimumPower(String minimumPower) {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(new MinimumPower(minimumPower)),
                Optional.empty(), Optional.empty());
    }

    private RideMatchesFindCriteriaPredicate predicateWithGroupSize(String groupSize) {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new GroupSize(groupSize)),
                Optional.empty());
    }

    private RideMatchesFindCriteriaPredicate predicateWithNotes(String notes) {
        return new RideMatchesFindCriteriaPredicate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(new Notes(notes)));
    }

    private RideMatchesFindCriteriaPredicate predicateWithName(String name) {
        return new RideMatchesFindCriteriaPredicate(Optional.of(new Name(name)), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty());
    }
}
