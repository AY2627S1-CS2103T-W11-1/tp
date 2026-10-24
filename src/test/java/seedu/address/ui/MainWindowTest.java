package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MainWindowTest {

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
