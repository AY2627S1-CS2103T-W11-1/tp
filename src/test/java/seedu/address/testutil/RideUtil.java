package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DISTANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_SIZE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import seedu.address.logic.commands.RideAddCommand;
import seedu.address.model.ride.Ride;

/**
 * A utility class for Ride.
 */
public class RideUtil {

    private RideUtil() {} // prevents instantiation

    /**
     * Returns a rideadd command string for adding the {@code ride}.
     */
    public static String getRideAddCommand(Ride ride) {
        return RideAddCommand.COMMAND_WORD + " " + getRideDetails(ride);
    }

    /**
     * Returns the part of command string for the given {@code ride}'s details.
     */
    public static String getRideDetails(Ride ride) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME).append(ride.getName().fullName).append(" ");
        sb.append(PREFIX_FROM).append(ride.getFrom()).append(" ");
        sb.append(PREFIX_TO).append(ride.getTo()).append(" ");
        sb.append(PREFIX_LOCATION).append(ride.getLocation().value).append(" ");
        sb.append(PREFIX_DISTANCE).append(ride.getDistance().value).append(" ");
        sb.append(PREFIX_FTP).append(ride.getMinimumPower().value).append(" ");
        sb.append(PREFIX_GROUP_SIZE).append(ride.getMaxGroupSize().value).append(" ");
        ride.getNotes().ifPresent(notes -> sb.append(PREFIX_NOTES).append(notes.value).append(" "));
        return sb.toString();
    }

}
