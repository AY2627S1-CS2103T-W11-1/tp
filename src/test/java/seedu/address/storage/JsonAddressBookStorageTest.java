package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.ride.Ride;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void readAndSaveAddressBook_withRides_keepsRidesAndContinuesIdNumbering() throws Exception {
        Path filePath = testFolder.resolve("RidesAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        original.addRide(new RideBuilder().withNotes("Bring lights").build());
        original.addRide(new RideBuilder().withName("Evening Spin").withRoster(ALICE.getPhone().value).build());
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        jsonAddressBookStorage.saveAddressBook(original, filePath);
        AddressBook readBack = new AddressBook(jsonAddressBookStorage.readAddressBook(filePath).get());

        assertEquals(original, readBack);
        assertEquals(2, readBack.getRideList().size());
        assertEquals(3, readBack.getNextRideId());

        // a ride added after a restart gets the next number
        Ride added = readBack.addRide(new RideBuilder().withName("Night Ride").build());
        assertEquals(3, added.getId());
        jsonAddressBookStorage.saveAddressBook(readBack, filePath);
        assertEquals(4, jsonAddressBookStorage.readAddressBook(filePath).get().getNextRideId());
    }

    @Test
    public void readAddressBook_fileWithoutRides_loadsWithNoRides() throws Exception {
        Path filePath = testFolder.resolve("NoRidesAddressBook.json");
        Files.writeString(filePath, "{ \"persons\" : [ ] }");

        AddressBook readBack = new AddressBook(new JsonAddressBookStorage(filePath).readAddressBook(filePath).get());

        assertEquals(0, readBack.getRideList().size());
        assertEquals(1, readBack.getNextRideId());
    }

    @Test
    public void readAddressBook_fileWithNullRideEntry_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("NullRideAddressBook.json");
        Files.writeString(filePath, "{ \"persons\" : [ ], \"rides\" : [ null ] }");

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(filePath).readAddressBook(filePath));
    }

    @Test
    public void readAddressBook_fileWithOnlyNullRides_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("OnlyNullRidesAddressBook.json");
        Files.writeString(filePath, "{ \"persons\" : [ ], \"rides\" : [ null, null ], \"nextRideId\" : 3 }");

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(filePath).readAddressBook(filePath));
    }

    @Test
    public void readAddressBook_fileWithInvalidRide_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("InvalidRideAddressBook.json");
        Files.writeString(filePath, "{ \"persons\" : [ ], \"rides\" : [ { \"id\" : 1, \"name\" : \"Ride\" } ] }");

        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(filePath).readAddressBook(filePath));
    }

    @Test
    public void readAndSaveAddressBook_mixedCaseEmail_preservesOriginalCasing() throws Exception {
        Path filePath = testFolder.resolve("MixedCaseEmailAddressBook.json");
        Person mixedCaseEmailPerson = new PersonBuilder().withEmail("Mixed.Case@Example.COM").build();
        AddressBook original = new AddressBook();
        original.addPerson(mixedCaseEmailPerson);
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();

        assertEquals(original, new AddressBook(readBack));
        assertEquals("Mixed.Case@Example.COM", readBack.getPersonList().get(0).getEmail().value);
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
