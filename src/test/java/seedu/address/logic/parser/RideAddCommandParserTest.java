package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DISTANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_SIZE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RideAddCommand;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;
import seedu.address.testutil.RideBuilder;

public class RideAddCommandParserTest {

    private static final String NAME_DESC = " " + PREFIX_NAME + "Sunday Sunrise Ride";
    private static final String FROM_DESC = " " + PREFIX_FROM + "18-10-2026 0630";
    private static final String TO_DESC = " " + PREFIX_TO + "18-10-2026 0930";
    private static final String LOCATION_DESC = " " + PREFIX_LOCATION + "East Coast";
    private static final String DISTANCE_DESC = " " + PREFIX_DISTANCE + "45";
    private static final String POWER_DESC = " " + PREFIX_FTP + "180";
    private static final String SIZE_DESC = " " + PREFIX_GROUP_SIZE + "12";
    private static final String NOTES_DESC = " " + PREFIX_NOTES + "Bring lights and a spare tube";

    private static final String ALL_REQUIRED = NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC
            + POWER_DESC + SIZE_DESC;
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RideAddCommand.MESSAGE_USAGE);

    private RideAddCommandParser parser = new RideAddCommandParser();

    @Test
    public void parse_numericDigitLimits_propagatesConstraintsAndAcceptsThreeDigitPower() {
        // Boundary values: 7 integer distance digits exceed maximum 6, and 4 power digits exceed maximum 3.
        // Leading zeros still count toward those digit limits.
        for (String distance : new String[] {"1000000", "0000001"}) {
            assertParseFailure(parser, ALL_REQUIRED.replace("d/45", "d/" + distance), Distance.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, ALL_REQUIRED.replace("f/180", "f/0001"), MinimumPower.MESSAGE_CONSTRAINTS);
        assertParseSuccess(parser, ALL_REQUIRED.replace("f/180", "f/001"),
                new RideAddCommand(new RideBuilder().withMinimumPower("1").build()));
    }

    @Test
    public void parse_unicodeNotes_enforcesBlankAndWordLimitRules() {
        // EP: ASCII/Unicode whitespace and quoted/unquoted notes.
        // Boundary values: blank notes, valid 10-word notes, and invalid 11-word notes.
        for (String separator : new String[] {" ", "\t", "\r\n", "\u2003", "\u00a0", "\u202f", "\u3000"}) {
            assertParseFailure(parser, ALL_REQUIRED + " notes/" + separator, Notes.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, ALL_REQUIRED + " notes/\"" + separator + "\"",
                    ArgumentTokenizer.MESSAGE_INVALID_QUOTED_NOTES);
            String ten = "one two three four five six seven eight nine ten";
            RideAddCommand expected = new RideAddCommand(new RideBuilder().withNotes(ten).build());
            for (String quote : new String[] {"", "\""}) {
                assertParseSuccess(parser, ALL_REQUIRED + " notes/" + quote + separator
                        + ten.replace(" ", separator) + separator + quote, expected);
                assertParseFailure(parser, ALL_REQUIRED + " notes/" + quote
                        + (ten + " eleven").replace(" ", separator) + quote, Notes.MESSAGE_CONSTRAINTS);
            }
        }
    }

    @Test
    public void parse_quotedNotes_allPrefixesAreLiteralAndOrderingIsFlexible() {
        for (String notes : new String[] {"Bring camera with aperture f/2",
            "n/ from/ to/ l/ d/ f/ s/ notes/ q/test", "  Bring   water  ", "Bring \\\\camera",
            "one two three four five six seven eight nine ten"}) {
            RideAddCommand expected = new RideAddCommand(new RideBuilder().withNotes(notes).build());
            assertParseSuccess(parser, ALL_REQUIRED + " notes/\"" + notes + "\"", expected);
            assertParseSuccess(parser, " notes/\"" + notes + "\"" + ALL_REQUIRED, expected);
            assertParseSuccess(parser, NAME_DESC + " notes/\"" + notes + "\"" + FROM_DESC + TO_DESC
                    + LOCATION_DESC + DISTANCE_DESC + POWER_DESC + SIZE_DESC, expected);
        }
    }

    @Test
    public void parse_invalidQuotedNotes_failure() {
        // EP: unclosed/blank quoted notes, extra trailing text, and embedded quotes.
        // Boundary value: the final quoted input has 11 words, one above the maximum 10.
        for (String notes : new String[] {"\"Bring camera f/2", "\"\"", "\"  \"", "\"Bring\"extra",
            "\"Bring \"camera\"\""}) {
            assertParseFailure(parser, ALL_REQUIRED + " notes/" + notes,
                    ArgumentTokenizer.MESSAGE_INVALID_QUOTED_NOTES);
        }
        assertParseFailure(parser, ALL_REQUIRED
                + " notes/\"one two three four five six seven eight nine ten eleven\"", Notes.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, ALL_REQUIRED + " notes/\"f/2\" f/200",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FTP));
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Ride expectedRide = new RideBuilder().withNotes("Bring lights and a spare tube").build();

        assertParseSuccess(parser, ALL_REQUIRED + NOTES_DESC, new RideAddCommand(expectedRide));
    }

    @Test
    public void parse_notesMissing_success() {
        // EP: the optional notes field is absent rather than supplied with an empty value.
        assertParseSuccess(parser, ALL_REQUIRED, new RideAddCommand(new RideBuilder().build()));
    }

    @Test
    public void parse_whitespaceOnlyPreamble_success() {
        assertParseSuccess(parser, "\t  \r  \n" + ALL_REQUIRED, new RideAddCommand(new RideBuilder().build()));
    }

    @Test
    public void parse_fieldsInAnyOrder_success() {
        String reversed = SIZE_DESC + POWER_DESC + DISTANCE_DESC + LOCATION_DESC + TO_DESC + FROM_DESC + NAME_DESC;

        assertParseSuccess(parser, reversed, new RideAddCommand(new RideBuilder().build()));
    }

    @Test
    public void parse_valuesWithExtraWhitespace_areTrimmed() {
        String spaced = " n/  Sunday Sunrise Ride   from/ 18-10-2026 0630   to/18-10-2026 0930  l/ East Coast  "
                + "d/ 45  f/ 180  s/ 12  ";

        assertParseSuccess(parser, spaced, new RideAddCommand(new RideBuilder().build()));
    }

    @Test
    public void parse_distanceWithDecimals_success() {
        Ride expectedRide = new RideBuilder().withDistance("45.5").build();

        assertParseSuccess(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + " d/45.50" + POWER_DESC
                + SIZE_DESC, new RideAddCommand(expectedRide));
    }

    @Test
    public void parse_endSameAsStart_success() {
        // Boundary value: zero-duration period; the end equals the start.
        Ride expectedRide = new RideBuilder().withTo("18-10-2026 0630").build();

        assertParseSuccess(parser, NAME_DESC + FROM_DESC + " to/18-10-2026 0630" + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, new RideAddCommand(expectedRide));
    }

    @Test
    public void parse_endOnLaterDay_success() {
        Ride expectedRide = new RideBuilder().withTo("19-10-2026 0100").build();

        assertParseSuccess(parser, NAME_DESC + FROM_DESC + " to/19-10-2026 0100" + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, new RideAddCommand(expectedRide));
    }

    @Test
    public void parse_notesWithTenWords_success() {
        String tenWords = "one two three four five six seven eight nine ten";
        Ride expectedRide = new RideBuilder().withNotes(tenWords).build();

        assertParseSuccess(parser, ALL_REQUIRED + " notes/" + tenWords, new RideAddCommand(expectedRide));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        // missing name
        assertParseFailure(parser, FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC + SIZE_DESC,
                INVALID_FORMAT_MESSAGE);

        // missing from
        assertParseFailure(parser, NAME_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC + SIZE_DESC,
                INVALID_FORMAT_MESSAGE);

        // missing to
        assertParseFailure(parser, NAME_DESC + FROM_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC + SIZE_DESC,
                INVALID_FORMAT_MESSAGE);

        // missing location
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + DISTANCE_DESC + POWER_DESC + SIZE_DESC,
                INVALID_FORMAT_MESSAGE);

        // missing distance
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + POWER_DESC + SIZE_DESC,
                INVALID_FORMAT_MESSAGE);

        // missing minimum power
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + SIZE_DESC,
                INVALID_FORMAT_MESSAGE);

        // missing group size
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC,
                INVALID_FORMAT_MESSAGE);

        // only the optional notes given
        assertParseFailure(parser, NOTES_DESC, INVALID_FORMAT_MESSAGE);

        // no arguments at all
        assertParseFailure(parser, "", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "     ", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_prefixWithoutSpaceBefore_failure() {
        // the prefix is not recognised, so the field counts as missing
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC
                + "s/12", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, "preamble" + ALL_REQUIRED, INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, " 4" + ALL_REQUIRED, INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_repeatedPrefix_failure() {
        assertParseFailure(parser, " n/Another Ride" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " from/18-10-2026 0600" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FROM));
        assertParseFailure(parser, " to/18-10-2026 1000" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO));
        assertParseFailure(parser, " l/Punggol" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_LOCATION));
        assertParseFailure(parser, " d/50" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DISTANCE));
        assertParseFailure(parser, " f/200" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FTP));
        assertParseFailure(parser, " s/20" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GROUP_SIZE));
        assertParseFailure(parser, ALL_REQUIRED + NOTES_DESC + " notes/More notes",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTES));

        // an invalid repeated value is still reported as a repeat
        assertParseFailure(parser, " d/abc" + ALL_REQUIRED,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DISTANCE));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, " n/Sunday & Sunrise" + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, Name.MESSAGE_CONSTRAINTS);

        // blank name
        assertParseFailure(parser, " n/ " + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC
                + SIZE_DESC, Name.MESSAGE_CONSTRAINTS);

        // invalid from
        assertParseFailure(parser, NAME_DESC + " from/2026-10-18 0630" + TO_DESC + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, RideDateTime.MESSAGE_CONSTRAINTS);

        // invalid to
        assertParseFailure(parser, NAME_DESC + FROM_DESC + " to/18-10-2026 25:00" + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, RideDateTime.MESSAGE_CONSTRAINTS);

        // date that does not exist
        assertParseFailure(parser, NAME_DESC + " from/31-02-2026 0630" + TO_DESC + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, RideDateTime.MESSAGE_CONSTRAINTS);

        // time out of range
        assertParseFailure(parser, NAME_DESC + FROM_DESC + " to/18-10-2026 2400" + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, RideDateTime.MESSAGE_CONSTRAINTS);

        // date without a time
        assertParseFailure(parser, NAME_DESC + " from/18-10-2026" + TO_DESC + LOCATION_DESC + DISTANCE_DESC
                + POWER_DESC + SIZE_DESC, RideDateTime.MESSAGE_CONSTRAINTS);

        // empty from
        assertParseFailure(parser, NAME_DESC + " from/ " + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC
                + SIZE_DESC, RideDateTime.MESSAGE_CONSTRAINTS);

        // invalid location
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + " l/East Coast!" + DISTANCE_DESC + POWER_DESC
                + SIZE_DESC, Location.MESSAGE_CONSTRAINTS);

        // blank location
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + " l/ " + DISTANCE_DESC + POWER_DESC
                + SIZE_DESC, Location.MESSAGE_CONSTRAINTS);

        // invalid distance
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + " d/forty" + POWER_DESC
                + SIZE_DESC, Distance.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + " d/0" + POWER_DESC
                + SIZE_DESC, Distance.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + " d/-4" + POWER_DESC
                + SIZE_DESC, Distance.MESSAGE_CONSTRAINTS);

        // invalid minimum power
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + " f/601"
                + SIZE_DESC, MinimumPower.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + " f/0"
                + SIZE_DESC, MinimumPower.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + " f/strong"
                + SIZE_DESC, MinimumPower.MESSAGE_CONSTRAINTS);

        // invalid group size
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC
                + " s/0", GroupSize.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC
                + " s/-2", GroupSize.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + FROM_DESC + TO_DESC + LOCATION_DESC + DISTANCE_DESC + POWER_DESC
                + " s/many", GroupSize.MESSAGE_CONSTRAINTS);

        // notes with more than 10 words
        assertParseFailure(parser, ALL_REQUIRED + " notes/one two three four five six seven eight nine ten eleven",
                Notes.MESSAGE_CONSTRAINTS);

        // blank notes
        assertParseFailure(parser, ALL_REQUIRED + " notes/ ", Notes.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, ALL_REQUIRED + " notes/", Notes.MESSAGE_CONSTRAINTS);

        // two invalid values, only the first one is reported
        assertParseFailure(parser, " n/Bad&Name" + FROM_DESC + TO_DESC + LOCATION_DESC + " d/0" + POWER_DESC
                + SIZE_DESC, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_endBeforeStart_failure() {
        // EP: end precedes start within a day, across dates, or across years.
        assertParseFailure(parser, NAME_DESC + " from/18-10-2026 0930 to/18-10-2026 0929" + LOCATION_DESC
                + DISTANCE_DESC + POWER_DESC + SIZE_DESC, Ride.MESSAGE_INVALID_PERIOD);

        // earlier date but later time
        assertParseFailure(parser, NAME_DESC + " from/18-10-2026 0630 to/17-10-2026 2359" + LOCATION_DESC
                + DISTANCE_DESC + POWER_DESC + SIZE_DESC, Ride.MESSAGE_INVALID_PERIOD);

        // earlier year
        assertParseFailure(parser, NAME_DESC + " from/18-10-2026 0630 to/18-10-2025 0630" + LOCATION_DESC
                + DISTANCE_DESC + POWER_DESC + SIZE_DESC, Ride.MESSAGE_INVALID_PERIOD);
    }

    @Test
    public void parse_endBeforeStartAndInvalidOtherValue_reportsInvalidValueFirst() {
        assertParseFailure(parser, NAME_DESC + " from/18-10-2026 0930 to/18-10-2026 0630" + LOCATION_DESC
                + " d/0" + POWER_DESC + SIZE_DESC, Distance.MESSAGE_CONSTRAINTS);
    }
}
