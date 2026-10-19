package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.RideBuilder;

public class RideListCommandTest {
    private Model model;
    private AddressBook originalAddressBook;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.setPersons(List.of(CARL, ALICE, BENSON));
        addressBook.setRides(List.of(
                new RideBuilder().withId(105).withName("Empty Ride").build(),
                new RideBuilder().withId(42)
                        .withRoster(CARL.getPhone().value, ALICE.getPhone().value).build(),
                new RideBuilder().withId(900).withName("Other Ride")
                        .withRoster(BENSON.getPhone().value).build()), 901);
        model = new ModelManager(addressBook, new UserPrefs());
        originalAddressBook = new AddressBook(model.getAddressBook());
    }

    @Test
    public void execute_persistentRideId_listsSortedRosterWithoutMutation() throws Exception {
        CommandResult result = new RideListCommand(42).execute(model);
        assertEquals(String.format(RideListCommand.MESSAGE_SUCCESS, 42), result.getFeedbackToUser());
        assertEquals(List.of(ALICE, CARL), model.getFilteredPersonList());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_emptyRoster_showsEmptyListWithoutMutation() throws Exception {
        CommandResult result = new RideListCommand(105).execute(model);
        assertEquals(String.format(RideListCommand.MESSAGE_EMPTY_ROSTER, 105), result.getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(originalAddressBook, model.getAddressBook());
    }

    @Test
    public void execute_unknownRide_preservesExistingFilterAndData() {
        model.updateFilteredPersonList(person -> person.equals(BENSON));

        assertCommandFailure(new RideListCommand(999), model,
                String.format(RideListCommand.MESSAGE_RIDE_NOT_FOUND, 999));

        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_unresolvedRosterPhone_preservesExistingFilterAndData() {
        AddressBook addressBook = new AddressBook(originalAddressBook);
        addressBook.setRides(List.of(new RideBuilder().withId(77).withRoster("+65 99999999").build()), 901);
        Model invalidModel = new ModelManager(addressBook, new UserPrefs());
        invalidModel.updateFilteredPersonList(person -> person.equals(BENSON));

        assertCommandFailure(new RideListCommand(77), invalidModel,
                RideListCommand.MESSAGE_ROSTER_MEMBER_NOT_FOUND);

        assertEquals(List.of(BENSON), invalidModel.getFilteredPersonList());
    }

    @Test
    public void constructor_nonPositiveId_throwsIllegalArgumentException() {
        for (int invalidId : List.of(0, -1)) {
            assertThrows(IllegalArgumentException.class, () -> new RideListCommand(invalidId));
        }
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RideListCommand(1).execute(null));
    }

    @Test
    public void equals_sameAndDifferentIds_returnsExpectedResult() {
        RideListCommand command = new RideListCommand(42);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new RideListCommand(42)));
        assertFalse(command.equals(new RideListCommand(105)));
        assertFalse(command.equals(null));
    }
}
