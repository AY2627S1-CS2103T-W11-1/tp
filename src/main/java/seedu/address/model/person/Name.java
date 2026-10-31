package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain Unicode letters, digits, combining marks, spaces, hyphens (-) "
            + "and apostrophes ('). Names should start with a letter or digit. "
            + "Combining marks must follow a letter or digit. Hyphens and apostrophes must join letters or digits.";

    /*
     * Start with a Unicode letter or digit so blank names and leading spaces are invalid.
     * Allow combining marks only after letters or digits, including other marks in the same sequence.
     * Allow a hyphen or apostrophe only between letter/digit sequences, including their combining marks.
     * The preceding sequence is already valid, so punctuation only needs to exclude a preceding space.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{Nd}]\\p{M}*(?:[\\p{L}\\p{Nd}]\\p{M}*| "
            + "|(?<! )[-'](?=[\\p{L}\\p{Nd}]))*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
