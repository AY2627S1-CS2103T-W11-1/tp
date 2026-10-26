package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;
import seedu.address.testutil.RideUtil;

/**
 * Checks alias dispatch, validation, persistence, and pending deletion cancellation.
 */
public class RideCommandAliasTest {
    private static final String PHONE = "+65 91234567";
    private static final String RIDE_ARGUMENTS = RideUtil.getRideAddCommand(new RideBuilder().build())
            .substring("rideadd".length());
    @TempDir
    public Path temporaryFolder;

    private final AddressBookParser parser = new AddressBookParser();

    private static List<String[]> rideCommands() {
        return List.of(
                new String[] {"rideadd", "ra", RIDE_ARGUMENTS},
                new String[] {"rideedit", "re", " 1 notes/Bring lights"},
                new String[] {"ridedelete", "rd", " 1"},
                new String[] {"ridefind", "rf", " n/Sunday"},
                new String[] {"ridelist", "rl", " 1"},
                new String[] {"rideaddcyclist", "rac", " 1 " + PHONE},
                new String[] {"ridedeletecyclist", "rdc", " 1 " + PHONE});
    }

    @Test
    public void parse_aliasAndFullName_createEquivalentCommands() throws Exception {
        for (String[] command : rideCommands()) {
            String word = command[0];
            String alias = command[1];
            String arguments = command[2];
            assertEquivalentCommands(word + arguments, alias + arguments);
            assertEquivalentCommands(word + arguments, "  " + alias + arguments + "  ");
            assertTrue(parser.isRecognisedCommand("  " + alias + "  "));
            assertTrue(parser.isRecognisedCommand(alias + " invalid arguments"));
        }
    }

    @Test
    public void parse_invalidArguments_preservesFullCommandError() {
        for (String[] command : rideCommands()) {
            String word = command[0];
            String alias = command[1];
            for (String invalidArguments : List.of("", " invalid arguments", " 0", " 1 extra")) {
                ParseException expected = assertThrows(ParseException.class, () ->
                        parser.parseCommand(word + invalidArguments));
                ParseException actual = assertThrows(ParseException.class, () ->
                        parser.parseCommand(alias + invalidArguments));
                assertEquals(expected.getMessage(), actual.getMessage());
            }
        }
    }

    @Test
    public void parse_unsupportedSpellings_remainUnknown() {
        for (String[] command : rideCommands()) {
            String alias = command[1];
            for (String unknown : List.of(alias.toUpperCase(Locale.ROOT), alias + "1", alias + "suffix")) {
                assertFalse(parser.isRecognisedCommand(unknown));
                assertEquals(Messages.MESSAGE_UNKNOWN_COMMAND,
                        assertThrows(ParseException.class, () -> parser.parseCommand(unknown)).getMessage());
            }
        }
    }

    @Test
    public void execute_aliasWorkflow_matchesFullNamesAndPersists() throws Exception {
        ModelManager fullModel = createModel(false);
        ModelManager aliasModel = createModel(false);
        StorageManager fullStorage = createStorage("full");
        StorageManager aliasStorage = createStorage("alias");
        Logic fullLogic = new LogicManager(fullModel, fullStorage);
        Logic aliasLogic = new LogicManager(aliasModel, aliasStorage);
        String[][] commands = {
            {"rideadd" + RIDE_ARGUMENTS, "ra" + RIDE_ARGUMENTS},
            {"rideedit 2 notes/Bring lights", "re 2 notes/Bring lights"},
            {"rideaddcyclist 2 " + PHONE, "rac 2 " + PHONE},
            {"ridelist 2", "rl 2"},
            {"ridefind notes/Bring lights", "rf notes/Bring lights"},
            {"ridedeletecyclist 2 " + PHONE, "rdc 2 " + PHONE},
            {"ridefind all", "rf all"},
            {"ridedelete 2", "rd 2"}
        };
        for (String[] pair : commands) {
            assertEquals(fullLogic.execute(pair[0]), aliasLogic.execute(pair[1]));
            assertEquals(fullModel.getAddressBook(), aliasModel.getAddressBook());
            assertEquals(fullModel.getFilteredRideList(), aliasModel.getFilteredRideList());
            assertEquals(fullModel.getFilteredPersonList(), aliasModel.getFilteredPersonList());
            assertEquals(aliasModel.getAddressBook(), aliasStorage.readAddressBook().orElseThrow());
            assertEquals(fullStorage.readAddressBook(), aliasStorage.readAddressBook());
        }
        assertEquals(1, aliasModel.getAddressBook().getPersonList().size());
        assertEquals(1, aliasModel.getAddressBook().getRideList().size());
        assertTrue(aliasModel.getAddressBook().getRideList().getFirst().getRoster().isEmpty());
    }

    @Test
    public void execute_alias_cancelsPendingDeletion() throws Exception {
        for (String[] command : rideCommands()) {
            String alias = command[1];
            String arguments = command[2];
            ModelManager model = createModel(alias.equals("rdc"));
            StorageManager storage = createStorage(alias);
            Logic logic = new LogicManager(model, storage);
            logic.execute("delete " + PHONE);

            assertTrue(logic.execute(alias + arguments).getFeedbackToUser()
                    .startsWith(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n"));
            assertEquals(1, model.getAddressBook().getPersonList().size());
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
            assertEquals(Messages.MESSAGE_UNKNOWN_COMMAND,
                    assertThrows(ParseException.class, () -> logic.execute("yes")).getMessage());
        }
    }

    @Test
    public void execute_malformedAlias_cancelsPendingDeletionWithoutSaving() throws Exception {
        for (String[] command : rideCommands()) {
            String word = command[0];
            String alias = command[1];
            ModelManager model = createModel(false);
            StorageManager storage = createStorage(alias);
            storage.saveAddressBook(model.getAddressBook());
            String originalJson = Files.readString(storage.getAddressBookFilePath());
            Logic logic = new LogicManager(model, storage);
            logic.execute("delete " + PHONE);
            String expectedError = assertThrows(ParseException.class, () -> parser.parseCommand(word)).getMessage();

            assertEquals(DeleteCommand.MESSAGE_DELETE_CANCELLED + "\n" + expectedError,
                    assertThrows(ParseException.class, () -> logic.execute(alias)).getMessage());
            assertEquals(originalJson, Files.readString(storage.getAddressBookFilePath()));
            assertEquals(1, model.getAddressBook().getPersonList().size());
            assertEquals(Messages.MESSAGE_UNKNOWN_COMMAND,
                    assertThrows(ParseException.class, () -> logic.execute("yes")).getMessage());
        }
    }

    private void assertEquivalentCommands(String fullInput, String aliasInput) throws Exception {
        ModelManager fullModel = createModel(fullInput.startsWith("ridedeletecyclist"));
        ModelManager aliasModel = createModel(fullInput.startsWith("ridedeletecyclist"));
        var fullCommand = parser.parseCommand(fullInput);
        var aliasCommand = parser.parseCommand(aliasInput);
        assertEquals(fullCommand.getClass(), aliasCommand.getClass());
        assertEquals(fullCommand.execute(fullModel), aliasCommand.execute(aliasModel));
        assertEquals(fullModel.getAddressBook(), aliasModel.getAddressBook());
        assertEquals(fullModel.getFilteredRideList(), aliasModel.getFilteredRideList());
        assertEquals(fullModel.getFilteredPersonList(), aliasModel.getFilteredPersonList());
    }

    private ModelManager createModel(boolean hasRoster) {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withPhone(PHONE).withFtp("600").build());
        RideBuilder builder = new RideBuilder();
        if (hasRoster) {
            builder.withRoster(PHONE);
        }
        model.addRide(builder.build());
        return model;
    }

    private StorageManager createStorage(String name) {
        return new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve(name + ".json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve(name + "-prefs.json")));
    }
}
