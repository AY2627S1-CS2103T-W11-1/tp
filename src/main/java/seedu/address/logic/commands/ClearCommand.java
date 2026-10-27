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

    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        AddressBook clearedAddressBook = new AddressBook();
        clearedAddressBook.setRides(List.of(), model.getAddressBook().getNextRideId());
        model.setAddressBook(clearedAddressBook);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
