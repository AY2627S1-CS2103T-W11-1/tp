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
 * The logic layer executes this command only after the user confirms the resolved cyclist.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a person in the displayed list identified by their phone number.\n"
            + "Enter y/yes to confirm or n/no to cancel, ignoring letter case.\n"
            + "Parameters: PHONE (including country code, for example +65 91234567)\n"
            + "Example: " + COMMAND_WORD + " +65 96742165";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_CONFIRM_DELETE = "Delete this cyclist? %1$s\n"
            + "This also removes them from all ride rosters.\n"
            + "Enter y/yes to confirm or n/no to cancel (case-insensitive).";
    public static final String MESSAGE_DELETE_CANCELLED = "Deletion cancelled.";
    public static final String MESSAGE_INVALID_CONFIRMATION =
            "Invalid confirmation. Enter y/yes to confirm or n/no to cancel.\nPending deletion: %1$s";
    public static final String MESSAGE_TARGET_CHANGED =
            "Deletion cancelled because the cyclist's details have changed. Enter delete PHONE again.";
    public static final String MESSAGE_PERSON_NOT_FOUND = Messages.MESSAGE_CYCLIST_NOT_FOUND;
    public static final String MESSAGE_AMBIGUOUS_PHONE = Messages.MESSAGE_AMBIGUOUS_PHONE;

    private final Phone targetPhone;

    /**
     * Creates a command targeting the displayed cyclist with the given phone number.
     */
    public DeleteCommand(Phone targetPhone) {
        this.targetPhone = targetPhone;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Person personToDelete = getPersonToDelete(model);
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    /**
     * Resolves the unique displayed cyclist without modifying the model.
     * The logic layer calls this before requesting confirmation and again before deletion.
     *
     * @throws CommandException If the phone matches no displayed cyclist or more than one cyclist.
     */
    public Person getPersonToDelete(Model model) throws CommandException {
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
        return matchingPersons.get(0);
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
