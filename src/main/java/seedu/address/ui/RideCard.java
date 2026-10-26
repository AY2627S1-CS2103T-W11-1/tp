package seedu.address.ui;

import java.util.stream.Collectors;

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
    private Label roster;
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
        roster.setText(formatRoster(ride));

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
     * {@code 18-10-2026 0630 to 18-10-2026 0930}. Each date and time is kept on one line, so the text can only
     * wrap around the word "to".
     */
    static String formatPeriod(Ride ride) {
        return DisplayText.keepTogether(ride.getFrom().toString()) + " to "
                + DisplayText.keepTogether(ride.getTo().toString());
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
     * The number and unit are kept on one line.
     */
    static String formatDistance(Ride ride) {
        return DisplayText.keepTogether(ride.getDistance().value + " km");
    }

    /**
     * Returns the minimum power of the ride in watts as display text, for example {@code 180 W}.
     * The number and unit are kept on one line.
     */
    static String formatMinimumPower(Ride ride) {
        return DisplayText.keepTogether(ride.getMinimumPower().value + " W");
    }

    /**
     * Returns the phone numbers on the ride's roster as display text, one per line, or {@code No cyclists} if
     * the roster is empty. Each phone number is kept whole, so it is never broken between its country code and
     * its number, however narrow the card is.
     */
    static String formatRoster(Ride ride) {
        if (ride.getRoster().isEmpty()) {
            return "No cyclists";
        }
        return ride.getRoster().stream()
                .map(phone -> DisplayText.keepTogether(phone.toString()))
                .collect(Collectors.joining("\n"));
    }
}
