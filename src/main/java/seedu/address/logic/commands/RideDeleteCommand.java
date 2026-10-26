package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ride.Ride;

/**
 * Deletes a group ride identified by its unique ride ID.
 */
public class RideDeleteCommand extends Command {

    public static final String COMMAND_WORD = "ridedelete";
    public static final String COMMAND_ALIAS = "rd";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + " (" + COMMAND_ALIAS + "): Deletes the group ride identified by its ride ID.\n"
            + "Parameters: ID (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 101";

    public static final String MESSAGE_DELETE_RIDE_SUCCESS = "Deleted ride: %1$s";
    public static final String MESSAGE_RIDE_NOT_FOUND = "Ride with ID %1$d cannot be found.";
    public static final String MESSAGE_INVALID_ID = "Ride ID must be a positive integer.";

    private final int targetId;

    /**
     * Creates a command that deletes the ride with {@code targetId}.
     */
    public RideDeleteCommand(int targetId) {
        checkArgument(targetId > Ride.UNASSIGNED_ID, MESSAGE_INVALID_ID);
        this.targetId = targetId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Ride rideToDelete = model.getAddressBook().getRideList().stream()
                .filter(ride -> ride.getId() == targetId)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_RIDE_NOT_FOUND, targetId)));

        model.deleteRide(rideToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_RIDE_SUCCESS, Messages.formatRide(rideToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RideDeleteCommand otherRideDeleteCommand)) {
            return false;
        }

        return targetId == otherRideDeleteCommand.targetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .toString();
    }
}
