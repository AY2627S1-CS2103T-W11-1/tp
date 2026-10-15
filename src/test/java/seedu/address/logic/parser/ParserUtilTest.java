package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.RideDateTime;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_EXPERIENCE_LEVEL = "Expert";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "+65 123456";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_EXPERIENCE_LEVEL = "Beginner";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseExperienceLevel_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseExperienceLevel((String) null));
    }

    @Test
    public void parseExperienceLevel_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseExperienceLevel(INVALID_EXPERIENCE_LEVEL));
    }

    @Test
    public void parseExperienceLevel_validValueWithoutWhitespace_returnsExperienceLevel() throws Exception {
        ExperienceLevel expectedExperienceLevel = new ExperienceLevel(VALID_EXPERIENCE_LEVEL);
        assertEquals(expectedExperienceLevel, ParserUtil.parseExperienceLevel(VALID_EXPERIENCE_LEVEL));
    }

    @Test
    public void parseExperienceLevel_validValueWithWhitespace_returnsTrimmedExperienceLevel() throws Exception {
        String experienceLevelWithWhitespace = WHITESPACE + VALID_EXPERIENCE_LEVEL + WHITESPACE;
        ExperienceLevel expectedExperienceLevel = new ExperienceLevel(VALID_EXPERIENCE_LEVEL);
        assertEquals(expectedExperienceLevel, ParserUtil.parseExperienceLevel(experienceLevelWithWhitespace));
    }

    @Test
    public void parseExperienceLevel_validValueDifferentCase_returnsCanonicalExperienceLevel() throws Exception {
        ExperienceLevel expectedExperienceLevel = new ExperienceLevel(VALID_EXPERIENCE_LEVEL);
        assertEquals(expectedExperienceLevel, ParserUtil.parseExperienceLevel("bEgInNeR"));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }


    @Test
    public void parseLocation_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseLocation(null));
    }

    @Test
    public void parseLocation_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseLocation(" "));
        assertThrows(ParseException.class, () -> ParserUtil.parseLocation("East Coast!"));
    }

    @Test
    public void parseLocation_validValueWithWhitespace_returnsTrimmedLocation() throws Exception {
        assertEquals(new Location("East Coast"), ParserUtil.parseLocation(WHITESPACE + "East Coast" + WHITESPACE));
    }

    @Test
    public void parseRideDateTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRideDateTime(null));
    }

    @Test
    public void parseRideDateTime_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, RideDateTime.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseRideDateTime("2026-10-18 0630"));
        assertThrows(ParseException.class, RideDateTime.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseRideDateTime("31-02-2026 0630"));
    }

    @Test
    public void parseRideDateTime_validValueWithWhitespace_returnsTrimmedDateTime() throws Exception {
        assertEquals(new RideDateTime("18-10-2026 0630"),
                ParserUtil.parseRideDateTime(WHITESPACE + "18-10-2026 0630" + WHITESPACE));
    }

    @Test
    public void parseDistance_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDistance(null));
    }

    @Test
    public void parseDistance_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Distance.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseDistance("0"));
        assertThrows(ParseException.class, Distance.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseDistance("far"));
    }

    @Test
    public void parseDistance_validValueWithWhitespace_returnsTrimmedDistance() throws Exception {
        assertEquals(new Distance("45.5"), ParserUtil.parseDistance(WHITESPACE + "45.5" + WHITESPACE));
    }

    @Test
    public void parseMinimumPower_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMinimumPower(null));
    }

    @Test
    public void parseMinimumPower_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, MinimumPower.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseMinimumPower("601"));
        assertThrows(ParseException.class, MinimumPower.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseMinimumPower("0"));
    }

    @Test
    public void parseMinimumPower_validValueWithWhitespace_returnsTrimmedMinimumPower() throws Exception {
        assertEquals(new MinimumPower("180"), ParserUtil.parseMinimumPower(WHITESPACE + "180" + WHITESPACE));
    }

    @Test
    public void parseGroupSize_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseGroupSize(null));
    }

    @Test
    public void parseGroupSize_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, GroupSize.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseGroupSize("0"));
        assertThrows(ParseException.class, GroupSize.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseGroupSize("2.5"));
    }

    @Test
    public void parseGroupSize_validValueWithWhitespace_returnsTrimmedGroupSize() throws Exception {
        assertEquals(new GroupSize("12"), ParserUtil.parseGroupSize(WHITESPACE + "12" + WHITESPACE));
    }

    @Test
    public void parseNotes_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNotes(null));
    }

    @Test
    public void parseNotes_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Notes.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseNotes("   "));
        assertThrows(ParseException.class, Notes.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseNotes("one two three four five six seven eight nine ten eleven"));
    }

    @Test
    public void parseNotes_validValueWithWhitespace_returnsTrimmedNotes() throws Exception {
        assertEquals(new Notes("Bring lights"), ParserUtil.parseNotes(WHITESPACE + "Bring lights" + WHITESPACE));
    }
}
