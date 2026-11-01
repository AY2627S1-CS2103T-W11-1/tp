package seedu.address.logic.parser;

import seedu.address.logic.commands.RideDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code RideDeleteCommand} object.
 */
public class RideDeleteCommandParser implements Parser<RideDeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of {@code RideDeleteCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public RideDeleteCommand parse(String args) throws ParseException {
        return new RideDeleteCommand(ParserUtil.parseRideId(args, RideDeleteCommand.MESSAGE_USAGE));
    }
}
