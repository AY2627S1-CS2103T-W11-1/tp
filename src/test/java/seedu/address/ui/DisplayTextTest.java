package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DisplayTextTest {

    @Test
    public void keepTogether_replacesEverySpaceWithNoBreakSpace() {
        assertEquals("+65 91234567", DisplayText.keepTogether("+65 91234567"));
        assertEquals("145.75 km", DisplayText.keepTogether("145.75 km"));
        assertEquals("a b c", DisplayText.keepTogether("a b c"));
    }

    @Test
    public void keepTogether_leavesTextWithoutSpacesUnchanged() {
        assertEquals("", DisplayText.keepTogether(""));
        assertEquals("alexyeoh@example.com", DisplayText.keepTogether("alexyeoh@example.com"));
    }

    @Test
    public void keepTogether_resultContainsNoOrdinarySpace() {
        String result = DisplayText.keepTogether("a  b   c");

        assertFalse(result.contains(" "));
        assertTrue(result.contains(" "));
        assertEquals("a  b   c".length(), result.length());
    }
}
