package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.RideRosterCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the ride ID and phone number shared by rideaddcyclist and ridedeletecyclist.
 */
public class RideRosterCommandParser implements Parser<RideRosterCommand> {
    private final boolean isJoining;

    /**
     * Creates a parser for joining a ride when {@code isJoining} is true, or leaving otherwise.
     */
    public RideRosterCommandParser(boolean isJoining) {
        this.isJoining = isJoining;
    }

    @Override
    public RideRosterCommand parse(String args) throws ParseException {
        String[] arguments = args.trim().split("\\s+", 2);
        if (arguments.length != 2) {
            String usage = isJoining ? RideRosterCommand.MESSAGE_JOIN_USAGE : RideRosterCommand.MESSAGE_LEAVE_USAGE;
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        String id = arguments[0];
        try {
            if (!id.matches("[1-9][0-9]*")) {
                throw new NumberFormatException();
            }
            return new RideRosterCommand(Integer.parseInt(id),
                    ParserUtil.parsePhone(arguments[1]), isJoining);
        } catch (NumberFormatException exception) {
            throw new ParseException("Ride ID should be a positive whole number no larger than 2147483647.");
        }
    }
}
