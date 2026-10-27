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

    /**
     * Returns this location as display text, with the first letter of every word as a capital letter, for example
     * {@code Marina Bay} for {@code marina bay}. The rest of each word is left as it was entered, so that
     * an abbreviation such as {@code ECP} stays as it is. The special location {@code any} is shown as {@code Any}.
     * The stored value is not changed.
     */
    public String toDisplayText() {
        if (value.equalsIgnoreCase("any")) {
            return "Any";
        }
        StringBuilder displayText = new StringBuilder();
        boolean startOfWord = true;
        for (char character : value.toCharArray()) {
            displayText.append(startOfWord ? Character.toUpperCase(character) : character);
            startOfWord = character == ' ';
        }
        return displayText.toString();
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
