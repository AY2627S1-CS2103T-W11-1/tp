package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
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
 * Jackson-friendly version of {@link Ride}.
 */
class JsonAdaptedRide {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Ride's %s field is missing.";
    public static final String MESSAGE_INVALID_ID = "Ride's id should be a positive whole number.";

    public static final String MESSAGE_DUPLICATE_ROSTER_MEMBER = "Ride roster contains duplicate cyclists.";
    public static final String MESSAGE_ROSTER_TOO_LARGE = "Ride roster exceeds its maximum group size.";

    private final Integer id;
    private final String name;
    private final String from;
    private final String to;
    private final String location;
    private final String distance;
    private final String minimumPower;
    private final String maxGroupSize;
    private final String notes;
    private final List<String> roster = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedRide} with the given ride details. {@code notes} and {@code roster} may be
     * null, because notes are optional and older data has no roster.
     */
    @JsonCreator
    public JsonAdaptedRide(@JsonProperty("id") Integer id, @JsonProperty("name") String name,
            @JsonProperty("from") String from, @JsonProperty("to") String to,
            @JsonProperty("location") String location, @JsonProperty("distance") String distance,
            @JsonProperty("minimumPower") String minimumPower,
            @JsonProperty("maxGroupSize") String maxGroupSize, @JsonProperty("notes") String notes,
            @JsonProperty("roster") List<String> roster) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
        this.location = location;
        this.distance = distance;
        this.minimumPower = minimumPower;
        this.maxGroupSize = maxGroupSize;
        this.notes = notes;
        if (roster != null) {
            this.roster.addAll(roster);
        }
    }

    /**
     * Converts a given {@code Ride} into this class for Jackson use.
     */
    public JsonAdaptedRide(Ride source) {
        id = source.getId();
        name = source.getName().fullName;
        from = source.getFrom().toString();
        to = source.getTo().toString();
        location = source.getLocation().value;
        distance = source.getDistance().value;
        minimumPower = source.getMinimumPower().value;
        maxGroupSize = source.getMaxGroupSize().value;
        notes = source.getNotes().map(value -> value.value).orElse(null);
        source.getRoster().forEach(phone -> roster.add(phone.value));
    }

    /**
     * Converts this Jackson-friendly adapted ride object into the model's {@code Ride} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted ride.
     */
    public Ride toModelType() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"));
        }
        if (id < 1) {
            throw new IllegalValueException(MESSAGE_INVALID_ID);
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        final RideDateTime modelFrom = toRideDateTime(from, "from");
        final RideDateTime modelTo = toRideDateTime(to, "to");
        if (!Ride.isValidPeriod(modelFrom, modelTo)) {
            throw new IllegalValueException(Ride.MESSAGE_INVALID_PERIOD);
        }

        if (location == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Location.class.getSimpleName()));
        }
        if (!Location.isValidLocation(location)) {
            throw new IllegalValueException(Location.MESSAGE_CONSTRAINTS);
        }
        final Location modelLocation = new Location(location);

        if (distance == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Distance.class.getSimpleName()));
        }
        if (!Distance.isValidDistance(distance)) {
            throw new IllegalValueException(Distance.MESSAGE_CONSTRAINTS);
        }
        final Distance modelDistance = new Distance(distance);

        if (minimumPower == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, MinimumPower.class.getSimpleName()));
        }
        if (!MinimumPower.isValidMinimumPower(minimumPower)) {
            throw new IllegalValueException(MinimumPower.MESSAGE_CONSTRAINTS);
        }
        final MinimumPower modelMinimumPower = new MinimumPower(minimumPower);

        if (maxGroupSize == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, GroupSize.class.getSimpleName()));
        }
        if (!GroupSize.isValidGroupSize(maxGroupSize)) {
            throw new IllegalValueException(GroupSize.MESSAGE_CONSTRAINTS);
        }
        final GroupSize modelGroupSize = new GroupSize(maxGroupSize);

        Optional<Notes> modelNotes = Optional.empty();
        if (notes != null) {
            if (!Notes.isValidNotes(notes)) {
                throw new IllegalValueException(Notes.MESSAGE_CONSTRAINTS);
            }
            modelNotes = Optional.of(new Notes(notes));
        }

        if (roster.size() > Integer.parseInt(modelGroupSize.value)) {
            throw new IllegalValueException(MESSAGE_ROSTER_TOO_LARGE);
        }
        final List<Phone> modelRoster = new ArrayList<>();
        Set<Phone> uniquePhones = new HashSet<>();
        for (String phone : roster) {
            if (phone == null || !Phone.isValidPhone(phone)) {
                throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
            }
            Phone modelPhone = new Phone(phone);
            if (!uniquePhones.add(modelPhone)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_ROSTER_MEMBER);
            }
            modelRoster.add(modelPhone);
        }

        return new Ride(id, modelName, modelFrom, modelTo, modelLocation, modelDistance, modelMinimumPower,
                modelGroupSize, modelNotes, modelRoster);
    }

    private static RideDateTime toRideDateTime(String dateTime, String fieldName) throws IllegalValueException {
        if (dateTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, fieldName));
        }
        if (!RideDateTime.isValidRideDateTime(dateTime)) {
            throw new IllegalValueException(RideDateTime.MESSAGE_CONSTRAINTS);
        }
        return new RideDateTime(dateTime);
    }

}
