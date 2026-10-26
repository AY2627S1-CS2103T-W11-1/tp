package seedu.address.logic;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.OverlapAwareCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data (%s).";

    public static final String MESSAGE_CONFIRM_OVERLAP = "Warning: overlapping ride times.\n"
            + Messages.MESSAGE_OVERLAP_INSTRUCTIONS + "\n%1$s";
    public static final String MESSAGE_OVERLAP_CANCELLED = "Ride change cancelled.";
    public static final String MESSAGE_INVALID_OVERLAP_REPLY = "Invalid confirmation. %1$s";
    public static final String MESSAGE_OVERLAP_DATA_CHANGED =
            "Cyclist or ride data has changed. Enter the ride command again to review the overlaps.";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /** The deletion awaiting a reply; confirmation state is never persisted. */
    private DeleteCommand pendingDeletion;
    /** Immutable snapshot of the cyclist shown in the confirmation request. */
    private Person pendingCyclist;

    /** The ride command awaiting overlap confirmation; never persisted. */
    private Command pendingRideChange;
    /** Snapshot used to reject confirmation if any relevant stored data changes. */
    private AddressBook pendingRideSnapshot;
    /** Full warning repeated after invalid confirmation replies. */
    private String pendingOverlapPrompt;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        requireAllNonNull(model, storage);
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        assert (pendingDeletion == null) == (pendingCyclist == null)
                : "Pending deletion and cyclist must be present or absent together.";
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        String cancellationNotice = "";
        if (pendingRideChange != null) {
            String reply = commandText.trim();
            if (reply.equalsIgnoreCase("y") || reply.equalsIgnoreCase("yes")) {
                return confirmRideChange();
            }
            if (reply.equalsIgnoreCase("n") || reply.equalsIgnoreCase("no")) {
                cancelRideChange();
                return new CommandResult(MESSAGE_OVERLAP_CANCELLED);
            }
            if (!addressBookParser.isRecognisedCommand(commandText)) {
                throw new CommandException(String.format(MESSAGE_INVALID_OVERLAP_REPLY, pendingOverlapPrompt));
            }
            cancelRideChange();
            cancellationNotice = MESSAGE_OVERLAP_CANCELLED + "\n";
        }
        if (pendingDeletion != null) {
            String reply = commandText.trim();
            if (reply.equalsIgnoreCase("y") || reply.equalsIgnoreCase("yes")) {
                return confirmDeletion();
            }
            if (reply.equalsIgnoreCase("n") || reply.equalsIgnoreCase("no")) {
                cancelDeletion();
                logger.fine("Pending deletion cancelled by user reply.");
                return new CommandResult(DeleteCommand.MESSAGE_DELETE_CANCELLED);
            }
            if (!addressBookParser.isRecognisedCommand(commandText)) {
                throw new CommandException(String.format(DeleteCommand.MESSAGE_INVALID_CONFIRMATION,
                        Messages.format(pendingCyclist)));
            }
            cancelDeletion();
            logger.fine("Pending deletion cancelled by a subsequent command.");
            cancellationNotice = DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n";
        }

        try {
            Command command = addressBookParser.parseCommand(commandText);
            String overlapWarning = command instanceof OverlapAwareCommand overlapAware
                    ? overlapAware.getOverlapWarning(model) : "";
            CommandResult result;
            if (command instanceof DeleteCommand deletion) {
                Person cyclist = deletion.getPersonToDelete(model);
                pendingDeletion = deletion;
                pendingCyclist = cyclist;
                logger.fine("Deletion confirmation requested.");
                result = new CommandResult(String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE,
                        Messages.format(cyclist)));
            } else if (!overlapWarning.isEmpty()) {
                pendingRideChange = command;
                pendingRideSnapshot = new AddressBook(model.getAddressBook());
                pendingOverlapPrompt = String.format(MESSAGE_CONFIRM_OVERLAP, overlapWarning);
                result = new CommandResult(pendingOverlapPrompt);
            } else {
                result = command.execute(model);
                saveAddressBook();
            }
            return !cancellationNotice.isEmpty()
                    ? new CommandResult(cancellationNotice + result.getFeedbackToUser(),
                            result.isShowHelp(), result.isExit()) : result;
        } catch (ParseException e) {
            if (!cancellationNotice.isEmpty()) {
                throw new ParseException(cancellationNotice + e.getMessage(), e);
            }
            throw e;
        } catch (CommandException e) {
            if (!cancellationNotice.isEmpty()) {
                throw new CommandException(cancellationNotice + e.getMessage(), e);
            }
            throw e;
        }
    }

    /**
     * Deletes only the unchanged cyclist shown in the pending request, then saves the result.
     * Consumes the request before execution so a failed save cannot replay the deletion.
     */
    private CommandResult confirmDeletion() throws CommandException {
        assert pendingDeletion != null : "A deletion request must exist before confirmation.";
        assert pendingCyclist != null : "A cyclist snapshot must exist before confirmation.";
        DeleteCommand deletion = pendingDeletion;
        Person cyclist = pendingCyclist;
        cancelDeletion();
        if (!deletion.getPersonToDelete(model).equals(cyclist)) {
            logger.fine("Deletion confirmation rejected because the target changed.");
            throw new CommandException(DeleteCommand.MESSAGE_TARGET_CHANGED);
        }
        CommandResult result = deletion.execute(model);
        logger.fine("Confirmed deletion executed; saving the updated address book.");
        saveAddressBook();
        return result;
    }

    /**
     * Clears the pending deletion without changing cyclist data or the active filter.
     */
    private void cancelDeletion() {
        pendingDeletion = null;
        pendingCyclist = null;
    }

    /**
     * Applies a confirmed ride command only to the data reviewed by the user.
     * Consumes the request before execution so failures cannot replay the change.
     */
    private CommandResult confirmRideChange() throws CommandException {
        Command command = pendingRideChange;
        AddressBook snapshot = pendingRideSnapshot;
        cancelRideChange();
        if (!snapshot.equals(model.getAddressBook())) {
            throw new CommandException(MESSAGE_OVERLAP_DATA_CHANGED);
        }
        CommandResult result = command.execute(model);
        saveAddressBook();
        return result;
    }

    /**
     * Clears the pending ride change without changing or saving data.
     */
    private void cancelRideChange() {
        pendingRideChange = null;
        pendingRideSnapshot = null;
        pendingOverlapPrompt = null;
    }

    /**
     * Saves the current address book using the existing storage-error messages.
     */
    private void saveAddressBook() throws CommandException {
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            logger.log(Level.WARNING, "Permission denied saving data to " + storage.getAddressBookFilePath(), e);
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Failed to save data to " + storage.getAddressBookFilePath(), ioe);
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<Ride> getRideList() {
        return model.getAddressBook().getRideList();
    }

    @Override
    public ObservableList<Ride> getFilteredRideList() {
        return model.getFilteredRideList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
