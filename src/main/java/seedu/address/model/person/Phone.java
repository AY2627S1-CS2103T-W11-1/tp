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
                    + "Parentheses and hyphens are not allowed. The country code and the number must each "
                    + "contain a non-zero digit, and the number needs at least 3 digits after any leading zeros.";
    public static final String MESSAGE_MISSING_COUNTRY_CODE =
            "Phone numbers must start with a '+' followed by the country code, for example '+65 91234567'.";
    public static final String MESSAGE_MISSING_SEPARATOR =
            "Phone numbers must contain one space between the country code and the number.";
    public static final String MESSAGE_MULTIPLE_SEPARATORS =
            "Phone numbers must contain exactly one space between the country code and the number.";
    /*
     * A "+", a country code of 1-3 digits as typed, one space, and a number. Leading zeros are allowed in both
     * parts, but the country code must contain a non-zero digit and the number needs 3 digits after its zeros.
     */
    public static final String VALIDATION_REGEX = "\\+(?=[0-9]{1,3} )0*[1-9][0-9]* 0*[1-9][0-9]{2,}";
    public final String value;

    /**
     * Constructs a {@code Phone}. Leading zeros are removed, so numbers that differ only by leading zeros
     * are equal, e.g. "+065 091234561" is stored as "+65 91234561".
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = removeLeadingZeros(phone);
    }

    /**
     * Returns {@code phone} with the leading zeros of its country code and of its number removed.
     */
    private static String removeLeadingZeros(String phone) {
        int separatorIndex = phone.indexOf(' ');
        String countryCode = phone.substring(1, separatorIndex).replaceFirst("^0+", "");
        String number = phone.substring(separatorIndex + 1).replaceFirst("^0+", "");
        return "+" + countryCode + " " + number;
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
