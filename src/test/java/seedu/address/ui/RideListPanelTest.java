package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class RideListPanelTest {

    @Test
    public void placeholderMessage_noSavedRides_tellsUserToCreateOne() {
        assertEquals("No rides yet. Use rideadd to create one.", RideListPanel.placeholderMessage(false));
    }

    @Test
    public void placeholderMessage_savedRidesHiddenByFilter_tellsUserToShowAll() {
        assertEquals("No rides match the current filter. Use ridefind all to show all rides.",
                RideListPanel.placeholderMessage(true));
    }

    @Test
    public void placeholderMessage_filteredCase_doesNotSuggestCreatingARide() {
        assertNotEquals(RideListPanel.NO_RIDES_MESSAGE, RideListPanel.placeholderMessage(true));
    }
}
