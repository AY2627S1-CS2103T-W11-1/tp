package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFindCriteriaPredicate;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        PersonMatchesFindCriteriaPredicate firstPredicate =
                new PersonMatchesFindCriteriaPredicate(List.of("first"), List.of());
        PersonMatchesFindCriteriaPredicate secondPredicate =
                new PersonMatchesFindCriteriaPredicate(List.of("second"), List.of());

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        PersonMatchesFindCriteriaPredicate predicate =
                new PersonMatchesFindCriteriaPredicate(List.of("Kurz", "Elle", "Kunz"), List.of());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameAndPhone_onlyPersonsMatchingBothFound() {
        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(
                List.of("Carl", "Elle"), List.of(CARL.getPhone(), new Phone("+65 12345678")));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }

    @Test
    public void execute_sharedName_returnsAllMatchingCyclists() {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone("+65 80000000")
                .withEmail("other.alice@example.com").build();
        model.addPerson(personWithSameName);
        expectedModel.addPerson(personWithSameName);
        PersonMatchesFindCriteriaPredicate predicate =
                new PersonMatchesFindCriteriaPredicate(List.of("Alice"), List.of());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(ALICE, personWithSameName), model.getFilteredPersonList());
    }

    @Test
    public void execute_fullName_matchesExactFullName() {
        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(
                List.of(ALICE.getName().fullName), List.of());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        // EP: an exact full-name criterion returns one matching person.
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleFullNames_matchesEitherFullName() {
        PersonMatchesFindCriteriaPredicate predicate = new PersonMatchesFindCriteriaPredicate(
                List.of(ALICE.getName().fullName, CARL.getName().fullName), List.of());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        // EP: repeated full-name criteria return every matching person.
        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(ALICE, CARL), model.getFilteredPersonList());
    }

    @Test
    public void execute_exactPhone_returnsOnlyMatchingCyclist() {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone("+65 80000000")
                .withEmail("other.alice@example.com").build();
        model.addPerson(personWithSameName);
        expectedModel.addPerson(personWithSameName);
        PersonMatchesFindCriteriaPredicate predicate =
                new PersonMatchesFindCriteriaPredicate(List.of(), List.of(personWithSameName.getPhone()));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(personWithSameName), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesFindCriteriaPredicate predicate =
                new PersonMatchesFindCriteriaPredicate(List.of("keyword"), List.of());
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

}
