package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Lists the number of persons in the address book to the user.
 */
public class CountCommand extends Command {

    public static final String COMMAND_WORD = "count";

    public static final String MESSAGE_SUCCESS = "There are %d persons in the address book.";

    @Override
    public CommandResult execute(Model model) {
        int personCount = model.getAddressBook().getPersonList().size();
        return new CommandResult(String.format(MESSAGE_SUCCESS, personCount));
    }
}
