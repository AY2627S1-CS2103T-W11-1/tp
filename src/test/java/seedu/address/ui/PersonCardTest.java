package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnJavaFxThread;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.testutil.JavaFxTestUtil;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

public class PersonCardTest {

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        if (System.getProperty("os.name").startsWith("Linux")) {
            String display = System.getenv("DISPLAY");
            assertNotNull(display, "JavaFX tests on Linux require an X display");
            assertFalse(display.isBlank(), "JavaFX tests on Linux require an X display");
        }

        JavaFxTestUtil.startJavaFxToolkit();
    }

    @Test
    public void constructor_rideRoster_populatesWrappingFxmlLabel() throws Exception {
        runOnJavaFxThread(() -> {
            Parent emptyCard = new RideCard(new RideBuilder().build()).getRoot();
            assertEquals("No cyclists", getLabel(emptyCard, "roster").getText());
            Parent populatedCard = new RideCard(new RideBuilder()
                    .withRoster("+65 91234567", "+65 98765432").build()).getRoot();
            Label roster = getLabel(populatedCard, "roster");
            assertEquals(DisplayText.keepTogether("+65 91234567") + "\n" + DisplayText.keepTogether("+65 98765432"),
                    roster.getText());
            assertTrue(roster.isWrapText());
            assertTrue(roster.isManaged());
            assertTrue(roster.isVisible());
        });
    }

    @Test
    public void constructor_emergencyPhone_showsNumberOrLegacyPlaceholder() throws Exception {
        Person legacy = new PersonBuilder().build();
        Person cyclist = new PersonBuilder(legacy).withEmergencyPhone("+65 99999999").build();
        runOnJavaFxThread(() -> {
            Parent root = new PersonCard(cyclist, 1).getRoot();
            Label emergencyPhone = getLabel(root, "emergencyPhone");
            assertEquals(DisplayText.keepTogether("+65 99999999"), emergencyPhone.getText());
            assertTrue(emergencyPhone.isWrapText());
            assertTrue(emergencyPhone.isVisible());
            assertTrue(emergencyPhone.isManaged());
            assertSame(getLabel(root, "phone").getParent(), emergencyPhone.getParent());
            assertTrue(getAllLabelTexts(root).contains("SOS"));
            assertEquals("Not provided", getLabel(new PersonCard(legacy, 2).getRoot(), "emergencyPhone").getText());
        });
    }

    @Test
    public void formatPhoneEmergencyPhoneAndFtp_keepValuesOnOneLine() {
        Person person = new PersonBuilder().withPhone("+65 98765432").withEmergencyPhone("+65 91234567")
                .withFtp("300").build();

        assertEquals(DisplayText.keepTogether("+65 98765432"), PersonCard.formatPhone(person));
        assertEquals(DisplayText.keepTogether("+65 91234567"), PersonCard.formatEmergencyPhone(person));
        assertEquals(DisplayText.keepTogether("300 W"), PersonCard.formatFtp(person));
        assertFalse(PersonCard.formatPhone(person).contains(" "));
        assertFalse(PersonCard.formatEmergencyPhone(person).contains(" "));
        assertFalse(PersonCard.formatFtp(person).contains(" "));
    }

    @Test
    public void formatEmergencyPhone_legacyCyclist_showsNotProvided() {
        assertEquals("Not provided", PersonCard.formatEmergencyPhone(new PersonBuilder().build()));
    }

    @Test
    public void formatLocations_multipleLocations_displaysEveryLocation() {
        Person person = new PersonBuilder()
                .withLocations("Marina Bay", "East Coast Park")
                .build();

        assertEquals("East Coast Park, Marina Bay", PersonCard.formatLocations(person));
    }

    @Test
    public void formatLocations_anyLocation_humanizesValue() {
        Person person = new PersonBuilder().withLocations("any").build();

        assertEquals("Any", PersonCard.formatLocations(person));
    }

    @Test
    public void formatLocations_mixedCaseLocation_preservesStoredCase() {
        Person person = new PersonBuilder().withLocations("eAsT Coast Park").build();

        assertEquals("eAsT Coast Park", PersonCard.formatLocations(person));
    }

    @Test
    public void constructor_allFields_populatesConnectedFxmlControls() throws Exception {
        Person person = new PersonBuilder()
                .withName("Zelda Longname Cyclist")
                .withPhone("+65 98765432")
                .withEmail("MixedCase.Email@Example.com")
                .withFtp("450")
                .withExperienceLevel("Advanced")
                .withLocations("Marina Bay", "East Coast Park")
                .build();

        runOnJavaFxThread(() -> {
            Parent root = new PersonCard(person, 7).getRoot();
            Label name = getLabel(root, "name");
            Label phone = getLabel(root, "phone");
            Label email = getLabel(root, "email");
            Label ftp = getLabel(root, "ftp");
            Label experienceLevel = getLabel(root, "experienceLevel");
            Label locations = getLabel(root, "locations");

            assertEquals("7. ", getLabel(root, "id").getText());
            assertEquals("Zelda Longname Cyclist", name.getText());
            assertEquals(DisplayText.keepTogether("+65 98765432"), phone.getText());
            assertEquals("MixedCase.Email@Example.com", email.getText());
            assertEquals(DisplayText.keepTogether("450 W"), ftp.getText());
            assertEquals("Advanced", experienceLevel.getText());
            assertEquals("East Coast Park, Marina Bay", locations.getText());
            assertTrue(name.isWrapText());
            assertTrue(email.isWrapText());
            assertTrue(locations.isWrapText());

            for (Node node : List.of(name, phone, email, ftp, experienceLevel, locations)) {
                assertTrue(node.isVisible());
                assertTrue(node.isManaged());
            }
        });
    }

    @Test
    public void constructor_anyLocation_displaysHumanReadableValue() throws Exception {
        Person person = new PersonBuilder().withLocations("any").build();

        runOnJavaFxThread(() -> {
            Parent root = new PersonCard(person, 1).getRoot();

            assertEquals("Any", getLabel(root, "locations").getText());
        });
    }

    @Test
    public void layout_supportedFields_usesTwoColumnsAndFullWidthBottomRows() throws Exception {
        Person person = new PersonBuilder().build();

        runOnJavaFxThread(() -> {
            Parent root = new PersonCard(person, 1).getRoot();
            Label phone = getLabel(root, "phone");
            Label email = getLabel(root, "email");
            Label ftp = getLabel(root, "ftp");
            Label experienceLevel = getLabel(root, "experienceLevel");
            Label locations = getLabel(root, "locations");

            GridPane contactFields = assertInstanceOf(GridPane.class, phone.getParent());
            GridPane cyclingFields = assertInstanceOf(GridPane.class, ftp.getParent());
            assertSame(contactFields, email.getParent());
            assertSame(cyclingFields, experienceLevel.getParent());

            VBox contactSection = assertInstanceOf(VBox.class, contactFields.getParent());
            VBox cyclingSection = assertInstanceOf(VBox.class, cyclingFields.getParent());
            assertEquals("Contact", getSectionHeading(contactSection).getText());
            assertEquals("Cycling Profile", getSectionHeading(cyclingSection).getText());

            GridPane columns = assertInstanceOf(GridPane.class, contactSection.getParent());
            assertSame(columns, cyclingSection.getParent());
            assertEquals(0, getGridColumn(contactSection));
            assertEquals(1, getGridColumn(cyclingSection));

            GridPane locationsRow = assertInstanceOf(GridPane.class, locations.getParent());
            VBox cardContent = assertInstanceOf(VBox.class, columns.getParent());
            assertSame(cardContent, locationsRow.getParent());
            assertTrue(cardContent.getChildren().indexOf(columns)
                    < cardContent.getChildren().indexOf(locationsRow));

            assertNull(root.lookup("#tags"));
            assertNull(root.lookup("#address"));
            assertFalse(getAllLabelTexts(root).contains("Address"));
        });
    }

    private static Label getLabel(Parent root, String id) {
        return assertInstanceOf(Label.class, root.lookup("#" + id));
    }

    private static Label getSectionHeading(VBox section) {
        return assertInstanceOf(Label.class, section.getChildren().get(0));
    }

    private static int getGridColumn(Node node) {
        Integer columnIndex = GridPane.getColumnIndex(node);
        return columnIndex == null ? 0 : columnIndex;
    }

    private static List<String> getAllLabelTexts(Parent root) {
        return root.lookupAll(".label").stream()
                .map(Label.class::cast)
                .map(Label::getText)
                .toList();
    }
}
