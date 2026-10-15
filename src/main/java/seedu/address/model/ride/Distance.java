package seedu.address.model.ride;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Represents the distance of a ride in kilometres.
 * Guarantees: immutable; is valid as declared in {@link #isValidDistance(String)}
 */
public class Distance {

    public static final String MESSAGE_CONSTRAINTS =
            "Distance should be a positive number of kilometres with at most 2 decimal places, "
                    + "for example 45 or 45.5.";

    private static final Pattern VALIDATION_PATTERN = Pattern.compile("\\d{1,6}(\\.\\d{1,2})?");

    /** The distance written in its simplest form, so that 45, 045 and 45.0 are all stored as 45. */
    public final String value;

    /**
     * Constructs a {@code Distance}.
     *
     * @param distance A valid distance in kilometres.
     */
    public Distance(String distance) {
        requireNonNull(distance);
        String trimmed = distance.trim();
        checkArgument(isValidDistance(trimmed), MESSAGE_CONSTRAINTS);
        value = new BigDecimal(trimmed).stripTrailingZeros().toPlainString();
    }

    /**
     * Returns true if a given string is a positive number with at most 2 decimal places.
     */
    public static boolean isValidDistance(String test) {
        requireNonNull(test);
        return VALIDATION_PATTERN.matcher(test).matches() && new BigDecimal(test).signum() > 0;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Distance otherDistance)) {
            return false;
        }

        return value.equals(otherDistance.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
