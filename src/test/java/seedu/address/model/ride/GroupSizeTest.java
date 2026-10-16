package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GroupSizeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupSize(null));
    }

    @Test
    public void constructor_invalidGroupSize_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new GroupSize("0"));
    }

    @Test
    public void isValidGroupSize() {
        // null group size
        assertThrows(NullPointerException.class, () -> GroupSize.isValidGroupSize(null));

        // invalid group sizes
        assertFalse(GroupSize.isValidGroupSize("")); // empty string
        assertFalse(GroupSize.isValidGroupSize(" ")); // spaces only
        assertFalse(GroupSize.isValidGroupSize("0")); // zero
        assertFalse(GroupSize.isValidGroupSize("00")); // zero with padding
        assertFalse(GroupSize.isValidGroupSize("-3")); // negative
        assertFalse(GroupSize.isValidGroupSize("2.5")); // decimal
        assertFalse(GroupSize.isValidGroupSize("ten")); // letters
        assertFalse(GroupSize.isValidGroupSize("1234567890")); // more than 9 digits
        assertFalse(GroupSize.isValidGroupSize("1 2")); // inner space

        // valid group sizes
        assertTrue(GroupSize.isValidGroupSize("1")); // smallest allowed
        assertTrue(GroupSize.isValidGroupSize("12"));
        assertTrue(GroupSize.isValidGroupSize("999999999")); // largest allowed
        assertTrue(GroupSize.isValidGroupSize("012")); // leading zero
    }

    @Test
    public void constructor_leadingZeros_areRemoved() {
        assertEquals("12", new GroupSize("012").value);
        assertEquals("12", new GroupSize(" 12 ").value);
    }

    @Test
    public void equals() {
        GroupSize size = new GroupSize("12");

        // same values -> returns true
        assertTrue(size.equals(new GroupSize("12")));

        // same object -> returns true
        assertTrue(size.equals(size));

        // null -> returns false
        assertFalse(size.equals(null));

        // different types -> returns false
        assertFalse(size.equals(5.0f));

        // different values -> returns false
        assertFalse(size.equals(new GroupSize("13")));
    }

    @Test
    public void hashCode_equalValues_haveEqualHashCodes() {
        assertEquals(new GroupSize("12").hashCode(), new GroupSize("012").hashCode());
    }

    @Test
    public void toString_returnsValue() {
        assertEquals("12", new GroupSize("12").toString());
    }
}
