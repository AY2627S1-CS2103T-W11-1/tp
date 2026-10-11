package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_samePhoneDifferentEmails_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_differentPhonesSameEmail_throwsIllegalValueException() {
        Person personWithSameEmail = new PersonBuilder(ALICE).withName("Different Name")
                .withPhone("+65 80000000").build();
        JsonSerializableAddressBook data = createSerializableAddressBook(ALICE, personWithSameEmail);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                data::toModelType);
    }

    @Test
    public void toModelType_emailsDifferOnlyInCase_throwsIllegalValueException() {
        Person personWithSameEmail = new PersonBuilder(ALICE).withName("Different Name")
                .withPhone("+65 80000000")
                .withEmail(ALICE.getEmail().value.toUpperCase(Locale.ROOT)).build();
        JsonSerializableAddressBook data = createSerializableAddressBook(ALICE, personWithSameEmail);

        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                data::toModelType);
    }

    @Test
    public void toModelType_sameNameWithDistinctIdentity_loadsAllCyclists() throws Exception {
        Person personWithSameName = new PersonBuilder(ALICE).withPhone("+65 80000000")
                .withEmail("other.alice@example.com").build();
        JsonSerializableAddressBook data = createSerializableAddressBook(ALICE, personWithSameName);
        AddressBook expectedAddressBook = new AddressBook();
        expectedAddressBook.addPerson(ALICE);
        expectedAddressBook.addPerson(personWithSameName);
        AddressBook actualAddressBook = data.toModelType();

        assertEquals(expectedAddressBook, actualAddressBook);
        assertEquals(2, actualAddressBook.getPersonList().size());
    }

    private JsonSerializableAddressBook createSerializableAddressBook(Person... persons) {
        return new JsonSerializableAddressBook(Arrays.stream(persons).map(JsonAdaptedPerson::new).toList());
    }

}
