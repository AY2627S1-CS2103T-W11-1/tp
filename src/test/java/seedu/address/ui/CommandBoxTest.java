package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.JavaFxTestUtil.runOnJavaFxThread;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Phone;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RideBuilder;

/**
 * Tests FXML command entry, failure recovery, and the overlap confirmation flow shown to the user.
 */
public class CommandBoxTest {
    private static final String PHONE = "+65 91234567";

    @TempDir
    public Path temporaryFolder;

    @Test
    public void enter_emptyInput_doesNotExecute() throws Exception {
        runOnJavaFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = new CommandBox(text -> {
                commands.add(text);
                return new CommandResult("Done");
            });
            input(box).fireEvent(new ActionEvent());
            assertTrue(commands.isEmpty());
        });
    }

    @Test
    public void enter_success_passesExactInputAndClearsField() throws Exception {
        runOnJavaFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = new CommandBox(text -> {
                commands.add(text);
                return new CommandResult("Done");
            });
            enter(box, "  yes  ");
            assertEquals(List.of("  yes  "), commands);
            assertEquals("", input(box).getText());
            assertFalse(input(box).getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        });
    }

    @Test
    public void enter_commandAndParseFailures_preserveInputWithoutDuplicateErrorStyles() throws Exception {
        runOnJavaFxThread(() -> {
            CommandBox box = new CommandBox(text -> {
                if (text.equals("invalid syntax")) {
                    throw new ParseException("Invalid syntax");
                }
                throw new CommandException("Invalid confirmation");
            });
            for (String text : List.of("maybe", "invalid syntax")) {
                enter(box, text);
                input(box).fireEvent(new ActionEvent());
                assertEquals(text, input(box).getText());
                assertEquals(1, input(box).getStyleClass().stream()
                        .filter(CommandBox.ERROR_STYLE_CLASS::equals).count());
            }
            box.setCommandText("rideaddcyclist ");
            assertFalse(input(box).getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertEquals("rideaddcyclist ".length(), input(box).getCaretPosition());
            assertEquals("", input(box).getSelectedText());
        });
    }

    @Test
    public void enter_overlapConfirmation_displaysWarningAndPersistsOnlyAfterYes() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withPhone(PHONE).withFtp("250").build());
        model.addRide(new RideBuilder().withName("Ride A").withRoster(PHONE).build());
        model.addRide(new RideBuilder().withName("Ride B").build());
        StorageManager storage = new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("book.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        storage.saveAddressBook(model.getAddressBook());
        LogicManager logic = new LogicManager(model, storage);

        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            TextArea feedback = (TextArea) display.getRoot().lookup("#resultDisplay");
            CommandBox box = new CommandBox(text -> {
                try {
                    CommandResult result = logic.execute(text);
                    display.setFeedbackToUser(result.getFeedbackToUser());
                    return result;
                } catch (CommandException | ParseException exception) {
                    display.setFeedbackToUser(exception.getMessage());
                    throw exception;
                }
            });
            enter(box, "rideaddcyclist 2 " + PHONE);
            assertTrue(feedback.getText().startsWith("Warning: overlapping ride times."));
            assertTrue(feedback.getText().contains("Ride A"));
            assertTrue(model.getAddressBook().getRideList().get(1).getRoster().isEmpty());
            assertEquals("", input(box).getText());
            assertFalse(input(box).getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));

            enter(box, "maybe");
            assertTrue(feedback.getText().startsWith("Invalid confirmation."));
            assertTrue(input(box).getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            enter(box, "no");
            assertEquals(LogicManager.MESSAGE_OVERLAP_CANCELLED, feedback.getText());
            assertTrue(model.getAddressBook().getRideList().get(1).getRoster().isEmpty());

            enter(box, "rideaddcyclist 2 " + PHONE);
            enter(box, "YeS");
            assertTrue(feedback.getText().contains("added to ride 2"));
            assertEquals(List.of(new Phone(PHONE)), model.getAddressBook().getRideList().get(1).getRoster());
            assertEquals("", input(box).getText());
            assertFalse(input(box).getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertFalse(feedback.isEditable());
        });
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void setFeedback_null_rejectsWithoutReplacingExistingMessage() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            display.setFeedbackToUser("Previous message");
            assertThrows(NullPointerException.class, () -> display.setFeedbackToUser(null));
            assertEquals("Previous message", ((TextArea) display.getRoot().lookup("#resultDisplay")).getText());
        });
    }

    private TextField input(CommandBox box) {
        return (TextField) box.getRoot().lookup("#commandTextField");
    }

    private void enter(CommandBox box, String text) {
        box.setCommandText(text);
        input(box).fireEvent(new ActionEvent());
    }
}
