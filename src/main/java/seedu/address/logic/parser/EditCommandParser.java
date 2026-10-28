package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        // Prevent the retired prefix from being absorbed into a supported field value.
        if (args.matches("(?s).*(?:^|\\s)t/.*")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMERGENCY_PHONE,
                        PREFIX_EMAIL, PREFIX_FTP, PREFIX_EXPERIENCE_LEVEL, PREFIX_LOCATION);

        if (argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        Phone targetPhone = ParserUtil.parsePhone(argMultimap.getPreamble());

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMERGENCY_PHONE,
                PREFIX_EMAIL, PREFIX_FTP, PREFIX_EXPERIENCE_LEVEL);

        EditPersonDescriptor editPersonDescriptor = parseEditedFields(argMultimap);
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(targetPhone, editPersonDescriptor);
    }

    /**
     * Parses only supplied fields so omitted fields retain their existing values during execution.
     */
    private EditPersonDescriptor parseEditedFields(ArgumentMultimap argMultimap) throws ParseException {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMERGENCY_PHONE).isPresent()) {
            editPersonDescriptor.setEmergencyPhone(
                    ParserUtil.parsePhone(argMultimap.getValue(PREFIX_EMERGENCY_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_FTP).isPresent()) {
            editPersonDescriptor.setFtp(ParserUtil.parseFtp(argMultimap.getValue(PREFIX_FTP).get()));
        }
        if (argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).isPresent()) {
            editPersonDescriptor.setExperienceLevel(
                    ParserUtil.parseExperienceLevel(argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).get()));
        }
        if (!argMultimap.getAllValues(PREFIX_LOCATION).isEmpty()) {
            editPersonDescriptor.setLocations(ParserUtil.parseLocations(argMultimap.getAllValues(PREFIX_LOCATION)));
        }

        return editPersonDescriptor;
    }

}
