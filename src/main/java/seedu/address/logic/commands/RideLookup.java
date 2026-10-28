package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ride.Ride;

/**
 * Resolves ride IDs against all saved rides, regardless of the active filter.
 */
final class RideLookup {
    static final String MESSAGE_RIDE_NOT_FOUND = "Ride with ID %1$d cannot be found.";

    private RideLookup() {}

    static Ride getRide(Model model, int targetId) throws CommandException {
        return model.getAddressBook().getRideList().stream()
                .filter(ride -> ride.getId() == targetId)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_RIDE_NOT_FOUND, targetId)));
    }
}
