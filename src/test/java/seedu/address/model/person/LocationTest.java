package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

public class LocationTest {
    @Test
    public void isValidLocation_validValues_returnsTrue() {
        assertTrue(Location.isValidLocation("East Coast Park"));
        assertTrue(Location.isValidLocation("Bishan-10"));
        assertTrue(Location.isValidLocation("Queen's Road"));
    }

    @Test
    public void isValidLocation_invalidValues_returnsFalse() {
        assertFalse(Location.isValidLocation(""));
        assertFalse(Location.isValidLocation("   "));
        assertFalse(Location.isValidLocation("East@Park"));
    }

    @Test
    public void equals_caseInsensitive_returnsTrue() {
        assertEquals(new Location("East Coast Park"), new Location("east coast park"));
    }

    @Test
    public void parseLocations_anyWithOtherLocation_throwsParseException() {
        ParseException exception = org.junit.jupiter.api.Assertions.assertThrows(ParseException.class,
                () -> ParserUtil.parseLocations(List.of("any", "Bishan")));
        assertEquals("Location 'any' cannot be combined with other locations.", exception.getMessage());
    }
}
