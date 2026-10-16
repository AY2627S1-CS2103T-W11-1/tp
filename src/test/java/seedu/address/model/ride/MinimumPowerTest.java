package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MinimumPowerTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MinimumPower(null));
    }

    @Test
    public void constructor_invalidMinimumPower_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MinimumPower("601"));
    }

    @Test
    public void isValidMinimumPower() {
        // null minimum power
        assertThrows(NullPointerException.class, () -> MinimumPower.isValidMinimumPower(null));

        // invalid minimum powers
        assertFalse(MinimumPower.isValidMinimumPower("")); // empty string
        assertFalse(MinimumPower.isValidMinimumPower(" ")); // spaces only
        assertFalse(MinimumPower.isValidMinimumPower("0")); // below the range
        assertFalse(MinimumPower.isValidMinimumPower("601")); // above the range
        assertFalse(MinimumPower.isValidMinimumPower("1000")); // four digits
        assertFalse(MinimumPower.isValidMinimumPower("-5")); // negative
        assertFalse(MinimumPower.isValidMinimumPower("180.5")); // decimal
        assertFalse(MinimumPower.isValidMinimumPower("180W")); // unit included
        assertFalse(MinimumPower.isValidMinimumPower("abc")); // letters

        // valid minimum powers
        assertTrue(MinimumPower.isValidMinimumPower("1")); // lowest allowed
        assertTrue(MinimumPower.isValidMinimumPower("180"));
        assertTrue(MinimumPower.isValidMinimumPower("600")); // highest allowed
        assertTrue(MinimumPower.isValidMinimumPower("050")); // leading zero
    }

    @Test
    public void constructor_leadingZeros_areRemoved() {
        assertEquals("50", new MinimumPower("050").value);
        assertEquals("180", new MinimumPower(" 180 ").value);
    }

    @Test
    public void equals() {
        MinimumPower power = new MinimumPower("180");

        // same values -> returns true
        assertTrue(power.equals(new MinimumPower("180")));

        // same object -> returns true
        assertTrue(power.equals(power));

        // null -> returns false
        assertFalse(power.equals(null));

        // different types -> returns false
        assertFalse(power.equals(5.0f));

        // different values -> returns false
        assertFalse(power.equals(new MinimumPower("181")));
    }

    @Test
    public void hashCode_equalValues_haveEqualHashCodes() {
        assertEquals(new MinimumPower("50").hashCode(), new MinimumPower("050").hashCode());
    }

    @Test
    public void toString_returnsValue() {
        assertEquals("180", new MinimumPower("180").toString());
    }
}
