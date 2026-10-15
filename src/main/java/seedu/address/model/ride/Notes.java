package seedu.address.model.ride;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;

/**
 * Represents the optional notes of a ride. Notes contain at most 10 words.
 * Guarantees: immutable; is valid as declared in {@link #isValidNotes(String)}
 */
public class Notes {

    public static final int MAX_WORDS = 10;
    public static final String MESSAGE_CONSTRAINTS =
            "Notes should not be blank and should contain at most " + MAX_WORDS + " words.";

    /** The notes with repeated spaces collapsed into single spaces. */
    public final String value;

    /**
     * Constructs a {@code Notes}.
     *
     * @param notes Valid notes of at most 10 words.
     */
    public Notes(String notes) {
        requireNonNull(notes);
        checkArgument(isValidNotes(notes), MESSAGE_CONSTRAINTS);
        value = String.join(" ", notes.trim().split("\\s+"));
    }

    /**
     * Returns true if a given string has at least one word and at most {@value #MAX_WORDS} words.
     */
    public static boolean isValidNotes(String test) {
        requireNonNull(test);
        String trimmed = test.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        return Arrays.stream(trimmed.split("\\s+")).count() <= MAX_WORDS;
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
        if (!(other instanceof Notes otherNotes)) {
            return false;
        }

        return value.equals(otherNotes.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
