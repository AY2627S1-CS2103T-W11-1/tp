package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DISTANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_SIZE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.RideAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;

/**
 * Parses input arguments and creates a new RideAddCommand object
 */
public class RideAddCommandParser implements Parser<RideAddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the RideAddCommand
     * and returns a RideAddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public RideAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_FROM, PREFIX_TO,
                PREFIX_LOCATION, PREFIX_DISTANCE, PREFIX_FTP, PREFIX_GROUP_SIZE, PREFIX_NOTES);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_FROM, PREFIX_TO, PREFIX_LOCATION,
                PREFIX_DISTANCE, PREFIX_FTP, PREFIX_GROUP_SIZE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RideAddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_FROM, PREFIX_TO, PREFIX_LOCATION,
                PREFIX_DISTANCE, PREFIX_FTP, PREFIX_GROUP_SIZE, PREFIX_NOTES);

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        RideDateTime from = ParserUtil.parseRideDateTime(argMultimap.getValue(PREFIX_FROM).get());
        RideDateTime to = ParserUtil.parseRideDateTime(argMultimap.getValue(PREFIX_TO).get());
        Location location = ParserUtil.parseLocation(argMultimap.getValue(PREFIX_LOCATION).get());
        Distance distance = ParserUtil.parseDistance(argMultimap.getValue(PREFIX_DISTANCE).get());
        MinimumPower minimumPower = ParserUtil.parseMinimumPower(argMultimap.getValue(PREFIX_FTP).get());
        GroupSize maxGroupSize = ParserUtil.parseGroupSize(argMultimap.getValue(PREFIX_GROUP_SIZE).get());

        Optional<Notes> notes = Optional.empty();
        if (argMultimap.getValue(PREFIX_NOTES).isPresent()) {
            notes = Optional.of(ParserUtil.parseNotes(argMultimap.getValue(PREFIX_NOTES).get()));
        }

        if (!Ride.isValidPeriod(from, to)) {
            throw new ParseException(Ride.MESSAGE_INVALID_PERIOD);
        }

        Ride ride = new Ride(name, from, to, location, distance, minimumPower, maxGroupSize, notes);
        return new RideAddCommand(ride);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
