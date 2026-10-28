package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class DeleteCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void getPersonToDelete_validPhone_resolvesWithoutChangingModelOrFilter() throws Exception {
        model.updateFilteredPersonList(person -> person.equals(ALICE));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(ALICE));
        assertEquals(ALICE, new DeleteCommand(ALICE.getPhone()).getPersonToDelete(model));
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_validPhone_success() {
        Person personToDelete = model.getAddressBook().getPersonList().get(0);
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getPhone());

        // EP: deleting an existing phone succeeds and removes the matching person.
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownPhone_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(new Phone("+65 99999999"));

        // EP: deleting an unknown phone is rejected.
        assertCommandFailure(deleteCommand, model,
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, "+65 99999999"));
    }

    @Test
    public void execute_hiddenPhone_rejectsWithoutChangingDisplayedList() {
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        assertCommandFailure(new DeleteCommand(ALICE.getPhone()), model,
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, ALICE.getPhone()));
    }

    @Test
    public void execute_emptyDisplayedList_rejectsExistingPhone() {
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new DeleteCommand(ALICE.getPhone()), model,
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, ALICE.getPhone()));
    }

    @Test
    public void execute_filteredSameNameCyclists_deletesOnlyDisplayedPhoneMatch() {
        Person otherAlice = new PersonBuilder(ALICE).withPhone("+65 80000000")
                .withEmail("other.alice@example.com").build();
        model.addPerson(otherAlice);
        model.updateFilteredPersonList(person -> person.getPhone().equals(otherAlice.getPhone()));
        assertCommandFailure(new DeleteCommand(ALICE.getPhone()), model,
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, ALICE.getPhone()));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.getPhone().equals(otherAlice.getPhone()));
        expectedModel.deletePerson(otherAlice);
        assertCommandSuccess(new DeleteCommand(otherAlice.getPhone()), model,
                String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(otherAlice)), expectedModel);
        assertTrue(model.getAddressBook().getPersonList().contains(ALICE));
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_sameNameCyclists_deletesOnlyExactPhoneMatch() {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone("+65 80000000")
                .withEmail("other.alice@example.com").build();
        model.addPerson(personWithSameName);
        DeleteCommand deleteCommand = new DeleteCommand(ALICE.getPhone());
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(ALICE));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
        assertFalse(model.getAddressBook().getPersonList().contains(ALICE));
        assertTrue(model.getAddressBook().getPersonList().contains(personWithSameName));
    }

    @Test
    public void equals() {
        DeleteCommand firstCommand = new DeleteCommand(new Phone("+65 111"));
        DeleteCommand sameCommand = new DeleteCommand(new Phone("+65 111"));
        DeleteCommand differentCommand = new DeleteCommand(new Phone("+65 222"));

        // EP: equality distinguishes identical and different phone targets, null, and other types.
        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(sameCommand));
        assertFalse(firstCommand.equals(differentCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        DeleteCommand deleteCommand = new DeleteCommand(new Phone("+65 111"));

        String expected = DeleteCommand.class.getCanonicalName() + "{targetPhone=+65 111}";
        // EP: toString includes the command's target phone.
        assertEquals(expected, deleteCommand.toString());
    }
}
