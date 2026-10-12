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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static final int JAVAFX_TIMEOUT_SECONDS = 5;
    private static Thread javaFxApplicationThread;

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        if (System.getProperty("os.name").startsWith("Linux")) {
            String display = System.getenv("DISPLAY");
            assertNotNull(display, "JavaFX tests on Linux require an X display");
            assertFalse(display.isBlank(), "JavaFX tests on Linux require an X display");
        }

        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(() -> {
            javaFxApplicationThread = Thread.currentThread();
            startupLatch.countDown();
        });
        assertTrue(startupLatch.await(JAVAFX_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "JavaFX toolkit did not start");
    }

    @AfterAll
    public static void stopJavaFxToolkit() throws InterruptedException {
        if (javaFxApplicationThread == null) {
            return;
        }
        Platform.exit();
        javaFxApplicationThread.join(TimeUnit.SECONDS.toMillis(JAVAFX_TIMEOUT_SECONDS));
        assertFalse(javaFxApplicationThread.isAlive(), "JavaFX application thread did not stop");
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
                .withTags("weekend", "pace")
                .build();

        runOnJavaFxThread(() -> {
            Parent root = new PersonCard(person, 7).getRoot();
            Label name = getLabel(root, "name");
            Label phone = getLabel(root, "phone");
            Label email = getLabel(root, "email");
            Label ftp = getLabel(root, "ftp");
            Label experienceLevel = getLabel(root, "experienceLevel");
            Label locations = getLabel(root, "locations");
            FlowPane tags = getFlowPane(root, "tags");

            assertEquals("7. ", getLabel(root, "id").getText());
            assertEquals("Zelda Longname Cyclist", name.getText());
            assertEquals("+65 98765432", phone.getText());
            assertEquals("MixedCase.Email@Example.com", email.getText());
            assertEquals("450 W", ftp.getText());
            assertEquals("Advanced", experienceLevel.getText());
            assertEquals("East Coast Park, Marina Bay", locations.getText());
            assertEquals(List.of("pace", "weekend"), getTagTexts(tags));
            assertTrue(name.isWrapText());
            assertTrue(email.isWrapText());
            assertTrue(locations.isWrapText());

            for (Node node : List.of(name, phone, email, ftp, experienceLevel, locations, tags)) {
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
    public void constructor_emptyTags_hidesAndUnmanagesTagContainer() throws Exception {
        Person person = new PersonBuilder().withTags().build();

        runOnJavaFxThread(() -> {
            FlowPane tags = getFlowPane(new PersonCard(person, 1).getRoot(), "tags");

            assertTrue(tags.getChildren().isEmpty());
            assertFalse(tags.isVisible());
            assertFalse(tags.isManaged());
        });
    }

    @Test
    public void layout_supportedFields_usesTwoColumnsAndFullWidthBottomRows() throws Exception {
        Person person = new PersonBuilder().withTags("weekend").build();

        runOnJavaFxThread(() -> {
            Parent root = new PersonCard(person, 1).getRoot();
            Label phone = getLabel(root, "phone");
            Label email = getLabel(root, "email");
            Label ftp = getLabel(root, "ftp");
            Label experienceLevel = getLabel(root, "experienceLevel");
            Label locations = getLabel(root, "locations");
            FlowPane tags = getFlowPane(root, "tags");

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
            assertSame(cardContent, tags.getParent());
            assertTrue(cardContent.getChildren().indexOf(columns)
                    < cardContent.getChildren().indexOf(locationsRow));
            assertTrue(cardContent.getChildren().indexOf(locationsRow)
                    < cardContent.getChildren().indexOf(tags));

            assertNull(root.lookup("#address"));
            assertFalse(getAllLabelTexts(root).contains("Address"));
        });
    }

    private static Label getLabel(Parent root, String id) {
        return assertInstanceOf(Label.class, root.lookup("#" + id));
    }

    private static FlowPane getFlowPane(Parent root, String id) {
        return assertInstanceOf(FlowPane.class, root.lookup("#" + id));
    }

    private static List<String> getTagTexts(FlowPane tags) {
        return tags.getChildren().stream()
                .map(Label.class::cast)
                .map(Label::getText)
                .toList();
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
