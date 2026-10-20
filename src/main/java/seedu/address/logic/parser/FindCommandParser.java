package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.PersonMatchesFindCriteriaPredicate;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_NAME, PREFIX_PHONE,
                PREFIX_EMERGENCY_PHONE, PREFIX_FTP, PREFIX_EXPERIENCE_LEVEL, PREFIX_LOCATION);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_FTP);
        List<String> nameKeywords = argMultimap.getAllValues(PREFIX_NAME);
        List<String> phoneValues = argMultimap.getAllValues(PREFIX_PHONE);
        List<String> emergencyPhoneValues = argMultimap.getAllValues(PREFIX_EMERGENCY_PHONE);
        List<String> locationValues = argMultimap.getAllValues(PREFIX_LOCATION);
        if (!argMultimap.getPreamble().isEmpty()
                || (nameKeywords.isEmpty() && phoneValues.isEmpty() && emergencyPhoneValues.isEmpty()
                && locationValues.isEmpty() && argMultimap.getValue(PREFIX_FTP).isEmpty()
                && argMultimap.getValue(PREFIX_EXPERIENCE_LEVEL).isEmpty())
                || nameKeywords.stream().anyMatch(String::isEmpty)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        for (String keyword : nameKeywords) {
            ParserUtil.parseName(keyword);
        }
        List<Phone> phones = new ArrayList<>();
        for (String value : phoneValues) {
            phones.add(ParserUtil.parsePhone(value));
        }
        List<Phone> emergencyPhones = new ArrayList<>();
        for (String value : emergencyPhoneValues) {
            emergencyPhones.add(ParserUtil.parsePhone(value));
        }
        Ftp minimumFtp = argMultimap.getValue(PREFIX_FTP).isPresent()
                ? ParserUtil.parseFtp(argMultimap.getValue(PREFIX_FTP).get()) : null;
        List<ExperienceLevel> experienceLevels = new ArrayList<>();
        for (String value : argMultimap.getAllValues(PREFIX_EXPERIENCE_LEVEL)) {
            experienceLevels.add(ParserUtil.parseExperienceLevel(value));
        }
        Set<Location> locations = new HashSet<>();
        for (String locationValue : locationValues) {
            locations.addAll(ParserUtil.parseLocations(List.of(locationValue)));
        }
        return new FindCommand(new PersonMatchesFindCriteriaPredicate(nameKeywords, phones, emergencyPhones,
                experienceLevels, locations, minimumFtp));
    }

}
