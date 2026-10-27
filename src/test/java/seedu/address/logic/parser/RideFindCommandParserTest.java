package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class RideFindCommandParserTest {
    private final RideFindCommandParser parser = new RideFindCommandParser();

    @Test
    public void parse_quotes_doNotProtectPrefixesInFind() {
        assertThrows(ParseException.class, () -> parser.parse("notes/\"camera f/2\""));
        assertThrows(ParseException.class, () -> parser.parse("notes/\"camera notes/other\""));
    }

    @Test
    public void parse_eachCriterionAlone_success() {
        assertDoesNotThrow(() -> parser.parse("n/Sunday Sunrise Ride"));
        assertDoesNotThrow(() -> parser.parse("from/18-10-2026"));
        assertDoesNotThrow(() -> parser.parse("to/18-10-2026 2359"));
        assertDoesNotThrow(() -> parser.parse("l/East Coast"));
        assertDoesNotThrow(() -> parser.parse("d/45"));
        assertDoesNotThrow(() -> parser.parse("f/180"));
        assertDoesNotThrow(() -> parser.parse("s/12"));
        assertDoesNotThrow(() -> parser.parse("notes/Bring lights"));
    }

    @Test
    public void parse_noCriteria_failure() {
        assertThrows(ParseException.class, () -> parser.parse(" "));
    }

    @Test
    public void parse_all_success() {
        assertDoesNotThrow(() -> parser.parse("all"));
    }

    @Test
    public void parse_allWithWhitespace_success() {
        assertDoesNotThrow(() -> parser.parse("  all  "));
    }

    @Test
    public void parse_allWithArguments_failure() {
        assertThrows(ParseException.class, () -> parser.parse("all n/Ride"));
    }

    @Test
    public void parse_allWithDifferentCase_failure() {
        assertThrows(ParseException.class, () -> parser.parse("ALL"));
    }

    @Test
    public void parse_toBeforeFrom_failure() {
        assertThrows(ParseException.class, () -> parser.parse(
                "from/19-10-2026 0000 to/18-10-2026 2359"));
    }

    @Test
    public void parse_dateOnlyBounds_success() {
        assertDoesNotThrow(() -> parser.parse("from/18-10-2026 to/18-10-2026"));
    }

    @Test
    public void parse_duplicateCriterion_failure() {
        assertThrows(ParseException.class, () -> parser.parse("l/East Coast l/Tampines"));
    }

    @Test
    public void parse_numericBoundaryValues_success() {
        assertDoesNotThrow(() -> parser.parse("d/0.01"));
        assertDoesNotThrow(() -> parser.parse("f/1"));
        assertDoesNotThrow(() -> parser.parse("f/600"));
        assertDoesNotThrow(() -> parser.parse("s/1"));
    }

    @Test
    public void parse_numericValuesOutsideConstraints_failure() {
        assertThrows(ParseException.class, () -> parser.parse("d/0"));
        assertThrows(ParseException.class, () -> parser.parse("f/0"));
        assertThrows(ParseException.class, () -> parser.parse("f/601"));
        assertThrows(ParseException.class, () -> parser.parse("s/0"));
    }

    @Test
    public void parse_invalidDateOrTime_failure() {
        assertThrows(ParseException.class, () -> parser.parse("from/31-02-2026"));
        assertThrows(ParseException.class, () -> parser.parse("to/18-10-2026 2460"));
        assertThrows(ParseException.class, () -> parser.parse("from/18-10-2026 063"));
    }

    @Test
    public void parse_duplicateDateBounds_failure() {
        assertThrows(ParseException.class, () -> parser.parse("from/18-10-2026 from/19-10-2026"));
        assertThrows(ParseException.class, () -> parser.parse("to/18-10-2026 to/19-10-2026"));
    }
}
