package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void isValidPhone_countryCodeAndSubscriberLengthBoundaries() {
        // EP: valid country-code lengths (1–3 digits) and invalid lengths (0 or at least 4).
        // Boundary values: code lengths 0/1 and 3/4; length 2 is an interior valid value.
        for (String code : java.util.List.of("1", "12", "123")) {
            // Boundary values: subscriber lengths 2, 3, and 4 straddle the minimum of 3 digits.
            assertFalse(Phone.isValidPhone("+" + code + " 12"));
            assertTrue(Phone.isValidPhone("+" + code + " 123"));
            assertTrue(Phone.isValidPhone("+" + code + " 1234"));
        }
        assertFalse(Phone.isValidPhone("+ 123"));
        assertFalse(Phone.isValidPhone("+1234 123"));
    }

    @Test
    public void isValidPhone_leadingZeros_requireNonZeroDigitsInCountryCodeAndNumber() {
        // EP: leading zeros in the country code, the number or both are accepted when a non-zero digit follows.
        assertTrue(Phone.isValidPhone("+065 91234561"));
        assertTrue(Phone.isValidPhone("+65 091234561"));
        assertTrue(Phone.isValidPhone("+065 0091234561"));
        // Boundary values: the number needs 3 digits after its leading zeros (2 is invalid, 3 is valid).
        assertFalse(Phone.isValidPhone("+65 0012"));
        assertTrue(Phone.isValidPhone("+65 0123"));
        // EP: an all-zero country code or number is invalid, as in the reported "+0 000".
        assertFalse(Phone.isValidPhone("+0 000"));
        assertFalse(Phone.isValidPhone("+00 91234561"));
        assertFalse(Phone.isValidPhone("+65 000"));
        assertFalse(Phone.isValidPhone("+65 0000000"));
        // Boundary value: leading zeros still count towards the 3-digit country code limit.
        assertFalse(Phone.isValidPhone("+0065 91234561"));
    }

    @Test
    public void constructor_leadingZeros_removedFromStoredValue() {
        // EP: zeros before the first non-zero digit of the country code and of the number are removed.
        assertEquals("+65 91234561", new Phone("+065 91234561").value);
        assertEquals("+65 91234561", new Phone("+65 091234561").value);
        assertEquals("+65 91234561", new Phone("+065 0091234561").value);
        assertEquals("+65 123", new Phone("+65 0123").value);
        // EP: zeros after the first non-zero digit are part of the number and are kept.
        assertEquals("+105 900100", new Phone("+105 0900100").value);
        assertEquals("+65 91234561", new Phone("+65 91234561").value);
    }

    @Test
    public void equals_differOnlyByLeadingZeros_returnsTrue() {
        Phone phone = new Phone("+65 91234561");

        // Regression for #239: the same number written with leading zeros is the same phone number.
        assertEquals(phone, new Phone("+065 91234561"));
        assertEquals(phone, new Phone("+65 091234561"));
        assertEquals(phone.hashCode(), new Phone("+065 091234561").hashCode());

        // EP: a trailing zero changes the number, so it is a different phone number.
        assertFalse(phone.equals(new Phone("+65 912345610")));
        assertFalse(phone.equals(new Phone("+650 91234561")));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // EP: blank input, nonnumeric text, malformed country code/separator, and forbidden characters.
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("+65 91")); // Boundary value: 2 subscriber digits, one below the minimum.
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("+65 9312 1534")); // multiple spaces
        assertFalse(Phone.isValidPhone("91234567")); // missing country code
        assertFalse(Phone.isValidPhone("+6591234567")); // missing separator
        assertFalse(Phone.isValidPhone("+65  91234567")); // extra separator
        assertFalse(Phone.isValidPhone("+65-91234567")); // hyphen
        assertFalse(Phone.isValidPhone("+65 (91234567)")); // parentheses
        assertFalse(Phone.isValidPhone("+65 9123a567")); // invalid character

        // EP: correctly formatted phone numbers with a country code and at least 3 subscriber digits.
        assertTrue(Phone.isValidPhone("+65 911")); // Boundary value: minimum of 3 subscriber digits.
        assertTrue(Phone.isValidPhone("+65 93121534"));
        assertTrue(Phone.isValidPhone("+124 293842033123")); // long phone numbers
    }

    @Test
    public void equals() {
        Phone phone = new Phone("+65 999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("+65 999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("+65 995")));
    }
}
