package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data (%s).";

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

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        boolean cancelledDeletion = false;
        if (pendingDeletion != null) {
            String reply = commandText.trim();
            if (reply.equalsIgnoreCase("y") || reply.equalsIgnoreCase("yes")) {
                return confirmDeletion();
            }
            if (reply.equalsIgnoreCase("n") || reply.equalsIgnoreCase("no")) {
                cancelDeletion();
                return new CommandResult(DeleteCommand.MESSAGE_DELETE_CANCELLED);
            }
            if (!addressBookParser.isRecognisedCommand(commandText)) {
                throw new CommandException(String.format(DeleteCommand.MESSAGE_INVALID_CONFIRMATION,
                        Messages.format(pendingCyclist)));
            }
            cancelDeletion();
            cancelledDeletion = true;
        }

        try {
            Command command = addressBookParser.parseCommand(commandText);
            CommandResult result;
            if (command instanceof DeleteCommand deletion) {
                Person cyclist = deletion.getPersonToDelete(model);
                pendingDeletion = deletion;
                pendingCyclist = cyclist;
                result = new CommandResult(String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE,
                        Messages.format(cyclist)));
            } else {
                result = command.execute(model);
                saveAddressBook();
            }
            return cancelledDeletion
                    ? new CommandResult(withCancellationNotice(result.getFeedbackToUser()),
                            result.isShowHelp(), result.isExit()) : result;
        } catch (ParseException e) {
            if (cancelledDeletion) {
                throw new ParseException(withCancellationNotice(e.getMessage()), e);
            }
            throw e;
        } catch (CommandException e) {
            if (cancelledDeletion) {
                throw new CommandException(withCancellationNotice(e.getMessage()), e);
            }
            throw e;
        }
    }

    /**
     * Deletes only the unchanged cyclist shown in the pending request, then saves the result.
     * Consumes the request before execution so a failed save cannot replay the deletion.
     */
    private CommandResult confirmDeletion() throws CommandException {
        DeleteCommand deletion = pendingDeletion;
        Person cyclist = pendingCyclist;
        cancelDeletion();
        if (!deletion.getPersonToDelete(model).equals(cyclist)) {
            throw new CommandException(DeleteCommand.MESSAGE_TARGET_CHANGED);
        }
        CommandResult result = deletion.execute(model);
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
     * Includes explicit cancellation feedback before the next command's normal output or error.
     */
    private String withCancellationNotice(String feedback) {
        return DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n" + feedback;
    }

    /**
     * Saves the current address book using the existing storage-error messages.
     */
    private void saveAddressBook() throws CommandException {
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<Ride> getRideList() {
        return model.getFilteredRideList();
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
