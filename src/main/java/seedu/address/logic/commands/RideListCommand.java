package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;

/**
 * Lists the cyclists registered for a ride in the cyclist panel.
 */
public class RideListCommand extends Command {

    public static final String COMMAND_WORD = "ridelist";
    public static final String COMMAND_ALIAS = "rl";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + " (" + COMMAND_ALIAS + "): Lists the cyclists registered for the ride identified by its ride ID.\n"
            + "Parameters: ID (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 101";

    public static final String MESSAGE_SUCCESS =
            "Showing registered cyclists for ride %1$d in the left panel.";
    public static final String MESSAGE_EMPTY_ROSTER = "Ride %1$d has no registered cyclists.";
    public static final String MESSAGE_RIDE_NOT_FOUND = RideLookup.MESSAGE_RIDE_NOT_FOUND;
    public static final String MESSAGE_ROSTER_MEMBER_NOT_FOUND =
            "Cannot list ride: a registered cyclist cannot be found.";
    public static final String MESSAGE_INVALID_ID = "Ride ID must be a positive integer.";

    private final int targetId;

    /**
     * Creates a command that lists the cyclists registered for {@code targetId}.
     */
    public RideListCommand(int targetId) {
        checkArgument(targetId > Ride.UNASSIGNED_ID, MESSAGE_INVALID_ID);
        this.targetId = targetId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Ride ride = RideLookup.getRide(model, targetId);
        for (Phone phone : ride.getRoster()) {
            boolean hasCyclist = model.getAddressBook().getPersonList().stream()
                    .anyMatch(person -> person.getPhone().equals(phone));
            if (!hasCyclist) {
                throw new CommandException(MESSAGE_ROSTER_MEMBER_NOT_FOUND);
            }
        }

        model.updateFilteredPersonListForRide(targetId);
        String message = ride.getRoster().isEmpty() ? MESSAGE_EMPTY_ROSTER : MESSAGE_SUCCESS;
        return new CommandResult(String.format(message, targetId));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RideListCommand otherRideListCommand)) {
            return false;
        }

        return targetId == otherRideListCommand.targetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .toString();
    }
}
