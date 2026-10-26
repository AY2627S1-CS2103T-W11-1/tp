package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;

/**
 * Adds or removes one cyclist from a group ride without changing the cyclist record.
 */
public class RideRosterCommand extends Command implements OverlapAwareCommand {
    public static final String JOIN_COMMAND_WORD = "rideaddcyclist";
    public static final String JOIN_COMMAND_ALIAS = "rac";
    public static final String LEAVE_COMMAND_WORD = "ridedeletecyclist";
    public static final String LEAVE_COMMAND_ALIAS = "rdc";
    public static final String MESSAGE_USAGE = JOIN_COMMAND_WORD
            + " (" + JOIN_COMMAND_ALIAS + ") ID PHONE: Adds a cyclist to a ride.\n"
            + LEAVE_COMMAND_WORD
            + " (" + LEAVE_COMMAND_ALIAS + ") ID PHONE: Removes a cyclist from a ride.\n"
            + "Example: rideaddcyclist 1 +65 91234567\n"
            + "Overlapping registrations require y/yes to proceed or n/no to cancel.";
    public static final String MESSAGE_RIDE_NOT_FOUND = "Ride ID cannot be found.";
    public static final String MESSAGE_PERSON_NOT_FOUND = "Cyclist with this phone number cannot be found.";
    public static final String MESSAGE_ALREADY_JOINED = "Cyclist is already on this ride's roster.";
    public static final String MESSAGE_NOT_JOINED = "Cyclist is not on this ride's roster.";
    public static final String MESSAGE_FULL = "Ride has reached its maximum group size.";
    public static final String MESSAGE_LOW_POWER = "Cyclist's FTP is below the ride's minimum power.";

    private static final Logger logger = LogsCenter.getLogger(RideRosterCommand.class);

    private final int rideId;
    private final Phone phone;
    private final boolean isJoining;

    /**
     * Creates a roster change targeting a stable ride ID and cyclist phone number.
     */
    public RideRosterCommand(int rideId, Phone phone, boolean isJoining) {
        this.rideId = rideId;
        this.phone = requireNonNull(phone);
        this.isJoining = isJoining;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Ride ride = getRide(model);
        Ride updatedRide = createUpdatedRide(model, ride);
        model.setRide(ride, updatedRide);
        logger.fine(() -> "Completed roster " + (isJoining ? "addition" : "removal")
                + " for ride ID " + rideId + "; roster size: " + updatedRide.getRoster().size() + ".");
        return new CommandResult(String.format("Cyclist %s %s ride %d.", phone,
                isJoining ? "added to" : "removed from", rideId));
    }

    @Override
    public String getOverlapWarning(Model model) throws CommandException {
        Ride updatedRide = createUpdatedRide(model, getRide(model));
        return isJoining ? RideOverlapWarning.describe(updatedRide, List.of(phone),
                model.getAddressBook().getRideList()) : "";
    }

    /**
     * Resolves the stable ride ID independently of the active filter.
     */
    private Ride getRide(Model model) throws CommandException {
        requireNonNull(model);
        return model.getAddressBook().getRideList().stream()
                .filter(candidate -> candidate.getId() == rideId).findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_RIDE_NOT_FOUND));
    }

    /**
     * Checks eligibility and builds the proposed roster without changing stored data.
     */
    private Ride createUpdatedRide(Model model, Ride ride) throws CommandException {
        ArrayList<Phone> roster = new ArrayList<>(ride.getRoster());
        if (isJoining) {
            Person cyclist = model.getAddressBook().getPersonList().stream()
                    .filter(person -> person.getPhone().equals(phone)).findFirst()
                    .orElseThrow(() -> new CommandException(MESSAGE_PERSON_NOT_FOUND));
            if (roster.contains(phone)) {
                throw new CommandException(MESSAGE_ALREADY_JOINED);
            }
            if (roster.size() >= Integer.parseInt(ride.getMaxGroupSize().value)) {
                throw new CommandException(MESSAGE_FULL);
            }
            if (Integer.parseInt(cyclist.getFtp().value) < Integer.parseInt(ride.getMinimumPower().value)) {
                throw new CommandException(MESSAGE_LOW_POWER);
            }
            roster.add(phone);
        } else if (!roster.remove(phone)) {
            throw new CommandException(MESSAGE_NOT_JOINED);
        }
        assert roster.contains(phone) == isJoining : "Roster membership must match the requested operation.";
        assert roster.size() == ride.getRoster().size() + (isJoining ? 1 : -1)
                : "A roster operation must add or remove exactly one cyclist.";
        return ride.withRoster(roster);
    }
}
