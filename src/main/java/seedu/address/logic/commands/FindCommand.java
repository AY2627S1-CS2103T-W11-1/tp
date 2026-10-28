package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesFindCriteriaPredicate;

/**
 * Finds and lists persons matching the supplied cyclist criteria.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds cyclists matching the supplied criteria.\n"
            + "Parameters: [n/NAME]... [p/PHONE]... [sos/EMERGENCY_PHONE]... [f/MIN_FTP] "
            + "[x/EXPERIENCE]... [l/LOCATION]...\n"
            + "At least one search criterion is required. Names and text criteria match complete words "
            + "case-insensitively. Multi-word values match when all words are present in order. "
            + "Use multiple values within a field for OR matching; different fields use AND matching. "
            + "f/MIN_FTP matches cyclists with FTP at least the specified value in watts.\n"
            + "Example: " + COMMAND_WORD + " l/Punggol x/Intermediate f/250";

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);
    private final PersonMatchesFindCriteriaPredicate predicate;

    /**
     * Creates a command that displays cyclists matching the supplied criteria.
     */
    public FindCommand(PersonMatchesFindCriteriaPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int matchingPersons = model.getFilteredPersonList().size();
        logger.fine("Find command matched " + matchingPersons + " person(s).");
        return new CommandResult(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, matchingPersons));
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
