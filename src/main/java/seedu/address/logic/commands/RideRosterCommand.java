package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;

/**
 * Adds or removes one cyclist from a group ride without changing the cyclist record.
 */
public class RideRosterCommand extends Command {
    public static final String JOIN_COMMAND_WORD = "rideaddcyclist";
    public static final String LEAVE_COMMAND_WORD = "ridedeletecyclist";
    public static final String MESSAGE_USAGE = "rideaddcyclist ID PHONE (alias: rac): Adds a cyclist to a ride.\n"
            + "ridedeletecyclist ID PHONE (alias: rdc): Removes a cyclist from a ride.\n"
            + "Example: rideaddcyclist 1 +65 91234567";
    public static final String MESSAGE_RIDE_NOT_FOUND = "Ride ID cannot be found.";
    public static final String MESSAGE_PERSON_NOT_FOUND = "Cyclist with this phone number cannot be found.";
    public static final String MESSAGE_ALREADY_JOINED = "Cyclist is already on this ride's roster.";
    public static final String MESSAGE_NOT_JOINED = "Cyclist is not on this ride's roster.";
    public static final String MESSAGE_FULL = "Ride has reached its maximum group size.";
    public static final String MESSAGE_LOW_POWER = "Cyclist's FTP is below the ride's minimum power.";

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
        requireNonNull(model);
        Ride ride = model.getAddressBook().getRideList().stream()
                .filter(candidate -> candidate.getId() == rideId).findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_RIDE_NOT_FOUND));
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
        model.setRide(ride, ride.withRoster(roster));
        return new CommandResult(String.format("Cyclist %s %s ride %d.", phone,
                isJoining ? "added to" : "removed from", rideId));
    }
}
