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
import java.util.stream.Stream;

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
        validateCriteria(argMultimap);

        List<String> nameKeywords = parseNameKeywords(argMultimap.getAllValues(PREFIX_NAME));
        List<Phone> phones = parsePhones(argMultimap.getAllValues(PREFIX_PHONE));
        List<Phone> emergencyPhones = parsePhones(argMultimap.getAllValues(PREFIX_EMERGENCY_PHONE));
        Ftp minimumFtp = argMultimap.getValue(PREFIX_FTP).isPresent()
                ? ParserUtil.parseFtp(argMultimap.getValue(PREFIX_FTP).get()) : null;
        List<ExperienceLevel> experienceLevels = parseExperienceLevels(
                argMultimap.getAllValues(PREFIX_EXPERIENCE_LEVEL));
        Set<Location> locations = parseLocationCriteria(argMultimap.getAllValues(PREFIX_LOCATION));
        return new FindCommand(new PersonMatchesFindCriteriaPredicate(nameKeywords, phones, emergencyPhones,
                experienceLevels, locations, minimumFtp));
    }

    /**
     * Rejects unexpected preambles, missing criteria, empty names, and repeated minimum FTP values.
     */
    private void validateCriteria(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_FTP);
        List<String> nameKeywords = argMultimap.getAllValues(PREFIX_NAME);
        boolean hasCriteria = Stream.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMERGENCY_PHONE,
                PREFIX_LOCATION, PREFIX_FTP, PREFIX_EXPERIENCE_LEVEL)
                .anyMatch(prefix -> argMultimap.getValue(prefix).isPresent());
        if (!argMultimap.getPreamble().isEmpty()
                || !hasCriteria
                || nameKeywords.stream().anyMatch(String::isEmpty)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Validates name criteria without changing their original spelling or word order.
     */
    private List<String> parseNameKeywords(List<String> keywords) throws ParseException {
        for (String keyword : keywords) {
            ParserUtil.parseName(keyword);
        }
        return keywords;
    }

    /**
     * Parses repeated phone criteria while retaining their input order.
     */
    private List<Phone> parsePhones(List<String> phoneValues) throws ParseException {
        List<Phone> phones = new ArrayList<>();
        for (String value : phoneValues) {
            phones.add(ParserUtil.parsePhone(value));
        }
        return phones;
    }

    /**
     * Parses the experience levels that may independently satisfy the experience criterion.
     */
    private List<ExperienceLevel> parseExperienceLevels(List<String> values) throws ParseException {
        List<ExperienceLevel> experienceLevels = new ArrayList<>();
        for (String value : values) {
            experienceLevels.add(ParserUtil.parseExperienceLevel(value));
        }
        return experienceLevels;
    }

    /**
     * Parses each location independently so searches can combine 'any' with specific locations.
     * An absent criterion stays empty rather than defaulting to 'any'.
     */
    private Set<Location> parseLocationCriteria(List<String> locationValues) throws ParseException {
        Set<Location> locations = new HashSet<>();
        for (String locationValue : locationValues) {
            locations.addAll(ParserUtil.parseLocations(List.of(locationValue)));
        }
        return locations;
    }

}
