package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.util.StringUtil;
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
        String trimmedId = args.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedId)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RideListCommand.MESSAGE_USAGE));
        }
        return new RideListCommand(Integer.parseInt(trimmedId));
    }
}
