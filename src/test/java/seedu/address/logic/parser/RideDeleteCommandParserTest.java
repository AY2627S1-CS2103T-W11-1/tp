package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RideDeleteCommand;

public class RideDeleteCommandParserTest {

    private static final String INVALID_FORMAT = String.format(
            MESSAGE_INVALID_COMMAND_FORMAT, RideDeleteCommand.MESSAGE_USAGE);

    private final RideDeleteCommandParser parser = new RideDeleteCommandParser();

    @Test
    public void parse_validPositiveId_returnsCommandForExactId() {
        for (int rideId : new int[] {1, 101, Integer.MAX_VALUE}) {
            assertParseSuccess(parser, Integer.toString(rideId), new RideDeleteCommand(rideId));
        }
    }

    @Test
    public void parse_invalidIdOrAdditionalArguments_throwsParseExceptionWithUsage() {
        for (String invalidInput : new String[] {"", "   ", "0", "-1", "abc", "1 extra"}) {
            assertParseFailure(parser, invalidInput, INVALID_FORMAT);
        }
    }

    @Test
    public void parse_overflowingId_reportsSupportedRange() {
        // Boundary and large values must explain the limit instead of suggesting the command syntax is wrong.
        for (String id : new String[] {"2147483648", "2147483649", "02147483648", "9".repeat(100)}) {
            assertParseFailure(parser, id, ParserUtil.MESSAGE_INVALID_RIDE_ID);
        }
    }
}
