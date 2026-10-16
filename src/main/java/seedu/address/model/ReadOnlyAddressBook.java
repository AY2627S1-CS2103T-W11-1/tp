package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an unmodifiable view of the rides list, in the order the rides were added.
     */
    ObservableList<Ride> getRideList();

    /**
     * Returns the ID that the next added ride will receive. It is higher than the ID of every existing ride.
     */
    int getNextRideId();

}
