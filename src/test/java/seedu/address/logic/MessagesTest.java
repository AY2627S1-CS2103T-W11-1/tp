package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

public class MessagesTest {

    @Test
    public void format_person_showsLocationsWithEveryWordCapitalised() {
        String message = Messages.format(new PersonBuilder().withLocations("marina bay").build());

        assertTrue(message.endsWith("; Locations: [Marina Bay]"), message);
    }

    @Test
    public void format_personWithSeveralLocations_listsEachCapitalised() {
        String message = Messages.format(new PersonBuilder().withLocations("marina bay", "punggol").build());

        assertTrue(message.contains("Marina Bay"), message);
        assertTrue(message.contains("Punggol"), message);
    }

    @Test
    public void format_personWithAnyLocation_showsAny() {
        String message = Messages.format(new PersonBuilder().withLocations("any").build());

        assertTrue(message.endsWith("; Locations: [Any]"), message);
    }

    @Test
    public void formatRide_showsLocationWithEveryWordCapitalised() {
        String message = Messages.formatRide(new RideBuilder().withLocation("east coast park").build());

        assertTrue(message.contains("; Location: East Coast Park;"), message);
    }
}
