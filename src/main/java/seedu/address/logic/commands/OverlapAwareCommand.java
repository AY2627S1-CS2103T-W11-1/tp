package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * A command that can request confirmation before introducing overlapping ride commitments.
 */
public interface OverlapAwareCommand {
    /**
     * Validates the proposed change and returns its overlap details, or an empty string if none need confirmation.
     * Does not change the model.
     *
     * @throws CommandException If the proposed change fails the normal command checks.
     */
    String getOverlapWarning(Model model) throws CommandException;
}
