package seedu.address.ui;

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
import seedu.address.logic.commands.RideListCommand;
import seedu.address.logic.commands.RideRosterCommand;

/**
 * Holds the one-line command formats shown in the Help dropdown menu. Command words and prefixes
 * come from the command classes and {@code CliSyntax} where available. Roster templates are
 * defined here so the dropdown can be delivered independently of the roster commands.
 */
public class CommandTemplates {

    /**
     * A command shown in the Help menu.
     *
     * @param display the one-line format shown as the menu item text
     * @param insertText the starter text placed in the command box when the menu item is clicked
     */
    public record Template(String display, String insertText) {}

    /** All commands, in the order they appear in the Help menu. */
    public static final List<Template> ALL = List.of(
            new Template(
                    AddCommand.COMMAND_WORD + " " + PREFIX_NAME + "NAME " + PREFIX_PHONE + "PHONE "
                            + PREFIX_EMERGENCY_PHONE + "EMERGENCY_PHONE "
                            + PREFIX_EMAIL + "EMAIL " + PREFIX_FTP + "FTP " + PREFIX_EXPERIENCE_LEVEL
                            + "EXPERIENCE_LEVEL " + PREFIX_LOCATION + "LOCATION [" + PREFIX_LOCATION + "LOCATION]...",
                    AddCommand.COMMAND_WORD + " " + PREFIX_NAME + " " + PREFIX_PHONE + " "
                            + PREFIX_EMERGENCY_PHONE + " " + PREFIX_EMAIL + " "
                            + PREFIX_FTP + " " + PREFIX_EXPERIENCE_LEVEL + " " + PREFIX_LOCATION),
            new Template(
                    EditCommand.COMMAND_WORD + " PHONE [" + PREFIX_NAME + "NAME] [" + PREFIX_PHONE + "PHONE] ["
                            + PREFIX_EMERGENCY_PHONE + "EMERGENCY_PHONE] ["
                            + PREFIX_EMAIL + "EMAIL] [" + PREFIX_FTP + "FTP] [" + PREFIX_EXPERIENCE_LEVEL
                            + "EXPERIENCE_LEVEL] [" + PREFIX_LOCATION + "LOCATION]...",
                    EditCommand.COMMAND_WORD + " "),
            new Template(
                    DeleteCommand.COMMAND_WORD + " PHONE",
                    DeleteCommand.COMMAND_WORD + " "),
            new Template(
                    FindCommand.COMMAND_WORD + " [" + PREFIX_NAME + "NAME_KEYWORD]... [" + PREFIX_PHONE
                            + "PHONE]... [" + PREFIX_EMERGENCY_PHONE + "EMERGENCY_PHONE]...",
                    FindCommand.COMMAND_WORD + " "),
            new Template(
                    RideAddCommand.COMMAND_WORD + " " + PREFIX_NAME + "NAME " + PREFIX_FROM + "FROM "
                            + PREFIX_TO + "TO " + PREFIX_LOCATION + "LOCATION " + PREFIX_DISTANCE + "DISTANCE "
                            + PREFIX_FTP + "MIN_POWER " + PREFIX_GROUP_SIZE + "MAX_GROUP_SIZE ["
                            + PREFIX_NOTES + "NOTES]",
                    RideAddCommand.COMMAND_WORD + " " + PREFIX_NAME + " " + PREFIX_FROM + " " + PREFIX_TO + " "
                            + PREFIX_LOCATION + " " + PREFIX_DISTANCE + " " + PREFIX_FTP + " "
                            + PREFIX_GROUP_SIZE),
            new Template(
                    RideEditCommand.COMMAND_WORD + " ID [" + PREFIX_NAME + "NAME] [" + PREFIX_FROM + "START] ["
                            + PREFIX_TO + "END] [" + PREFIX_LOCATION + "LOCATION] [" + PREFIX_DISTANCE
                            + "DISTANCE] [" + PREFIX_FTP + "MIN_POWER] [" + PREFIX_GROUP_SIZE + "MAX_GROUP_SIZE] ["
                            + PREFIX_NOTES + "NOTES]",
                    RideEditCommand.COMMAND_WORD + " "),
            new Template(
                    RideDeleteCommand.COMMAND_WORD + " ID",
                    RideDeleteCommand.COMMAND_WORD + " "),
            new Template("rideaddcyclist ID PHONE", RideRosterCommand.JOIN_COMMAND_WORD + " "),
            new Template("ridedeletecyclist ID PHONE", RideRosterCommand.LEAVE_COMMAND_WORD + " "),
            new Template(RideListCommand.COMMAND_WORD + " ID", RideListCommand.COMMAND_WORD + " "),
            new Template(ListCommand.COMMAND_WORD, ListCommand.COMMAND_WORD),
            new Template(ClearCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD),
            new Template(HelpCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD),
            new Template(ExitCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD));

    private CommandTemplates() {} // prevents instantiation

}
