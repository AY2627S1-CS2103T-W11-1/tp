package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesFindCriteriaPredicate;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_NAME, PREFIX_PHONE);
        List<String> nameKeywords = argMultimap.getAllValues(PREFIX_NAME);
        List<String> phoneValues = argMultimap.getAllValues(PREFIX_PHONE);
        if (!argMultimap.getPreamble().isEmpty() || nameKeywords.isEmpty() && phoneValues.isEmpty()
                || nameKeywords.stream().anyMatch(keyword -> keyword.isEmpty() || keyword.matches(".*\\s+.*"))) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        for (String keyword : nameKeywords) {
            ParserUtil.parseName(keyword);
        }
        List<Phone> phones = new ArrayList<>();
        for (String value : phoneValues) {
            phones.add(ParserUtil.parsePhone(value));
        }
        return new FindCommand(new PersonMatchesFindCriteriaPredicate(nameKeywords, phones));
    }

}
