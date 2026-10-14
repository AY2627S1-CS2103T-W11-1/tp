package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static final int JAVAFX_TIMEOUT_SECONDS = 5;

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        if (System.getProperty("os.name").startsWith("Linux")) {
            String display = System.getenv("DISPLAY");
            assertNotNull(display, "JavaFX tests on Linux require an X display");
            assertFalse(display.isBlank(), "JavaFX tests on Linux require an X display");
        }

        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(startupLatch::countDown);
        assertTrue(startupLatch.await(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "JavaFX toolkit did not start");
    }

    @AfterAll
    public static void stopJavaFxToolkit() {
        Platform.exit();
    }

    @Test
    public void constructor_emergencyPhone_showsNumberOrLegacyPlaceholder() throws Exception {
        Person legacy = new PersonBuilder().build();
        Person cyclist = new PersonBuilder(legacy).withEmergencyPhone("+65 99999999").build();
        runOnJavaFxThread(() -> {
            Parent root = new PersonCard(cyclist, 1).getRoot();
            Label emergencyPhone = getLabel(root, "emergencyPhone");
            assertEquals("+65 99999999", emergencyPhone.getText());
            assertTrue(emergencyPhone.isWrapText());
            assertTrue(emergencyPhone.isVisible());
            assertTrue(emergencyPhone.isManaged());
            assertSame(getLabel(root, "phone").getParent(), emergencyPhone.getParent());
            assertTrue(getAllLabelTexts(root).contains("SOS"));
            assertEquals("Not provided", getLabel(new PersonCard(legacy, 2).getRoot(), "emergencyPhone").getText());
        });
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
            assertEquals("+65 98765432", phone.getText());
            assertEquals("MixedCase.Email@Example.com", email.getText());
            assertEquals("450 W", ftp.getText());
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

    private static void runOnJavaFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        try {
            task.get(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException exception) {
            if (exception.getCause() instanceof AssertionError assertionError) {
                throw assertionError;
            }
            if (exception.getCause() instanceof Exception cause) {
                throw cause;
            }
            throw new RuntimeException(exception.getCause());
        }
    }
}
