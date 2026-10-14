
package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EXPERIENCE_LEVEL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EXPERIENCE_LEVEL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.FTP_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EXPERIENCE_LEVEL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.LOCATION_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EXPERIENCE_LEVEL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String SOS_DESC = " sos/+65 99999999";

    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_emergencyPhone_requiredAndSingleValued() {
        String fields = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + FTP_DESC_BOB
                + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB;
        assertParseFailure(parser, fields,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        assertParseFailure(parser, fields + " sos/", Phone.MESSAGE_MISSING_COUNTRY_CODE);
        assertParseFailure(parser, fields + SOS_DESC + SOS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMERGENCY_PHONE));
        assertParseSuccess(parser, SOS_DESC + fields,
                new AddCommand(new PersonBuilder(BOB).withEmergencyPhone("+65 99999999").build()));
    }

    @Test
    public void parse_removedPrefix_failure() {
        String validInput = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        for (String removedPrefix : List.of(" t/friend", " t/", " t/!", "\n t/friend")) {
            assertParseFailure(parser, validInput + removedPrefix, expectedMessage);
            assertParseFailure(parser, removedPrefix + validInput, expectedMessage);
        }
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withEmergencyPhone("+65 99999999").build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC,
                new AddCommand(expectedPerson));

    }

    @Test
    public void parse_repeatedSingleValue_failure() {
        String validExpectedPersonString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + FTP_DESC_BOB
                + EXPERIENCE_LEVEL_DESC_BOB
                + LOCATION_DESC_BOB + SOS_DESC;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple FTP values
        assertParseFailure(parser, FTP_DESC_BOB + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FTP));

        // multiple experience levels
        assertParseFailure(parser, EXPERIENCE_LEVEL_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EXPERIENCE_LEVEL));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY
                        + EXPERIENCE_LEVEL_DESC_AMY + FTP_DESC_BOB + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_EMAIL, PREFIX_PHONE,
                        PREFIX_EXPERIENCE_LEVEL, PREFIX_FTP, PREFIX_EMERGENCY_PHONE));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedPersonString + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedPersonString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    @Test
    public void parse_allRequiredFields_success() {
        Person expectedPerson = new PersonBuilder(AMY).withEmergencyPhone("+65 99999999").build();
        assertParseSuccess(parser,
                NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
                        + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_AMY + LOCATION_DESC_BOB + SOS_DESC,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser,
                VALID_NAME_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC,
                expectedMessage);

        // missing phone prefix
        assertParseFailure(parser,
                NAME_DESC_BOB + VALID_PHONE_BOB + EMAIL_DESC_BOB
                        + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC,
                expectedMessage);

        // missing email prefix
        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + VALID_EMAIL_BOB
                        + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC,
                expectedMessage);

        // missing experience level prefix
        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + FTP_DESC_BOB + VALID_EXPERIENCE_LEVEL_BOB + LOCATION_DESC_BOB + SOS_DESC,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser,
                VALID_NAME_BOB + VALID_PHONE_BOB + VALID_EMAIL_BOB
                        + VALID_EXPERIENCE_LEVEL_BOB,
                expectedMessage);

        // missing FTP
        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC,
                AddCommand.MESSAGE_MISSING_FTP);

        // missing location
        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + SOS_DESC,
                AddCommand.MESSAGE_MISSING_LOCATION);
    }

    @Test
    public void parse_ftpBoundariesAndInvalidValues_failureOrSuccess() {
        String requiredFields = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB
                + LOCATION_DESC_BOB + SOS_DESC;
        String ftpConstraints = "FTP should be a whole number between 1 and 600 watts.";

        assertDoesNotThrow(() -> parser.parse(requiredFields + " f/1"));
        assertDoesNotThrow(() -> parser.parse(requiredFields + " f/600"));
        assertParseFailure(parser, requiredFields + " f/0", ftpConstraints);
        assertParseFailure(parser, requiredFields + " f/601", ftpConstraints);
        assertParseFailure(parser, requiredFields + " f/abc", ftpConstraints);
        assertParseFailure(parser, requiredFields + " f/", "Please include a number for FTP. " + ftpConstraints);
        assertParseFailure(parser, requiredFields, AddCommand.MESSAGE_MISSING_FTP);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB
                + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC
                + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC, Email.MESSAGE_CONSTRAINTS);

        // invalid experience level
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + FTP_DESC_BOB + INVALID_EXPERIENCE_LEVEL_DESC + LOCATION_DESC_BOB + SOS_DESC,
                ExperienceLevel.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC, Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + FTP_DESC_BOB + EXPERIENCE_LEVEL_DESC_BOB + LOCATION_DESC_BOB + SOS_DESC,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
