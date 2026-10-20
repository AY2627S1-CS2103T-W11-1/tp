package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.ExperienceLevel;
import seedu.address.model.person.Ftp;
import seedu.address.model.person.Location;
import seedu.address.model.person.PersonMatchesFindCriteriaPredicate;
import seedu.address.model.person.Phone;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emergencyPhones_supportsRepeatedAndCombinedFields() {
        List<Phone> emergencyPhones = List.of(new Phone("+65 99999999"), new Phone("+1 123"));
        assertParseSuccess(parser, "sos/+65 99999999 sos/+1 123", new FindCommand(
                new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), emergencyPhones)));
        FindCommand combined = new FindCommand(new PersonMatchesFindCriteriaPredicate(
                List.of("Alice"), List.of(new Phone("+65 91234567")), emergencyPhones));
        assertParseSuccess(parser, "sos/+65 99999999 n/Alice p/+65 91234567 sos/+1 123", combined);
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "alex david",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "n/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseSuccess(parser, "n/alex david", new FindCommand(
                new PersonMatchesFindCriteriaPredicate(List.of("alex david"), List.of())));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesFindCriteriaPredicate(List.of("Alice", "Bob"), List.of()));
        assertParseSuccess(parser, "n/Alice n/Bob", expectedFindCommand);
        assertParseSuccess(parser, " n/Alice  n/Bob ", expectedFindCommand);

        assertParseSuccess(parser, "n/John Doe", new FindCommand(
                new PersonMatchesFindCriteriaPredicate(List.of("John Doe"), List.of())));
        assertParseSuccess(parser, "n/John Doe n/Jane Doe", new FindCommand(
                new PersonMatchesFindCriteriaPredicate(List.of("John Doe", "Jane Doe"), List.of())));

        FindCommand phoneCommand = new FindCommand(new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(new Phone("+65 96320842"))));
        assertParseSuccess(parser, "p/+65 96320842", phoneCommand);

        FindCommand combinedCommand = new FindCommand(new PersonMatchesFindCriteriaPredicate(
                List.of("alex"), List.of(new Phone("+65 96320842"))));
        assertParseSuccess(parser, "n/alex p/+65 96320842", combinedCommand);
        assertParseSuccess(parser, "p/+65 96320842 n/alex", combinedCommand);
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        assertParseFailure(parser, "p/+65 abc", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "p/", Phone.MESSAGE_MISSING_COUNTRY_CODE);
    }

    @Test
    public void parse_cyclingCriteria_returnsFindCommand() {
        FindCommand expected = new FindCommand(new PersonMatchesFindCriteriaPredicate(List.of(), List.of(), List.of(),
                java.util.List.of(new ExperienceLevel("Intermediate")),
                java.util.Set.of(new Location("Punggol")), new Ftp("250")));
        assertParseSuccess(parser, "l/punggol x/intermediate f/250", expected);
        FindCommand multipleExperienceLevels = new FindCommand(new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(), java.util.List.of(new ExperienceLevel("Beginner"),
                        new ExperienceLevel("Intermediate")), java.util.Set.of(), null));
        assertParseSuccess(parser, "x/beginner x/intermediate", multipleExperienceLevels);
        FindCommand anyAndSpecificLocations = new FindCommand(new PersonMatchesFindCriteriaPredicate(
                List.of(), List.of(), List.of(), java.util.List.of(),
                java.util.Set.of(new Location("any"), new Location("Punggol")), null));
        assertParseSuccess(parser, "l/any l/punggol", anyAndSpecificLocations);
    }

    @Test
    public void parse_invalidCyclingCriteria_throwsParseException() {
        assertParseFailure(parser, "f/0", Ftp.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "x/Expert", ExperienceLevel.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "l/", Location.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "f/300 x/Intermediate find l/Punggol f/300 x/Intermediate",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_FTP));
    }

}
