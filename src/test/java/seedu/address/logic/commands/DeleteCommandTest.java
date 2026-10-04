package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class DeleteCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validPhone_success() {
        Person personToDelete = model.getAddressBook().getPersonList().get(0);
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getPhone());
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownPhone_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(new Phone("99999999"));

        assertCommandFailure(deleteCommand, model,
                String.format(DeleteCommand.MESSAGE_PERSON_NOT_FOUND, "99999999"));
    }

    @Test
    public void equals() {
        DeleteCommand firstCommand = new DeleteCommand(new Phone("111"));
        DeleteCommand sameCommand = new DeleteCommand(new Phone("111"));
        DeleteCommand differentCommand = new DeleteCommand(new Phone("222"));

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(sameCommand));
        assertFalse(firstCommand.equals(differentCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        DeleteCommand deleteCommand = new DeleteCommand(new Phone("111"));

        String expected = DeleteCommand.class.getCanonicalName() + "{targetPhone=111}";
        assertEquals(expected, deleteCommand.toString());
    }
}
