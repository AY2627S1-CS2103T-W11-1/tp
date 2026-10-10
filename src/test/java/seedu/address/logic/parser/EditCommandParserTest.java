package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EXPERIENCE_LEVEL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.FTP_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EXPERIENCE_LEVEL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.LOCATION_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EXPERIENCE_LEVEL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;
    private static final Phone TARGET_PHONE = new Phone("+65 91234567");

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no target phone specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, TARGET_PHONE.toString(), EditCommand.MESSAGE_NOT_EDITED);

        // no target phone and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // non-numeric phone
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // phone that is too short
        assertParseFailure(parser, "12" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // old phone format without a country code
        assertParseFailure(parser, "91234567" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // malformed international phone
        assertParseFailure(parser, "+65 abc" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, TARGET_PHONE + " some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, TARGET_PHONE + " i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, TARGET_PHONE + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, TARGET_PHONE + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, TARGET_PHONE + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, TARGET_PHONE + " f/0", Ftp.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " f/601", Ftp.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " f/abc", Ftp.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " f/-1", Ftp.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " f/1.5", Ftp.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " f/",
                "Please include a number for FTP. " + Ftp.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + INVALID_EXPERIENCE_LEVEL_DESC,
                ExperienceLevel.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " x/", ExperienceLevel.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " l/", Location.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " l/East@Park", Location.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + " l/any l/Marina Bay",
                "Location 'any' cannot be combined with other locations.");
        assertParseFailure(parser, TARGET_PHONE + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, TARGET_PHONE + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, TARGET_PHONE + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, TARGET_PHONE + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TARGET_PHONE + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser,
                TARGET_PHONE + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);

        // a valid field must not cause an invalid field to be partially accepted
        assertParseFailure(parser, TARGET_PHONE + NAME_DESC_AMY + " f/601", Ftp.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = TARGET_PHONE + PHONE_DESC_BOB + TAG_DESC_HUSBAND + FTP_DESC_BOB
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + EXPERIENCE_LEVEL_DESC_BOB
                + " l/East Coast Park l/Marina Bay" + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withFtp("1").withExperienceLevel(VALID_EXPERIENCE_LEVEL_BOB)
                .withLocations("East Coast Park", "Marina Bay")
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(TARGET_PHONE, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        String userInput = TARGET_PHONE + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(TARGET_PHONE, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_validExperienceLevels_success() {
        assertExperienceLevelParseSuccess("Beginner", "Beginner");
        assertExperienceLevelParseSuccess("Intermediate", "Intermediate");
        assertExperienceLevelParseSuccess("Advanced", "Advanced");
        assertExperienceLevelParseSuccess("aDvAnCeD", "Advanced");
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        String userInput = TARGET_PHONE + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = TARGET_PHONE + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = TARGET_PHONE + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // FTP boundaries
        userInput = TARGET_PHONE + " f/1";
        descriptor = new EditPersonDescriptorBuilder().withFtp("1").build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        userInput = TARGET_PHONE + " f/600";
        descriptor = new EditPersonDescriptorBuilder().withFtp("600").build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // experience level
        userInput = TARGET_PHONE + EXPERIENCE_LEVEL_DESC_BOB;
        descriptor = new EditPersonDescriptorBuilder().withExperienceLevel(VALID_EXPERIENCE_LEVEL_BOB).build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // locations
        userInput = TARGET_PHONE + " l/East Coast Park l/Marina Bay";
        descriptor = new EditPersonDescriptorBuilder().withLocations("East Coast Park", "Marina Bay").build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // case-insensitive duplicate locations
        userInput = TARGET_PHONE + " l/East Coast Park l/east coast park";
        descriptor = new EditPersonDescriptorBuilder().withLocations("East Coast Park").build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // any location
        userInput = TARGET_PHONE + LOCATION_DESC_BOB;
        descriptor = new EditPersonDescriptorBuilder().withLocations("any").build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = TARGET_PHONE + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = TARGET_PHONE + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(TARGET_PHONE, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        String userInput = TARGET_PHONE + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = TARGET_PHONE + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple valid fields repeated
        userInput = TARGET_PHONE + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        userInput = TARGET_PHONE + " f/1 f/600 x/Beginner x/Advanced";
        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FTP, PREFIX_EXPERIENCE_LEVEL));

        // multiple invalid values
        userInput = TARGET_PHONE + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        String userInput = TARGET_PHONE + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(TARGET_PHONE, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_targetPhoneAndReplacementPhone_success() {
        Phone targetPhone = new Phone("+65 96742165");
        String userInput = targetPhone + PHONE_DESC_BOB;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(VALID_PHONE_BOB)
                .build();

        EditCommand expectedCommand = new EditCommand(targetPhone, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    private void assertExperienceLevelParseSuccess(String input, String expectedExperienceLevel) {
        String userInput = TARGET_PHONE + " x/" + input;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withExperienceLevel(expectedExperienceLevel).build();

        assertParseSuccess(parser, userInput, new EditCommand(TARGET_PHONE, descriptor));
    }
}
