package seedu.address.model.ride;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.regex.Pattern;

/**
 * Represents the maximum number of cyclists that can join a ride.
 * Guarantees: immutable; is valid as declared in {@link #isValidGroupSize(String)}
 */
public class GroupSize {

    public static final String MESSAGE_CONSTRAINTS =
            "Maximum group size should be a positive whole number of up to 9 digits.";

    private static final Pattern VALIDATION_PATTERN = Pattern.compile("\\d{1,9}");

    /** The size written without leading zeros. */
    public final String value;

    /**
     * Constructs a {@code GroupSize}.
     *
     * @param groupSize A valid maximum group size.
     */
    public GroupSize(String groupSize) {
        requireNonNull(groupSize);
        String trimmed = groupSize.trim();
        checkArgument(isValidGroupSize(trimmed), MESSAGE_CONSTRAINTS);
        value = String.valueOf(Integer.parseInt(trimmed));
    }

    /**
     * Returns true if a given string is a whole number of at least 1.
     */
    public static boolean isValidGroupSize(String test) {
        requireNonNull(test);
        return VALIDATION_PATTERN.matcher(test).matches() && Integer.parseInt(test) >= 1;
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
        if (!(other instanceof GroupSize otherSize)) {
            return false;
        }

        return value.equals(otherSize.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
