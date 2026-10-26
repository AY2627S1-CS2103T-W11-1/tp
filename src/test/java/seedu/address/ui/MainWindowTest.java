package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.JavaFxToolkit;

public class MainWindowTest {

    @TempDir
    public Path testFolder;

    @BeforeAll
    public static void startJavaFxToolkit() throws InterruptedException {
        JavaFxToolkit.start();
    }

    @Test
    public void helpMenuText_everyCommand_hasBoldCommandWordThenTheRestOfTheFormat() throws Exception {
        JavaFxToolkit.call(() -> {
            for (CommandTemplates.Template template : CommandTemplates.ALL) {
                TextFlow text = MainWindow.helpMenuText(template, 1000);

                assertEquals(2, text.getChildren().size());
                Text commandWord = (Text) text.getChildren().get(0);
                Text format = (Text) text.getChildren().get(1);
                assertEquals(template.commandWord(), commandWord.getText());
                assertTrue(commandWord.getStyleClass().contains("help-menu-command"));
                assertFalse(format.getStyleClass().contains("help-menu-command"));
                // the format only differs from the displayed text in that spaces inside brackets cannot break
                assertEquals(template.display(), (commandWord.getText() + format.getText()).replace('\u00A0', ' '));
            }
            return null;
        });
    }

    @Test
    public void helpMenuText_longFormat_wrapsInsteadOfGrowingPastTheMaximumWidth() throws Exception {
        CommandTemplates.Template rideEdit = CommandTemplates.ALL.stream()
                .filter(template -> template.commandWord().equals("rideedit")).findFirst().orElseThrow();

        JavaFxToolkit.call(() -> {
            TextFlow wide = MainWindow.helpMenuText(rideEdit, 5000);
            TextFlow narrow = MainWindow.helpMenuText(rideEdit, 300);

            assertTrue(narrow.prefWidth(-1) <= 300);
            assertTrue(wide.prefWidth(-1) > 300);
            // a narrow menu item is taller, because it needs more lines
            assertTrue(narrow.prefHeight(-1) > wide.prefHeight(-1));
            // asking for the height at an explicit width gives the same answer as at the width it will be given
            assertEquals(narrow.prefHeight(-1), narrow.prefHeight(300), 0.001);
            return null;
        });
    }

    @Test
    public void helpMenu_opened_fitsItsEntriesInsideTheWindow() throws Exception {
        Logic logic = createLogic();

        JavaFxToolkit.call(() -> {
            double wideWindowEntryWidth = firstHelpEntryMaxWidthWhenOpened(logic, 1000);
            double narrowWindowEntryWidth = firstHelpEntryMaxWidthWhenOpened(logic, 720);

            // a wider window gives wider entries, and an entry is always narrower than the window it is in
            assertTrue(wideWindowEntryWidth > narrowWindowEntryWidth);
            assertTrue(wideWindowEntryWidth < 1000);
            assertTrue(narrowWindowEntryWidth < 720);
            assertTrue(narrowWindowEntryWidth >= 200);
            return null;
        });
    }

    /**
     * Shows a main window {@code windowWidth} wide, opens its Help menu and returns the maximum width given to the
     * first command entry. Must be called on the JavaFX thread.
     */
    private double firstHelpEntryMaxWidthWhenOpened(Logic logic, double windowWidth) {
        MainWindow mainWindow = new MainWindow(new Stage(), logic, testFolder.resolve("addressbook.json"));
        mainWindow.fillInnerParts();
        Stage stage = mainWindow.getPrimaryStage();
        stage.setWidth(windowWidth);
        stage.show();
        try {
            stage.getScene().getRoot().applyCss();
            stage.getScene().getRoot().layout();
            MenuBar menuBar = (MenuBar) stage.getScene().getRoot().lookup(".menu-bar");
            Menu helpMenu = menuBar.getMenus().stream()
                    .filter(menu -> menu.getText().equals("Help")).findFirst().orElseThrow();
            helpMenu.show();
            double maxWidth = ((CustomMenuItem) helpMenu.getItems().get(0)).getContent() instanceof TextFlow text
                    ? text.getMaxWidth() : Double.NaN;
            helpMenu.hide();
            return maxWidth;
        } finally {
            stage.hide();
        }
    }

    private Logic createLogic() {
        return new LogicManager(new ModelManager(), new StorageManager(
                new JsonAddressBookStorage(testFolder.resolve("addressbook.json")),
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json"))));
    }

    @Test
    public void helpMenuLabelWidth_widerWindow_givesWiderMenu() {
        double narrow = MainWindow.helpMenuLabelWidth(740, 55);
        double wide = MainWindow.helpMenuLabelWidth(1000, 55);

        assertTrue(wide > narrow);
        assertEquals(260, wide - narrow, 0.001);
    }

    @Test
    public void helpMenuLabelWidth_leavesRoomBetweenTheMenuAndTheRightEdgeOfTheWindow() {
        double windowWidth = 740;
        double menuLeft = 55;

        double labelWidth = MainWindow.helpMenuLabelWidth(windowWidth, menuLeft);

        // the menu starts at menuLeft, so the label must end before the right edge of the window
        assertTrue(menuLeft + labelWidth < windowWidth);
    }

    @Test
    public void helpMenuLabelWidth_veryNarrowWindow_neverBelowReadableMinimum() {
        assertEquals(200, MainWindow.helpMenuLabelWidth(100, 55), 0.001);
        assertEquals(200, MainWindow.helpMenuLabelWidth(0, 0), 0.001);
    }
}
