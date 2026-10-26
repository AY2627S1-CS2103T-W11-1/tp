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
        Ride overnight = ride("28-10-2026 2330", "29-10-2026 0830");
        assertTrue(morning.overlaps(overnight));
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
