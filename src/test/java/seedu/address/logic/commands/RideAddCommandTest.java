package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.RideBuilder;

public class RideAddCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullRide_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RideAddCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        RideAddCommand command = new RideAddCommand(new RideBuilder().build());

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_validRide_addsRideWithFirstId() {
        Ride draft = new RideBuilder().withNotes("Bring lights").build();
        Ride expectedRide = new RideBuilder(draft).withId(1).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addRide(draft);

        String expectedMessage = String.format(RideAddCommand.MESSAGE_SUCCESS, Messages.formatRide(expectedRide));
        assertCommandSuccess(new RideAddCommand(draft), model, expectedMessage, expectedModel);
        assertEquals(List.of(expectedRide), model.getAddressBook().getRideList());
    }

    @Test
    public void execute_twoRides_getIncreasingIds() throws Exception {
        new RideAddCommand(new RideBuilder().build()).execute(model);
        new RideAddCommand(new RideBuilder().withName("Evening Spin").build()).execute(model);

        List<Ride> rides = model.getAddressBook().getRideList();
        assertEquals(2, rides.size());
        assertEquals(1, rides.get(0).getId());
        assertEquals(2, rides.get(1).getId());
    }

    @Test
    public void execute_sameRideTwice_addsBothRides() throws Exception {
        RideAddCommand command = new RideAddCommand(new RideBuilder().build());

        command.execute(model);
        command.execute(model);

        assertEquals(2, model.getAddressBook().getRideList().size());
    }

    @Test
    public void execute_afterExistingRides_continuesNumbering() throws Exception {
        model.addRide(new RideBuilder().build());
        model.addRide(new RideBuilder().build());

        new RideAddCommand(new RideBuilder().build()).execute(model);

        assertEquals(3, model.getAddressBook().getRideList().get(2).getId());
    }

    @Test
    public void execute_newRide_hasEmptyRosterAndLeavesCyclistsUnchanged() throws Exception {
        int cyclistCountBefore = model.getAddressBook().getPersonList().size();

        new RideAddCommand(new RideBuilder().build()).execute(model);

        assertTrue(model.getAddressBook().getRideList().get(0).getRoster().isEmpty());
        assertEquals(cyclistCountBefore, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_successMessage_containsIdAndDetails() throws Exception {
        CommandResult result = new RideAddCommand(new RideBuilder().withNotes("Bring lights").build())
                .execute(model);

        String message = result.getFeedbackToUser();
        assertTrue(message.startsWith("New ride added: Ride 1: Sunday Sunrise Ride"));
        assertTrue(message.contains("From: 18-10-2026 0630"));
        assertTrue(message.contains("To: 18-10-2026 0930"));
        assertTrue(message.contains("Location: East Coast"));
        assertTrue(message.contains("Distance: 45 km"));
        assertTrue(message.contains("Minimum power: 180 W"));
        assertTrue(message.contains("Max group size: 12"));
        assertTrue(message.contains("Notes: Bring lights"));
    }

    @Test
    public void execute_rideWithoutNotes_messageShowsNone() throws Exception {
        CommandResult result = new RideAddCommand(new RideBuilder().build()).execute(model);

        assertTrue(result.getFeedbackToUser().contains("Notes: None"));
    }

    @Test
    public void equals() {
        Ride sunrise = new RideBuilder().build();
        Ride evening = new RideBuilder().withName("Evening Spin").build();
        RideAddCommand addSunriseCommand = new RideAddCommand(sunrise);
        RideAddCommand addEveningCommand = new RideAddCommand(evening);

        // same object -> returns true
        assertTrue(addSunriseCommand.equals(addSunriseCommand));

        // same values -> returns true
        assertTrue(addSunriseCommand.equals(new RideAddCommand(new RideBuilder().build())));

        // different types -> returns false
        assertFalse(addSunriseCommand.equals(1));

        // null -> returns false
        assertFalse(addSunriseCommand.equals(null));

        // different ride -> returns false
        assertFalse(addSunriseCommand.equals(addEveningCommand));
    }

    @Test
    public void toStringMethod() {
        Ride ride = new RideBuilder().build();
        RideAddCommand command = new RideAddCommand(ride);

        assertEquals(RideAddCommand.class.getCanonicalName() + "{toAdd=" + ride + "}", command.toString());
    }
}
