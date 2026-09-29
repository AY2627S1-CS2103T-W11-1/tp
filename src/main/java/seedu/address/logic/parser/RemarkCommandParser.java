package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new RemarkCommand object.
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the RemarkCommand
     * and returns a RemarkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_REMARK);

        try {
            Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
            String remark = argMultimap.getValue(PREFIX_REMARK).orElse("");

            return new RemarkCommand(index, remark);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE), pe);
        }
    }
}