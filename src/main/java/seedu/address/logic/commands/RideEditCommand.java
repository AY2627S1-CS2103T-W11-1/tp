package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DISTANCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FTP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GROUP_SIZE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;

/**
 * Edits the details of an existing group ride.
 */
public class RideEditCommand extends Command {

    public static final String COMMAND_WORD = "rideedit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the group ride identified by its ride ID. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: ID "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_FROM + "START] "
            + "[" + PREFIX_TO + "END] "
            + "[" + PREFIX_LOCATION + "LOCATION] "
            + "[" + PREFIX_DISTANCE + "DISTANCE] "
            + "[" + PREFIX_FTP + "MINIMUM_POWER] "
            + "[" + PREFIX_GROUP_SIZE + "MAX_GROUP_SIZE] "
            + "[" + PREFIX_NOTES + "NOTES]\n"
            + "START and END are written as DD-MM-YYYY TTTT (time on a 24-hour clock, 0000 to 2359), "
            + "and END must not be earlier than START. An empty NOTES value clears existing notes.\n"
            + "Example: " + COMMAND_WORD + " 101 "
            + PREFIX_NAME + "Morning Ride "
            + PREFIX_LOCATION + "East Coast Park "
            + PREFIX_FTP + "200";

    public static final String MESSAGE_SUCCESS = "Edited ride: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_RIDE_NOT_FOUND = "Ride with ID %1$d cannot be found.";
    public static final String MESSAGE_INVALID_ID = "Ride ID must be a positive integer.";

    private final int targetId;
    private final EditRideDescriptor editRideDescriptor;

    /**
     * Creates a command that edits the ride with {@code targetId} using {@code editRideDescriptor}.
     */
    public RideEditCommand(int targetId, EditRideDescriptor editRideDescriptor) {
        requireNonNull(editRideDescriptor);
        checkArgument(targetId > Ride.UNASSIGNED_ID, MESSAGE_INVALID_ID);
        this.targetId = targetId;
        this.editRideDescriptor = new EditRideDescriptor(editRideDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Ride rideToEdit = model.getAddressBook().getRideList().stream()
                .filter(ride -> ride.getId() == targetId)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_RIDE_NOT_FOUND, targetId)));
        Ride editedRide = createEditedRide(rideToEdit, editRideDescriptor);

        model.setRide(rideToEdit, editedRide);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.formatRide(editedRide)));
    }

    /**
     * Returns a complete ride containing the edited and preserved details of {@code rideToEdit}.
     */
    private static Ride createEditedRide(Ride rideToEdit, EditRideDescriptor editRideDescriptor)
            throws CommandException {
        assert rideToEdit != null;

        Name updatedName = editRideDescriptor.getName().orElse(rideToEdit.getName());
        RideDateTime updatedFrom = editRideDescriptor.getFrom().orElse(rideToEdit.getFrom());
        RideDateTime updatedTo = editRideDescriptor.getTo().orElse(rideToEdit.getTo());
        Location updatedLocation = editRideDescriptor.getLocation().orElse(rideToEdit.getLocation());
        Distance updatedDistance = editRideDescriptor.getDistance().orElse(rideToEdit.getDistance());
        MinimumPower updatedMinimumPower = editRideDescriptor.getMinimumPower()
                .orElse(rideToEdit.getMinimumPower());
        GroupSize updatedMaxGroupSize = editRideDescriptor.getMaxGroupSize()
                .orElse(rideToEdit.getMaxGroupSize());
        Optional<Notes> updatedNotes = editRideDescriptor.isNotesEdited()
                ? editRideDescriptor.getNotes() : rideToEdit.getNotes();

        if (!Ride.isValidPeriod(updatedFrom, updatedTo)) {
            throw new CommandException(Ride.MESSAGE_INVALID_PERIOD);
        }

        return new Ride(rideToEdit.getId(), updatedName, updatedFrom, updatedTo, updatedLocation,
                updatedDistance, updatedMinimumPower, updatedMaxGroupSize, updatedNotes, rideToEdit.getRoster());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RideEditCommand otherRideEditCommand)) {
            return false;
        }

        return targetId == otherRideEditCommand.targetId
                && editRideDescriptor.equals(otherRideEditCommand.editRideDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("editRideDescriptor", editRideDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit a ride with.
     */
    public static class EditRideDescriptor {
        private Name name;
        private RideDateTime from;
        private RideDateTime to;
        private Location location;
        private Distance distance;
        private MinimumPower minimumPower;
        private GroupSize maxGroupSize;
        /** Replacement notes; an empty value clears notes when {@code isNotesEdited} is true. */
        private Optional<Notes> notes = Optional.empty();
        /** Whether the notes prefix was supplied. */
        private boolean isNotesEdited;

        public EditRideDescriptor() {}

        /**
         * Creates a defensive copy of {@code toCopy}.
         */
        public EditRideDescriptor(EditRideDescriptor toCopy) {
            requireNonNull(toCopy);
            name = toCopy.name;
            from = toCopy.from;
            to = toCopy.to;
            location = toCopy.location;
            distance = toCopy.distance;
            minimumPower = toCopy.minimumPower;
            maxGroupSize = toCopy.maxGroupSize;
            notes = toCopy.notes;
            isNotesEdited = toCopy.isNotesEdited;
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return isNotesEdited
                    || CollectionUtil.isAnyNonNull(name, from, to, location, distance, minimumPower, maxGroupSize);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setFrom(RideDateTime from) {
            this.from = from;
        }

        public Optional<RideDateTime> getFrom() {
            return Optional.ofNullable(from);
        }

        public void setTo(RideDateTime to) {
            this.to = to;
        }

        public Optional<RideDateTime> getTo() {
            return Optional.ofNullable(to);
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Optional<Location> getLocation() {
            return Optional.ofNullable(location);
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Optional<Distance> getDistance() {
            return Optional.ofNullable(distance);
        }

        public void setMinimumPower(MinimumPower minimumPower) {
            this.minimumPower = minimumPower;
        }

        public Optional<MinimumPower> getMinimumPower() {
            return Optional.ofNullable(minimumPower);
        }

        public void setMaxGroupSize(GroupSize maxGroupSize) {
            this.maxGroupSize = maxGroupSize;
        }

        public Optional<GroupSize> getMaxGroupSize() {
            return Optional.ofNullable(maxGroupSize);
        }

        /**
         * Marks notes as edited and sets their replacement. An empty value clears existing notes.
         */
        public void setNotes(Optional<Notes> notes) {
            this.notes = requireNonNull(notes);
            isNotesEdited = true;
        }

        public Optional<Notes> getNotes() {
            return notes;
        }

        public boolean isNotesEdited() {
            return isNotesEdited;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditRideDescriptor otherEditRideDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditRideDescriptor.name)
                    && Objects.equals(from, otherEditRideDescriptor.from)
                    && Objects.equals(to, otherEditRideDescriptor.to)
                    && Objects.equals(location, otherEditRideDescriptor.location)
                    && Objects.equals(distance, otherEditRideDescriptor.distance)
                    && Objects.equals(minimumPower, otherEditRideDescriptor.minimumPower)
                    && Objects.equals(maxGroupSize, otherEditRideDescriptor.maxGroupSize)
                    && isNotesEdited == otherEditRideDescriptor.isNotesEdited
                    && Objects.equals(notes, otherEditRideDescriptor.notes);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("from", from)
                    .add("to", to)
                    .add("location", location)
                    .add("distance", distance)
                    .add("minimumPower", minimumPower)
                    .add("maxGroupSize", maxGroupSize)
                    .add("notes", notes)
                    .add("isNotesEdited", isNotesEdited)
                    .toString();
        }
    }
}
