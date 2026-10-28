package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book. Every cyclist and ride is removed, but ride IDs are never reused, so the
 * next ride added still receives the next unused ID.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes every cyclist and every ride. "
            + "Takes no parameters and does not ask for confirmation.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "All cyclists and rides have been cleared. "
            + "Ride IDs are never reused, so the next ride you add will be ride %1$d.";

    /** Used instead of {@link #MESSAGE_SUCCESS} when there is no next ride ID to promise. */
    public static final String MESSAGE_SUCCESS_RIDE_IDS_EXHAUSTED = "All cyclists and rides have been cleared. "
            + AddressBook.MESSAGE_RIDE_IDS_EXHAUSTED + " New rides cannot be added.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        int nextRideId = model.getAddressBook().getNextRideId();
        AddressBook clearedAddressBook = new AddressBook();
        clearedAddressBook.setRides(List.of(), nextRideId);
        model.setAddressBook(clearedAddressBook);
        if (nextRideId == Integer.MAX_VALUE) {
            // the same condition under which rideadd rejects every new ride
            return new CommandResult(MESSAGE_SUCCESS_RIDE_IDS_EXHAUSTED);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, nextRideId));
    }
}
