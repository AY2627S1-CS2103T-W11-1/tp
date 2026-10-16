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
import seedu.address.logic.commands.RideAddCommand;

public class HelpWindowTest {

    @Test
    public void commandList_rosterCommands_includesFormatsAliasesAndExample() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("rideaddcyclist ID PHONE (alias: rac)"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("ridedeletecyclist ID PHONE (alias: rdc)"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: rideaddcyclist 1 +65 91234567"));
        assertTrue(CommandTemplates.ALL.stream().anyMatch(template ->
                template.display().equals("rideaddcyclist ID PHONE (rac)")
                        && template.insertText().equals("rideaddcyclist ")));
        assertTrue(CommandTemplates.ALL.stream().anyMatch(template ->
                template.display().equals("ridedeletecyclist ID PHONE (rdc)")
                        && template.insertText().equals("ridedeletecyclist ")));
    }

    @Test
    public void commandList_deleteUsage_explainsConfirmationAndCancellation() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("Enter y/yes to confirm or n/no to cancel"));
    }

    @Test
    public void emergencyPhone_helpFormatsAndFindDropdown_includePrefix() {
        for (String usage : new String[] {AddCommand.MESSAGE_USAGE, EditCommand.MESSAGE_USAGE,
            FindCommand.MESSAGE_USAGE}) {
            assertTrue(usage.contains("sos/EMERGENCY_PHONE"));
            assertTrue(HelpWindow.COMMAND_LIST.contains(usage));
        }
        assertTrue(CommandTemplates.ALL.get(3).display().contains("sos/EMERGENCY_PHONE"));
    }

    @Test
    public void commandList_containsEveryCommandWord() {
        String[] commandWords = {
            AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, RideAddCommand.COMMAND_WORD
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
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideAddCommand.MESSAGE_USAGE));
    }

    @Test
    public void commandList_rideAddUsage_includesDateFormatAndExample() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("rideadd: Adds a group ride."));
        assertTrue(HelpWindow.COMMAND_LIST.contains("DD-MM-YYYY TTTT"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: rideadd n/Sunday Sunrise Ride"));
    }

    @Test
    public void commandList_listsRideAddAfterFindAndBeforeList() {
        int findIndex = HelpWindow.COMMAND_LIST.indexOf(FindCommand.MESSAGE_USAGE);
        int rideAddIndex = HelpWindow.COMMAND_LIST.indexOf(RideAddCommand.MESSAGE_USAGE);
        int listIndex = HelpWindow.COMMAND_LIST.indexOf(ListCommand.COMMAND_WORD + ": Lists all persons.");

        assertTrue(findIndex < rideAddIndex);
        assertTrue(rideAddIndex < listIndex);
    }
}
