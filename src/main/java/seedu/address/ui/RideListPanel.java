package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.ride.Ride;

/**
 * A minimal, read-only panel that lists the rides, a few short lines per ride.
 */
public class RideListPanel extends UiPart<Region> {

    public static final String NO_RIDES_MESSAGE = "No rides yet. Use rideadd to create one.";

    private static final String FXML = "RideListPanel.fxml";

    @FXML
    private ListView<Ride> rideListView;

    /**
     * Creates a {@code RideListPanel} with the given {@code ObservableList}.
     */
    public RideListPanel(ObservableList<Ride> rideList) {
        super(FXML);
        rideListView.setItems(rideList);
        rideListView.setCellFactory(listView -> new RideListViewCell());
        Label placeholder = new Label(NO_RIDES_MESSAGE);
        placeholder.getStyleClass().add("label-bright");
        rideListView.setPlaceholder(placeholder);
    }

    /**
     * Returns the text shown for {@code ride}, on up to four short lines so that it fits the narrow panel:
     * the ID and name, the start and end, the location, distance, minimum power and maximum group size,
     * and the notes (only if the ride has any).
     */
    static String formatRide(Ride ride) {
        return "#" + ride.getId() + " " + ride.getName()
                + "\n" + ride.getFrom() + " to " + ride.getTo()
                + "\n" + ride.getLocation() + " | " + ride.getDistance() + " km | " + ride.getMinimumPower()
                + " W | max " + ride.getMaxGroupSize()
                + ride.getNotes().map(notes -> "\n" + notes.value).orElse("");
    }

    /**
     * Custom {@code ListCell} that shows a {@code Ride} as a few short lines of text.
     */
    static class RideListViewCell extends ListCell<Ride> {
        RideListViewCell() {
            // Lets long lines wrap at the width of the list instead of widening it.
            setPrefWidth(0);
            setWrapText(true);
        }

        @Override
        protected void updateItem(Ride ride, boolean empty) {
            super.updateItem(ride, empty);
            setText(empty || ride == null ? null : formatRide(ride));
        }
    }

}
