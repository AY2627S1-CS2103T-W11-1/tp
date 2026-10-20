package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.ride.Ride;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Ride> filteredRides;
    private final SortedList<Person> sortedPersons;
    /** Persistent ID of the ride whose roster is actively filtering persons, or null for another filter. */
    private Integer activeRideListId;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        // Use stored positions to preserve the relative order of names that differ only in case.
        sortedPersons = new SortedList<>(filteredPersons,
                Comparator.comparing((Person person) -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparingInt(person -> this.addressBook.getPersonList().indexOf(person)));
        filteredRides = new FilteredList<>(this.addressBook.getRideList());
        this.addressBook.getRideList().addListener(
                (ListChangeListener<Ride>) change -> refreshRideListFilter());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        requireNonNull(addressBook);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public Ride addRide(Ride ride) {
        requireNonNull(ride);
        return addressBook.addRide(ride);
    }

    @Override
    public void deleteRide(Ride target) {
        requireNonNull(target);
        addressBook.removeRide(target);
    }

    @Override
    public void setRide(Ride target, Ride editedRide) {
        requireAllNonNull(target, editedRide);
        addressBook.setRide(target, editedRide);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable, live view of matching persons sorted by full name, ignoring case.
     * Equal names retain their relative order in the address book.
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public ObservableList<Ride> getFilteredRideList() {
        return filteredRides;
    }

    @Override
    public void updateFilteredRideList(Predicate<Ride> predicate) {
        filteredRides.setPredicate(predicate);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        activeRideListId = null;
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updateFilteredPersonListForRide(int rideId) {
        if (rideId <= Ride.UNASSIGNED_ID) {
            throw new IllegalArgumentException("Ride ID must be a positive integer.");
        }
        activeRideListId = rideId;
        refreshRideListFilter();
    }

    /**
     * Replaces the active person predicate with one based on the selected ride's latest roster.
     */
    private void refreshRideListFilter() {
        if (activeRideListId == null) {
            return;
        }

        List<Phone> roster = addressBook.getRideList().stream()
                .filter(ride -> ride.getId() == activeRideListId)
                .map(Ride::getRoster)
                .findFirst()
                .orElse(List.of());
        filteredPersons.setPredicate(person -> roster.contains(person.getPhone()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
