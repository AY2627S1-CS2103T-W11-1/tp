package seedu.address.ui;

import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Location;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label emergencyPhone;
    @FXML
    private Label email;
    @FXML
    private Label ftp;
    @FXML
    private Label locationsLabel;
    @FXML
    private Label locations;
    @FXML
    private Label experienceLevel;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(formatPhone(person));
        emergencyPhone.setText(formatEmergencyPhone(person));
        email.setText(person.getEmail().value);
        experienceLevel.setText(person.getExperienceLevel().value);
        ftp.setText(formatFtp(person));
        locationsLabel.setText(formatLocationsLabel(person));
        locations.setText(formatLocations(person));
    }

    /**
     * Returns the person's phone number as display text, kept on one line so that it is never split between the
     * country code and the number.
     */
    static String formatPhone(Person person) {
        return DisplayText.keepTogether(person.getPhone().value);
    }

    /**
     * Returns the person's emergency phone number as display text (kept on one line), or {@code Not provided} if
     * the person has none.
     */
    static String formatEmergencyPhone(Person person) {
        return person.getEmergencyPhone()
                .map(number -> DisplayText.keepTogether(number.value))
                .orElse("Not provided");
    }

    /**
     * Returns the person's FTP in watts as display text, for example {@code 300 W}, kept on one line.
     */
    static String formatFtp(Person person) {
        return DisplayText.keepTogether(person.getFtp().value + " W");
    }

    /**
     * Returns the label for the person's locations: {@code Location} when there is exactly one,
     * otherwise {@code Locations}.
     */
    static String formatLocationsLabel(Person person) {
        return person.getLocations().size() == 1 ? "Location" : "Locations";
    }

    /**
     * Returns the person's preferred locations as display text.
     * The first letter of every word is capitalised, and {@code any} is shown as {@code Any}, without changing
     * the stored representation.
     */
    static String formatLocations(Person person) {
        return person.getLocations().stream()
                .map(Location::toDisplayText)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));
    }
}
