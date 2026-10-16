package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;
import seedu.address.model.ride.Ride;

/**
 * Panel containing the list of rides, shown as cards.
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
        placeholder.getStyleClass().add("ride-placeholder");
        placeholder.setWrapText(true);
        placeholder.setTextAlignment(TextAlignment.CENTER);
        placeholder.setAlignment(Pos.CENTER);
        rideListView.setPlaceholder(placeholder);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Ride} using a {@code RideCard}.
     */
    class RideListViewCell extends ListCell<Ride> {
        RideListViewCell() {
            // Lets the card take the width of the list, so long text wraps instead of widening the list.
            setPrefWidth(0);
        }

        @Override
        protected void updateItem(Ride ride, boolean empty) {
            super.updateItem(ride, empty);

            if (empty || ride == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new RideCard(ride).getRoot());
            }
        }
    }

}
