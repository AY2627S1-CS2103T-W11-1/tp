package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DISTANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_SIZE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RideAddCommand;
import seedu.address.logic.commands.RideDeleteCommand;
import seedu.address.logic.commands.RideEditCommand;
import seedu.address.logic.commands.RideFindCommand;
import seedu.address.logic.commands.RideListCommand;
import seedu.address.logic.commands.RideRosterCommand;

public class CommandTemplatesTest {

    @Test
    public void allTemplates_excludeRemovedTagPrefix() {
        for (CommandTemplates.Template template : CommandTemplates.ALL) {
            assertFalse(template.display().contains("t/"));
            assertFalse(template.insertText().contains("t/"));
        }
    }

    @Test
    public void all_listsEveryCommandInOrder() {
        List<String> commandWords = CommandTemplates.ALL.stream()
                .map(template -> template.insertText().trim().split(" ")[0])
                .toList();

        assertEquals(List.of(AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
                FindCommand.COMMAND_WORD, RideAddCommand.COMMAND_WORD, RideEditCommand.COMMAND_WORD,
                RideDeleteCommand.COMMAND_WORD,
                RideRosterCommand.JOIN_COMMAND_WORD, RideRosterCommand.LEAVE_COMMAND_WORD,
                RideListCommand.COMMAND_WORD, RideFindCommand.COMMAND_WORD, ListCommand.COMMAND_WORD,
                ClearCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD), commandWords);
    }

    @Test
    public void all_displayStartsWithCommandWord() {
        for (CommandTemplates.Template template : CommandTemplates.ALL) {
            String commandWord = template.insertText().trim().split(" ")[0];
            assertTrue(template.display().startsWith(commandWord));
        }
    }

    @Test
    public void editTemplate_displayListsEveryEditableFieldAsOptional() {
        String display = CommandTemplates.ALL.get(1).display();

        for (Object prefix : new Object[] {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMERGENCY_PHONE, PREFIX_EMAIL, PREFIX_FTP,
            PREFIX_EXPERIENCE_LEVEL, PREFIX_LOCATION}) {
            assertTrue(display.contains("[" + prefix), "Edit format should list optional " + prefix);
        }
    }

    @Test
    public void addTemplate_insertTextContainsCompulsoryPrefixes() {
        String insertText = CommandTemplates.ALL.get(0).insertText();

        for (Object prefix : new Object[] {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMERGENCY_PHONE, PREFIX_EMAIL, PREFIX_FTP,
            PREFIX_EXPERIENCE_LEVEL, PREFIX_LOCATION}) {
            assertTrue(insertText.contains(prefix.toString()));
        }
    }

    @Test
    public void rideAddTemplate_displayShowsCompulsoryPrefixesAndNotesAsOptional() {
        String display = templateFor(RideAddCommand.COMMAND_WORD).display();

        assertEquals("rideadd n/NAME from/FROM to/TO l/LOCATION d/DISTANCE f/MIN_POWER s/MAX_GROUP_SIZE"
                + " [notes/NOTES]", display);
        for (Object prefix : new Object[] {PREFIX_NAME, PREFIX_FROM, PREFIX_TO, PREFIX_LOCATION, PREFIX_DISTANCE,
            PREFIX_FTP, PREFIX_GROUP_SIZE}) {
            assertTrue(display.contains(" " + prefix) || display.contains(prefix.toString()));
            assertFalse(display.contains("[" + prefix), prefix + " is compulsory, so it must not be in brackets");
        }
        assertTrue(display.contains("[" + PREFIX_NOTES));
    }

    @Test
    public void rideAddTemplate_insertTextHasCompulsoryPrefixesOnly() {
        String insertText = templateFor(RideAddCommand.COMMAND_WORD).insertText();

        assertEquals("rideadd n/ from/ to/ l/ d/ f/ s/", insertText);
        assertFalse(insertText.contains(PREFIX_NOTES.toString()));
    }

    @Test
    public void rideAddTemplate_isListedAfterFindAndBeforeRideEdit() {
        List<String> commandWords = CommandTemplates.ALL.stream()
                .map(template -> template.insertText().trim().split(" ")[0])
                .toList();
        int rideAddIndex = commandWords.indexOf(RideAddCommand.COMMAND_WORD);

        assertEquals(commandWords.indexOf(FindCommand.COMMAND_WORD) + 1, rideAddIndex);
        assertEquals(commandWords.indexOf(RideEditCommand.COMMAND_WORD) - 1, rideAddIndex);
    }

    @Test
    public void rideEditTemplate_displayListsEveryEditableFieldAsOptional() {
        String display = templateFor(RideEditCommand.COMMAND_WORD).display();

        assertEquals("rideedit ID [n/NAME] [from/START] [to/END] [l/LOCATION] [d/DISTANCE] [f/MIN_POWER]"
                + " [s/MAX_GROUP_SIZE] [notes/NOTES]", display);
        for (Object prefix : new Object[] {PREFIX_NAME, PREFIX_FROM, PREFIX_TO, PREFIX_LOCATION, PREFIX_DISTANCE,
            PREFIX_FTP, PREFIX_GROUP_SIZE, PREFIX_NOTES}) {
            assertTrue(display.contains("[" + prefix), "Ride Edit format should list optional " + prefix);
        }
    }

    @Test
    public void rideEditTemplate_insertTextContainsNoDocumentationBrackets() {
        String insertText = templateFor(RideEditCommand.COMMAND_WORD).insertText();

        assertEquals("rideedit ", insertText);
        assertFalse(insertText.contains("["));
        assertFalse(insertText.contains("]"));
    }

    @Test
    public void rideEditTemplate_isListedAfterRideAddAndBeforeRosterCommands() {
        List<String> commandWords = CommandTemplates.ALL.stream()
                .map(template -> template.insertText().trim().split(" ")[0])
                .toList();
        int rideEditIndex = commandWords.indexOf(RideEditCommand.COMMAND_WORD);

        assertEquals(commandWords.indexOf(RideAddCommand.COMMAND_WORD) + 1, rideEditIndex);
        assertEquals(commandWords.indexOf(RideDeleteCommand.COMMAND_WORD) - 1, rideEditIndex);
    }

    @Test
    public void rideDeleteTemplate_hasExpectedFormatInsertionAndOrder() {
        CommandTemplates.Template template = templateFor(RideDeleteCommand.COMMAND_WORD);
        List<String> commandWords = CommandTemplates.ALL.stream()
                .map(commandTemplate -> commandTemplate.insertText().trim().split(" ")[0])
                .toList();

        assertEquals("ridedelete ID", template.display());
        assertEquals("ridedelete ", template.insertText());
        assertEquals(commandWords.indexOf(RideEditCommand.COMMAND_WORD) + 1,
                commandWords.indexOf(RideDeleteCommand.COMMAND_WORD));
        assertEquals(commandWords.indexOf(RideRosterCommand.JOIN_COMMAND_WORD) - 1,
                commandWords.indexOf(RideDeleteCommand.COMMAND_WORD));
    }

    @Test
    public void rosterTemplates_showPositionalArguments() {
        assertEquals("rideaddcyclist ID PHONE", templateFor(RideRosterCommand.JOIN_COMMAND_WORD).display());
        assertEquals("ridedeletecyclist ID PHONE", templateFor(RideRosterCommand.LEAVE_COMMAND_WORD).display());
    }

    @Test
    public void rosterTemplates_insertCommandAndTrailingSpaceOnly() {
        assertEquals("rideaddcyclist ", templateFor(RideRosterCommand.JOIN_COMMAND_WORD).insertText());
        assertEquals("ridedeletecyclist ", templateFor(RideRosterCommand.LEAVE_COMMAND_WORD).insertText());
    }

    @Test
    public void rideListTemplate_hasExactFormatInsertionAndSingleEntry() {
        CommandTemplates.Template template = templateFor(RideListCommand.COMMAND_WORD);
        long occurrences = CommandTemplates.ALL.stream()
                .filter(candidate -> candidate.insertText().trim().equals(RideListCommand.COMMAND_WORD))
                .count();

        assertEquals("ridelist ID", template.display());
        assertEquals("ridelist ", template.insertText());
        assertEquals(1, occurrences);
    }

    private static CommandTemplates.Template templateFor(String commandWord) {
        return CommandTemplates.ALL.stream()
                .filter(template -> template.insertText().trim().split(" ")[0].equals(commandWord))
                .findFirst()
                .orElseThrow();
    }
}
