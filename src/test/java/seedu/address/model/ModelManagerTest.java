package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void getFilteredPersonList_initialData_sortsByFullNameIgnoringCase() {
        Person alexZ = new PersonBuilder().withName("Alex Zane").build();
        Person alexA = new PersonBuilder().withName("alex Adams").withPhone("+65 91111111")
                .withEmail("alexa@example.com").build();
        Person david = new PersonBuilder().withName("David").withPhone("+65 92222222")
                .withEmail("david@example.com").build();
        AddressBook book = new AddressBookBuilder().withPerson(david).withPerson(alexZ).withPerson(alexA).build();
        modelManager = new ModelManager(book, new UserPrefs());

        assertEquals(List.of(alexA, alexZ, david), modelManager.getFilteredPersonList());
        assertEquals(List.of(david, alexZ, alexA), modelManager.getAddressBook().getPersonList());
    }

    @Test
    public void addPerson_unsortedAdditions_updatesExistingDisplayedView() {
        var displayedPersons = modelManager.getFilteredPersonList();
        Person alex = new PersonBuilder().withName("Alex").build();
        Person bernice = new PersonBuilder().withName("Bernice").withPhone("+65 91111111")
                .withEmail("bernice@example.com").build();
        Person david = new PersonBuilder().withName("David").withPhone("+65 92222222")
                .withEmail("david@example.com").build();
        modelManager.addPerson(david);
        modelManager.addPerson(alex);
        modelManager.updateFilteredPersonList(person -> false);
        modelManager.addPerson(bernice);

        assertEquals(List.of(alex, bernice, david), displayedPersons);
    }

    @Test
    public void updateFilteredPersonList_matchingPersons_remainsSortedAndResets() {
        modelManager.addPerson(BENSON);
        modelManager.addPerson(ALICE);
        Person zara = new PersonBuilder().withName("Zara").build();
        modelManager.addPerson(zara);
        modelManager.updateFilteredPersonList(person -> !person.equals(zara));
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPersonList());

        modelManager.updateFilteredPersonList(person -> false);
        assertTrue(modelManager.getFilteredPersonList().isEmpty());
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(List.of(ALICE, BENSON, zara), modelManager.getFilteredPersonList());
    }

    @Test
    public void setPerson_nameChanges_movesInBothDirections() {
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        Person zara = new PersonBuilder(ALICE).withName("Zara").build();
        modelManager.setPerson(ALICE, zara);
        assertEquals(List.of(BENSON, zara), modelManager.getFilteredPersonList());

        Person aaron = new PersonBuilder(zara).withName("Aaron").build();
        modelManager.setPerson(zara, aaron);
        assertEquals(List.of(aaron, BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void getFilteredPersonList_equalNames_preservesStoredOrderAcrossChanges() {
        Person first = new PersonBuilder().withName("aLEX").build();
        Person second = new PersonBuilder().withName("Alex").withPhone("+65 91111111")
                .withEmail("second@example.com").build();
        Person third = new PersonBuilder().withName("ALEX").withPhone("+65 92222222")
                .withEmail("third@example.com").build();
        modelManager = new ModelManager(new AddressBookBuilder().withPerson(first).withPerson(second).build(),
                new UserPrefs());
        modelManager.addPerson(third);
        assertEquals(List.of(first, second, third), modelManager.getFilteredPersonList());

        modelManager.updateFilteredPersonList(person -> !person.equals(second));
        assertEquals(List.of(first, third), modelManager.getFilteredPersonList());
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        Person editedFirst = new PersonBuilder(first).withName("alex").build();
        modelManager.setPerson(first, editedFirst);
        Person editedSecond = new PersonBuilder(second).withEmail("updated@example.com").build();
        modelManager.setPerson(second, editedSecond);
        assertEquals(List.of(editedFirst, editedSecond, third), modelManager.getFilteredPersonList());

        modelManager.deletePerson(editedFirst);
        assertEquals(List.of(editedSecond, third), modelManager.getFilteredPersonList());
    }

    @Test
    public void setPerson_filteredList_updatesOrderingAndMembership() {
        modelManager.addPerson(BENSON);
        modelManager.addPerson(ALICE);
        modelManager.updateFilteredPersonList(person -> person.getName().fullName.startsWith("A"));
        Person aaron = new PersonBuilder(BENSON).withName("Aaron").build();
        modelManager.setPerson(BENSON, aaron);
        assertEquals(List.of(aaron, ALICE), modelManager.getFilteredPersonList());

        Person zara = new PersonBuilder(aaron).withName("Zara").build();
        modelManager.setPerson(aaron, zara);
        assertEquals(List.of(ALICE), modelManager.getFilteredPersonList());
    }

    @Test
    public void setPerson_identicalNames_preservesStoredOrderWhenJoiningAndLeavingTie() {
        Person first = new PersonBuilder().withName("Zara").build();
        Person second = new PersonBuilder().withName("Alex").withPhone("+65 91111111")
                .withEmail("second@example.com").build();
        Person third = new PersonBuilder().withName("Alex").withPhone("+65 92222222")
                .withEmail("third@example.com").build();
        modelManager.addPerson(first);
        modelManager.addPerson(second);
        modelManager.addPerson(third);
        assertEquals(List.of(second, third, first), modelManager.getFilteredPersonList());

        Person editedFirst = new PersonBuilder(first).withName("Alex").build();
        modelManager.setPerson(first, editedFirst);
        assertEquals(List.of(editedFirst, second, third), modelManager.getFilteredPersonList());
        modelManager.updateFilteredPersonList(person -> !person.equals(second));
        assertEquals(List.of(editedFirst, third), modelManager.getFilteredPersonList());
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        modelManager.setPerson(editedFirst, first);
        assertEquals(List.of(second, third, first), modelManager.getFilteredPersonList());
    }

    @Test
    public void setAddressBook_replacement_updatesExistingSortedView() {
        var displayedPersons = modelManager.getFilteredPersonList();
        modelManager.addPerson(new PersonBuilder().withName("Zara").build());
        modelManager.setAddressBook(new AddressBookBuilder().withPerson(BENSON).withPerson(ALICE).build());
        assertEquals(List.of(ALICE, BENSON), displayedPersons);

        modelManager.setAddressBook(new AddressBook());
        assertTrue(displayedPersons.isEmpty());
    }

    @Test
    public void setAddressBook_activeRideListFilter_cancelsSelectionAndKeepsListenerAttached() {
        AddressBook initialBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        Ride selectedRide = new RideBuilder().withId(5).withRoster(ALICE.getPhone().value).build();
        initialBook.setRides(List.of(selectedRide), 6);
        modelManager = new ModelManager(initialBook, new UserPrefs());
        List<Ride> observedRides = modelManager.getAddressBook().getRideList();
        modelManager.updateFilteredPersonListForRide(5);
        modelManager.deleteRide(selectedRide);
        assertTrue(modelManager.getFilteredPersonList().isEmpty());

        AddressBook replacementBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        Ride replacementRide = new RideBuilder().withId(5).withRoster(ALICE.getPhone().value).build();
        replacementBook.setRides(List.of(replacementRide), 6);
        modelManager.setAddressBook(replacementBook);

        assertSame(observedRides, modelManager.getAddressBook().getRideList());
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPersonList());

        modelManager.updateFilteredPersonListForRide(5);
        Ride updatedRide = replacementRide.withRoster(List.of(BENSON.getPhone()));
        modelManager.setRide(replacementRide, updatedRide);
        assertEquals(List.of(BENSON), modelManager.getFilteredPersonList());
    }

    @Test
    public void addRide_assignsIdAndAddsToAddressBook() {
        Ride added = modelManager.addRide(new RideBuilder().build());

        assertEquals(1, added.getId());
        assertEquals(List.of(added), modelManager.getAddressBook().getRideList());
    }

    @Test
    public void addRide_activeRideFilter_showsAllRidesAfterAddition() {
        Ride firstRide = modelManager.addRide(new RideBuilder().withName("Morning Ride").build());
        modelManager.updateFilteredRideList(ride -> ride.equals(firstRide));

        Ride secondRide = modelManager.addRide(new RideBuilder().withName("Evening Ride").build());

        // EP: a new ride is added while a ride filter is active.
        assertEquals(List.of(firstRide, secondRide), modelManager.getFilteredRideList());
    }

    @Test
    public void addRide_secondRide_getsNextId() {
        modelManager.addRide(new RideBuilder().build());

        Ride second = modelManager.addRide(new RideBuilder().withName("Evening Spin").build());

        assertEquals(2, second.getId());
    }

    @Test
    public void addRide_nullRide_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.addRide(null));
    }

    @Test
    public void setRide_validReplacement_updatesExistingObservableList() {
        Ride target = modelManager.addRide(new RideBuilder().build());
        Ride replacement = new RideBuilder(target).withName("Edited Ride").build();
        List<Ride> observedRides = modelManager.getAddressBook().getRideList();

        modelManager.setRide(target, replacement);

        assertEquals(List.of(replacement), observedRides);
        assertEquals(2, modelManager.getAddressBook().getNextRideId());
    }

    @Test
    public void setRide_activeRideFilter_preservesFilterWhenReplacementIsHidden() {
        Ride original = modelManager.addRide(new RideBuilder().withName("Morning Ride").build());
        modelManager.updateFilteredRideList(ride -> ride.equals(original));
        Ride edited = new RideBuilder(original).withName("Renamed Ride").build();

        modelManager.setRide(original, edited);

        // EP: a replacement no longer matches the active ride filter.
        assertTrue(modelManager.getFilteredRideList().isEmpty());
    }

    @Test
    public void setAddressBook_activeRideFilter_clearsRideFilter() {
        Ride ride = modelManager.addRide(new RideBuilder().withName("Morning Ride").build());
        modelManager.updateFilteredRideList(candidate -> candidate.equals(ride));
        Ride replacementRide = new RideBuilder().withName("Evening Ride").build();
        AddressBook replacementBook = new AddressBook();
        Ride addedReplacement = replacementBook.addRide(replacementRide);

        modelManager.setAddressBook(replacementBook);

        // EP: the address book is replaced while a ride filter is active.
        assertEquals(List.of(addedReplacement), modelManager.getFilteredRideList());
    }

    @Test
    public void setRide_nullArguments_throwsNullPointerException() {
        Ride target = modelManager.addRide(new RideBuilder().build());

        assertThrows(NullPointerException.class, () -> modelManager.setRide(null, target));
        assertThrows(NullPointerException.class, () -> modelManager.setRide(target, null));
        assertEquals(List.of(target), modelManager.getAddressBook().getRideList());
    }

    @Test
    public void equals_differentRides_returnsFalse() {
        ModelManager other = new ModelManager();
        other.addRide(new RideBuilder().build());

        assertFalse(modelManager.equals(other));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
