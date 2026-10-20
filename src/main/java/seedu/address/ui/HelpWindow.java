package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
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

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://se-education.org/addressbook-level3/UserGuide.html";
    public static final String HELP_MESSAGE = "Refer to the user guide: " + USERGUIDE_URL;

    /** Additional Ride Edit guidance and executable examples shown after its command usage. */
    public static final String RIDE_EDIT_HELP = RideEditCommand.MESSAGE_USAGE + "\n"
            + "Supply at least one editable field. The ride ID, cyclist roster and unspecified fields stay "
            + "unchanged. Omit notes/ to preserve notes, or use notes/ without a value to clear them. "
            + "The minimum FTP cannot exceed any current roster member's FTP, and the maximum group size cannot "
            + "be smaller than the current roster size. Invalid edits leave the ride unchanged.\n"
            + "More examples:\n"
            + "rideedit 101 n/NightRide\n"
            + "rideedit 101 f/250\n"
            + "rideedit 101 from/20-10-2026 1400 to/20-10-2026 1600\n"
            + "rideedit 101 notes/";

    private static final String RIDE_DELETE_DETAILS = "Use the ride's persistent ID. Deletion requires no "
            + "confirmation. It removes the ride and its roster associations, but leaves cyclist records and "
            + "other rides and their rosters unchanged. Remaining ride IDs are not renumbered, deleted IDs are "
            + "not reused, and an unknown ID is rejected.";
    private static final String RIDE_LIST_GUIDANCE = RideListCommand.MESSAGE_USAGE + "\n"
            + "The ID is a persistent identifier, not a list position. The left-hand cyclist panel shows the "
            + "ride's registered cyclists using its existing alphabetically sorted cards; the Rides panel is "
            + "unchanged.\n"
            + "An empty roster shows no cyclist cards. Successful roster additions and removals refresh this list "
            + "automatically. Deleting the selected ride leaves the filter active and shows no cyclists.\n"
            + "list restores all cyclists, while find or another ridelist replaces the Ride List filter.\n"
            + "Invalid or unknown IDs and unresolved roster references are rejected without changing the previous "
            + "filter. This command does not modify ride or cyclist records.";

    /**
     * The usage text of every command, shown in the help window. Commands that already define a
     * {@code MESSAGE_USAGE} reuse it so the help window stays in sync with the commands themselves.
     */
    public static final String COMMAND_LIST = String.join("\n\n",
            AddCommand.MESSAGE_USAGE,
            EditCommand.MESSAGE_USAGE,
            DeleteCommand.MESSAGE_USAGE,
            FindCommand.MESSAGE_USAGE,
            RideAddCommand.MESSAGE_USAGE,
            RIDE_EDIT_HELP,
            RideDeleteCommand.MESSAGE_USAGE + "\n" + RIDE_DELETE_DETAILS,
            RideRosterCommand.MESSAGE_USAGE,
            RIDE_LIST_GUIDANCE,
            RideFindCommand.MESSAGE_USAGE,
            ListCommand.COMMAND_WORD + ": Lists all persons.\nExample: " + ListCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD + ": Clears all entries.\nExample: " + ClearCommand.COMMAND_WORD,
            HelpCommand.MESSAGE_USAGE,
            ExitCommand.COMMAND_WORD + ": Exits the program.\nExample: " + ExitCommand.COMMAND_WORD);

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private TextArea commandList;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        commandList.setText(COMMAND_LIST);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
