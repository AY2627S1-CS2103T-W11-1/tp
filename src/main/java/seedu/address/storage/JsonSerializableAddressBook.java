package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_UNKNOWN_ROSTER_MEMBER =
            "Ride roster refers to a cyclist who is not in the cyclist list.";
    public static final String MESSAGE_MISSING_PERSONS = "Cyclist list is missing.";
    public static final String MESSAGE_NULL_PERSON = "Cyclist list contains a null entry.";
    public static final String MESSAGE_DUPLICATE_PERSON = "Cyclist list contains duplicate cyclists.";
    public static final String MESSAGE_DUPLICATE_RIDE_ID = "Ride list contains duplicate ride IDs.";
    public static final String MESSAGE_NULL_RIDE = "Ride list contains a null entry.";
    public static final String MESSAGE_INVALID_NEXT_RIDE_ID = "Next ride ID should be a positive whole number.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedRide> rides = new ArrayList<>();
    private final Integer nextRideId;
    /** Tracks a missing required list so conversion can report a checked validation error. */
    @JsonIgnore
    private final boolean isPersonsMissing;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons, rides and next ride ID.
     * Files saved before rides existed have no rides or next ride ID, so both may be null.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("rides") List<JsonAdaptedRide> rides,
            @JsonProperty("nextRideId") Integer nextRideId) {
        isPersonsMissing = persons == null;
        if (persons != null) {
            this.persons.addAll(persons);
        }
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
        isPersonsMissing = false;
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
        addModelPersons(addressBook);
        List<Ride> modelRides = toModelRides(addressBook);
        // Without a saved counter, carry on numbering after the highest saved ID.
        addressBook.setRides(modelRides, nextRideId == null ? 1 : nextRideId);
        return addressBook;
    }

    /**
     * Validates and adds cyclists before ride rosters are checked against them.
     */
    private void addModelPersons(AddressBook addressBook) throws IllegalValueException {
        if (isPersonsMissing) {
            throw new IllegalValueException(MESSAGE_MISSING_PERSONS);
        }
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            if (jsonAdaptedPerson == null) {
                throw new IllegalValueException(MESSAGE_NULL_PERSON);
            }
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
    }

    /**
     * Converts rides, checking ID uniqueness and references to the converted cyclists.
     */
    private List<Ride> toModelRides(AddressBook addressBook) throws IllegalValueException {
        if (nextRideId != null && nextRideId < 1) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_RIDE_ID);
        }
        List<Ride> modelRides = new ArrayList<>();
        Set<Integer> rideIds = new HashSet<>();
        Set<Phone> cyclistPhones = addressBook.getPersonList().stream()
                .map(Person::getPhone).collect(Collectors.toSet());
        for (JsonAdaptedRide jsonAdaptedRide : rides) {
            if (jsonAdaptedRide == null) {
                throw new IllegalValueException(MESSAGE_NULL_RIDE);
            }
            Ride ride = jsonAdaptedRide.toModelType();
            if (ride.getId() == Integer.MAX_VALUE) {
                throw new IllegalValueException(AddressBook.MESSAGE_INVALID_RIDE_IDS);
            }
            if (!rideIds.add(ride.getId())) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_RIDE_ID);
            }
            if (!cyclistPhones.containsAll(ride.getRoster())) {
                throw new IllegalValueException(MESSAGE_UNKNOWN_ROSTER_MEMBER);
            }
            modelRides.add(ride);
        }
        return modelRides;
    }
}
