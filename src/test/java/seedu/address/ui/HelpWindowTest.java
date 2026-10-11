package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;

public class HelpWindowTest {

    @Test
    public void commandList_containsEveryCommandWord() {
        String[] commandWords = {
            AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD
        };

        for (String commandWord : commandWords) {
            assertTrue(HelpWindow.COMMAND_LIST.contains(commandWord + ":"),
                    "Help window should list the '" + commandWord + "' command");
        }
    }

    @Test
    public void commandList_reusesCommandUsageMessages() {
        assertTrue(HelpWindow.COMMAND_LIST.contains(AddCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(EditCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(DeleteCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(FindCommand.MESSAGE_USAGE));
    }
}
