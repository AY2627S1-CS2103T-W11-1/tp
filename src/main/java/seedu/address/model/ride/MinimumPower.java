package seedu.address.model.ride;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.regex.Pattern;

/**
 * Represents the minimum power, in watts, that a cyclist needs to join a ride.
 * It uses the same range as a cyclist's FTP.
 * Guarantees: immutable; is valid as declared in {@link #isValidMinimumPower(String)}
 */
public class MinimumPower {

    public static final String MESSAGE_CONSTRAINTS =
            "Minimum power should be a whole number from 1 to 600 watts, using at most 3 digits. "
                    + "Leading zeroes count towards the digit limit.";

    private static final Pattern VALIDATION_PATTERN = Pattern.compile("\\d{1,3}");
    private static final int MIN_POWER = 1;
    private static final int MAX_POWER = 600;

    /** The power written without leading zeros. */
    public final String value;

    /**
     * Constructs a {@code MinimumPower}.
     *
     * @param minimumPower A valid minimum power in watts.
     */
    public MinimumPower(String minimumPower) {
        requireNonNull(minimumPower);
        String trimmed = minimumPower.trim();
        checkArgument(isValidMinimumPower(trimmed), MESSAGE_CONSTRAINTS);
        value = String.valueOf(Integer.parseInt(trimmed));
    }

    /**
     * Returns true if a given string is a whole number between 1 and 600.
     */
    public static boolean isValidMinimumPower(String test) {
        requireNonNull(test);
        if (!VALIDATION_PATTERN.matcher(test).matches()) {
            return false;
        }
        int power = Integer.parseInt(test);
        return power >= MIN_POWER && power <= MAX_POWER;
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
        if (!(other instanceof MinimumPower otherPower)) {
            return false;
        }

        return value.equals(otherPower.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
