package seedu.address.model.ride;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * Represents the start or end date and time of a ride, written as {@code DD-MM-YYYY TTTT}
 * where {@code TTTT} is the time on a 24-hour clock from 0000 to 2359.
 * Guarantees: immutable; is valid as declared in {@link #isValidRideDateTime(String)}
 */
public class RideDateTime implements Comparable<RideDateTime> {

    public static final String MESSAGE_CONSTRAINTS =
            "Date and time should follow DD-MM-YYYY TTTT, where TTTT is the time on a 24-hour clock "
                    + "from 0000 to 2359 and the date is a real date, for example 18-10-2026 0630.";

    // The pattern check keeps the digit counts exact, because the formatter alone accepts wider years.
    private static final Pattern SHAPE = Pattern.compile("\\d{2}-\\d{2}-\\d{4} \\d{4}");
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

    public final LocalDateTime value;

    /**
     * Constructs a {@code RideDateTime}.
     *
     * @param dateTime A valid date and time in the format {@code DD-MM-YYYY TTTT}.
     */
    public RideDateTime(String dateTime) {
        requireNonNull(dateTime);
        String trimmed = dateTime.trim();
        checkArgument(isValidRideDateTime(trimmed), MESSAGE_CONSTRAINTS);
        value = LocalDateTime.parse(trimmed, FORMATTER);
    }

    /**
     * Returns true if a given string is a real date and time in the format {@code DD-MM-YYYY TTTT}.
     */
    public static boolean isValidRideDateTime(String test) {
        requireNonNull(test);
        if (!SHAPE.matcher(test).matches()) {
            return false;
        }
        try {
            LocalDateTime.parse(test, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if this date and time is strictly earlier than {@code other}.
     */
    public boolean isBefore(RideDateTime other) {
        requireNonNull(other);
        return value.isBefore(other.value);
    }

    @Override
    public int compareTo(RideDateTime other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.format(FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RideDateTime otherDateTime)) {
            return false;
        }

        return value.equals(otherDateTime.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
