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
import seedu.address.logic.commands.RideDeleteCommand;

public class HelpWindowTest {

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
            HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, RideAddCommand.COMMAND_WORD,
            RideDeleteCommand.COMMAND_WORD
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
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideDeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void commandList_rideAddUsage_includesDateFormatAndExample() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("rideadd: Adds a group ride."));
        assertTrue(HelpWindow.COMMAND_LIST.contains("DD-MM-YYYY TTTT"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: rideadd n/Sunday Sunrise Ride"));
    }

    @Test
    public void commandList_listsRideCommandsAfterFindAndBeforeList() {
        int findIndex = HelpWindow.COMMAND_LIST.indexOf(FindCommand.MESSAGE_USAGE);
        int rideAddIndex = HelpWindow.COMMAND_LIST.indexOf(RideAddCommand.MESSAGE_USAGE);
        int rideDeleteIndex = HelpWindow.COMMAND_LIST.indexOf(RideDeleteCommand.MESSAGE_USAGE);
        int listIndex = HelpWindow.COMMAND_LIST.indexOf(ListCommand.COMMAND_WORD + ": Lists all persons.");

        assertTrue(findIndex < rideAddIndex);
        assertTrue(rideAddIndex < rideDeleteIndex);
        assertTrue(rideDeleteIndex < listIndex);
    }

    @Test
    public void commandList_rideDeleteUsage_explainsDeletionSemantics() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("Use the ride's persistent ID."));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Deletion requires no confirmation."));
        assertTrue(HelpWindow.COMMAND_LIST.contains("leaves cyclist records and other rides and their rosters "
                + "unchanged"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Remaining ride IDs are not renumbered"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("deleted IDs are not reused"));
    }
}
