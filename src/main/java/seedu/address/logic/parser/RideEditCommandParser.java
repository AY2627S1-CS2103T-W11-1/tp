
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

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.RideEditCommand;
import seedu.address.logic.commands.RideEditCommand.EditRideDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ride.Notes;

/**
 * Parses input arguments and creates a new {@code RideEditCommand}.
 */
public class RideEditCommandParser implements Parser<RideEditCommand> {

    /**
     * Parses {@code args} in the context of {@code RideEditCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public RideEditCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenizeWithQuotedNotes(args, PREFIX_NAME, PREFIX_FROM,
                PREFIX_TO, PREFIX_LOCATION, PREFIX_DISTANCE, PREFIX_FTP, PREFIX_GROUP_SIZE, PREFIX_NOTES);

        int targetId = parseRideId(argMultimap.getPreamble());
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_FROM, PREFIX_TO, PREFIX_LOCATION,
                PREFIX_DISTANCE, PREFIX_FTP, PREFIX_GROUP_SIZE, PREFIX_NOTES);

        EditRideDescriptor editRideDescriptor = parseEditedFields(argMultimap);

        if (!editRideDescriptor.isAnyFieldEdited()) {
            throw new ParseException(RideEditCommand.MESSAGE_NOT_EDITED);
        }

        return new RideEditCommand(targetId, editRideDescriptor);
    }

    /**
     * Returns a descriptor containing the ride fields supplied in {@code argMultimap}.
     * An empty notes value indicates that existing notes should be cleared.
     *
     * @throws ParseException if any supplied field contains an invalid value.
     */
    private static EditRideDescriptor parseEditedFields(ArgumentMultimap argMultimap) throws ParseException {
        EditRideDescriptor editRideDescriptor = new EditRideDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editRideDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_FROM).isPresent()) {
            editRideDescriptor.setFrom(ParserUtil.parseRideDateTime(argMultimap.getValue(PREFIX_FROM).get()));
        }
        if (argMultimap.getValue(PREFIX_TO).isPresent()) {
            editRideDescriptor.setTo(ParserUtil.parseRideDateTime(argMultimap.getValue(PREFIX_TO).get()));
        }
        if (argMultimap.getValue(PREFIX_LOCATION).isPresent()) {
            editRideDescriptor.setLocation(ParserUtil.parseLocation(argMultimap.getValue(PREFIX_LOCATION).get()));
        }
        if (argMultimap.getValue(PREFIX_DISTANCE).isPresent()) {
            editRideDescriptor.setDistance(ParserUtil.parseDistance(argMultimap.getValue(PREFIX_DISTANCE).get()));
        }
        if (argMultimap.getValue(PREFIX_FTP).isPresent()) {
            editRideDescriptor.setMinimumPower(
                    ParserUtil.parseMinimumPower(argMultimap.getValue(PREFIX_FTP).get()));
        }
        if (argMultimap.getValue(PREFIX_GROUP_SIZE).isPresent()) {
            editRideDescriptor.setMaxGroupSize(
                    ParserUtil.parseGroupSize(argMultimap.getValue(PREFIX_GROUP_SIZE).get()));
        }
        if (argMultimap.getValue(PREFIX_NOTES).isPresent()) {
            String notes = argMultimap.getValue(PREFIX_NOTES).get();
            editRideDescriptor.setNotes(Notes.isBlank(notes)
                    ? Optional.empty() : Optional.of(ParserUtil.parseNotes(notes)));
        }

        return editRideDescriptor;
    }

    /**
     * Parses a positive ride ID from the command preamble.
     *
     * @throws ParseException if the ride ID is missing or invalid.
     */
    private static int parseRideId(String preamble) throws ParseException {
        String trimmedId = preamble.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedId)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RideEditCommand.MESSAGE_USAGE));
        }
        return Integer.parseInt(trimmedId);
    }
}
