package seedu.address.ui;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;

/**
 * Holds the one-line command formats shown in the Help dropdown menu. Command words and prefixes
 * come from the command classes and {@code CliSyntax}, so the menu follows any change to them.
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
                            + PREFIX_EMAIL + "EMAIL " + PREFIX_FTP + "FTP " + PREFIX_EXPERIENCE_LEVEL
                            + "EXPERIENCE_LEVEL " + PREFIX_LOCATION + "LOCATION [" + PREFIX_ADDRESS
                            + "ADDRESS] [" + PREFIX_TAG + "TAG]...",
                    AddCommand.COMMAND_WORD + " " + PREFIX_NAME + " " + PREFIX_PHONE + " " + PREFIX_EMAIL + " "
                            + PREFIX_FTP + " " + PREFIX_EXPERIENCE_LEVEL + " " + PREFIX_LOCATION),
            new Template(
                    EditCommand.COMMAND_WORD + " PHONE [" + PREFIX_NAME + "NAME] [" + PREFIX_PHONE + "PHONE] ["
                            + PREFIX_EMAIL + "EMAIL] [" + PREFIX_ADDRESS + "ADDRESS] [" + PREFIX_TAG + "TAG]...",
                    EditCommand.COMMAND_WORD + " "),
            new Template(
                    DeleteCommand.COMMAND_WORD + " PHONE",
                    DeleteCommand.COMMAND_WORD + " "),
            new Template(
                    FindCommand.COMMAND_WORD + " [" + PREFIX_NAME + "NAME_KEYWORD]... [" + PREFIX_PHONE
                            + "PHONE]...",
                    FindCommand.COMMAND_WORD + " "),
            new Template(ListCommand.COMMAND_WORD, ListCommand.COMMAND_WORD),
            new Template(ClearCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD),
            new Template(HelpCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD),
            new Template(ExitCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD));

    private CommandTemplates() {} // prevents instantiation

}
