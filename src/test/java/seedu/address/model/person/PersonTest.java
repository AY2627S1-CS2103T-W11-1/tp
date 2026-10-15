package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EXPERIENCE_LEVEL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void emergencyPhone_changesDataButNotIdentity() {
        Person updated = new PersonBuilder(ALICE).withEmergencyPhone("+65 99999999").build();
        assertTrue(ALICE.getEmergencyPhone().isEmpty());
        assertEquals(new Phone("+65 99999999"), updated.getEmergencyPhone().orElseThrow());
        assertFalse(ALICE.equals(updated));
        assertTrue(ALICE.isSamePerson(updated));
        Person copy = new PersonBuilder(updated).build();
        assertEquals(updated, copy);
        assertEquals(updated.hashCode(), copy.hashCode());
    }

    @Test
    public void getLocations_modifySet_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getLocations().clear());
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // same phone and email -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.isSamePerson(aliceCopy));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same phone, different name and email -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withEmail(VALID_EMAIL_BOB)
                .build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same email, different name and phone -> returns true
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same name, different phone and email -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // different name, phone and email -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // email differs only in case -> returns true
        String uppercaseEmail = ALICE.getEmail().value.toUpperCase(Locale.ROOT);
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withEmail(uppercaseEmail).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // email has mixed case -> returns true
        String mixedCaseEmail = ALICE.getEmail().value.substring(0, 1).toUpperCase(Locale.ROOT)
                + ALICE.getEmail().value.substring(1);
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB)
                .withEmail(mixedCaseEmail).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // non-identity fields differ -> returns true
        editedAlice = new PersonBuilder(ALICE).withFtp("300").withExperienceLevel(VALID_EXPERIENCE_LEVEL_BOB)
                .withLocations("East Coast Park")
                .build();
        assertTrue(ALICE.isSamePerson(editedAlice));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // email differs only in case -> returns false
        editedAlice = new PersonBuilder(ALICE)
                .withEmail(ALICE.getEmail().value.toUpperCase(Locale.ROOT)).build();
        assertFalse(ALICE.equals(editedAlice));

        // different FTP -> returns false
        editedAlice = new PersonBuilder(ALICE).withFtp("300").build();
        assertFalse(ALICE.equals(editedAlice));

        // different experience level -> returns false
        editedAlice = new PersonBuilder(ALICE).withExperienceLevel(VALID_EXPERIENCE_LEVEL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different preferred locations -> returns false
        editedAlice = new PersonBuilder(ALICE).withLocations("East Coast Park").build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", emergencyPhone=" + ALICE.getEmergencyPhone().orElse(null)
                + ", email=" + ALICE.getEmail() + ", ftp=" + ALICE.getFtp()
                + ", experienceLevel=" + ALICE.getExperienceLevel() + ", locations=" + ALICE.getLocations()
                + "}";
        assertEquals(expected, ALICE.toString());
    }
}
