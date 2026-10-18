package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.ride.Ride;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 * Rides are identified by their ID, so rides with the same details are allowed.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final ObservableList<Ride> rides = FXCollections.observableArrayList();
    private final ObservableList<Ride> unmodifiableRides = FXCollections.unmodifiableObservableList(rides);
    private int nextRideId = 1;

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the ride list with {@code rides} and sets the ID that the next added ride receives.
     * The next ride ID is raised if needed so that it is always higher than every ID in {@code rides}.
     */
    public void setRides(List<Ride> rides, int nextRideId) {
        requireNonNull(rides);
        int lowestSafeId = rides.stream().mapToInt(Ride::getId).max().orElse(0) + 1;
        this.rides.setAll(rides);
        this.nextRideId = Math.max(Math.max(nextRideId, lowestSafeId), 1);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setRides(newData.getRideList(), newData.getNextRideId());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        if (!target.getPhone().equals(editedPerson.getPhone())) {
            rides.replaceAll(ride -> ride.withRoster(ride.getRoster().stream()
                    .map(phone -> phone.equals(target.getPhone()) ? editedPerson.getPhone() : phone).toList()));
        }
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        rides.replaceAll(ride -> ride.withRoster(ride.getRoster().stream()
                .filter(phone -> !phone.equals(key.getPhone())).toList()));
    }

    //// ride-level operations

    /**
     * Adds a ride to the address book and returns it with the next free ride ID.
     * IDs go up in the order rides are added and are never reused.
     */
    public Ride addRide(Ride ride) {
        requireNonNull(ride);
        Ride storedRide = ride.withId(nextRideId);
        nextRideId++;
        rides.add(storedRide);
        return storedRide;
    }

    /**
     * Replaces the given ride {@code target} with {@code editedRide} without changing its ID or position.
     * {@code target} must exist in the address book.
     */
    public void setRide(Ride target, Ride editedRide) {
        requireNonNull(target);
        requireNonNull(editedRide);
        if (target.getId() != editedRide.getId()) {
            throw new IllegalArgumentException("An edited ride must preserve its ride ID.");
        }

        int index = rides.indexOf(target);
        if (index == -1) {
            throw new IllegalArgumentException("Target ride must exist in the address book.");
        }
        rides.set(index, editedRide);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("rides", rides)
                .add("nextRideId", nextRideId)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Ride> getRideList() {
        return unmodifiableRides;
    }

    @Override
    public int getNextRideId() {
        return nextRideId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && rides.equals(otherAddressBook.rides)
                && nextRideId == otherAddressBook.nextRideId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, rides, nextRideId);
    }
}
