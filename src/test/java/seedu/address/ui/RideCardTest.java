package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.RideBuilder;

public class RideCardTest {

    @Test
    public void formatTitle_showsHashIdThenName() {
        assertEquals("#1 Sunday Sunrise Ride", RideCard.formatTitle(new RideBuilder().withId(1).build()));
        assertEquals("#12 Evening Spin",
                RideCard.formatTitle(new RideBuilder().withId(12).withName("Evening Spin").build()));
    }

    @Test
    public void formatPeriod_showsStartToEnd() {
        assertEquals("18-10-2026 0630 to 18-10-2026 0930", RideCard.formatPeriod(new RideBuilder().build()));
        assertEquals("31-12-2026 2300 to 01-01-2027 0100", RideCard.formatPeriod(
                new RideBuilder().withFrom("31-12-2026 2300").withTo("01-01-2027 0100").build()));
    }

    @Test
    public void formatLocation_normalLocation_isUnchanged() {
        assertEquals("East Coast", RideCard.formatLocation(new RideBuilder().build()));
    }

    @Test
    public void formatLocation_anyInAnyCase_isShownAsAny() {
        assertEquals("Any", RideCard.formatLocation(new RideBuilder().withLocation("any").build()));
        assertEquals("Any", RideCard.formatLocation(new RideBuilder().withLocation("ANY").build()));
    }

    @Test
    public void formatLocation_locationContainingAny_isUnchanged() {
        assertEquals("Anyang Road", RideCard.formatLocation(new RideBuilder().withLocation("Anyang Road").build()));
    }

    @Test
    public void formatDistance_addsKilometreUnit() {
        assertEquals("45 km", RideCard.formatDistance(new RideBuilder().build()));
        assertEquals("30.5 km", RideCard.formatDistance(new RideBuilder().withDistance("30.50").build()));
    }

    @Test
    public void formatMinimumPower_addsWattUnit() {
        assertEquals("180 W", RideCard.formatMinimumPower(new RideBuilder().build()));
        assertEquals("1 W", RideCard.formatMinimumPower(new RideBuilder().withMinimumPower("001").build()));
    }
}
