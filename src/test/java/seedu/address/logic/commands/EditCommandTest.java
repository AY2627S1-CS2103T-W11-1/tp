package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Location;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(personToEdit.getPhone(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(lastPerson.getPhone(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_cyclingFieldsSpecifiedUnfilteredList_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withFtp("300")
                .withExperienceLevel("Advanced").withLocations("East Coast Park", "Marina Bay").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withFtp("300")
                .withExperienceLevel("Advanced").withLocations("East Coast Park", "Marina Bay").build();
        EditCommand editCommand = new EditCommand(personToEdit.getPhone(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_onlyFtpSpecified_success() {
        Person personToEdit = addLocationRider("West Coast Park", "Marina Bay");
        Person editedPerson = new PersonBuilder(personToEdit).withFtp("300").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withFtp("300").build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
    }

    @Test
    public void execute_onlyExperienceLevelSpecified_success() {
        Person personToEdit = addLocationRider("West Coast Park", "Marina Bay");
        Person editedPerson = new PersonBuilder(personToEdit).withExperienceLevel("Advanced").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withExperienceLevel("Advanced").build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
    }

    @Test
    public void execute_replaceMultipleLocationsWithOne_success() {
        Person personToEdit = addLocationRider("West Coast Park", "Marina Bay");
        Person editedPerson = new PersonBuilder(personToEdit).withLocations("East Coast Park").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withLocations("East Coast Park").build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
        assertEquals(Set.of(new Location("East Coast Park")), editedPersonInModel(personToEdit).getLocations());
        assertFalse(editedPersonInModel(personToEdit).getLocations().contains(new Location("West Coast Park")));
        assertFalse(editedPersonInModel(personToEdit).getLocations().contains(new Location("Marina Bay")));
    }

    @Test
    public void execute_replaceOneLocationWithMultiple_success() {
        Person personToEdit = addLocationRider("West Coast Park");
        Person editedPerson = new PersonBuilder(personToEdit)
                .withLocations("East Coast Park", "Marina Bay").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withLocations("East Coast Park", "Marina Bay").build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
        assertEquals(Set.of(new Location("East Coast Park"), new Location("Marina Bay")),
                editedPersonInModel(personToEdit).getLocations());
        assertFalse(editedPersonInModel(personToEdit).getLocations().contains(new Location("West Coast Park")));
    }

    @Test
    public void execute_replaceSpecificLocationsWithAny_success() {
        Person personToEdit = addLocationRider("West Coast Park", "Marina Bay");
        Person editedPerson = new PersonBuilder(personToEdit).withLocations("any").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withLocations("any").build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
        assertEquals(Set.of(new Location("any")), editedPersonInModel(personToEdit).getLocations());
        assertFalse(editedPersonInModel(personToEdit).getLocations().contains(new Location("West Coast Park")));
    }

    @Test
    public void execute_replaceAnyWithSpecificLocations_success() {
        Person personToEdit = addLocationRider("any");
        Person editedPerson = new PersonBuilder(personToEdit)
                .withLocations("East Coast Park", "Marina Bay").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withLocations("East Coast Park", "Marina Bay").build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
        assertEquals(Set.of(new Location("East Coast Park"), new Location("Marina Bay")),
                editedPersonInModel(personToEdit).getLocations());
        assertFalse(editedPersonInModel(personToEdit).getLocations().contains(new Location("any")));
    }

    @Test
    public void execute_existingFieldsSpecified_preservesCyclingFields() {
        Person personToEdit = addLocationRider("West Coast Park", "Marina Bay");
        Person editedPerson = new PersonBuilder(personToEdit).withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        assertEditSuccess(personToEdit, descriptor, editedPerson);
    }

    @Test
    public void execute_fieldSetToExistingValue_success() {
        Person personToEdit = addLocationRider("West Coast Park");
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withFtp(personToEdit.getFtp().value).build();

        assertEditSuccess(personToEdit, descriptor, personToEdit);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(editedPerson.getPhone(), new EditPersonDescriptor());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(personInFilteredList.getPhone(),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(secondPerson.getPhone(), descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(personInFilteredList.getPhone(),
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_phoneNotFoundUnfilteredList_failure() {
        Phone unknownPhone = new Phone("+65 00000000");
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(unknownPhone, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_partialPhoneNumber_failure() {
        Phone partialPhone = new Phone("+65 943");
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(partialPhone, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    /**
     * Attempts to edit a person who exists in the address book but is absent from the filtered list.
     */
    @Test
    public void execute_phoneNotFoundFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person personOutsideFilteredList = model.getAddressBook().getPersonList()
                .get(INDEX_SECOND_PERSON.getZeroBased());
        assertFalse(model.getFilteredPersonList().contains(personOutsideFilteredList));

        EditCommand editCommand = new EditCommand(personOutsideFilteredList.getPhone(),
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void equals() {
        Phone firstPhone = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getPhone();
        Phone secondPhone = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()).getPhone();
        final EditCommand standardCommand = new EditCommand(firstPhone, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(firstPhone, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different target phone -> returns false
        assertFalse(standardCommand.equals(new EditCommand(secondPhone, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(firstPhone, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Phone targetPhone = new Phone("+65 91234567");
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(targetPhone, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{targetPhone=" + targetPhone
                + ", editPersonDescriptor=" + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    private Person addLocationRider(String... locations) {
        Person person = new PersonBuilder().withName("Location Rider").withPhone("+65 80000000")
                .withEmail("location@example.com").withFtp("250").withExperienceLevel("Intermediate")
                .withLocations(locations).withTags("weekend").build();
        model.addPerson(person);
        return person;
    }

    private void assertEditSuccess(Person personToEdit, EditPersonDescriptor descriptor, Person editedPerson) {
        EditCommand editCommand = new EditCommand(personToEdit.getPhone(), descriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    private Person editedPersonInModel(Person originalPerson) {
        return model.getFilteredPersonList().stream()
                .filter(person -> person.getPhone().equals(originalPerson.getPhone()))
                .findFirst()
                .orElseThrow();
    }

}
