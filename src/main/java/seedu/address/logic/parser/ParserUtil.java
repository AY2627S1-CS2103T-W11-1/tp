package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.RideDateTime;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!trimmedPhone.startsWith("+")) {
            throw new ParseException(Phone.MESSAGE_MISSING_COUNTRY_CODE);
        }
        if (!trimmedPhone.matches("[+0-9 ]+")) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        if (trimmedPhone.indexOf(' ') < 0) {
            throw new ParseException(Phone.MESSAGE_MISSING_SEPARATOR);
        }
        if (trimmedPhone.indexOf(' ') != trimmedPhone.lastIndexOf(' ')) {
            throw new ParseException(Phone.MESSAGE_MULTIPLE_SEPARATORS);
        }
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String} as an {@code Ftp}.
     */
    public static Ftp parseFtp(String ftp) throws ParseException {
        String trimmedFtp = ftp.trim();
        if (trimmedFtp.isEmpty()) {
            throw new ParseException("Please include a number for FTP. " + Ftp.MESSAGE_CONSTRAINTS);
        }
        if (!Ftp.isValidFtp(trimmedFtp)) {
            throw new ParseException(Ftp.MESSAGE_CONSTRAINTS);
        }
        return new Ftp(trimmedFtp);
    }

    /** Parses location values and removes case-insensitive duplicates. */
    public static Set<Location> parseLocations(List<String> locations) throws ParseException {
        Set<Location> parsedLocations = new HashSet<>();
        boolean hasAnyLocation = false;
        for (String location : locations) {
            String trimmedLocation = location.trim();
            if (!Location.isValidLocation(trimmedLocation)) {
                throw new ParseException(Location.MESSAGE_CONSTRAINTS);
            }
            if (trimmedLocation.equalsIgnoreCase("any")) {
                hasAnyLocation = true;
            }
            parsedLocations.add(new Location(trimmedLocation));
        }
        if (hasAnyLocation && parsedLocations.size() > 1) {
            throw new ParseException("Location 'any' cannot be combined with other locations.");
        }
        if (parsedLocations.isEmpty()) {
            parsedLocations.add(new Location("any"));
        }
        assert !parsedLocations.isEmpty() : "Parsed locations must contain at least one location.";
        return parsedLocations;
    }

    /**
     * Parses a {@code String location} into a single {@code Location}, as used by a ride.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code location} is invalid.
     */
    public static Location parseLocation(String location) throws ParseException {
        requireNonNull(location);
        String trimmedLocation = location.trim();
        if (!Location.isValidLocation(trimmedLocation)) {
            throw new ParseException(Location.MESSAGE_CONSTRAINTS);
        }
        return new Location(trimmedLocation);
    }

    /**
     * Parses a {@code String dateTime} written as {@code DD-MM-YYYY TTTT} into a {@code RideDateTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code dateTime} is invalid.
     */
    public static RideDateTime parseRideDateTime(String dateTime) throws ParseException {
        requireNonNull(dateTime);
        String trimmedDateTime = dateTime.trim();
        if (!RideDateTime.isValidRideDateTime(trimmedDateTime)) {
            throw new ParseException(RideDateTime.MESSAGE_CONSTRAINTS);
        }
        return new RideDateTime(trimmedDateTime);
    }

    /**
     * Parses a {@code String distance} into a {@code Distance}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code distance} is invalid.
     */
    public static Distance parseDistance(String distance) throws ParseException {
        requireNonNull(distance);
        String trimmedDistance = distance.trim();
        if (!Distance.isValidDistance(trimmedDistance)) {
            throw new ParseException(Distance.MESSAGE_CONSTRAINTS);
        }
        return new Distance(trimmedDistance);
    }

    /**
     * Parses a {@code String minimumPower} into a {@code MinimumPower}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code minimumPower} is invalid.
     */
    public static MinimumPower parseMinimumPower(String minimumPower) throws ParseException {
        requireNonNull(minimumPower);
        String trimmedMinimumPower = minimumPower.trim();
        if (!MinimumPower.isValidMinimumPower(trimmedMinimumPower)) {
            throw new ParseException(MinimumPower.MESSAGE_CONSTRAINTS);
        }
        return new MinimumPower(trimmedMinimumPower);
    }

    /**
     * Parses a {@code String groupSize} into a {@code GroupSize}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code groupSize} is invalid.
     */
    public static GroupSize parseGroupSize(String groupSize) throws ParseException {
        requireNonNull(groupSize);
        String trimmedGroupSize = groupSize.trim();
        if (!GroupSize.isValidGroupSize(trimmedGroupSize)) {
            throw new ParseException(GroupSize.MESSAGE_CONSTRAINTS);
        }
        return new GroupSize(trimmedGroupSize);
    }

    /**
     * Parses a {@code String notes} into a {@code Notes}.
     * Unicode whitespace is trimmed and collapsed according to {@link Notes}.
     *
     * @throws ParseException if the given {@code notes} is invalid.
     */
    public static Notes parseNotes(String notes) throws ParseException {
        requireNonNull(notes);
        if (!Notes.isValidNotes(notes)) {
            throw new ParseException(Notes.MESSAGE_CONSTRAINTS);
        }
        return new Notes(notes);
    }

    /**
     * Parses a {@code String experienceLevel} into an {@code ExperienceLevel}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code experienceLevel} is invalid.
     */
    public static ExperienceLevel parseExperienceLevel(String experienceLevel) throws ParseException {
        requireNonNull(experienceLevel);
        String trimmedExperienceLevel = experienceLevel.trim();
        if (!ExperienceLevel.isValidExperienceLevel(trimmedExperienceLevel)) {
            throw new ParseException(ExperienceLevel.MESSAGE_CONSTRAINTS);
        }
        return new ExperienceLevel(trimmedExperienceLevel);
    }

}
