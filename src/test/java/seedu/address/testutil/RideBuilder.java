package seedu.address.testutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Distance;
import seedu.address.model.ride.GroupSize;
import seedu.address.model.ride.MinimumPower;
import seedu.address.model.ride.Notes;
import seedu.address.model.ride.Ride;
import seedu.address.model.ride.RideDateTime;

/**
 * A utility class to help with building Ride objects.
 */
public class RideBuilder {

    public static final String DEFAULT_NAME = "Sunday Sunrise Ride";
    public static final String DEFAULT_FROM = "18-10-2026 0630";
    public static final String DEFAULT_TO = "18-10-2026 0930";
    public static final String DEFAULT_LOCATION = "East Coast";
    public static final String DEFAULT_DISTANCE = "45";
    public static final String DEFAULT_MINIMUM_POWER = "180";
    public static final String DEFAULT_GROUP_SIZE = "12";

    private int id;
    private Name name;
    private RideDateTime from;
    private RideDateTime to;
    private Location location;
    private Distance distance;
    private MinimumPower minimumPower;
    private GroupSize maxGroupSize;
    private Optional<Notes> notes;
    private List<Phone> roster;

    /**
     * Creates a {@code RideBuilder} with the default details, no notes, no ID yet and an empty roster.
     */
    public RideBuilder() {
        id = Ride.UNASSIGNED_ID;
        name = new Name(DEFAULT_NAME);
        from = new RideDateTime(DEFAULT_FROM);
        to = new RideDateTime(DEFAULT_TO);
        location = new Location(DEFAULT_LOCATION);
        distance = new Distance(DEFAULT_DISTANCE);
        minimumPower = new MinimumPower(DEFAULT_MINIMUM_POWER);
        maxGroupSize = new GroupSize(DEFAULT_GROUP_SIZE);
        notes = Optional.empty();
        roster = new ArrayList<>();
    }

    /**
     * Initializes the RideBuilder with the data of {@code rideToCopy}.
     */
    public RideBuilder(Ride rideToCopy) {
        id = rideToCopy.getId();
        name = rideToCopy.getName();
        from = rideToCopy.getFrom();
        to = rideToCopy.getTo();
        location = rideToCopy.getLocation();
        distance = rideToCopy.getDistance();
        minimumPower = rideToCopy.getMinimumPower();
        maxGroupSize = rideToCopy.getMaxGroupSize();
        notes = rideToCopy.getNotes();
        roster = new ArrayList<>(rideToCopy.getRoster());
    }

    /**
     * Sets the ID of the {@code Ride} that we are building.
     */
    public RideBuilder withId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Ride} that we are building.
     */
    public RideBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the start {@code RideDateTime} of the {@code Ride} that we are building.
     */
    public RideBuilder withFrom(String from) {
        this.from = new RideDateTime(from);
        return this;
    }

    /**
     * Sets the end {@code RideDateTime} of the {@code Ride} that we are building.
     */
    public RideBuilder withTo(String to) {
        this.to = new RideDateTime(to);
        return this;
    }

    /**
     * Sets the {@code Location} of the {@code Ride} that we are building.
     */
    public RideBuilder withLocation(String location) {
        this.location = new Location(location);
        return this;
    }

    /**
     * Sets the {@code Distance} of the {@code Ride} that we are building.
     */
    public RideBuilder withDistance(String distance) {
        this.distance = new Distance(distance);
        return this;
    }

    /**
     * Sets the {@code MinimumPower} of the {@code Ride} that we are building.
     */
    public RideBuilder withMinimumPower(String minimumPower) {
        this.minimumPower = new MinimumPower(minimumPower);
        return this;
    }

    /**
     * Sets the maximum {@code GroupSize} of the {@code Ride} that we are building.
     */
    public RideBuilder withMaxGroupSize(String maxGroupSize) {
        this.maxGroupSize = new GroupSize(maxGroupSize);
        return this;
    }

    /**
     * Sets the {@code Notes} of the {@code Ride} that we are building.
     */
    public RideBuilder withNotes(String notes) {
        this.notes = Optional.of(new Notes(notes));
        return this;
    }

    /**
     * Sets the roster of the {@code Ride} that we are building to the given phone numbers.
     */
    public RideBuilder withRoster(String... phones) {
        roster = new ArrayList<>();
        for (String phone : phones) {
            roster.add(new Phone(phone));
        }
        return this;
    }

    public Ride build() {
        return new Ride(id, name, from, to, location, distance, minimumPower, maxGroupSize, notes, roster);
    }

}
