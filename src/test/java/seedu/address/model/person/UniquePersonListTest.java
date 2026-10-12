package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.PersonBuilder;

public class UniquePersonListTest {

    private final UniquePersonList uniquePersonList = new UniquePersonList();

    @Test
    public void contains_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.contains(null));
    }

    @Test
    public void contains_personNotInList_returnsFalse() {
        assertFalse(uniquePersonList.contains(ALICE));
    }

    @Test
    public void contains_personInList_returnsTrue() {
        uniquePersonList.add(ALICE);
        assertTrue(uniquePersonList.contains(ALICE));
    }

    @Test
    public void contains_personWithSameIdentityFieldsInList_returnsTrue() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(uniquePersonList.contains(editedAlice));
    }

    @Test
    public void contains_identityMatchesByPhoneOrEmail_returnsTrue() {
        uniquePersonList.add(ALICE);
        Person samePhone = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        Person sameEmail = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        String mixedCaseEmail = ALICE.getEmail().value.substring(0, 1).toUpperCase(Locale.ROOT)
                + ALICE.getEmail().value.substring(1);
        Person sameEmailIgnoringCase = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withEmail(mixedCaseEmail).build();

        assertTrue(uniquePersonList.contains(samePhone));
        assertTrue(uniquePersonList.contains(sameEmail));
        assertTrue(uniquePersonList.contains(sameEmailIgnoringCase));
    }

    @Test
    public void add_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.add(null));
    }

    @Test
    public void add_duplicatePerson_throwsDuplicatePersonException() {
        uniquePersonList.add(ALICE);
        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.add(ALICE));
    }

    @Test
    public void add_samePhoneDifferentEmail_throwsDuplicatePersonException() {
        Person samePhone = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();

        assertAddingDuplicateThrows(samePhone);
    }

    @Test
    public void add_differentPhoneSameEmail_throwsDuplicatePersonException() {
        Person sameEmail = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();

        assertAddingDuplicateThrows(sameEmail);
    }

    @Test
    public void add_emailDiffersOnlyInCase_throwsDuplicatePersonException() {
        Person sameEmailIgnoringCase = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withEmail(ALICE.getEmail().value.toUpperCase(Locale.ROOT)).build();

        assertAddingDuplicateThrows(sameEmailIgnoringCase);
    }

    @Test
    public void add_sameNameWithDifferentPhoneAndEmail_success() {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).build();

        uniquePersonList.add(ALICE);
        uniquePersonList.add(personWithSameName);

        assertEquals(List.of(ALICE, personWithSameName), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void setPerson_nullTargetPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPerson(null, ALICE));
    }

    @Test
    public void setPerson_nullEditedPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPerson(ALICE, null));
    }

    @Test
    public void setPerson_targetPersonNotInList_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> uniquePersonList.setPerson(ALICE, ALICE));
    }

    @Test
    public void setPerson_editedPersonIsSamePerson_success() {
        uniquePersonList.add(ALICE);
        uniquePersonList.setPerson(ALICE, ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(ALICE);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonHasSameIdentity_success() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        uniquePersonList.setPerson(ALICE, editedAlice);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(editedAlice);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPerson_editedPersonHasDifferentIdentity_success() {
        uniquePersonList.add(ALICE);
        uniquePersonList.setPerson(ALICE, BOB);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(BOB);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPerson_phoneChangedToUniqueValue_success() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();

        uniquePersonList.setPerson(ALICE, editedAlice);

        assertEquals(List.of(editedAlice), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void setPerson_emailChangedToUniqueValue_success() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();

        uniquePersonList.setPerson(ALICE, editedAlice);

        assertEquals(List.of(editedAlice), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void setPerson_ownEmailCaseChanged_success() {
        uniquePersonList.add(ALICE);
        Person editedAlice = new PersonBuilder(ALICE)
                .withEmail(ALICE.getEmail().value.toUpperCase(Locale.ROOT)).build();

        uniquePersonList.setPerson(ALICE, editedAlice);

        assertEquals(List.of(editedAlice), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void setPerson_editedPersonHasNonUniqueIdentity_throwsDuplicatePersonException() {
        uniquePersonList.add(ALICE);
        uniquePersonList.add(BOB);
        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.setPerson(ALICE, BOB));
    }

    @Test
    public void setPerson_editedPersonRetainsEmailAndDuplicatesPhone_throwsDuplicatePersonException() {
        uniquePersonList.add(ALICE);
        uniquePersonList.add(BOB);
        Person editedAlice = new PersonBuilder(ALICE).withPhone(BOB.getPhone().value).build();

        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.setPerson(ALICE, editedAlice));
        assertEquals(List.of(ALICE, BOB), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void setPerson_editedPersonDuplicatesEmailIgnoringCase_throwsDuplicatePersonException() {
        uniquePersonList.add(ALICE);
        uniquePersonList.add(BOB);
        Person editedAlice = new PersonBuilder(ALICE)
                .withEmail(BOB.getEmail().value.toUpperCase(Locale.ROOT)).build();

        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.setPerson(ALICE, editedAlice));
        assertEquals(List.of(ALICE, BOB), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void remove_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.remove(null));
    }

    @Test
    public void remove_personDoesNotExist_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> uniquePersonList.remove(ALICE));
    }

    @Test
    public void remove_existingPerson_removesPerson() {
        uniquePersonList.add(ALICE);
        uniquePersonList.remove(ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void remove_sameNameCyclists_removesOnlyFullyEqualPerson() {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).build();
        uniquePersonList.add(ALICE);
        uniquePersonList.add(personWithSameName);

        uniquePersonList.remove(ALICE);

        assertEquals(List.of(personWithSameName), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void setPersons_nullUniquePersonList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPersons((UniquePersonList) null));
    }

    @Test
    public void setPersons_uniquePersonList_replacesOwnListWithProvidedUniquePersonList() {
        uniquePersonList.add(ALICE);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(BOB);
        uniquePersonList.setPersons(expectedUniquePersonList);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPersons_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniquePersonList.setPersons((List<Person>) null));
    }

    @Test
    public void setPersons_list_replacesOwnListWithProvidedList() {
        uniquePersonList.add(ALICE);
        List<Person> personList = List.of(BOB);
        uniquePersonList.setPersons(personList);
        UniquePersonList expectedUniquePersonList = new UniquePersonList();
        expectedUniquePersonList.add(BOB);
        assertEquals(expectedUniquePersonList, uniquePersonList);
    }

    @Test
    public void setPersons_listWithDuplicatePersons_throwsDuplicatePersonException() {
        List<Person> listWithDuplicatePersons = List.of(ALICE, ALICE);
        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.setPersons(listWithDuplicatePersons));
    }

    @Test
    public void setPersons_listWithDuplicatePhones_throwsDuplicatePersonExceptionAndPreservesList() {
        Person samePhone = new PersonBuilder(BOB).withPhone(ALICE.getPhone().value).build();

        assertSetPersonsRejectsAndPreservesOriginal(List.of(ALICE, samePhone));
    }

    @Test
    public void setPersons_listWithDuplicateEmails_throwsDuplicatePersonExceptionAndPreservesList() {
        Person sameEmail = new PersonBuilder(BOB).withEmail(ALICE.getEmail().value).build();

        assertSetPersonsRejectsAndPreservesOriginal(List.of(ALICE, sameEmail));
    }

    @Test
    public void setPersons_listWithEmailsDifferingOnlyInCase_throwsDuplicatePersonExceptionAndPreservesList() {
        Person sameEmailIgnoringCase = new PersonBuilder(BOB)
                .withEmail(ALICE.getEmail().value.toUpperCase(Locale.ROOT)).build();

        assertSetPersonsRejectsAndPreservesOriginal(List.of(ALICE, sameEmailIgnoringCase));
    }

    @Test
    public void setPersons_sameNameWithDifferentPhoneAndEmail_replacesListWithoutDiscardingRecords() {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).build();

        uniquePersonList.setPersons(List.of(ALICE, personWithSameName));

        assertEquals(List.of(ALICE, personWithSameName), uniquePersonList.asUnmodifiableObservableList());
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniquePersonList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniquePersonList.asUnmodifiableObservableList().toString(), uniquePersonList.toString());
    }

    private void assertAddingDuplicateThrows(Person duplicatePerson) {
        uniquePersonList.add(ALICE);
        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.add(duplicatePerson));
        assertEquals(List.of(ALICE), uniquePersonList.asUnmodifiableObservableList());
    }

    private void assertSetPersonsRejectsAndPreservesOriginal(List<Person> replacement) {
        uniquePersonList.add(BOB);

        assertThrows(DuplicatePersonException.class, () -> uniquePersonList.setPersons(replacement));
        assertEquals(List.of(BOB), uniquePersonList.asUnmodifiableObservableList());
    }
}
