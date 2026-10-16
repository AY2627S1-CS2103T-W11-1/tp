package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.RideBuilder;

public class RideListPanelTest {

    @Test
    public void formatRide_rideWithoutNotes_showsThreeLines() {
        String text = RideListPanel.formatRide(new RideBuilder().withId(1).build());

        assertEquals("#1 Sunday Sunrise Ride\n18-10-2026 0630 to 18-10-2026 0930\nEast Coast | 45 km | 180 W | max 12",
                text);
    }

    @Test
    public void formatRide_rideWithNotes_endsWithNotesLine() {
        String text = RideListPanel.formatRide(new RideBuilder().withId(2).withName("Evening Spin")
                .withDistance("30.5").withNotes("Bring lights").build());

        assertEquals("#2 Evening Spin\n18-10-2026 0630 to 18-10-2026 0930\nEast Coast | 30.5 km | 180 W | max 12"
                + "\nBring lights", text);
    }
}
