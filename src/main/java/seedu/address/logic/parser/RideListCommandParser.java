package seedu.address.logic.parser;

import seedu.address.logic.commands.RideListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a ride ID and creates a {@code RideListCommand}.
 */
public class RideListCommandParser implements Parser<RideListCommand> {

    /**
     * Parses {@code args} as one positive ride ID.
     *
     * @throws ParseException if the input is not exactly one positive ride ID.
     */
    @Override
    public RideListCommand parse(String args) throws ParseException {
        return new RideListCommand(ParserUtil.parseRideId(args, RideListCommand.MESSAGE_USAGE));
    }
}
