package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class SampleDataUtilTest {

    @Test
    public void getSampleAddressBook_validPhoneNumbers_allSamplePersonsIncluded() {
        Person[] samplePersons = assertDoesNotThrow(SampleDataUtil::getSamplePersons);

        assertEquals(6, samplePersons.length);
        for (Person samplePerson : samplePersons) {
            assertTrue(Phone.isValidPhone(samplePerson.getPhone().value));
        }

        ReadOnlyAddressBook sampleAddressBook = assertDoesNotThrow(SampleDataUtil::getSampleAddressBook);
        assertEquals(samplePersons.length, sampleAddressBook.getPersonList().size());
    }
}
