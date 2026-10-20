package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RideDateTimeTest {

    @Test
    public void isValidRideDateTime_calendarAndClockBoundaryNeighbours() {
        for (String value : java.util.List.of("28-02-1900 2359", "01-03-1900 0000", "29-02-2000 0000",
                "28-02-2027 2359", "01-03-2027 0000", "29-02-2028 2359", "30-04-2026 2359",
                "01-05-2026 0000", "31-12-9999 2359", "01-01-0001 0000")) {
            assertTrue(RideDateTime.isValidRideDateTime(value), value);
        }
        for (String value : java.util.List.of("29-02-1900 0000", "29-02-2100 0000", "31-04-2026 0000",
                "01-00-2026 0000", "01-13-2026 0000", "32-12-2026 0000", "18-10-2026 23560",
                "18-10-2026 2360", "18-10-2026 2400", "01-01-10000 0000")) {
            assertFalse(RideDateTime.isValidRideDateTime(value), value);
        }
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RideDateTime(null));
    }

    @Test
    public void constructor_invalidDateTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new RideDateTime("2026-10-18 0630"));
    }

    @Test
    public void isValidRideDateTime() {
        // null date and time
        assertThrows(NullPointerException.class, () -> RideDateTime.isValidRideDateTime(null));

        // invalid shape
        assertFalse(RideDateTime.isValidRideDateTime("")); // empty string
        assertFalse(RideDateTime.isValidRideDateTime(" ")); // spaces only
        assertFalse(RideDateTime.isValidRideDateTime("18-10-2026")); // missing time
        assertFalse(RideDateTime.isValidRideDateTime("0630")); // missing date
        assertFalse(RideDateTime.isValidRideDateTime("18-10-2026 06:30")); // colon in time
        assertFalse(RideDateTime.isValidRideDateTime("18-10-2026  0630")); // two spaces
        assertFalse(RideDateTime.isValidRideDateTime("18/10/2026 0630")); // wrong date separators
        assertFalse(RideDateTime.isValidRideDateTime("2026-10-18 0630")); // year first
        assertFalse(RideDateTime.isValidRideDateTime("8-10-2026 0630")); // single-digit day
        assertFalse(RideDateTime.isValidRideDateTime("18-10-26 0630")); // two-digit year
        assertFalse(RideDateTime.isValidRideDateTime("18-10-2026 630")); // three-digit time
        assertFalse(RideDateTime.isValidRideDateTime(" 18-10-2026 0630")); // leading space

        // well-shaped but not real
        assertFalse(RideDateTime.isValidRideDateTime("31-02-2026 0630")); // 31 February
        assertFalse(RideDateTime.isValidRideDateTime("29-02-2027 0630")); // not a leap year
        assertFalse(RideDateTime.isValidRideDateTime("18-13-2026 0630")); // month 13
        assertFalse(RideDateTime.isValidRideDateTime("00-10-2026 0630")); // day 0
        assertFalse(RideDateTime.isValidRideDateTime("18-10-2026 2400")); // hour 24
        assertFalse(RideDateTime.isValidRideDateTime("18-10-2026 0660")); // minute 60

        // valid date and time
        assertTrue(RideDateTime.isValidRideDateTime("18-10-2026 0630"));
        assertTrue(RideDateTime.isValidRideDateTime("01-01-2027 0000")); // earliest time of a day
        assertTrue(RideDateTime.isValidRideDateTime("31-12-2026 2359")); // latest time of a day
        assertTrue(RideDateTime.isValidRideDateTime("29-02-2028 1200")); // leap day
    }

    @Test
    public void constructor_surroundingWhitespace_isTrimmed() {
        assertEquals(new RideDateTime("18-10-2026 0630"), new RideDateTime("  18-10-2026 0630 "));
    }

    @Test
    public void toString_usesDayMonthYearAndFourDigitTime() {
        assertEquals("18-10-2026 0630", new RideDateTime("18-10-2026 0630").toString());
        assertEquals("01-01-2027 0005", new RideDateTime("01-01-2027 0005").toString());
    }

    @Test
    public void isBefore() {
        RideDateTime early = new RideDateTime("18-10-2026 0630");
        RideDateTime late = new RideDateTime("18-10-2026 0631");

        assertTrue(early.isBefore(late));
        assertFalse(late.isBefore(early));
        assertFalse(early.isBefore(new RideDateTime("18-10-2026 0630"))); // same time is not before
        assertTrue(new RideDateTime("31-12-2026 2359").isBefore(new RideDateTime("01-01-2027 0000")));
        assertThrows(NullPointerException.class, () -> early.isBefore(null));
    }

    @Test
    public void compareTo() {
        RideDateTime early = new RideDateTime("18-10-2026 0630");
        RideDateTime late = new RideDateTime("19-10-2026 0630");

        assertTrue(early.compareTo(late) < 0);
        assertTrue(late.compareTo(early) > 0);
        assertEquals(0, early.compareTo(new RideDateTime("18-10-2026 0630")));
    }

    @Test
    public void equals() {
        RideDateTime dateTime = new RideDateTime("18-10-2026 0630");

        // same values -> returns true
        assertTrue(dateTime.equals(new RideDateTime("18-10-2026 0630")));

        // same object -> returns true
        assertTrue(dateTime.equals(dateTime));

        // null -> returns false
        assertFalse(dateTime.equals(null));

        // different types -> returns false
        assertFalse(dateTime.equals(5.0f));

        // different values -> returns false
        assertFalse(dateTime.equals(new RideDateTime("18-10-2026 0631")));
    }

    @Test
    public void hashCode_equalValues_haveEqualHashCodes() {
        assertEquals(new RideDateTime("18-10-2026 0630").hashCode(), new RideDateTime("18-10-2026 0630").hashCode());
    }
}
