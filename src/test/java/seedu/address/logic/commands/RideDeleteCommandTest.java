package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.RideBuilder;

public class RideDeleteCommandTest {

    private static final int RETAINED_RIDE_ID = 42;
    private static final int DELETED_RIDE_ID = 105;
    private static final int NEXT_RIDE_ID = 200;

    private static final Ride RETAINED_RIDE = new RideBuilder().withId(RETAINED_RIDE_ID)
            .withName("Evening Spin").withRoster(BENSON.getPhone().toString()).build();
    private static final Ride RIDE_TO_DELETE = new RideBuilder().withId(DELETED_RIDE_ID)
            .withRoster(ALICE.getPhone().toString()).build();

    private final Model model = createModel();

    @Test
    public void execute_validPersistentId_deletesRideAndPreservesOtherData() throws Exception {
        List<?> cyclistsBefore = List.copyOf(model.getAddressBook().getPersonList());

        CommandResult result = new RideDeleteCommand(DELETED_RIDE_ID).execute(model);

        String expectedMessage = String.format(RideDeleteCommand.MESSAGE_DELETE_RIDE_SUCCESS,
                Messages.formatRide(RIDE_TO_DELETE));
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(List.of(RETAINED_RIDE), model.getAddressBook().getRideList());
        assertEquals(RETAINED_RIDE.getRoster(), model.getAddressBook().getRideList().get(0).getRoster());
        assertEquals(cyclistsBefore, model.getAddressBook().getPersonList());
        assertEquals(NEXT_RIDE_ID, model.getAddressBook().getNextRideId());
    }

    @Test
    public void execute_unknownRideId_throwsCommandExceptionWithoutModifyingModel() {
        RideDeleteCommand command = new RideDeleteCommand(999);

        assertCommandFailure(command, model, String.format(RideDeleteCommand.MESSAGE_RIDE_NOT_FOUND, 999));
    }

    @Test
    public void constructor_nonPositiveRideId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new RideDeleteCommand(0));
        assertThrows(IllegalArgumentException.class, () -> new RideDeleteCommand(-1));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RideDeleteCommand(DELETED_RIDE_ID).execute(null));
    }

    @Test
    public void equals() {
        RideDeleteCommand command = new RideDeleteCommand(DELETED_RIDE_ID);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new RideDeleteCommand(DELETED_RIDE_ID)));
        assertFalse(command.equals(new RideDeleteCommand(RETAINED_RIDE_ID)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(DELETED_RIDE_ID));
    }

    private static Model createModel() {
        AddressBook addressBook = getTypicalAddressBook();
        addressBook.setRides(List.of(RETAINED_RIDE, RIDE_TO_DELETE), NEXT_RIDE_ID);
        return new ModelManager(addressBook, new UserPrefs());
    }
}
