package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;
import seedu.address.model.person.Name;
import seedu.address.model.ride.RideMatchesFindCriteriaPredicate;
import seedu.address.testutil.RideBuilder;

public class RideFindCommandTest {

    @Test
    public void execute_updatesFilteredRideList() {
        ModelManager model = new ModelManager();
        model.addRide(new RideBuilder().withName("Sunday Sunrise Ride").build());
        model.addRide(new RideBuilder().withName("Evening Loop").build());
        RideMatchesFindCriteriaPredicate predicate = new RideMatchesFindCriteriaPredicate(
                Optional.of(new Name("Evening Loop")), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        new RideFindCommand(predicate).execute(model);

        // EP: a criterion matches one ride in a list containing multiple rides.
        assertEquals(1, model.getFilteredRideList().size());
        assertEquals("Evening Loop", model.getFilteredRideList().getFirst().getName().fullName);
    }

    @Test
    public void execute_allPredicate_listsAllRides() {
        ModelManager model = new ModelManager();
        model.addRide(new RideBuilder().withName("Sunday Sunrise Ride").build());
        model.addRide(new RideBuilder().withName("Evening Loop").build());
        model.updateFilteredRideList(ride -> ride.getName().fullName.equals("Evening Loop"));

        new RideFindCommand(new RideMatchesFindCriteriaPredicate()).execute(model);

        // EP: ridefind all uses an empty predicate and returns every ride.
        assertEquals(2, model.getFilteredRideList().size());
    }

    @Test
    public void execute_allPredicate_emptyList() {
        ModelManager model = new ModelManager();

        new RideFindCommand(new RideMatchesFindCriteriaPredicate()).execute(model);

        // EP: an all-rides search on an empty list returns no rides.
        assertEquals(0, model.getFilteredRideList().size());
    }

    @Test
    public void equals_sameCriteria_returnsTrue() {
        RideMatchesFindCriteriaPredicate first = new RideMatchesFindCriteriaPredicate();
        RideMatchesFindCriteriaPredicate second = new RideMatchesFindCriteriaPredicate();

        // EP: commands with equivalent predicates compare equal.
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(new RideFindCommand(first).equals(new RideFindCommand(second)));
    }

    @Test
    public void equals_differentCriteria_returnsFalse() {
        RideMatchesFindCriteriaPredicate first = new RideMatchesFindCriteriaPredicate();
        RideMatchesFindCriteriaPredicate second = new RideMatchesFindCriteriaPredicate(
                Optional.of(new Name("Evening Loop")), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

        // EP: commands with different predicates do not compare equal.
        assertNotEquals(first, second);
    }
}
