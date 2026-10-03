package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesFindCriteriaPredicate;
import seedu.address.model.person.Phone;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "alex david",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "n/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "n/alex david",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesFindCriteriaPredicate(List.of("Alice", "Bob"), List.of()));
        assertParseSuccess(parser, "n/Alice n/Bob", expectedFindCommand);
        assertParseSuccess(parser, " n/Alice  n/Bob ", expectedFindCommand);

        FindCommand phoneCommand = new FindCommand(new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(new Phone("96320842"))));
        assertParseSuccess(parser, "p/96320842", phoneCommand);

        FindCommand combinedCommand = new FindCommand(new PersonMatchesFindCriteriaPredicate(
                List.of("alex"), List.of(new Phone("96320842"))));
        assertParseSuccess(parser, "n/alex p/96320842", combinedCommand);
        assertParseSuccess(parser, "p/96320842 n/alex", combinedCommand);
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        assertParseFailure(parser, "p/abc", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "p/", Phone.MESSAGE_CONSTRAINTS);
    }

}
