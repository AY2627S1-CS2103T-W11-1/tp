package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Checks invalid saved-data partitions through the actual storage boundary.
 */
public class RideDataIntegrityTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void read_nullJsonRoot_reportsLoadingFailure() throws Exception {
        // EP: JSON null instead of an address-book object.
        Path path = temporaryFolder.resolve("null.json");
        Files.writeString(path, "null");
        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(path).readAddressBook());
    }

    @Test
    public void read_duplicateRosterMember_reportsLoadingFailure() throws Exception {
        // EP: duplicate phone entries in a stored roster.
        Person cyclist = new PersonBuilder().withFtp("180").build();
        assertInvalidRoster(new RideBuilder().withRoster(cyclist.getPhone().value, cyclist.getPhone().value),
                List.of(cyclist));
    }

    @Test
    public void read_overCapacityRoster_reportsLoadingFailure() throws Exception {
        // Boundary value: two registered cyclists exceed capacity 1 by one.
        Person first = new PersonBuilder().withFtp("180").build();
        Person second = new PersonBuilder().withPhone("+65 98888888").withEmail("second@example.com").build();
        assertInvalidRoster(new RideBuilder().withMaxGroupSize("1")
                .withRoster(first.getPhone().value, second.getPhone().value), List.of(first, second));
    }

    @Test
    public void read_missingCyclistReference_reportsLoadingFailure() throws Exception {
        // EP: a stored roster phone has no corresponding cyclist record.
        assertInvalidRoster(new RideBuilder().withRoster("+65 99999999"), List.of());
    }

    @Test
    public void read_rosterAtCapacity_roundTrips() throws Exception {
        // Boundary value: roster size equals the stored maximum group size.
        Person cyclist = new PersonBuilder().withFtp("180").build();
        AddressBook book = new AddressBook();
        book.addPerson(cyclist);
        book.addRide(new RideBuilder().withMaxGroupSize("1").withRoster(cyclist.getPhone().value).build());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("valid.json"));
        storage.saveAddressBook(book);
        assertEquals(book, storage.readAddressBook().orElseThrow());
    }

    private void assertInvalidRoster(RideBuilder builder, List<Person> cyclists) throws Exception {
        AddressBook book = new AddressBook();
        cyclists.forEach(book::addPerson);
        book.addRide(builder.build());
        Path path = temporaryFolder.resolve("invalid.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(path);
        storage.saveAddressBook(book);
        String saved = Files.readString(path);
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(saved, Files.readString(path));
    }
}
