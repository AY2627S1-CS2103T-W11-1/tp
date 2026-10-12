package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
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
    private Label email;
    @FXML
    private Label ftp;
    @FXML
    private Label locations;
    @FXML
    private Label experienceLevel;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        experienceLevel.setText(person.getExperienceLevel().value);
        ftp.setText(person.getFtp().value + " W");
        locations.setText(formatLocations(person));
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        tags.setManaged(!tags.getChildren().isEmpty());
        tags.setVisible(!tags.getChildren().isEmpty());
    }

    /**
     * Returns the person's preferred locations as display text.
     * The special {@code any} value is humanized without changing its stored representation.
     */
    static String formatLocations(Person person) {
        return person.getLocations().stream()
                .map(location -> location.value.equalsIgnoreCase("any") ? "Any" : location.value)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));
    }
}
