package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a Cyclist's experience level in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidExperienceLevel(String)}
 */
public class ExperienceLevel {

    public static final String MESSAGE_CONSTRAINTS =
            "Experience level should be one of: Beginner, Intermediate, Advanced (case-insensitive)";

    /** The experience levels Cyclique accepts, in their canonical (correctly-capitalised) form. */
    private static final List<String> VALID_LEVELS = Arrays.asList("Beginner", "Intermediate", "Advanced");

    public final String value;

    /**
     * Constructs an {@code ExperienceLevel}.
     *
     * @param experienceLevel A valid experience level.
     */
    public ExperienceLevel(String experienceLevel) {
        requireNonNull(experienceLevel);
        String trimmed = experienceLevel.trim();
        checkArgument(isValidExperienceLevel(trimmed), MESSAGE_CONSTRAINTS);
        value = VALID_LEVELS.stream()
                .filter(level -> level.equalsIgnoreCase(trimmed))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Returns true if {@code test} matches one of the valid experience levels, ignoring case.
     */
    public static boolean isValidExperienceLevel(String test) {
        requireNonNull(test);
        return VALID_LEVELS.stream().anyMatch(level -> level.equalsIgnoreCase(test));
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
        if (!(other instanceof ExperienceLevel otherLevel)) {
            return false;
        }

        return value.equals(otherLevel.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
