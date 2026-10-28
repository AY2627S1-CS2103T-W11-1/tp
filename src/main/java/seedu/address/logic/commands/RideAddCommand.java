package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DISTANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_SIZE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ride.Ride;

/**
 * Adds a group ride to the address book.
 */
public class RideAddCommand extends Command {

    public static final String COMMAND_WORD = "rideadd";
    public static final String COMMAND_ALIAS = "ra";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " (" + COMMAND_ALIAS + "): Adds a group ride. "
            + "The ride ID is assigned automatically.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_FROM + "FROM "
            + PREFIX_TO + "TO "
            + PREFIX_LOCATION + "LOCATION "
            + PREFIX_DISTANCE + "DISTANCE "
            + PREFIX_FTP + "MIN_POWER "
            + PREFIX_GROUP_SIZE + "MAX_GROUP_SIZE "
            + "[" + PREFIX_NOTES + "NOTES]\n"
            + "FROM and TO are written as DD-MM-YYYY TTTT (time on a 24-hour clock, 0000 to 2359), "
            + "and TO must not be earlier than FROM. DISTANCE is in kilometres, MIN_POWER is in watts "
            + "(1 to 600) and supplied NOTES must contain 1 to 10 words.\n"
            + "Unicode spaces separate words and are stored as single spaces; whitespace-only notes are rejected.\n"
            + "Notes may be unquoted. Use notes/\"Bring camera with aperture f/2\" to keep prefixes literal. "
            + "Quotes are removed; other parameters may precede or follow. Quoted notes must be non-blank; "
            + "embedded double quotes and escaping are unsupported.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Sunday Sunrise Ride "
            + PREFIX_FROM + "18-10-2026 0630 "
            + PREFIX_TO + "18-10-2026 0930 "
            + PREFIX_LOCATION + "East Coast "
            + PREFIX_DISTANCE + "45 "
            + PREFIX_FTP + "180 "
            + PREFIX_GROUP_SIZE + "12 "
            + PREFIX_NOTES + "Bring lights and a spare tube";

    public static final String MESSAGE_SUCCESS = "New ride added: %1$s";

    private final Ride toAdd;

    /**
     * Creates a RideAddCommand to add the specified {@code Ride}. The ride has no ID yet, because the model
     * assigns one when the ride is added.
     */
    public RideAddCommand(Ride ride) {
        requireNonNull(ride);
        toAdd = ride;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.getAddressBook().getNextRideId() == Integer.MAX_VALUE) {
            throw new CommandException(AddressBook.MESSAGE_RIDE_IDS_EXHAUSTED);
        }
        Ride addedRide = model.addRide(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.formatRide(addedRide)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RideAddCommand otherRideAddCommand)) {
            return false;
        }

        return toAdd.equals(otherRideAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
