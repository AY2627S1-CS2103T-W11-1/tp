package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
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
import java.util.regex.Pattern;

import seedu.address.logic.commands.RideFindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;
import seedu.address.model.ride.RideMatchesFindCriteriaPredicate;

/** Parses arguments for {@code ridefind}. */
public class RideFindCommandParser implements Parser<RideFindCommand> {
    private static final Pattern DATE_ONLY = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");

    @Override
    public RideFindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.trim().equals("all")) {
            return new RideFindCommand(new RideMatchesFindCriteriaPredicate());
        }

        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_NAME, PREFIX_FROM, PREFIX_TO,
                PREFIX_LOCATION, PREFIX_DISTANCE, PREFIX_FTP, PREFIX_GROUP_SIZE, PREFIX_NOTES);
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_FROM, PREFIX_TO, PREFIX_LOCATION,
                PREFIX_DISTANCE, PREFIX_FTP, PREFIX_GROUP_SIZE, PREFIX_NOTES);
        if (!arguments.getPreamble().isEmpty() || !hasCriteria(arguments)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RideFindCommand.MESSAGE_USAGE));
        }

        Optional<Name> name = parseName(arguments.getValue(PREFIX_NAME));
        Optional<RideDateTime> from = parseBoundary(arguments.getValue(PREFIX_FROM), false);
        Optional<RideDateTime> to = parseBoundary(arguments.getValue(PREFIX_TO), true);
        Optional<Location> location = parseLocation(arguments.getValue(PREFIX_LOCATION));
        Optional<Distance> distance = parseDistance(arguments.getValue(PREFIX_DISTANCE));
        Optional<MinimumPower> minimumPower = parseMinimumPower(arguments.getValue(PREFIX_FTP));
        Optional<GroupSize> groupSize = parseGroupSize(arguments.getValue(PREFIX_GROUP_SIZE));
        Optional<Notes> notes = parseNotes(arguments.getValue(PREFIX_NOTES));

        if (from.isPresent() && to.isPresent() && !Ride.isValidPeriod(from.get(), to.get())) {
            throw new ParseException(Ride.MESSAGE_INVALID_PERIOD);
        }
        return new RideFindCommand(new RideMatchesFindCriteriaPredicate(name, from, to, location, distance,
                minimumPower, groupSize, notes));
    }

    private boolean hasCriteria(ArgumentMultimap arguments) {
        return arguments.getValue(PREFIX_NAME).isPresent() || arguments.getValue(PREFIX_FROM).isPresent()
                || arguments.getValue(PREFIX_TO).isPresent() || arguments.getValue(PREFIX_LOCATION).isPresent()
                || arguments.getValue(PREFIX_DISTANCE).isPresent() || arguments.getValue(PREFIX_FTP).isPresent()
                || arguments.getValue(PREFIX_GROUP_SIZE).isPresent() || arguments.getValue(PREFIX_NOTES).isPresent();
    }

    private Optional<RideDateTime> parseBoundary(Optional<String> value, boolean isEnd) throws ParseException {
        if (value.isEmpty()) {
            return Optional.empty();
        }
        String raw = value.get().trim();
        if (DATE_ONLY.matcher(raw).matches()) {
            raw += isEnd ? " 2359" : " 0000";
        }
        return Optional.of(ParserUtil.parseRideDateTime(raw));
    }

    private Optional<Name> parseName(Optional<String> value) throws ParseException {
        return value.isEmpty() ? Optional.empty() : Optional.of(ParserUtil.parseName(value.get()));
    }

    private Optional<Location> parseLocation(Optional<String> value) throws ParseException {
        return value.isEmpty() ? Optional.empty() : Optional.of(ParserUtil.parseLocation(value.get()));
    }

    private Optional<Distance> parseDistance(Optional<String> value) throws ParseException {
        return value.isEmpty() ? Optional.empty() : Optional.of(ParserUtil.parseDistance(value.get()));
    }

    private Optional<MinimumPower> parseMinimumPower(Optional<String> value) throws ParseException {
        return value.isEmpty() ? Optional.empty() : Optional.of(ParserUtil.parseMinimumPower(value.get()));
    }

    private Optional<GroupSize> parseGroupSize(Optional<String> value) throws ParseException {
        return value.isEmpty() ? Optional.empty() : Optional.of(ParserUtil.parseGroupSize(value.get()));
    }

    private Optional<Notes> parseNotes(Optional<String> value) throws ParseException {
        return value.isEmpty() ? Optional.empty() : Optional.of(ParserUtil.parseNotes(value.get()));
    }
}
