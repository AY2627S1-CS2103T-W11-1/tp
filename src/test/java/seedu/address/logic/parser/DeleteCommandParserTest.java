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
        assertParseFailure(parser, "12", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_USAGE));
    }
}
