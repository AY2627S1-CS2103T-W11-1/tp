package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ride.Ride;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.JavaFxToolkit;

public class MainWindowRidePanelTest {

    @TempDir
    public Path testFolder;

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        JavaFxToolkit.start();
    }

    @Test
    public void commandBox_quotedRideNotes_displaysSuccessAndRejectsMalformedInputWithoutChanges() throws Exception {
        Path dataFile = testFolder.resolve("quoted-notes.json");
        ModelManager model = new ModelManager();
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("quoted-prefs.json")));
        Logic logic = new LogicManager(model, storage);
        JavaFxToolkit.call(() -> {
            MainWindow window = new MainWindow(new Stage(), logic, dataFile);
            window.fillInnerParts();
            TextField input = (TextField) window.getRoot().getScene().lookup("#commandTextField");
            TextArea feedback = (TextArea) window.getRoot().getScene().lookup("#resultDisplay");
            enterCommand(input, "rideadd n/Camera Ride from/29-10-2026 1600 to/29-10-2026 1800 "
                    + "l/East Coast d/20 f/180 s/2 notes/\"Bring camera with aperture f/2\"");
            assertEquals("", input.getText());
            assertTrue(feedback.getText().contains("Notes: Bring camera with aperture f/2"));
            assertTrue(feedback.getText().contains("Minimum power: 180 W"));
            enterCommand(input, "rideedit 1 notes/\"Bring camera with aperture f/2\" s/5");
            assertEquals("", input.getText());
            assertTrue(feedback.getText().startsWith("Edited ride:"));
            assertTrue(feedback.getText().contains("Minimum power: 180 W"));
            Ride ride = model.getAddressBook().getRideList().getFirst();
            RideCard card = new RideCard(ride);
            assertEquals("Bring camera with aperture f/2", ((Label) card.getRoot().lookup("#notes")).getText());
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

            AddressBook before = new AddressBook(model.getAddressBook());
            String saved = Files.readString(dataFile);
            String malformed = "rideedit 1 notes/\"Bring camera with aperture f/2";
            enterCommand(input, malformed);
            assertEquals(malformed, input.getText());
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertTrue(feedback.getText().startsWith("Invalid quoted ride notes."));
            assertEquals(before, model.getAddressBook());
            assertEquals(saved, Files.readString(dataFile));
            enterCommand(input, "rideedit 1 notes/Bring water");
            assertEquals("", input.getText());
            assertFalse(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            return null;
        });
    }

    private static void enterCommand(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    @Test
    public void fillInnerParts_ridePanelTellsFilteredOutRidesApartFromNoRides() throws Exception {
        Path dataFile = testFolder.resolve("addressbook.json");
        Logic logic = new LogicManager(new ModelManager(), new StorageManager(
                new JsonAddressBookStorage(dataFile), new JsonUserPrefsStorage(testFolder.resolve("prefs.json"))));

        JavaFxToolkit.call(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, dataFile);
            mainWindow.fillInnerParts();
            Label placeholder = ridePlaceholder(mainWindow);

            assertEquals(RideListPanel.NO_RIDES_MESSAGE, placeholder.getText());

            logic.execute("rideadd n/Alpha Test Ride from/29-10-2026 0800 to/29-10-2026 1000 l/East Coast "
                    + "d/20 f/180 s/2 notes/Bring lights");
            logic.execute("ridefind n/No Such Ride");
            assertEquals(RideListPanel.NO_MATCHING_RIDES_MESSAGE, placeholder.getText());

            logic.execute("ridefind all");
            logic.execute("ridedelete 1");
            assertEquals(RideListPanel.NO_RIDES_MESSAGE, placeholder.getText());
            return null;
        });
    }

    private static Label ridePlaceholder(MainWindow mainWindow) {
        StackPane ridePanelHolder = (StackPane) mainWindow.getRoot().getScene().getRoot()
                .lookup("#rideListPanelPlaceholder");
        ListView<?> rideListView = (ListView<?>) ridePanelHolder.lookup("#rideListView");
        return (Label) rideListView.getPlaceholder();
    }
}
