package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.ride.Ride;
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
        assertEquals(DisplayText.keepTogether("18-10-2026 0630") + " to " + DisplayText.keepTogether("18-10-2026 0930"),
                RideCard.formatPeriod(new RideBuilder().build()));
        assertEquals(DisplayText.keepTogether("31-12-2026 2300") + " to " + DisplayText.keepTogether("01-01-2027 0100"),
                RideCard.formatPeriod(
                        new RideBuilder().withFrom("31-12-2026 2300").withTo("01-01-2027 0100").build()));
    }

    @Test
    public void formatPeriod_keepsEachDateTimeTogetherAndMayWrapOnlyAtTo() {
        String period = RideCard.formatPeriod(new RideBuilder().build());

        // the only ordinary spaces are the two around the word "to"
        assertEquals(2, period.chars().filter(character -> character == ' ').count());
        assertTrue(period.contains(" to "));
    }

    @Test
    public void formatLocation_normalLocation_isUnchanged() {
        assertEquals("East Coast", RideCard.formatLocation(new RideBuilder().build()));
    }

    @Test
    public void formatLocation_lowercaseLocation_capitalisesEveryWord() {
        assertEquals("Marina Bay", RideCard.formatLocation(new RideBuilder().withLocation("marina bay").build()));
        assertEquals("East Coast Park",
                RideCard.formatLocation(new RideBuilder().withLocation("east coast park").build()));
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
        assertEquals(DisplayText.keepTogether("45 km"), RideCard.formatDistance(new RideBuilder().build()));
        assertEquals(DisplayText.keepTogether("30.5 km"),
                RideCard.formatDistance(new RideBuilder().withDistance("30.50").build()));
    }

    @Test
    public void formatDistanceAndMinimumPower_neverContainBreakableSpace() {
        Ride ride = new RideBuilder().withDistance("145.75").build();

        assertFalse(RideCard.formatDistance(ride).contains(" "));
        assertFalse(RideCard.formatMinimumPower(ride).contains(" "));
    }

    @Test
    public void formatMinimumPower_addsWattUnit() {
        assertEquals(DisplayText.keepTogether("180 W"), RideCard.formatMinimumPower(new RideBuilder().build()));
        assertEquals(DisplayText.keepTogether("1 W"),
                RideCard.formatMinimumPower(new RideBuilder().withMinimumPower("001").build()));
    }

    @Test
    public void formatRoster_emptyRoster_showsNoCyclists() {
        assertEquals("No cyclists", RideCard.formatRoster(new RideBuilder().build()));
    }

    @Test
    public void formatRoster_phoneNumbers_areKeptWholeAndEachOnItsOwnLine() {
        Ride ride = new RideBuilder().withRoster("+65 91234567", "+65 98765432").build();

        assertEquals(DisplayText.keepTogether("+65 91234567") + "\n" + DisplayText.keepTogether("+65 98765432"),
                RideCard.formatRoster(ride));
        // a phone number is never split between the country code and the number
        for (String entry : RideCard.formatRoster(ride).split("\n")) {
            assertFalse(entry.contains(" "));
        }
    }

    @Test
    public void formatRoster_severalPhoneNumbers_oneLineEachInRosterOrder() {
        Ride ride = new RideBuilder().withRoster("+65 91234567", "+65 98765432", "+65 81234567").build();

        assertEquals(List.of(DisplayText.keepTogether("+65 91234567"), DisplayText.keepTogether("+65 98765432"),
                DisplayText.keepTogether("+65 81234567")), List.of(RideCard.formatRoster(ride).split("\n")));
    }

    @Test
    public void formatRoster_onePhoneNumber_hasNoLineBreak() {
        Ride ride = new RideBuilder().withRoster("+65 91234567").build();

        assertEquals(DisplayText.keepTogether("+65 91234567"), RideCard.formatRoster(ride));
    }
}
