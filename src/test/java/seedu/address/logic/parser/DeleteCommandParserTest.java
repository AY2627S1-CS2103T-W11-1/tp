package seedu.address.logic.parser;

import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.Phone;

public class DeleteCommandParserTest {

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validPhone_returnsDeleteCommand() {
        assertParseSuccess(parser, VALID_PHONE_AMY, new DeleteCommand(new Phone(VALID_PHONE_AMY)));
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        String[] values = {"12", "87438807", "+6587438807", "+65  87438807", "+65 12", "+65 abc"};
        String[] messages = {Phone.MESSAGE_MISSING_COUNTRY_CODE, Phone.MESSAGE_MISSING_COUNTRY_CODE,
            Phone.MESSAGE_MISSING_SEPARATOR, Phone.MESSAGE_MULTIPLE_SEPARATORS,
            Phone.MESSAGE_CONSTRAINTS, Phone.MESSAGE_CONSTRAINTS};
        for (int i = 0; i < values.length; i++) {
            assertParseFailure(parser, values[i], messages[i]);
        }
    }

    @Test
    public void parse_missingPhone_throwsUsageException() {
        for (String args : new String[] {"", " ", "\t\n"}) {
            assertParseFailure(parser, args,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_validPhoneWithSurroundingWhitespace_returnsDeleteCommand() {
        assertParseSuccess(parser, " \t" + VALID_PHONE_AMY + " \n",
                new DeleteCommand(new Phone(VALID_PHONE_AMY)));
    }
}
