package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import seedu.address.model.ride.Ride;

/**
 * A UI component that displays information of a {@code Ride}.
 */
public class RideCard extends UiPart<Region> {

    private static final String FXML = "RideListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime. The location label is therefore
     * named {@code rideLocation}.
     */

    public final Ride ride;

    @FXML
    private Label title;
    @FXML
    private Label period;
    @FXML
    private Label rideLocation;
    @FXML
    private Label distance;
    @FXML
    private Label minimumPower;
    @FXML
    private Label maxGroupSize;
    @FXML
    private GridPane notesRow;
    @FXML
    private Label notes;

    /**
     * Creates a {@code RideCard} with the given {@code Ride} to display.
     */
    public RideCard(Ride ride) {
        super(FXML);
        this.ride = ride;
        title.setText(formatTitle(ride));
        period.setText(formatPeriod(ride));
        rideLocation.setText(formatLocation(ride));
        distance.setText(formatDistance(ride));
        minimumPower.setText(formatMinimumPower(ride));
        maxGroupSize.setText(ride.getMaxGroupSize().value);

        // The notes line takes up no space when the ride has no notes.
        boolean hasNotes = ride.getNotes().isPresent();
        notesRow.setVisible(hasNotes);
        notesRow.setManaged(hasNotes);
        ride.getNotes().ifPresent(rideNotes -> notes.setText(rideNotes.value));
    }

    /**
     * Returns the title of the ride as display text: its ID followed by its name, for example
     * {@code #1 Sunday Sunrise Ride}.
     */
    static String formatTitle(Ride ride) {
        return "#" + ride.getId() + " " + ride.getName().fullName;
    }

    /**
     * Returns the start and end of the ride as display text, for example
     * {@code 18-10-2026 0630 to 18-10-2026 0930}.
     */
    static String formatPeriod(Ride ride) {
        return ride.getFrom() + " to " + ride.getTo();
    }

    /**
     * Returns the location of the ride as display text.
     * The special {@code any} value is humanized without changing its stored representation.
     */
    static String formatLocation(Ride ride) {
        String location = ride.getLocation().value;
        return location.equalsIgnoreCase("any") ? "Any" : location;
    }

    /**
     * Returns the distance of the ride in kilometres as display text, for example {@code 45 km}.
     */
    static String formatDistance(Ride ride) {
        return ride.getDistance().value + " km";
    }

    /**
     * Returns the minimum power of the ride in watts as display text, for example {@code 180 W}.
     */
    static String formatMinimumPower(Ride ride) {
        return ride.getMinimumPower().value + " W";
    }
}
