package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RideListCommand;

public class RideListCommandParserTest {
    private final RideListCommandParser parser = new RideListCommandParser();

    @Test
    public void parse_validPositiveId_returnsRideListCommand() {
        for (int validId : List.of(1, 101, Integer.MAX_VALUE)) {
            assertParseSuccess(parser, " " + validId, new RideListCommand(validId));
        }
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RideListCommand.MESSAGE_USAGE);
        for (String invalidInput : List.of("", " 0", " -1", " abc", " 2147483648", " 1 extra")) {
            assertParseFailure(parser, invalidInput, expectedMessage);
        }
    }
}
