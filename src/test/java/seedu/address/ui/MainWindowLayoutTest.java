package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnJavaFxThread;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.MinimumPower;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Checks the real main-window layout without displaying a desktop window.
 */
public class MainWindowLayoutTest {
    private static final String PHONE = "+65 91234567";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void resultPane_numericErrorsAtDefaultWidth_doNotRequireScrolling() throws Exception {
        ModelManager model = new ModelManager();
        model.addRide(new RideBuilder().build());
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("book.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        LogicManager logic = new LogicManager(model, storage);

        runOnJavaFxThread(() -> {
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, logic, storage.getAddressBookFilePath());
            window.fillInnerParts();
            VBox root = (VBox) stage.getScene().getRoot();
            TextField input = (TextField) root.lookup("#commandTextField");
            TextArea feedback = (TextArea) root.lookup("#resultDisplay");
            for (String field : new String[] {"d/1000000", "d/0000001", "f/0001"}) {
                input.setText("rideedit 1 " + field);
                input.fireEvent(new ActionEvent());
                layout(root, 740, 600);
                String expectedMessage = field.startsWith("d/")
                        ? Distance.MESSAGE_CONSTRAINTS : MinimumPower.MESSAGE_CONSTRAINTS;
                assertEquals(expectedMessage, feedback.getText());
                assertFalse(feedback.isWrapText());
                for (Orientation orientation : Orientation.values()) {
                    ScrollBar scrollBar = feedback.lookupAll(".scroll-bar").stream()
                            .map(ScrollBar.class::cast)
                            .filter(bar -> bar.getOrientation() == orientation)
                            .findFirst().orElseThrow();
                    assertFalse(scrollBar.isVisible(), orientation.toString());
                }
            }
        });
    }

    @Test
    public void resultPane_confirmationAndConstrainedWindow_staysBoundedAndResets() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withPhone(PHONE).withFtp("250").build());
        model.addRide(new RideBuilder().withRoster(PHONE).build());
        model.addRide(new RideBuilder().withName("Proposed Ride").build());
        for (int i = 0; i < 20; i++) {
            model.addRide(new RideBuilder().withName("Conflicting Ride " + i).withRoster(PHONE).build());
        }
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("book.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        LogicManager logic = new LogicManager(model, storage);

        runOnJavaFxThread(() -> {
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, logic, storage.getAddressBookFilePath());
            window.fillInnerParts();
            VBox root = (VBox) stage.getScene().getRoot();
            root.applyCss();
            Region pane = (Region) root.lookup("#resultDisplayPlaceholder");
            TextField input = (TextField) root.lookup("#commandTextField");

            layout(root, 740, 600);
            assertEquals(100, pane.getHeight());
            assertEquals(100, pane.getMinHeight());
            assertEquals(200, pane.getMaxHeight());

            input.setText("rideaddcyclist 2 " + PHONE);
            input.fireEvent(new ActionEvent());
            layout(root, 740, 600);
            assertEquals(200, pane.getPrefHeight());
            assertTrue(pane.getHeight() > 100);
            assertTrue(pane.getHeight() <= 200);

            layout(root, 600, 350);
            assertTrue(pane.getHeight() >= 100);
            assertTrue(pane.getHeight() < 200);
            assertEquals(200, pane.getPrefHeight());

            layout(root, 1600, 1200);
            assertEquals(200, pane.getHeight());
            input.setText("maybe");
            input.fireEvent(new ActionEvent());
            layout(root, 740, 600);
            assertEquals(200, pane.getPrefHeight());
            assertTrue(pane.getHeight() >= 100);
            assertTrue(pane.getHeight() <= 200);

            input.setText("list");
            input.fireEvent(new ActionEvent());
            layout(root, 740, 600);
            assertEquals(100, pane.getHeight());
            assertTrue(model.getAddressBook().getRideList().get(1).getRoster().isEmpty());
        });
    }

    private void layout(VBox root, double width, double height) {
        root.resize(width, height);
        root.applyCss();
        root.requestLayout();
        root.layout();
    }
}
