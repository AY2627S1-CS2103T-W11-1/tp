package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
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
