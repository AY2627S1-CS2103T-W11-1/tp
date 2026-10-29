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
    public void toDisplayText_lowercaseWords_capitalisesEveryWord() {
        assertEquals("Marina Bay", new Location("marina bay").toDisplayText());
        assertEquals("East Coast Park", new Location("east coast park").toDisplayText());
        assertEquals("Punggol", new Location("punggol").toDisplayText());
    }

    @Test
    public void toDisplayText_alreadyCapitalised_isUnchanged() {
        assertEquals("East Coast Park", new Location("East Coast Park").toDisplayText());
    }

    @Test
    public void toDisplayText_restOfEachWord_isLeftAsEntered() {
        assertEquals("ECP", new Location("ECP").toDisplayText());
        assertEquals("East COAST", new Location("east COAST").toDisplayText());
        assertEquals("Bishan-10", new Location("bishan-10").toDisplayText());
        assertEquals("Queen's Road", new Location("queen's road").toDisplayText());
    }

    @Test
    public void toDisplayText_wordStartingWithDigit_isUnchanged() {
        assertEquals("10 Mile Loop", new Location("10 mile loop").toDisplayText());
    }

    @Test
    public void toDisplayText_anyInAnyCase_isShownAsAny() {
        // EP: exact reserved word any, ignoring case, versus an ordinary location beginning with the same letters.
        assertEquals("Any", new Location("any").toDisplayText());
        assertEquals("Any", new Location("ANY").toDisplayText());
        assertEquals("Anyang Road", new Location("anyang road").toDisplayText());
    }

    @Test
    public void toDisplayText_doesNotChangeTheStoredValue() {
        Location location = new Location("marina bay");

        location.toDisplayText();

        assertEquals("marina bay", location.value);
        assertEquals("marina bay", location.toString());
    }

    @Test
    public void parseStoredLocations_anyWithSpecificLocation_throwsParseException() {
        // EP: stored location preferences cannot combine reserved "any" with a specific location.
        List<String> locations = List.of("any", "Bishan");
        ParseException exception = assertThrows(ParseException.class, () -> ParserUtil.parseLocations(locations));
        assertEquals("Location 'any' cannot be combined with other locations.", exception.getMessage());
    }
}
