package seedu.address.model.ride;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the optional notes of a ride. Notes contain at most 10 words.
 * Guarantees: immutable; is valid as declared in {@link #isValidNotes(String)}
 */
public class Notes {

    public static final int MAX_WORDS = 10;
    public static final String MESSAGE_CONSTRAINTS =
            "Notes should not be blank and should contain at most " + MAX_WORDS + " words.";

    /** The notes with Unicode whitespace trimmed and collapsed into single ASCII spaces. */
    public final String value;

    /**
     * Constructs a {@code Notes}.
     *
     * @param notes Valid notes of at most 10 words.
     */
    public Notes(String notes) {
        requireNonNull(notes);
        checkArgument(isValidNotes(notes), MESSAGE_CONSTRAINTS);
        value = normalizeWhitespace(notes);
    }

    /**
     * Returns true if a given string has at least one word and at most {@value #MAX_WORDS} words.
     */
    public static boolean isValidNotes(String test) {
        requireNonNull(test);
        String normalized = normalizeWhitespace(test);
        return !normalized.isEmpty() && normalized.split(" ").length <= MAX_WORDS;
    }

    /**
     * Returns true if the notes contain only Unicode whitespace or space characters, or are empty.
     */
    public static boolean isBlank(String notes) {
        requireNonNull(notes);
        return notes.codePoints().allMatch(Notes::isNotesWhitespace);
    }

    /**
     * Trims and collapses Unicode whitespace runs to single ASCII spaces, preserving all other code points.
     */
    private static String normalizeWhitespace(String notes) {
        StringBuilder normalized = new StringBuilder();
        boolean hasSeparator = false;
        for (int offset = 0; offset < notes.length();) {
            int codePoint = notes.codePointAt(offset);
            offset += Character.charCount(codePoint);
            if (isNotesWhitespace(codePoint)) {
                hasSeparator = normalized.length() > 0;
            } else {
                if (hasSeparator) {
                    normalized.append(' ');
                    hasSeparator = false;
                }
                normalized.appendCodePoint(codePoint);
            }
        }
        return normalized.toString();
    }

    /**
     * Returns whether a code point separates words in notes.
     */
    private static boolean isNotesWhitespace(int codePoint) {
        return Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint);
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
