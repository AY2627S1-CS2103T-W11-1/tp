package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Invalid phone number. Example of an accepted format: +65 91234567. "
                    + "Parentheses and hyphens are not allowed.";
    public static final String MESSAGE_MISSING_COUNTRY_CODE =
            "Phone numbers must start with a '+' followed by the country code, for example '+65 91234567'.";
    public static final String MESSAGE_MISSING_SEPARATOR =
            "Phone numbers must contain one space between the country code and the number.";
    public static final String MESSAGE_MULTIPLE_SEPARATORS =
            "Phone numbers must contain exactly one space between the country code and the number.";
    public static final String VALIDATION_REGEX = "\\+[0-9]{1,3} [0-9]{3,}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
