package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;

public class LocationTest {
    @Test
    public void isValidLocation_validValues_returnsTrue() {
        // EP: valid locations may contain spaces, hyphens, or apostrophes.
        assertTrue(Location.isValidLocation("East Coast Park"));
        assertTrue(Location.isValidLocation("Bishan-10"));
        assertTrue(Location.isValidLocation("Queen's Road"));
    }

    @Test
    public void isValidLocation_invalidValues_returnsFalse() {
        // EP: locations are invalid when blank or containing unsupported characters.
        assertFalse(Location.isValidLocation(""));
        assertFalse(Location.isValidLocation("   "));
        assertFalse(Location.isValidLocation("East@Park"));
    }

    @Test
    public void equals_caseInsensitive_returnsTrue() {
        // EP: location equality ignores letter case.
        assertEquals(new Location("East Coast Park"), new Location("east coast park"));
    }

    @Test
    public void parseStoredLocations_anyWithSpecificLocation_throwsParseException() {
        // EP: stored location preferences cannot combine reserved "any" with a specific location.
        List<String> locations = List.of("any", "Bishan");
        ParseException exception = assertThrows(ParseException.class, () -> ParserUtil.parseLocations(locations));
        assertEquals("Location 'any' cannot be combined with other locations.", exception.getMessage());
    }
}
