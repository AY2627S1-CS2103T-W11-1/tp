package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Cyclist list contains duplicate cyclists.";
    public static final String MESSAGE_DUPLICATE_RIDE_ID = "Ride list contains duplicate ride IDs.";
    public static final String MESSAGE_NULL_RIDE = "Ride list contains a null entry.";
    public static final String MESSAGE_INVALID_NEXT_RIDE_ID = "Next ride ID should be a positive whole number.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedRide> rides = new ArrayList<>();
    private final Integer nextRideId;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons, rides and next ride ID.
     * Files saved before rides existed have no rides or next ride ID, so both may be null.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("rides") List<JsonAdaptedRide> rides,
            @JsonProperty("nextRideId") Integer nextRideId) {
        this.persons.addAll(persons);
        if (rides != null) {
            this.rides.addAll(rides);
        }
        this.nextRideId = nextRideId;
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        rides.addAll(source.getRideList().stream().map(JsonAdaptedRide::new).collect(Collectors.toList()));
        nextRideId = source.getNextRideId();
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }

        if (nextRideId != null && nextRideId < 1) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_RIDE_ID);
        }
        List<Ride> modelRides = new ArrayList<>();
        Set<Integer> rideIds = new HashSet<>();
        for (JsonAdaptedRide jsonAdaptedRide : rides) {
            if (jsonAdaptedRide == null) {
                throw new IllegalValueException(MESSAGE_NULL_RIDE);
            }
            Ride ride = jsonAdaptedRide.toModelType();
            if (!rideIds.add(ride.getId())) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_RIDE_ID);
            }
            modelRides.add(ride);
        }
        // Without a saved counter, carry on numbering after the highest saved ID.
        addressBook.setRides(modelRides, nextRideId == null ? 1 : nextRideId);
        return addressBook;
    }

}
