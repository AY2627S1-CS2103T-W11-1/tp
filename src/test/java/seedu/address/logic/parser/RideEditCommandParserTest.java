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

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RideEditCommand;
import seedu.address.logic.commands.RideEditCommand.EditRideDescriptor;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.RideDateTime;

public class RideEditCommandParserTest {

    private static final String VALID_ID = " 101";
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RideEditCommand.MESSAGE_USAGE);

    private final RideEditCommandParser parser = new RideEditCommandParser();

    @Test
    public void parse_distanceDigitLimits_reportsCompleteConstraints() {
        String expectedMessage = "Distance should be a positive number of kilometres from 0.01 to 999999.99, "
                + "with 1 to 6 digits before the decimal point and, if present, 1 to 2 digits after it. "
                + "Leading zeroes count towards the digit limits. For example, 45 or 45.5.";
        assertParseFailure(parser, " 1 d/1000000", expectedMessage);
        assertParseFailure(parser, " 1 d/0000001", expectedMessage);
    }

    @Test
    public void parse_powerDigitLimit_reportsCompleteConstraint() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setMinimumPower(new MinimumPower("1"));
        assertParseSuccess(parser, " 1 f/001", new RideEditCommand(1, descriptor));
        assertParseFailure(parser, " 1 f/0001",
                "Minimum power should be a whole number from 1 to 600 watts, using at most 3 digits. "
                        + "Leading zeroes count towards the digit limit.");
    }

    @Test
    public void parse_unicodeNotes_enforcesBlankAndWordLimitRules() {
        for (String separator : new String[] {" ", "\t", "\r\n", "\u2003", "\u00a0", "\u202f", "\u3000"}) {
            EditRideDescriptor cleared = new EditRideDescriptor();
            cleared.setNotes(Optional.empty());
            assertParseSuccess(parser, VALID_ID + " notes/" + separator, new RideEditCommand(101, cleared));
            assertParseFailure(parser, VALID_ID + " notes/\"" + separator + "\"",
                    ArgumentTokenizer.MESSAGE_INVALID_QUOTED_NOTES);
            String ten = "one two three four five six seven eight nine ten";
            EditRideDescriptor descriptor = new EditRideDescriptor();
            descriptor.setNotes(Optional.of(new Notes(ten)));
            RideEditCommand expected = new RideEditCommand(101, descriptor);
            for (String quote : new String[] {"", "\""}) {
                assertParseSuccess(parser, VALID_ID + " notes/" + quote + separator
                        + ten.replace(" ", separator) + separator + quote, expected);
                assertParseFailure(parser, VALID_ID + " notes/" + quote
                        + (ten + " eleven").replace(" ", separator) + quote, Notes.MESSAGE_CONSTRAINTS);
            }
        }
    }

    @Test
    public void parse_quotedNotes_onlyExplicitFieldsAreEdited() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setNotes(Optional.of(new Notes("Bring camera with aperture f/2")));
        assertParseSuccess(parser, VALID_ID + " notes/\"Bring camera with aperture f/2\"",
                new RideEditCommand(101, descriptor));
        descriptor.setMaxGroupSize(new GroupSize("5"));
        assertParseSuccess(parser, VALID_ID + " notes/\"Bring camera with aperture f/2\" s/5",
                new RideEditCommand(101, descriptor));
        descriptor.setMinimumPower(new MinimumPower("200"));
        assertParseSuccess(parser, VALID_ID + " f/200 notes/\"Bring camera with aperture f/2\" s/5",
                new RideEditCommand(101, descriptor));
    }

    @Test
    public void parse_malformedQuotedNotes_failure() {
        for (String notes : new String[] {"\"Bring camera f/2", "\"\"", "\"  \"", "\"Bring\"tail",
            "\"Bring\" tail f/200", "\"Bring \"camera\"\""}) {
            assertParseFailure(parser, VALID_ID + " notes/" + notes,
                    ArgumentTokenizer.MESSAGE_INVALID_QUOTED_NOTES);
        }
        assertParseFailure(parser, VALID_ID + " notes/\"f/2\" f/200 f/250",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FTP));
        assertParseFailure(parser, VALID_ID + " notes/\"notes/literal\" notes/second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTES));
        assertParseFailure(parser, VALID_ID + " notes/\"f/2\" s/bad", GroupSize.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID
                + " notes/\"one two three four five six seven eight nine ten eleven\"", Notes.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_eachEditableField_success() {
        EditRideDescriptor nameDescriptor = new EditRideDescriptor();
        nameDescriptor.setName(new Name("Night Ride"));
        assertParseSuccess(parser, VALID_ID + " n/Night Ride", new RideEditCommand(101, nameDescriptor));

        EditRideDescriptor fromDescriptor = new EditRideDescriptor();
        fromDescriptor.setFrom(new RideDateTime("20-10-2026 1400"));
        assertParseSuccess(parser, VALID_ID + " from/20-10-2026 1400",
                new RideEditCommand(101, fromDescriptor));

        EditRideDescriptor toDescriptor = new EditRideDescriptor();
        toDescriptor.setTo(new RideDateTime("20-10-2026 1600"));
        assertParseSuccess(parser, VALID_ID + " to/20-10-2026 1600", new RideEditCommand(101, toDescriptor));

        EditRideDescriptor locationDescriptor = new EditRideDescriptor();
        locationDescriptor.setLocation(new Location("East Coast Park"));
        assertParseSuccess(parser, VALID_ID + " l/East Coast Park",
                new RideEditCommand(101, locationDescriptor));

        EditRideDescriptor distanceDescriptor = new EditRideDescriptor();
        distanceDescriptor.setDistance(new Distance("50.5"));
        assertParseSuccess(parser, VALID_ID + " d/50.50", new RideEditCommand(101, distanceDescriptor));

        EditRideDescriptor powerDescriptor = new EditRideDescriptor();
        powerDescriptor.setMinimumPower(new MinimumPower("250"));
        assertParseSuccess(parser, VALID_ID + " f/250", new RideEditCommand(101, powerDescriptor));

        EditRideDescriptor sizeDescriptor = new EditRideDescriptor();
        sizeDescriptor.setMaxGroupSize(new GroupSize("20"));
        assertParseSuccess(parser, VALID_ID + " s/20", new RideEditCommand(101, sizeDescriptor));

        EditRideDescriptor notesDescriptor = new EditRideDescriptor();
        notesDescriptor.setNotes(Optional.of(new Notes("New meeting point")));
        assertParseSuccess(parser, VALID_ID + " notes/New meeting point",
                new RideEditCommand(101, notesDescriptor));
    }

    @Test
    public void parse_multipleFields_success() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setName(new Name("Morning Ride"));
        descriptor.setFrom(new RideDateTime("20-10-2026 1400"));
        descriptor.setTo(new RideDateTime("20-10-2026 1600"));
        descriptor.setLocation(new Location("East Coast Park"));
        descriptor.setDistance(new Distance("50"));
        descriptor.setMinimumPower(new MinimumPower("200"));
        descriptor.setMaxGroupSize(new GroupSize("15"));
        descriptor.setNotes(Optional.of(new Notes("Meet at carpark")));

        String input = VALID_ID + " n/Morning Ride from/20-10-2026 1400 to/20-10-2026 1600"
                + " l/East Coast Park d/50 f/200 s/15 notes/Meet at carpark";
        assertParseSuccess(parser, input, new RideEditCommand(101, descriptor));
    }

    @Test
    public void parse_positiveIdBoundaries_success() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setName(new Name("NightRide"));

        assertParseSuccess(parser, " 1 n/NightRide", new RideEditCommand(1, descriptor));
        assertParseSuccess(parser, " " + Integer.MAX_VALUE + " n/NightRide",
                new RideEditCommand(Integer.MAX_VALUE, descriptor));
    }

    @Test
    public void parse_noEditedField_failure() {
        assertParseFailure(parser, VALID_ID, RideEditCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, VALID_ID + "   ", RideEditCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidId_failure() {
        for (String invalidId : new String[] {"", "0", "-1", "abc", "1.0", "+1", "2147483648",
            "999999999999999999999999"}) {
            assertParseFailure(parser, " " + invalidId + " n/NightRide", INVALID_FORMAT_MESSAGE);
        }
        assertParseFailure(parser, " [101] n/NightRide", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        assertDuplicateFailure(" n/First n/Second", PREFIX_NAME);
        assertDuplicateFailure(" from/20-10-2026 1400 from/20-10-2026 1500", PREFIX_FROM);
        assertDuplicateFailure(" to/20-10-2026 1600 to/20-10-2026 1700", PREFIX_TO);
        assertDuplicateFailure(" l/Punggol l/Changi", PREFIX_LOCATION);
        assertDuplicateFailure(" d/40 d/50", PREFIX_DISTANCE);
        assertDuplicateFailure(" f/200 f/250", PREFIX_FTP);
        assertDuplicateFailure(" s/10 s/20", PREFIX_GROUP_SIZE);
        assertDuplicateFailure(" notes/First notes/Second", PREFIX_NOTES);
    }

    @Test
    public void parse_emptyRequiredField_failure() {
        assertParseFailure(parser, VALID_ID + " n/", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " from/", RideDateTime.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " to/", RideDateTime.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " l/", Location.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " d/", Distance.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " f/", MinimumPower.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " s/", GroupSize.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_malformedDateTime_failure() {
        for (String invalidDateTime : new String[] {"2026-10-20 1400", "20-10-2026", "20-10-2026 14:00",
            "20-10-2026 2400", "31-02-2026 1400"}) {
            assertParseFailure(parser, VALID_ID + " from/" + invalidDateTime, RideDateTime.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidNumericFields_failure() {
        assertParseFailure(parser, VALID_ID + " d/0", Distance.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " d/far", Distance.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " f/0", MinimumPower.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " f/601", MinimumPower.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " s/0", GroupSize.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID + " s/many", GroupSize.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_notesStates_success() {
        EditRideDescriptor clearDescriptor = new EditRideDescriptor();
        clearDescriptor.setNotes(Optional.empty());
        assertParseSuccess(parser, VALID_ID + " notes/", new RideEditCommand(101, clearDescriptor));
        assertParseSuccess(parser, VALID_ID + " notes/   ", new RideEditCommand(101, clearDescriptor));

        EditRideDescriptor clearAndRenameDescriptor = new EditRideDescriptor();
        clearAndRenameDescriptor.setNotes(Optional.empty());
        clearAndRenameDescriptor.setName(new Name("NightRide"));
        assertParseSuccess(parser, VALID_ID + " notes/ n/NightRide",
                new RideEditCommand(101, clearAndRenameDescriptor));
    }

    @Test
    public void parse_notesWordLimit_enforced() {
        EditRideDescriptor descriptor = new EditRideDescriptor();
        descriptor.setNotes(Optional.of(new Notes("one two three four five six seven eight nine ten")));
        assertParseSuccess(parser, VALID_ID + " notes/one two three four five six seven eight nine ten",
                new RideEditCommand(101, descriptor));
        assertParseFailure(parser,
                VALID_ID + " notes/one two three four five six seven eight nine ten eleven",
                Notes.MESSAGE_CONSTRAINTS);
    }

    private void assertDuplicateFailure(String fields, Prefix duplicatePrefix) {
        assertParseFailure(parser, VALID_ID + fields,
                Messages.getErrorMessageForDuplicatePrefixes(duplicatePrefix));
    }
}
