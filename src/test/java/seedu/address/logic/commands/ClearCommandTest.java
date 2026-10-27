package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.RideBuilder;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, String.format(ClearCommand.MESSAGE_SUCCESS, 1), expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, String.format(ClearCommand.MESSAGE_SUCCESS, 1), expectedModel);
    }

    @Test
    public void execute_withRides_clearsRidesButKeepsNextRideId() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        model.addRide(new RideBuilder().build());
        model.addRide(new RideBuilder().withName("Evening Spin").build());

        assertCommandSuccess(new ClearCommand(), model, String.format(ClearCommand.MESSAGE_SUCCESS, 3),
                expectedClearedModel(3));

        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertTrue(model.getAddressBook().getRideList().isEmpty());
        assertEquals(3, model.getAddressBook().getNextRideId());
    }

    @Test
    public void execute_rideAddAfterClear_doesNotReuseRideIds() throws Exception {
        Model model = new ModelManager();
        new RideAddCommand(new RideBuilder().build()).execute(model);
        new RideAddCommand(new RideBuilder().withName("Evening Spin").build()).execute(model);

        new ClearCommand().execute(model);
        new RideAddCommand(new RideBuilder().withName("Night Ride").build()).execute(model);

        List<Ride> rides = model.getAddressBook().getRideList();
        assertEquals(1, rides.size());
        assertEquals(3, rides.get(0).getId());
        assertEquals(4, model.getAddressBook().getNextRideId());
    }

    @Test
    public void execute_withRides_messageExplainsThatRideNumberingContinues() throws Exception {
        Model model = new ModelManager();
        new RideAddCommand(new RideBuilder().build()).execute(model);
        new RideAddCommand(new RideBuilder().withName("Evening Spin").build()).execute(model);

        CommandResult result = new ClearCommand().execute(model);

        assertEquals("All cyclists and rides have been cleared. "
                + "Ride IDs are never reused, so the next ride you add will be ride 3.", result.getFeedbackToUser());
    }

    @Test
    public void execute_messageNamesTheIdTheNextRideActuallyGets() throws Exception {
        Model model = new ModelManager();
        new RideAddCommand(new RideBuilder().build()).execute(model);
        new RideAddCommand(new RideBuilder().withName("Evening Spin").build()).execute(model);
        new RideAddCommand(new RideBuilder().withName("Night Ride").build()).execute(model);

        CommandResult result = new ClearCommand().execute(model);
        new RideAddCommand(new RideBuilder().withName("Dawn Ride").build()).execute(model);

        int newRideId = model.getAddressBook().getRideList().get(0).getId();
        assertTrue(result.getFeedbackToUser().endsWith("will be ride " + newRideId + "."));
    }

    @Test
    public void execute_clearTwice_keepsNextRideId() throws Exception {
        Model model = new ModelManager();
        model.addRide(new RideBuilder().build());

        new ClearCommand().execute(model);
        new ClearCommand().execute(model);

        assertEquals(2, model.getAddressBook().getNextRideId());
    }

    private Model expectedClearedModel(int nextRideId) {
        AddressBook cleared = new AddressBook();
        cleared.setRides(List.of(), nextRideId);
        return new ModelManager(cleared, new UserPrefs());
    }

}
