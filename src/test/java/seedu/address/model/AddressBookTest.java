package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    public void setRides_invalidIds_leavesRidesAndCounterUnchanged() {
        // EP: unassigned ID, ID leaving no room for a counter, and duplicate stored IDs.
        // Boundary values: 0 is below the minimum ID; Integer.MAX_VALUE is one above the last assignable ID.
        Ride original = addressBook.addRide(new RideBuilder().build());
        List<List<Ride>> invalidLists = List.of(
                List.of(new RideBuilder().withId(0).build()),
                List.of(new RideBuilder().withId(Integer.MAX_VALUE).build()),
                List.of(original, new RideBuilder().withId(original.getId()).withName("Duplicate ID").build()));
        for (List<Ride> invalidRides : invalidLists) {
            assertThrows(IllegalArgumentException.class, () -> addressBook.setRides(invalidRides, 100));
            assertEquals(List.of(original), addressBook.getRideList());
            assertEquals(2, addressBook.getNextRideId());
        }
        assertThrows(NullPointerException.class, () ->
                addressBook.setRides(java.util.Arrays.asList(original, null), 100));
        assertEquals(List.of(original), addressBook.getRideList());
        assertEquals(2, addressBook.getNextRideId());
    }

    @Test
    public void addRide_lastAvailableId_thenRejectsWithoutMutation() {
        // Boundary values: last assignable ID Integer.MAX_VALUE - 1, then exhausted counter Integer.MAX_VALUE.
        addressBook.setRides(List.of(), Integer.MAX_VALUE - 1);
        Ride last = addressBook.addRide(new RideBuilder().build());
        assertEquals(Integer.MAX_VALUE - 1, last.getId());
        assertEquals(Integer.MAX_VALUE, addressBook.getNextRideId());
        assertThrows(IllegalStateException.class, AddressBook.MESSAGE_RIDE_IDS_EXHAUSTED, () ->
                addressBook.addRide(new RideBuilder().build()));
        assertEquals(List.of(last), addressBook.getRideList());
        assertEquals(Integer.MAX_VALUE, addressBook.getNextRideId());
    }

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

    @Test
    public void setRide_validReplacement_preservesPositionOtherRidesAndNextId() {
        Ride first = addressBook.addRide(new RideBuilder().withName("First Ride").build());
        Ride target = addressBook.addRide(new RideBuilder().withName("Target Ride").build());
        Ride third = addressBook.addRide(new RideBuilder().withName("Third Ride").build());
        Ride replacement = new RideBuilder(target).withName("Edited Ride").build();
        ObservableList<Ride> observedRides = addressBook.getRideList();

        addressBook.setRide(target, replacement);

        assertSame(observedRides, addressBook.getRideList());
        assertEquals(List.of(first, replacement, third), observedRides);
        assertEquals(4, addressBook.getNextRideId());
    }

    @Test
    public void setRide_nullArguments_throwsNullPointerException() {
        Ride target = addressBook.addRide(new RideBuilder().build());

        assertThrows(NullPointerException.class, () -> addressBook.setRide(null, target));
        assertThrows(NullPointerException.class, () -> addressBook.setRide(target, null));
        assertEquals(List.of(target), addressBook.getRideList());
    }

    @Test
    public void setRide_differentId_throwsIllegalArgumentExceptionAndLeavesDataUnchanged() {
        Ride target = addressBook.addRide(new RideBuilder().build());
        Ride replacement = new RideBuilder(target).withId(2).withName("Edited Ride").build();
        AddressBook original = new AddressBook(addressBook);

        assertThrows(IllegalArgumentException.class, () -> addressBook.setRide(target, replacement));
        assertEquals(original, addressBook);
    }

    @Test
    public void setRide_missingOrStaleTarget_throwsIllegalArgumentExceptionAndLeavesDataUnchanged() {
        Ride storedRide = addressBook.addRide(new RideBuilder().build());
        Ride missingRide = new RideBuilder(storedRide).withName("Missing Ride").build();
        Ride replacement = new RideBuilder(storedRide).withName("Edited Ride").build();
        AddressBook original = new AddressBook(addressBook);

        assertThrows(IllegalArgumentException.class, () -> addressBook.setRide(missingRide, replacement));
        assertEquals(original, addressBook);

        addressBook.setRide(storedRide, replacement);
        AddressBook afterReplacement = new AddressBook(addressBook);
        Ride secondReplacement = new RideBuilder(storedRide).withName("Another Edit").build();
        assertThrows(IllegalArgumentException.class, () -> addressBook.setRide(storedRide, secondReplacement));
        assertEquals(afterReplacement, addressBook);
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
