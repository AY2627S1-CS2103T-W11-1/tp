package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.ride.RideMatchesFindCriteriaPredicate;

/** Finds rides matching at least one supplied criterion. */
public class RideFindCommand extends Command {
    public static final String COMMAND_WORD = "ridefind";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds rides matching the supplied criteria.\n"
            + "Parameters: [n/NAME] [from/DATE [TIME]] [to/DATE [TIME]] [l/LOCATION] [d/DISTANCE] "
            + "[f/MINIMUM_POWER] [s/MAX_GROUP_SIZE] [notes/NOTES]\n"
            + "At least one criterion is required. Date-only from/ and to/ values mean 0000 and 2359. "
            + "When both are supplied, to/ must not be earlier than from/.";
    public static final String MESSAGE_SUCCESS = "%1$d ride(s) listed!";

    private final RideMatchesFindCriteriaPredicate predicate;

    /** Creates a command with the supplied ride predicate. */
    public RideFindCommand(RideMatchesFindCriteriaPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredRideList(predicate);
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredRideList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RideFindCommand command && predicate.equals(command.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
