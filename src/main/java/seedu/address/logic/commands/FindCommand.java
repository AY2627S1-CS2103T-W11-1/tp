package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesFindCriteriaPredicate;

/**
 * Finds and lists persons matching the supplied name and phone criteria.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds persons by name or phone. "
            + "Name keywords match full words, ignoring case. Repeated values within a field match any value; "
            + "when both fields are given, both must match.\n"
            + "Parameters: [n/NAME_KEYWORD]... [p/PHONE]... (at least one required)\n"
            + "Example: " + COMMAND_WORD + " n/alice p/91234567";

    private final PersonMatchesFindCriteriaPredicate predicate;

    public FindCommand(PersonMatchesFindCriteriaPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
