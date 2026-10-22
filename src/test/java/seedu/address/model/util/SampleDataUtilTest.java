package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class SampleDataUtilTest {

    @Test
    public void getSampleAddressBook_validPhoneNumbers_allSamplePersonsIncluded() {
        Person[] samplePersons = assertDoesNotThrow(SampleDataUtil::getSamplePersons);

        assertEquals(3, samplePersons.length);
        for (Person samplePerson : samplePersons) {
            assertTrue(Phone.isValidPhone(samplePerson.getPhone().value));
            assertTrue(samplePerson.getEmergencyPhone().isPresent());
            assertTrue(Phone.isValidPhone(samplePerson.getEmergencyPhone().orElseThrow().value));
        }

        ReadOnlyAddressBook sampleAddressBook = assertDoesNotThrow(SampleDataUtil::getSampleAddressBook);
        assertEquals(samplePersons.length, sampleAddressBook.getPersonList().size());
    }

    @Test
    public void getSampleAddressBook_userGuideSetup_matchesCommandResults() throws Exception {
        Model expected = new ModelManager();
        AddressBookParser parser = new AddressBookParser();
        String[] commands = {
            "add n/Alex Tan p/+65 91234561 sos/+65 90000001 e/alex@example.com "
                    + "f/280 x/Advanced l/East Coast Park l/Marina Bay",
            "add n/Bernice Lee p/+65 91234562 sos/+65 90000002 e/bernice@example.com "
                    + "f/220 x/Intermediate l/Punggol",
            "add n/Chloe Lim p/+65 91234563 sos/+65 90000003 e/chloe@example.com "
                    + "f/180 x/Beginner l/any",
            "rideadd n/Sunday Sunrise Ride from/25-10-2026 0630 to/25-10-2026 0930 "
                    + "l/East Coast Park d/45 f/180 s/3 notes/Bring lights and a spare tube",
            "rideadd n/Evening Spin from/27-10-2026 1800 to/27-10-2026 1930 "
                    + "l/Punggol d/30.5 f/150 s/8"
        };
        for (String command : commands) {
            parser.parseCommand(command).execute(expected);
        }

        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();
        assertEquals(expected.getAddressBook(), sampleAddressBook);
        assertEquals(3, sampleAddressBook.getNextRideId());
        assertTrue(sampleAddressBook.getRideList().stream().allMatch(ride -> ride.getRoster().isEmpty()));
    }

}
