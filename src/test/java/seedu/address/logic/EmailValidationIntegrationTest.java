package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Email;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Tests email validation through add/edit commands and JSON persistence.
 */
public class EmailValidationIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private StorageManager storage;
    private Path bookPath;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(new AddressBook(), new UserPrefs());
        bookPath = temporaryFolder.resolve("book.json");
        storage = new StorageManager(new JsonAddressBookStorage(bookPath),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_addAndEditAdjacentSpecialCharacters_persistsEmail() throws Exception {
        String[] emails = {"rider__club@example.com", "rider++club@example.com", "rider_+.-club@example.com"};
        for (String email : emails) {
            logic.execute("add n/Alpha Rider p/+65 91234567 sos/+65 99999999 e/" + email
                    + " f/200 x/Beginner l/any");
            assertSavedEmail(email);
            logic.execute("clear");
        }

        logic.execute("add n/Alpha Rider p/+65 91234567 sos/+65 99999999"
                + " e/rider_club@example.com f/200 x/Beginner l/any");
        assertSavedEmail("rider_club@example.com");
        for (String email : emails) {
            logic.execute("edit +65 91234567 e/" + email);
            assertSavedEmail(email);
        }
    }

    @Test
    public void execute_invalidEmail_rejectsAddAndEditWithoutChanges() throws Exception {
        logic.execute("add n/Alpha Rider p/+65 91234567 sos/+65 99999999"
                + " e/rider_club@example.com f/200 x/Beginner l/any");
        String originalJson = Files.readString(bookPath);
        AddressBook originalBook = new AddressBook(model.getAddressBook());

        for (String email : new String[] {"rider..club@example.com", "__rider@example.com",
            "rider++@example.com", "rider__club@exam_ple.com"}) {
            String editCommand = "edit +65 91234567 e/" + email;
            String addCommand = "add n/Beta Rider p/+65 92234567 sos/+65 99999999 e/" + email
                    + " f/200 x/Beginner l/any";
            assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> logic.execute(editCommand));
            assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> logic.execute(addCommand));
            assertEquals(originalBook, model.getAddressBook());
            assertEquals(originalJson, Files.readString(bookPath));
        }
    }

    private void assertSavedEmail(String email) throws Exception {
        assertEquals(new Email(email), model.getAddressBook().getPersonList().get(0).getEmail());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}
