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
        validateId();
        Name modelName = toModelName();
        RideDateTime modelFrom = toRideDateTime(from, "from");
        RideDateTime modelTo = toRideDateTime(to, "to");
        validatePeriod(modelFrom, modelTo);
        Location modelLocation = toModelLocation();
        Distance modelDistance = toModelDistance();
        MinimumPower modelMinimumPower = toModelMinimumPower();
        GroupSize modelGroupSize = toModelGroupSize();
        Optional<Notes> modelNotes = toModelNotes();
        List<Phone> modelRoster = toModelRoster(modelGroupSize);
        return new Ride(id, modelName, modelFrom, modelTo, modelLocation, modelDistance, modelMinimumPower,
                modelGroupSize, modelNotes, modelRoster);
    }

    /**
     * Checks that the stored ride has a positive ID before converting its fields.
     */
    private void validateId() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"));
        }
        if (id < 1) {
            throw new IllegalValueException(MESSAGE_INVALID_ID);
        }
    }

    /**
     * Checks that the end is not earlier than the start.
     */
    private static void validatePeriod(RideDateTime from, RideDateTime to) throws IllegalValueException {
        if (!Ride.isValidPeriod(from, to)) {
            throw new IllegalValueException(Ride.MESSAGE_INVALID_PERIOD);
        }
    }

    /**
     * Converts and validates the required ride name.
     */
    private Name toModelName() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(name);
    }

    /**
     * Converts and validates the required location.
     */
    private Location toModelLocation() throws IllegalValueException {
        if (location == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Location.class.getSimpleName()));
        }
        if (!Location.isValidLocation(location)) {
            throw new IllegalValueException(Location.MESSAGE_CONSTRAINTS);
        }
        return new Location(location);
    }

    /**
     * Converts and validates the required distance.
     */
    private Distance toModelDistance() throws IllegalValueException {
        if (distance == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Distance.class.getSimpleName()));
        }
        if (!Distance.isValidDistance(distance)) {
            throw new IllegalValueException(Distance.MESSAGE_CONSTRAINTS);
        }
        return new Distance(distance);
    }

    /**
     * Converts and validates the minimum power.
     */
    private MinimumPower toModelMinimumPower() throws IllegalValueException {
        if (minimumPower == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, MinimumPower.class.getSimpleName()));
        }
        if (!MinimumPower.isValidMinimumPower(minimumPower)) {
            throw new IllegalValueException(MinimumPower.MESSAGE_CONSTRAINTS);
        }
        return new MinimumPower(minimumPower);
    }

    /**
     * Converts and validates the maximum group size.
     */
    private GroupSize toModelGroupSize() throws IllegalValueException {
        if (maxGroupSize == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, GroupSize.class.getSimpleName()));
        }
        if (!GroupSize.isValidGroupSize(maxGroupSize)) {
            throw new IllegalValueException(GroupSize.MESSAGE_CONSTRAINTS);
        }
        return new GroupSize(maxGroupSize);
    }

    /**
     * Converts optional notes without inventing a value when absent.
     */
    private Optional<Notes> toModelNotes() throws IllegalValueException {
        if (notes == null) {
            return Optional.empty();
        }
        if (!Notes.isValidNotes(notes)) {
            throw new IllegalValueException(Notes.MESSAGE_CONSTRAINTS);
        }
        return Optional.of(new Notes(notes));
    }

    /**
     * Converts roster phones, checking capacity and duplicates.
     */
    private List<Phone> toModelRoster(GroupSize modelGroupSize) throws IllegalValueException {
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
        return modelRoster;
    }

    /**
     * Converts a required date-time, identifying missing values by their stored field name.
     */
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
