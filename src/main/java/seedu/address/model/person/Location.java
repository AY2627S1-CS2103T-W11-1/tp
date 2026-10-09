package seedu.address.model.person;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/** Represents a preferred cycling location. */
public class Location {
    public static final String MESSAGE_CONSTRAINTS =
            "Location should not be blank and should contain only letters, numbers, spaces, hyphens or apostrophes.";
    private static final Pattern VALIDATION_PATTERN = Pattern.compile("[A-Za-z0-9 '-]+");
    public final String value;

    /** Constructs a location after validating and trimming it. */
    public Location(String value) {
        String trimmedValue = value == null ? "" : value.trim();
        if (!isValidLocation(trimmedValue)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.value = trimmedValue;
    }

    /** Returns true if the given value is a valid location. */
    public static boolean isValidLocation(String value) {
        return value != null && !value.trim().isEmpty() && VALIDATION_PATTERN.matcher(value.trim()).matches();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Location otherLocation
                && value.toLowerCase(Locale.ROOT).equals(otherLocation.value.toLowerCase(Locale.ROOT));
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return value;
    }
}
