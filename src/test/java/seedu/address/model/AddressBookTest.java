package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withLocations("Punggol").build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withLocations("Punggol").build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", rides=" + addressBook.getRideList() + ", nextRideId=" + addressBook.getNextRideId() + "}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void addRide_assignsIncreasingIdsStartingFromOne() {
        Ride first = addressBook.addRide(new RideBuilder().build());
        Ride second = addressBook.addRide(new RideBuilder().withName("Evening Spin").build());

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
        assertEquals(3, addressBook.getNextRideId());
        assertEquals(List.of(first, second), addressBook.getRideList());
    }

    @Test
    public void addRide_sameDetailsAsExistingRide_isAllowed() {
        addressBook.addRide(new RideBuilder().build());
        addressBook.addRide(new RideBuilder().build());

        assertEquals(2, addressBook.getRideList().size());
    }

    @Test
    public void addRide_nullRide_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.addRide(null));
    }

    @Test
    public void removeRide_existingRide_removesRideAndNotifiesObservableList() {
        Ride first = addressBook.addRide(new RideBuilder().build());
        Ride second = addressBook.addRide(new RideBuilder().withName("Evening Spin").build());
        int nextRideId = addressBook.getNextRideId();
        AtomicBoolean listChanged = new AtomicBoolean();
        addressBook.getRideList().addListener((ListChangeListener<Ride>) change -> listChanged.set(true));

        addressBook.removeRide(first);

        assertEquals(List.of(second), addressBook.getRideList());
        assertEquals(nextRideId, addressBook.getNextRideId());
        assertTrue(listChanged.get());
    }

    @Test
    public void removeRide_invalidTarget_throwsException() {
        Ride staleRide = addressBook.addRide(new RideBuilder().build());
        addressBook.removeRide(staleRide);

        assertThrows(NullPointerException.class, () -> addressBook.removeRide(null));
        assertThrows(IllegalArgumentException.class, () -> addressBook.removeRide(staleRide));
        assertThrows(IllegalArgumentException.class, () ->
                addressBook.removeRide(new RideBuilder().withId(99).build()));
    }

    @Test
    public void getRideList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () ->
                addressBook.getRideList().add(new RideBuilder().withId(1).build()));
    }

    @Test
    public void resetData_withRides_copiesRidesAndNextRideId() {
        AddressBook source = new AddressBook();
        source.addRide(new RideBuilder().build());
        source.addRide(new RideBuilder().withName("Evening Spin").build());

        addressBook.resetData(source);

        assertEquals(source, addressBook);
        assertEquals(3, addressBook.getNextRideId());
    }

    @Test
    public void setRides_nextRideIdLowerThanExistingIds_raisesNextRideId() {
        Ride ride = new RideBuilder().withId(7).build();

        addressBook.setRides(List.of(ride), 2);

        assertEquals(8, addressBook.getNextRideId());
    }

    @Test
    public void setRides_nextRideIdHigherThanExistingIds_keepsNextRideId() {
        Ride ride = new RideBuilder().withId(1).build();

        addressBook.setRides(List.of(ride), 5);

        assertEquals(5, addressBook.getNextRideId());
        assertEquals(5, addressBook.addRide(new RideBuilder().build()).getId());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Ride> getRideList() {
            return FXCollections.observableArrayList();
        }

        @Override
        public int getNextRideId() {
            return 1;
        }
    }

}
