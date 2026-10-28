package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class DistanceTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Distance(null));
    }

    @Test
    public void constructor_invalidDistance_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Distance("0"));
    }

    @Test
    public void isValidDistance() {
        // null distance
        assertThrows(NullPointerException.class, () -> Distance.isValidDistance(null));

        // EP: blank/nonpositive values, nonnumeric text, unsupported numeric syntax, and excess digits.
        assertFalse(Distance.isValidDistance("")); // empty string
        assertFalse(Distance.isValidDistance(" ")); // spaces only
        assertFalse(Distance.isValidDistance("0")); // zero
        assertFalse(Distance.isValidDistance("0.00")); // zero with decimals
        assertFalse(Distance.isValidDistance("-5")); // negative
        assertFalse(Distance.isValidDistance("abc")); // letters
        assertFalse(Distance.isValidDistance("45km")); // unit included
        assertFalse(Distance.isValidDistance("45.")); // trailing decimal point
        assertFalse(Distance.isValidDistance(".5")); // no whole part
        assertFalse(Distance.isValidDistance("45.123")); // Boundary value: 3 decimal places; maximum is 2.
        assertFalse(Distance.isValidDistance("4,5")); // comma
        assertFalse(Distance.isValidDistance("1234567")); // Boundary value: 7 integer digits; maximum is 6.
        assertFalse(Distance.isValidDistance("4 5")); // inner space

        // EP: positive whole and decimal distances within the digit and precision limits.
        assertTrue(Distance.isValidDistance("1"));
        assertTrue(Distance.isValidDistance("45"));
        assertTrue(Distance.isValidDistance("45.5"));
        assertTrue(Distance.isValidDistance("45.25"));
        assertTrue(Distance.isValidDistance("0.5")); // less than one kilometre
        assertTrue(Distance.isValidDistance("0.01")); // Boundary value: minimum distance, 0.01 km.
        assertTrue(Distance.isValidDistance("999999")); // Boundary value: maximum of 6 integer digits.
    }

    @Test
    public void constructor_equivalentNumbers_areStoredInSimplestForm() {
        assertEquals("45", new Distance("45").value);
        assertEquals("45", new Distance("045").value);
        assertEquals("45", new Distance("45.0").value);
        assertEquals("45.5", new Distance("45.50").value);
        assertEquals("100", new Distance("100").value);
        assertEquals("0.5", new Distance("0.50").value);
        assertEquals("45", new Distance(" 45 ").value);
    }

    @Test
    public void equals() {
        Distance distance = new Distance("45");

        // same values -> returns true
        assertTrue(distance.equals(new Distance("45")));

        // same number written differently -> returns true
        assertTrue(distance.equals(new Distance("45.0")));

        // same object -> returns true
        assertTrue(distance.equals(distance));

        // null -> returns false
        assertFalse(distance.equals(null));

        // different types -> returns false
        assertFalse(distance.equals(5.0f));

        // different values -> returns false
        assertFalse(distance.equals(new Distance("46")));
    }

    @Test
    public void hashCode_equalValues_haveEqualHashCodes() {
        assertEquals(new Distance("45").hashCode(), new Distance("45.0").hashCode());
    }

    @Test
    public void toString_returnsSimplestForm() {
        assertEquals("45.5", new Distance("45.50").toString());
    }
}
