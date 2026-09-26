package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/** Adds or replaces a person's remark. */
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a remark to a person.\n"
            + "Parameters: INDEX (positive integer) " + PREFIX_REMARK + "REMARK\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_REMARK + "Likes baseball";
    public static final String MESSAGE_SUCCESS = "Added remark to Person: %1$s";

    private final Index index;
    private final Remark remark;

    public RemarkCommand(Index index, Remark remark) {
        requireNonNull(index);
        requireNonNull(remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person person = persons.get(index.getZeroBased());
        Person edited = new Person(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), remark, person.getTags());
        model.setPerson(person, edited);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(edited)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof RemarkCommand command
                && index.equals(command.index) && remark.equals(command.remark));
    }
}
