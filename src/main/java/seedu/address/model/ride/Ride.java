package seedu.address.model.ride;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Represents a group ride in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Ride {

    /** The ID of a ride that has not been added to the address book yet. */
    public static final int UNASSIGNED_ID = 0;

    public static final String MESSAGE_INVALID_PERIOD =
            "The end date and time (to/) cannot be earlier than the start date and time (from/).";
    public static final String MESSAGE_INVALID_ID = "Ride ID should not be negative.";

    private final int id;
    private final Name name;
    private final RideDateTime from;
    private final RideDateTime to;
    private final Location location;
    private final Distance distance;
    private final MinimumPower minimumPower;
    private final GroupSize maxGroupSize;
    private final Optional<Notes> notes;
    private final List<Phone> roster;

    /**
     * Creates a ride with the given {@code id} and {@code roster}.
     * Every field must be present and not null, and {@code to} must not be earlier than {@code from}.
     */
    public Ride(int id, Name name, RideDateTime from, RideDateTime to, Location location, Distance distance,
            MinimumPower minimumPower, GroupSize maxGroupSize, Optional<Notes> notes, List<Phone> roster) {
        requireAllNonNull(name, from, to, location, distance, minimumPower, maxGroupSize, notes, roster);
        checkArgument(id >= UNASSIGNED_ID, MESSAGE_INVALID_ID);
        checkArgument(isValidPeriod(from, to), MESSAGE_INVALID_PERIOD);
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
        this.location = location;
        this.distance = distance;
        this.minimumPower = minimumPower;
        this.maxGroupSize = maxGroupSize;
        this.notes = notes;
        this.roster = List.copyOf(roster);
    }

    /**
     * Creates a new ride that has no ID yet and an empty roster. The ID is assigned when the ride is added to
     * the address book.
     */
    public Ride(Name name, RideDateTime from, RideDateTime to, Location location, Distance distance,
            MinimumPower minimumPower, GroupSize maxGroupSize, Optional<Notes> notes) {
        this(UNASSIGNED_ID, name, from, to, location, distance, minimumPower, maxGroupSize, notes,
                Collections.emptyList());
    }

    /**
     * Returns true if a ride may run from {@code from} to {@code to}, that is, {@code to} is not earlier than
     * {@code from}. A ride that ends at the same time it starts is allowed.
     */
    public static boolean isValidPeriod(RideDateTime from, RideDateTime to) {
        return !to.isBefore(from);
    }

    /**
     * Returns a copy of this ride that has the given {@code newId}.
     */
    public Ride withId(int newId) {
        return new Ride(newId, name, from, to, location, distance, minimumPower, maxGroupSize, notes, roster);
    }

    /**
     * Returns a copy with the supplied roster, preserving all ride details.
     */
    public Ride withRoster(List<Phone> newRoster) {
        return new Ride(id, name, from, to, location, distance, minimumPower, maxGroupSize, notes, newRoster);
    }

    public int getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public RideDateTime getFrom() {
        return from;
    }

    public RideDateTime getTo() {
        return to;
    }

    public Location getLocation() {
        return location;
    }

    public Distance getDistance() {
        return distance;
    }

    public MinimumPower getMinimumPower() {
        return minimumPower;
    }

    public GroupSize getMaxGroupSize() {
        return maxGroupSize;
    }

    public Optional<Notes> getNotes() {
        return notes;
    }

    /**
     * Returns the phone numbers of the cyclists on this ride's roster, which is unmodifiable.
     */
    public List<Phone> getRoster() {
        return roster;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Ride otherRide)) {
            return false;
        }

        return id == otherRide.id
                && name.equals(otherRide.name)
                && from.equals(otherRide.from)
                && to.equals(otherRide.to)
                && location.equals(otherRide.location)
                && distance.equals(otherRide.distance)
                && minimumPower.equals(otherRide.minimumPower)
                && maxGroupSize.equals(otherRide.maxGroupSize)
                && notes.equals(otherRide.notes)
                && roster.equals(otherRide.roster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, from, to, location, distance, minimumPower, maxGroupSize, notes, roster);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("from", from)
                .add("to", to)
                .add("location", location)
                .add("distance", distance)
                .add("minimumPower", minimumPower)
                .add("maxGroupSize", maxGroupSize)
                .add("notes", notes.map(Notes::toString).orElse("none"))
                .add("roster", roster)
                .toString();
    }

}
