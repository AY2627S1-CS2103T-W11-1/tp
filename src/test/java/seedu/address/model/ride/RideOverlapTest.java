package seedu.address.model.ride;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.RideBuilder;

/**
 * Checks overlap partitions, exact boundaries, and periods crossing midnight.
 */
public class RideOverlapTest {
    private final Ride morning = ride("29-10-2026 0800", "29-10-2026 1000");

    @Test
    public void overlaps_partialContainedContainingAndIdentical_returnsTrueSymmetrically() {
        // EP: partial overlap at either end, containment in either direction, and identical periods.
        for (Ride other : List.of(ride("29-10-2026 0900", "29-10-2026 1100"),
                ride("29-10-2026 0700", "29-10-2026 0900"),
                ride("29-10-2026 0830", "29-10-2026 0930"),
                ride("29-10-2026 0700", "29-10-2026 1100"),
                ride("29-10-2026 0800", "29-10-2026 1000"))) {
            assertTrue(morning.overlaps(other));
            assertTrue(other.overlaps(morning));
        }
    }

    @Test
    public void overlaps_adjacentSeparateAndZeroDuration_returnsFalseSymmetrically() {
        // EP: adjacent periods, separate dates, and zero-duration periods do not overlap.
        // Boundary values: touching 0800/1000 endpoints and duration zero, including at an endpoint.
        for (Ride other : List.of(ride("29-10-2026 1000", "29-10-2026 1100"),
                ride("29-10-2026 0700", "29-10-2026 0800"),
                ride("30-10-2026 0800", "30-10-2026 1000"),
                ride("29-10-2026 0900", "29-10-2026 0900"),
                ride("29-10-2026 0800", "29-10-2026 0800"))) {
            assertFalse(morning.overlaps(other));
            assertFalse(other.overlaps(morning));
        }
    }

    @Test
    public void overlaps_crossingMidnight_comparesCompleteDateTimes() {
        // EP: overlapping periods may cross midnight; comparison includes both the date and time.
        Ride overnight = ride("28-10-2026 2330", "29-10-2026 0830");
        assertTrue(morning.overlaps(overnight));
        // Boundary value: the overnight period ends exactly when the morning ride starts.
        assertFalse(morning.overlaps(ride("28-10-2026 2330", "29-10-2026 0800")));
    }

    @Test
    public void overlaps_nullOther_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> morning.overlaps(null));
    }

    private Ride ride(String from, String to) {
        return new RideBuilder().withFrom(from).withTo(to).build();
    }
}
