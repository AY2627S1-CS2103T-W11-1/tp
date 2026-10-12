package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Deletes a displayed person identified using its phone number from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a person in the displayed list identified by their phone number.\n"
            + "Parameters: PHONE (must be at least 3 digits)\n"
            + "Example: " + COMMAND_WORD + " +65 96742165";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_PERSON_NOT_FOUND = Messages.MESSAGE_CYCLIST_NOT_FOUND;
    public static final String MESSAGE_AMBIGUOUS_PHONE = Messages.MESSAGE_AMBIGUOUS_PHONE;

    private final Phone targetPhone;

    public DeleteCommand(Phone targetPhone) {
        this.targetPhone = targetPhone;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> persons = model.getFilteredPersonList();
        List<Person> matchingPersons = persons.stream()
                .filter(person -> person.getPhone().equals(targetPhone))
                .toList();
        if (matchingPersons.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_PERSON_NOT_FOUND, targetPhone));
        }
        if (matchingPersons.size() > 1) {
            throw new CommandException(MESSAGE_AMBIGUOUS_PHONE);
        }
        Person personToDelete = matchingPersons.get(0);
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetPhone.equals(otherDeleteCommand.targetPhone);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetPhone", targetPhone)
                .toString();
    }
}
