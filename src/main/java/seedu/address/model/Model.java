package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Adds the given ride and returns it with the next free ride ID. IDs are never reused.
     */
    Ride addRide(Ride ride);

    /**
     * Deletes the given ride.
     * The ride must exist in the address book.
     */
    void deleteRide(Ride target);

    /**
     * Replaces the given ride {@code target} with {@code editedRide}, preserving its ID and position.
     * {@code target} must exist in the address book.
     */
    void setRide(Ride target, Ride editedRide);

    /**
     * Returns an unmodifiable, live view of the filtered person list sorted by full name, ignoring case.
     * Persons with equal names retain their relative order in the address book.
     */
    ObservableList<Person> getFilteredPersonList();

    /** Returns the live view of rides matching the current ride filter. */
    default ObservableList<Ride> getFilteredRideList() {
        return getAddressBook().getRideList();
    }

    /** Updates the ride list filter to the given predicate. */
    default void updateFilteredRideList(Predicate<Ride> predicate) {
        throw new UnsupportedOperationException("Ride filtering is not supported by this model.");
    }

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Filters the person list by the current roster of the ride identified by {@code rideId}.
     * The filter remains active and refreshes when the ride list changes.
     *
     * @throws IllegalArgumentException if {@code rideId} is not positive.
     */
    void updateFilteredPersonListForRide(int rideId);
}
