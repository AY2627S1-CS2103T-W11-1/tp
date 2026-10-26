package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.JavaFxToolkit;
import seedu.address.testutil.RideBuilder;

public class RideListPanelTest {

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        JavaFxToolkit.start();
    }

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

    @Test
    public void constructor_noSavedRides_showsNoRidesMessage() throws Exception {
        ObservableList<Ride> allRides = FXCollections.observableArrayList();

        assertEquals(RideListPanel.NO_RIDES_MESSAGE, placeholderOf(allRides, new FilteredList<>(allRides)));
    }

    @Test
    public void constructor_savedRideHiddenByFilter_showsNoMatchingRidesMessage() throws Exception {
        ObservableList<Ride> allRides = FXCollections.observableArrayList(new RideBuilder().build());
        FilteredList<Ride> filteredRides = new FilteredList<>(allRides, ride -> false);

        assertEquals(RideListPanel.NO_MATCHING_RIDES_MESSAGE, placeholderOf(allRides, filteredRides));
    }

    @Test
    public void placeholder_savedRidesChange_messageFollowsWhetherAnyRideIsSaved() throws Exception {
        ObservableList<Ride> allRides = FXCollections.observableArrayList();
        FilteredList<Ride> filteredRides = new FilteredList<>(allRides, ride -> false);

        JavaFxToolkit.call(() -> {
            RideListPanel panel = new RideListPanel(filteredRides, allRides);
            Label placeholder = placeholderLabel(panel);

            assertEquals(RideListPanel.NO_RIDES_MESSAGE, placeholder.getText());
            allRides.add(new RideBuilder().build());
            assertEquals(RideListPanel.NO_MATCHING_RIDES_MESSAGE, placeholder.getText());
            allRides.clear();
            assertEquals(RideListPanel.NO_RIDES_MESSAGE, placeholder.getText());
            return null;
        });
    }

    private static String placeholderOf(ObservableList<Ride> allRides, ObservableList<Ride> shownRides)
            throws Exception {
        return JavaFxToolkit.call(() -> placeholderLabel(new RideListPanel(shownRides, allRides)).getText());
    }

    private static Label placeholderLabel(RideListPanel panel) {
        ListView<?> rideListView = (ListView<?>) panel.getRoot().lookup("#rideListView");
        return (Label) rideListView.getPlaceholder();
    }
}
