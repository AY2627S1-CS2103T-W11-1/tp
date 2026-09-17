package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_indexSpecified_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " r/Likes to swim.";
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes to swim."));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_emptyRemark_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " r/";
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingRemarkPrefix_success() {
        String userInput = String.valueOf(INDEX_FIRST_PERSON.getOneBased());
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingIndex_failure() {
        String userInput = "r/Likes to swim.";
        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidIndex_failure() {
        String userInput = "a r/Likes to swim.";
        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }
}
