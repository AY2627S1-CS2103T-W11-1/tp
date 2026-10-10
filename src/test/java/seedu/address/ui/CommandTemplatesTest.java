package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

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

public class CommandTemplatesTest {

    @Test
    public void all_listsEveryCommandInOrder() {
        List<String> commandWords = CommandTemplates.ALL.stream()
                .map(template -> template.insertText().trim().split(" ")[0])
                .toList();

        assertEquals(List.of(AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
                FindCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD,
                HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD), commandWords);
    }

    @Test
    public void all_displayStartsWithCommandWord() {
        for (CommandTemplates.Template template : CommandTemplates.ALL) {
            String commandWord = template.insertText().trim().split(" ")[0];
            assertTrue(template.display().startsWith(commandWord));
        }
    }

    @Test
    public void addTemplate_insertTextContainsCompulsoryPrefixes() {
        String insertText = CommandTemplates.ALL.get(0).insertText();

        for (Object prefix : new Object[] {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_FTP,
            PREFIX_EXPERIENCE_LEVEL, PREFIX_LOCATION}) {
            assertTrue(insertText.contains(prefix.toString()));
        }
    }
}
