package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import seedu.address.testutil.JavaFxToolkit;

public class MainWindowTest {

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
            return null;
        });
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
