package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import seedu.address.logic.commands.RideEditCommand;
import seedu.address.logic.commands.RideFindCommand;
import seedu.address.logic.commands.RideListCommand;
import seedu.address.logic.commands.RideRosterCommand;

public class HelpWindowTest {

    @Test
    public void userGuideUrl_pointsToCycliqueUserGuide() {
        assertEquals("https://ay2627s1-cs2103t-w11-1.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
        assertFalse(HelpWindow.USERGUIDE_URL.contains("addressbook-level3"));
        assertTrue(HelpWindow.HELP_MESSAGE.endsWith(HelpWindow.USERGUIDE_URL));
    }

    @Test
    public void commandList_rosterCommands_includesFormatsAndExample() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("rideaddcyclist (rac) ID PHONE"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("ridedeletecyclist (rdc) ID PHONE"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: rideaddcyclist 1 +65 91234567"));
        assertTrue(CommandTemplates.ALL.stream().anyMatch(template ->
                template.display().equals("rideaddcyclist (rac) ID PHONE")
                        && template.insertText().equals("rideaddcyclist ")));
        assertTrue(CommandTemplates.ALL.stream().anyMatch(template ->
                template.display().equals("ridedeletecyclist (rdc) ID PHONE")
                        && template.insertText().equals("ridedeletecyclist ")));
    }

    @Test
    public void commandList_rideAliases_placesEachAliasBesideItsCommand() {
        for (String mapping : new String[] {"rideadd (ra):", "rideedit (re):", "ridedelete (rd):",
            "ridefind (rf):", "ridelist (rl):", "rideaddcyclist (rac) ID PHONE:",
            "ridedeletecyclist (rdc) ID PHONE:"}) {
            assertTrue(HelpWindow.COMMAND_LIST.contains(mapping));
        }
        assertTrue(HelpWindow.COMMAND_LIST.contains("Aliases are case-sensitive."));
        assertFalse(HelpWindow.COMMAND_LIST.contains("Ride command aliases (use the same arguments)"));
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
            HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD,
            RideAddCommand.COMMAND_WORD + " (" + RideAddCommand.COMMAND_ALIAS + ")",
            RideEditCommand.COMMAND_WORD + " (" + RideEditCommand.COMMAND_ALIAS + ")",
            RideDeleteCommand.COMMAND_WORD + " (" + RideDeleteCommand.COMMAND_ALIAS + ")",
            RideListCommand.COMMAND_WORD + " (" + RideListCommand.COMMAND_ALIAS + ")",
            RideFindCommand.COMMAND_WORD + " (" + RideFindCommand.COMMAND_ALIAS + ")"
        };

        for (String commandWord : commandWords) {
            assertTrue(HelpWindow.COMMAND_LIST.contains(commandWord + ":"),
                    "Help window should list the '" + commandWord + "' command");
        }
    }

    @Test
    public void commandList_rosterUsage_includesBothOperationExamples() {
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideRosterCommand.MESSAGE_JOIN_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideRosterCommand.MESSAGE_LEAVE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: rideaddcyclist 1 +65 91234567"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: ridedeletecyclist 1 +65 91234567"));
    }

    @Test
    public void commandList_clearUsage_warnsItDeletesRidesAndTakesNoParameters() {
        assertTrue(ClearCommand.MESSAGE_USAGE.contains("every cyclist and every ride"));
        assertTrue(ClearCommand.MESSAGE_USAGE.contains("Takes no parameters"));
        assertTrue(ClearCommand.MESSAGE_USAGE.contains("does not ask for confirmation"));
        assertFalse(HelpWindow.COMMAND_LIST.contains("Clears all entries"));
    }

    @Test
    public void commandList_reusesCommandUsageMessages() {
        assertTrue(HelpWindow.COMMAND_LIST.contains(AddCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(EditCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(DeleteCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(FindCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(ClearCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideAddCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideEditCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideDeleteCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideListCommand.MESSAGE_USAGE));
        assertTrue(HelpWindow.COMMAND_LIST.contains(RideFindCommand.MESSAGE_USAGE));
    }

    @Test
    public void commandList_rideAddUsage_includesDateFormatAndExample() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("rideadd (ra): Adds a group ride."));
        assertTrue(HelpWindow.COMMAND_LIST.contains("DD-MM-YYYY TTTT"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: rideadd n/Sunday Sunrise Ride"));
    }

    @Test
    public void rideCommands_nameMinimumPowerMinPower_everywhereHelpIsShown() {
        // The User Guide calls this parameter MIN_POWER for rideadd, rideedit and ridefind.
        // Full help (the command usage messages)
        assertTrue(RideAddCommand.MESSAGE_USAGE.contains("f/MIN_POWER s/MAX_GROUP_SIZE"));
        assertTrue(RideAddCommand.MESSAGE_USAGE.contains("MIN_POWER is in watts"));
        assertTrue(RideEditCommand.MESSAGE_USAGE.contains("[f/MIN_POWER] [s/MAX_GROUP_SIZE]"));
        assertTrue(RideFindCommand.MESSAGE_USAGE.contains("[f/MIN_POWER] [s/MAX_GROUP_SIZE]"));
        // Help menu
        for (String commandWord : new String[] {RideAddCommand.COMMAND_WORD, RideEditCommand.COMMAND_WORD,
            RideFindCommand.COMMAND_WORD}) {
            String menuEntry = CommandTemplates.ALL.stream()
                    .filter(template -> template.insertText().trim().split(" ")[0].equals(commandWord))
                    .findFirst().orElseThrow().display();
            assertTrue(menuEntry.contains("f/MIN_POWER"), menuEntry);
        }
        // the old long name must not come back anywhere help is shown
        assertFalse(HelpWindow.COMMAND_LIST.contains("MINIMUM_POWER"));
        for (CommandTemplates.Template template : CommandTemplates.ALL) {
            assertFalse(template.display().contains("MINIMUM_POWER"), template.display());
        }
    }

    @Test
    public void commandList_listsRideCommandsAfterFindAndBeforeList() {
        int findIndex = HelpWindow.COMMAND_LIST.indexOf(FindCommand.MESSAGE_USAGE);
        int rideAddIndex = HelpWindow.COMMAND_LIST.indexOf(RideAddCommand.MESSAGE_USAGE);
        int rideEditIndex = HelpWindow.COMMAND_LIST.indexOf(RideEditCommand.MESSAGE_USAGE);
        int rideDeleteIndex = HelpWindow.COMMAND_LIST.indexOf(RideDeleteCommand.MESSAGE_USAGE);
        int rosterIndex = HelpWindow.COMMAND_LIST.indexOf(RideRosterCommand.MESSAGE_USAGE);
        int rideListIndex = HelpWindow.COMMAND_LIST.indexOf(RideListCommand.MESSAGE_USAGE);
        int listIndex = HelpWindow.COMMAND_LIST.indexOf(ListCommand.COMMAND_WORD + ": Lists all persons.");

        assertTrue(findIndex < rideAddIndex);
        assertTrue(rideAddIndex < rideEditIndex);
        assertTrue(rideEditIndex < rideDeleteIndex);
        assertTrue(rideDeleteIndex < rosterIndex);
        assertTrue(rideAddIndex < rosterIndex);
        assertTrue(rosterIndex < rideListIndex);
        assertTrue(rideListIndex < listIndex);
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

    @Test
    public void commandList_rideEditHelp_explainsPreservationClearingAndExecutableExamples() {
        assertTrue(HelpWindow.RIDE_EDIT_HELP.contains("DD-MM-YYYY TTTT"));
        assertTrue(HelpWindow.RIDE_EDIT_HELP.contains("The ride ID, cyclist roster and unspecified fields stay "
                + "unchanged."));
        assertTrue(HelpWindow.RIDE_EDIT_HELP.contains("Omit notes/ to preserve notes"));
        assertTrue(HelpWindow.RIDE_EDIT_HELP.contains("notes/ without a value to clear them"));
        assertTrue(HelpWindow.RIDE_EDIT_HELP.contains("Invalid edits leave the ride unchanged."));

        for (String example : new String[] {
            "rideedit 101 n/NightRide",
            "rideedit 101 f/250",
            "rideedit 101 from/20-10-2026 1400 to/20-10-2026 1600",
            "rideedit 101 notes/"
        }) {
            assertTrue(HelpWindow.RIDE_EDIT_HELP.contains(example));
            assertFalse(example.contains("["));
            assertFalse(example.contains("]"));
        }
    }

    @Test
    public void commandList_rideListUsage_hasGuidanceAndExecutableExample() {
        assertTrue(HelpWindow.COMMAND_LIST.contains("persistent identifier, not a list position"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Rides panel is unchanged"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("roster additions and removals refresh this list automatically"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Deleting the selected ride leaves the filter active"));
        assertTrue(HelpWindow.COMMAND_LIST.contains("Example: ridelist 101"));
        assertFalse(HelpWindow.COMMAND_LIST.contains("Run ridelist again after roster changes"));
        assertFalse(RideListCommand.MESSAGE_USAGE.contains("[ID]"));
    }

    @Test
    public void addAndEditFormats_useSameParameterNamesAsTheUserGuide_everywhereHelpIsShown() {
        // The User Guide calls these parameters FTP_LEVEL and EXPERIENCE_LEVEL for both add and edit.
        String addMenu = CommandTemplates.ALL.get(0).display();
        String editMenu = CommandTemplates.ALL.get(1).display();

        // Full help (the command usage messages)
        assertTrue(AddCommand.MESSAGE_USAGE.contains("f/FTP_LEVEL x/EXPERIENCE_LEVEL"));
        assertTrue(EditCommand.MESSAGE_USAGE.contains("[f/FTP_LEVEL] [x/EXPERIENCE_LEVEL]"));
        assertTrue(AddCommand.MESSAGE_MISSING_FTP.endsWith("f/FTP_LEVEL."));
        // Help menu
        assertTrue(addMenu.contains("f/FTP_LEVEL x/EXPERIENCE_LEVEL"));
        assertTrue(editMenu.contains("[f/FTP_LEVEL] [x/EXPERIENCE_LEVEL]"));
        // the old short names must not come back in either place
        for (String text : new String[] {AddCommand.MESSAGE_USAGE, EditCommand.MESSAGE_USAGE, addMenu, editMenu}) {
            assertFalse(text.contains("f/FTP "), text);
            assertFalse(text.contains("[f/FTP]"), text);
            assertFalse(text.contains("[x/EXPERIENCE]"), text);
        }
    }
}
